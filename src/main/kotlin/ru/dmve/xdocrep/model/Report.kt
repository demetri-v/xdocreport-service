package ru.dmve.xdocrep.model

import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * Отчет из БД
 */
@Getter
@Setter
@Entity
@Table(name = "report")
data class Report(
        /**
     * Идентификатор отчета.
     */
    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "reportIdSeq")
    @SequenceGenerator(name = "reportIdSeq", sequenceName = "report_id_seq", allocationSize = 1)
        val reportId: Long,

        /**
     * Имя отчета.
     */
    @Column(name = "REPORT_NAME")
        val reportName: String,

        /**
     * Файл отчета
     */
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "REPORT_FILE")
        val reportFile: ByteArray,

        /**
     * SQL-запросы для отчета.
     */
    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER)
        val requests: List<ReportRequest> = ArrayList<ReportRequest>()
) : Serializable {

    override fun toString() = "Id: $reportId, Report name: $reportName, Report file: $reportFile"

}