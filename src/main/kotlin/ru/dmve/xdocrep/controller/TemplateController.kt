package ru.dmve.xdocrep.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
interface TemplateController {

    @Operation(
            summary = "Get Report File By Template Id",
            description = "The endpoint to generate Report by Template Id exported to a file")
    @RequestMapping(method = [RequestMethod.GET], value = ["/document/{id}/report.{format}"])
    fun getReportByTemplateId(request: HttpServletRequest,
                              response: HttpServletResponse,
                              @PathVariable(value = "id") templateId: String,
                              @PathVariable("format") format: String)

    @Operation(
            summary = "Upload Template File to the server and DB",
            description = "The endpoint to upload Template File to the server and add it to DB")
    @PutMapping("/template")
    fun addTemplate(@RequestParam("file") file: MultipartFile): ResponseEntity<String>

    @Operation(
            summary = "Add sql request to DB",
            description = "The endpoint to add sql request to DB")
    @PutMapping("/template/{id}/query")
    fun addSqlRequest(@PathVariable(value = "id") templateId: String,
                      @RequestParam("sql") sql: String,
                      @RequestParam("prefix") prefix: String): ResponseEntity<String>
}