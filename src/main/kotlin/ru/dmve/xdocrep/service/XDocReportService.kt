package ru.dmve.xdocrep.service

import fr.opensagres.xdocreport.template.TemplateEngineKind
import ru.dmve.xdocrep.utlis.OutFileType

interface XDocReportService {
    /**
     * Генерация отчета из БД.
     *
     * @param reportId
     * @param params
     * @return byte[] - документ в формате docx.
     */
    fun generateReportById(reportId: String, params: Map<String, String>,
                             objects: Map<String, Any>?, outFileType: OutFileType,
                             engineKind: TemplateEngineKind): ByteArray
}