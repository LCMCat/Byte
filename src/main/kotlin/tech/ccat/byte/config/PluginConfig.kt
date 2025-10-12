package tech.ccat.byte.config

import org.bukkit.configuration.ConfigurationSection

class PluginConfig(config: ConfigurationSection) {
    val commandEntrance = config.getString("command.entrance") ?: "byte"

    val byteCurrencyConfig = CurrencyConfig(
        config.getConfigurationSection("currencies.byte") ?: config.createSection("currencies.byte"),
        "byte",
        "Byte",
        "§9§k|||§r",
        "&9",
        "byte"
    )
    
    val coinCurrencyConfig = CurrencyConfig(
        config.getConfigurationSection("currencies.coin") ?: config.createSection("currencies.coin"),
        "coin",
        "Coin",
        "＄",
        "&e",
        "coin"
    )
    
    val pointCurrencyConfig = CurrencyConfig(
        config.getConfigurationSection("currencies.point") ?: config.createSection("currencies.point"),
        "point",
        "Point",
        "■",
        "&3",
        "point"
    )

    // MongoDB配置
    val mongoUri: String = config.getString("mongo.uri") ?: "mongodb://localhost:27017"
    
    // 默认MongoDB配置（向后兼容）
    val mongoDatabase: String = config.getString("mongo.database") ?: "CatTitanium"
    val mongoCollection: String = config.getString("mongo.collection") ?: "bytesCollection"
    val transactionRecordsCollection: String = config.getString("mongo.transaction_records_collection") ?: "transactionRecords"
    
    // 每种货币的独立MongoDB配置
    val byteMongoConfig = MongoConfig(
        config.getConfigurationSection("currencies.byte.mongo") ?: config.createSection("currencies.byte.mongo"),
        "CatTitanium",  // 默认数据库
        "bytesCollection",  // 默认集合
        "bytesTransactionRecords"  // 默认交易记录集合
    )
    
    val coinMongoConfig = MongoConfig(
        config.getConfigurationSection("currencies.coin.mongo") ?: config.createSection("currencies.coin.mongo"),
        "CatTitanium",
        "coinsCollection",
        "coinsTransactionRecords"
    )
    
    val pointMongoConfig = MongoConfig(
        config.getConfigurationSection("currencies.point.mongo") ?: config.createSection("currencies.point.mongo"),
        "CatTitanium",
        "pointsCollection",
        "pointsTransactionRecords"
    )
    
    val shutdownOnFailure: Boolean = config.getBoolean("shutdown-on-failure", false)
}

/**
 * 货币配置类
 */
data class CurrencyConfig(
    val id: String,
    val defaultName: String,
    val defaultSymbol: String,
    val defaultColor: String,
    val defaultCommand: String,
    val name: String,
    val symbol: String,
    val color: String,
    val command: String,
    val enabled: Boolean
) {
    constructor(
        config: ConfigurationSection,
        id: String,
        defaultName: String,
        defaultSymbol: String,
        defaultColor: String,
        defaultCommand: String
    ) : this(
        id = id,
        defaultName = defaultName,
        defaultSymbol = defaultSymbol,
        defaultColor = defaultColor,
        defaultCommand = defaultCommand,
        name = config.getString("name") ?: defaultName,
        symbol = config.getString("symbol") ?: defaultSymbol,
        color = config.getString("color") ?: defaultColor,
        command = config.getString("command") ?: defaultCommand,
        enabled = config.getBoolean("enabled", true)
    )
}

/**
 * MongoDB配置类
 */
data class MongoConfig(
    val database: String,
    val collection: String,
    val transactionRecordsCollection: String
) {
    constructor(
        config: ConfigurationSection,
        defaultDatabase: String,
        defaultCollection: String,
        defaultTransactionRecordsCollection: String
    ) : this(
        database = config.getString("database") ?: defaultDatabase,
        collection = config.getString("collection") ?: defaultCollection,
        transactionRecordsCollection = config.getString("transaction_records_collection") ?: defaultTransactionRecordsCollection
    )
}