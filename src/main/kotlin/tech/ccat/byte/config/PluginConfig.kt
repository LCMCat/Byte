package tech.ccat.byte.config

import org.bukkit.configuration.ConfigurationSection

class PluginConfig(config: ConfigurationSection) {
    val currencyName = config.getString("currency.name")?: "Byte"
    val currencyFlag = config.getString("currency.flag")?: "§9§k|||§r"

    val commandEntrance = config.getString("command.entrance")?: "byte"

    val mongoUri: String = config.getString("mongo.uri") ?: "mongodb://localhost:27017"
    val mongoDatabase: String = config.getString("mongo.database") ?: "byte_economy"
    val mongoCollection: String = config.getString("mongo.collection")?: "BytesCollection"
    
    val shutdownOnFailure: Boolean = config.getBoolean("shutdown-on-failure", false)
}