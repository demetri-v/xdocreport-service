package ru.dmve.xdocrep.controller.impl

import fr.opensagres.xdocreport.template.TemplateEngineKind
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.dmve.xdocrep.controller.TemplateController
import ru.dmve.xdocrep.model.Report
import ru.dmve.xdocrep.model.ReportRequest
import ru.dmve.xdocrep.repository.ReportRepository
import ru.dmve.xdocrep.repository.ReportRequestRepository
import ru.dmve.xdocrep.service.FileStorageService
import ru.dmve.xdocrep.service.XDocReportService
import ru.dmve.xdocrep.utlis.OutFileType
import ru.dmve.xdocrep.service.ex.PrintReportException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
class TemplateControllerImpl(private val reportService: XDocReportService,
                             private val fileStorageService: FileStorageService,
                             private val reportRepository: ReportRepository,
                             private val requestRepository: ReportRequestRepository) : TemplateController {

    companion object {
        private const val DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessing"
        private const val PDF_CONTENT_TYPE  = "application/pdf"
        private val FILE_TO_CONTENT = mapOf(
                OutFileType.DOCX to DOCX_CONTENT_TYPE,
                OutFileType.PDF to PDF_CONTENT_TYPE)
    }

    override fun getReportByTemplateId(request: HttpServletRequest, response: HttpServletResponse, templateId: String, format: String) {
        val params: Map<String, String> = request.parameterMap.mapValues { it.value[0] }
        val objects: Map<String, String> = request.parameterMap.mapValues { it.value[0] }
        val outFileType =  OutFileType.valueOf(format.toUpperCase())
        val byteFile = reportService.generateReportById(templateId, params, objects, outFileType, TemplateEngineKind.Freemarker)
        response.contentType = FILE_TO_CONTENT[outFileType]
        response.outputStream.write(byteFile)
    }

    override fun addTemplate(file: MultipartFile): ResponseEntity<String> {
        val fileName = fileStorageService.storeFile(file)
        val report = Report(0, fileName, file.bytes)
        reportRepository.save(report)
        return ResponseEntity<String>(report.reportId.toString(), HttpStatus.OK)
    }

    override fun addSqlRequest(templateId: String, sql: String, prefix: String): ResponseEntity<String> {
        val report: Report? = reportRepository.findByReportId(templateId.toLong())
        if (report == null) {
            val message = "Report template not found: $templateId"
            throw PrintReportException(message)
        }
        val request = ReportRequest(0, sql, prefix, report)
        requestRepository.save(request)
        return ResponseEntity<String>(request.id.toString(), HttpStatus.OK)
    }



}