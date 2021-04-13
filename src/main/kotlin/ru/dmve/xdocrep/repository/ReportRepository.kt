package ru.dmve.xdocrep.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.dmve.xdocrep.model.Report

/**
 * Repository for reports.
 */
@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    fun findByReportName(reportName: String): Report?

    fun findByReportId(id: Long): Report?
}