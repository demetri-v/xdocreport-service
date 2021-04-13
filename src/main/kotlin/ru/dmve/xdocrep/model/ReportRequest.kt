package ru.dmve.xdocrep.model

import lombok.Getter
import lombok.Setter
import java.io.Serializable
import javax.persistence.*

/**
 * SQL запросы для отчетов.
 *
 */
@Getter
@Setter
@Entity
@Table(name = "report_request")
data class ReportRequest (
    /**
     * Идентификатор запроса.
     */
    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "reportRequestIdSeq")
    @SequenceGenerator(name = "reportRequestIdSeq", sequenceName = "report_request_id_seq", allocationSize = 1)
    val id: Long,

    /**
     * SQL запрос.
     */
    @Column(name = "SQL_REQUEST")
    val request: String,

    /**
     * Префикс для sql запросов.
     */
    @Column(name = "PREFIX")
    val prefix: String,

    /**
     * Отчет связанный с запросом.
     */
    @ManyToOne
    @JoinColumn(name = "ID_REPORT")
    val report: Report
) : Serializable