package org.example.project.infastructure

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.example.project.domain.entities.Evidences
import org.example.project.domain.entities.FormSatuTable
import org.example.project.domain.entities.Products
import org.example.project.domain.entities.Reports
import org.example.project.model.entities.FormSatu
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init(config: ApplicationConfig) {
        try {
            val config = HikariConfig().apply {
                jdbcUrl = config.propertyOrNull("database.url")?.getString()
                  driverClassName = config.propertyOrNull("database.driver")?.getString()
                username = System.getenv("DATABASE_USER")
                    ?: config.property("haijuf").getString()

                password = System.getenv("DATABASE_PASSWORD")
                    ?: config.property("haijuf").getString()
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
            dataSource = HikariDataSource(config)
            Database.connect(dataSource)
            logger.info("Connected to database")
            transaction {
                SchemaUtils.create(Products, Reports, Evidences, FormSatuTable)
                logger.info("Tables created successfully.")
            }
        } catch (e: Exception) {
            logger.error("Failed to connect to database", e)
        }
    }

    fun dropTables() {
        transaction {
            exec("DROP TABLE IF EXISTS evidences CASCADE")
            exec("DROP TABLE IF EXISTS reports CASCADE")
            exec("DROP TABLE IF EXISTS form_satu CASCADE")
            exec("DROP TABLE IF EXISTS products CASCADE")
            logger.info("Tables dropped successfully.")
        }
    }
}

