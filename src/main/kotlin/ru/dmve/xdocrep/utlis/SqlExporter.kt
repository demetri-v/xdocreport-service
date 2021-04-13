package ru.dmve.xdocrep.utlis

import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.hibernate.internal.SessionImpl
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import javax.persistence.EntityManager

class SqlExporter(private var sql: String?,
                  private var table: String?,
                  entityManager: EntityManager) {

    companion object : Log

    private var dbConnection: Connection? = null

    init {
        dbConnection = entityManager.unwrap(SessionImpl::class.java).connection()
    }

    fun addFieldsMetaDataAndFieldsValue(fieldsMetadata: FieldsMetadata, context: IContext) {
        val rs = getResultSet(sql)

        val colCount = rs!!.metaData.columnCount
        val columnNames: MutableList<String> = ArrayList()
        for (i in 0 until colCount) {
            val colName = rs.metaData.getColumnName(i + 1)
            columnNames.add(colName.toLowerCase())
        }
        for (column in columnNames) {
            fieldsMetadata.addFieldAsList("$table.$column")
        }
        val fields: MutableMap<String, Any> = mutableMapOf()
        while (rs.next()) {
            for (column in columnNames) {
                var resultObject = rs.getObject(column)
                if (null == resultObject) {
                    resultObject = ""
                }
                fields[column] = resultObject
            }
        }
        context.put(table, fields)
        dbConnection!!.close()
    }

    fun addListFieldsMetaDataAndFieldsValue(fieldsMetadata: FieldsMetadata, context: IContext) {
        val rs = getResultSet(sql)
        val colCount = rs!!.metaData.columnCount
        val columnNames: MutableList<String> = ArrayList()
        for (i in 0 until colCount) {
            val colName = rs.metaData.getColumnName(i + 1)
            columnNames.add(colName.toLowerCase())
        }
        for (column in columnNames) {
            fieldsMetadata.addFieldAsList("$table.$column")
        }
        val fields: MutableList<Map<String, Any>> = ArrayList()
        while (rs.next()) {
            val pojo: MutableMap<String, Any> = HashMap()
            for (column in columnNames) {
                var resultObject = rs.getObject(column)
                if (null == resultObject) {
                    resultObject = ""
                }
                pojo[column] = resultObject
            }
            fields.add(pojo)
        }
        if (fields.isEmpty()) {
            val emptyResult: MutableMap<String, Any> = HashMap()
            for (column in columnNames) {
                emptyResult[column] = ""
            }
            fields.add(emptyResult)
        }
        context.put(table, fields)
        dbConnection!!.close()
    }

    private fun getResultSet(sql: String?): ResultSet? {
        val stmt: Statement
        var rs: ResultSet? = null
        try {
            stmt = dbConnection!!.createStatement()
            rs = stmt.executeQuery(sql)
        } catch (e: SQLException) {
            logger().warn(e.message, e)
        }
        return rs
    }


}