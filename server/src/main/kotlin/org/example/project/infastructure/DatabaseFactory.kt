package org.example.project.infastructure

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.project.common.DatabaseConfig
import org.example.project.domain.entities.Products
import org.example.project.domain.entities.Reports
import org.example.project.domain.entities.Evidences
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init() {
        try {
            val config = HikariConfig().apply {
                jdbcUrl = DatabaseConfig.DATABASE_URL
                driverClassName = DatabaseConfig.DATABASE_DRIVER
                username = DatabaseConfig.DATABASE_USER
                password = DatabaseConfig.DATABASE_PASSWORD
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
            dataSource = HikariDataSource(config)
            Database.connect(dataSource)
            logger.info("Connected to database")
            transaction {
                SchemaUtils.create(Products, Reports, Evidences)
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
            exec("DROP TABLE IF EXISTS products CASCADE")
            logger.info("Tables dropped successfully.")
        }
    }
}

