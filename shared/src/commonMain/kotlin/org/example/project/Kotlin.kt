package org.example.project

import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import java.io.InputStream

object Config {
    private val yaml = Load(LoadSettings.builder().build())

    private fun loadConfig(): Map<String, Any> {
        val inputStream: InputStream = Config::class.java.getResourceAsStream("Application.yaml")
            ?: throw IllegalStateException("application.yaml not found")
        return yaml.loadFromInputStream(inputStream) as Map<String, Any>
    }

    val serverHost: String by lazy {
        val config = loadConfig()
        (config["server"] as Map<*, *>)["host"] as String
    }

    val serverPort: Int by lazy {
        val config = loadConfig()
        (config["server"] as Map<*, *>)["port"] as Int
    }

    val databaseUrl: String by lazy {
        val config = loadConfig()
        (config["database"] as Map<*, *>)["url"] as String
    }
    val databaseDriver: String by lazy {
        val config = loadConfig()
        (config["database"] as Map<*, *>)["driver"] as String
    }
    val databaseUsername: String by lazy {
        val config = loadConfig()
        (config["database"] as Map<*, *>)["username"] as String
    }
    val databasePassword: String by lazy {
        val config = loadConfig()
        (config["database"] as Map<*, *>)["password"] as String
    }

    val apiEndpoint: String by lazy {
        val config = loadConfig()
        (config["api"] as Map<*, *>)["endpoint"] as String
    }
}