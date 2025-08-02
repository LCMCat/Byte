package tech.ccat.byte.config

import org.bukkit.configuration.ConfigurationSection

class PluginConfig(private val config: ConfigurationSection) {
    val mongoUri: String = config.getString("mongo.uri") ?: "mongodb://localhost:27017"
    val mongoDatabase: String = config.getString("mongo.database") ?: "byte_economy"
    val symbol: String = config.getString("currency.symbol") ?: "§9|||"
    val cacheTTL: Long = config.getLong("cache.ttl", 900000)
    val syncInterval: Int = config.getInt("cache.sync-interval", 60)
}