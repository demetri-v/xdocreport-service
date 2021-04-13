package ru.dmve.xdocrep.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.dmve.xdocrep.model.ReportRequest

@Repository
interface ReportRequestRepository: JpaRepository<ReportRequest, Long> {
}