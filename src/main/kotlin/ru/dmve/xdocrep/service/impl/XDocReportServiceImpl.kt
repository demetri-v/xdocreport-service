package ru.dmve.xdocrep.service.impl

import fr.opensagres.xdocreport.converter.ConverterTypeTo
import fr.opensagres.xdocreport.converter.ConverterTypeVia
import fr.opensagres.xdocreport.converter.Options
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.TemplateEngineKind
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.apache.poi.xwpf.converter.pdf.PdfOptions
import org.springframework.stereotype.Service
import ru.dmve.xdocrep.model.Report
import ru.dmve.xdocrep.repository.ReportRepository
import ru.dmve.xdocrep.service.XDocReportService
import ru.dmve.xdocrep.utlis.*
import ru.dmve.xdocrep.service.ex.PrintReportException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.transaction.Transactional


/**
 * Сервис формирования отчетов на XDocReport
 */
@Service
@Transactional
class XDocReportServiceImpl(private val entityManagerFactory: EntityManagerFactory,
                            private val reportRepository: ReportRepository) : XDocReportService {

    companion object : Log

    override fun generateReportById(reportId: String, params: Map<String, String>,
                             objects: Map<String, Any>?, outFileType: OutFileType,
                             engineKind: TemplateEngineKind): ByteArray {
        val report: Report? = reportRepository.findByReportId(reportId.toLong())
        if (report == null) {
            val message = "Report template not found: $reportId"
            throw PrintReportException(message)
        }
        return try {
            val ixDocReport = XDocReportRegistry.getRegistry()
                    .loadReport(ByteArrayInputStream(report.reportFile), engineKind)
            val context = ixDocReport.createContext()
            context.put("sysdate", SimpleDateFormat("yyyy-MM-dd").format(Date()))
            if (objects != null) {
                context.putMap(objects)
            }
            val metadata = ixDocReport.createFieldsMetadata()
            fillReportFieldsMetadataAndContext(report, params, context, metadata)
            val out = ByteArrayOutputStream()
            when(outFileType) {
                OutFileType.PDF -> {
                    val pdfOptions = PdfOptions.create().fontEncoding("CP1251")
                    val options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF).subOptions(pdfOptions)
                    ixDocReport.convert(context, options, out)
                }
                OutFileType.DOCX -> {
                    ixDocReport.process(context, out)
                }
            }
            out.toByteArray()
        } catch (e: Exception) {
            val message = "Failed to print report '$reportId'. Details: ${e.message}"
            logger().error(message, e)
            throw PrintReportException(message)
        }
    }

    private fun initRequestParams(sql: String, params: Map<String, String>): String {
        var resultQuery = sql
        params.forEach { (key, value) -> resultQuery = resultQuery.replace(":$key", value) }
        return resultQuery
    }

    private fun fillReportFieldsMetadataAndContext(xReport: Report, params: Map<String, String>, context: IContext,
                                                   metadata: FieldsMetadata) {
        for (reportRequest in xReport.requests) {
            val sql: String = reportRequest.request
            val sqlWithParams = initRequestParams(sql, params)
            val sqlExporter = SqlExporter(sqlWithParams, reportRequest.prefix, entityManagerFactory.createEntityManager())
            try {
                if(reportRequest.prefix.toLowerCase().contains("list"))
                    sqlExporter.addListFieldsMetaDataAndFieldsValue(metadata, context)
                else
                    sqlExporter.addFieldsMetaDataAndFieldsValue(metadata, context)
            } catch (ex: SQLException) {
                logger().warn(ex.message, ex)
            }
        }
    }
}
