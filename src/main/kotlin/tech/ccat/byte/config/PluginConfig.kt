package tech.ccat.byte.config

import org.bukkit.configuration.ConfigurationSection

class PluginConfig(config: ConfigurationSection) {
    val mongoUri: String = config.getString("mongo.uri") ?: "mongodb://localhost:27017"
    val mongoDatabase: String = config.getString("mongo.database") ?: "byte_economy"
}