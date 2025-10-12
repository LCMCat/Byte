package tech.ccat.byte.storage

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import tech.ccat.byte.config.PluginConfig
import tech.ccat.byte.currency.CurrencyType
import tech.ccat.byte.storage.dao.MongoPlayerDataDao
import tech.ccat.byte.storage.dao.MongoTransactionRecordDao
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.dao.TransactionRecordDao
import tech.ccat.byte.storage.model.PlayerData
import tech.ccat.byte.storage.model.TransactionRecord
import java.util.concurrent.TimeUnit

class MongoDBManager(private val config: PluginConfig) {
    private var mongoClient = MongoClients.create()

    private lateinit var database: MongoDatabase

    // 创建包含 POJO 支持的编解码器注册表
    private val codecRegistry: CodecRegistry by lazy {
        CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(UuidCodec()),
            CodecRegistries.fromProviders(
                PojoCodecProvider.builder()
                    .automatic(true)
                    .register(PlayerData::class.java)
                    .register(TransactionRecord::class.java)
                    .build()
            )
        )
    }

    fun connect() {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(config.mongoUri))
            .codecRegistry(codecRegistry)
            .uuidRepresentation(UuidRepresentation.STANDARD) // 关键配置
            // 连接池配置
            .applyToConnectionPoolSettings { builder ->
                builder
                    .maxSize(20) // 最大连接数
                    .minSize(5)  // 最小连接数
                    .maxWaitTime(10, TimeUnit.SECONDS) // 获取连接的最大等待时间
                    .maxConnectionLifeTime(30, TimeUnit.MINUTES) // 连接的最大生命周期
                    .maxConnectionIdleTime(10, TimeUnit.MINUTES) // 连接的最大空闲时间
            }
            // 服务器设置
            .applyToSocketSettings { builder ->
                builder
                    .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
            }
            .build()

        mongoClient = MongoClients.create(settings)
        database = mongoClient.getDatabase(config.mongoDatabase)
            .withCodecRegistry(codecRegistry)
    }

    fun getPlayerDataDao(): PlayerDataDao {
        return MongoPlayerDataDao(
            database.getCollection(config.mongoCollection, PlayerData::class.java)
                .withCodecRegistry(codecRegistry) // 应用编解码器到集合
        )
    }

    fun getTransactionRecordDao(): TransactionRecordDao {
        // 使用单独的集合存储交易记录
        return MongoTransactionRecordDao(
            database.getCollection(config.transactionRecordsCollection, TransactionRecord::class.java)
                .withCodecRegistry(codecRegistry) // 应用编解码器到集合
        )
    }
    
    // 为特定货币获取PlayerDataDao
    fun getPlayerDataDao(currencyId: String): PlayerDataDao {
        val databaseName = getDatabaseName(currencyId)
        val collectionName = getCollectionName(currencyId)
        val db = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
        return MongoPlayerDataDao(
            db.getCollection(collectionName, PlayerData::class.java)
                .withCodecRegistry(codecRegistry)
        )
    }
    
    // 为特定货币获取PlayerDataDao
    fun getPlayerDataDao(currencyType: CurrencyType): PlayerDataDao {
        return getPlayerDataDao(currencyType.getId())
    }
    
    // 为特定货币获取TransactionRecordDao
    fun getTransactionRecordDao(currencyId: String): TransactionRecordDao {
        val databaseName = getDatabaseName(currencyId)
        val collectionName = getTransactionRecordsCollectionName(currencyId)
        val db = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
        return MongoTransactionRecordDao(
            db.getCollection(collectionName, TransactionRecord::class.java)
                .withCodecRegistry(codecRegistry)
        )
    }
    
    // 为特定货币获取TransactionRecordDao
    fun getTransactionRecordDao(currencyType: CurrencyType): TransactionRecordDao {
        return getTransactionRecordDao(currencyType.getId())
    }
    
    // 根据货币ID获取数据库名称
    private fun getDatabaseName(currencyId: String): String {
        return when (currencyId.lowercase()) {
            "byte" -> config.byteMongoConfig.database
            "coin" -> config.coinMongoConfig.database
            "point" -> config.pointMongoConfig.database
            else -> config.mongoDatabase // 默认数据库
        }
    }
    
    // 根据货币类型获取数据库名称
    private fun getDatabaseName(currencyType: CurrencyType): String {
        return getDatabaseName(currencyType.getId())
    }
    
    // 根据货币ID获取集合名称
    private fun getCollectionName(currencyId: String): String {
        return when (currencyId.lowercase()) {
            "byte" -> config.byteMongoConfig.collection
            "coin" -> config.coinMongoConfig.collection
            "point" -> config.pointMongoConfig.collection
            else -> config.mongoCollection // 默认集合
        }
    }
    
    // 根据货币类型获取集合名称
    private fun getCollectionName(currencyType: CurrencyType): String {
        return getCollectionName(currencyType.getId())
    }
    
    // 根据货币ID获取交易记录集合名称
    private fun getTransactionRecordsCollectionName(currencyId: String): String {
        return when (currencyId.lowercase()) {
            "byte" -> config.byteMongoConfig.transactionRecordsCollection
            "coin" -> config.coinMongoConfig.transactionRecordsCollection
            "point" -> config.pointMongoConfig.transactionRecordsCollection
            else -> config.transactionRecordsCollection // 默认交易记录集合
        }
    }
    
    // 根据货币类型获取交易记录集合名称
    private fun getTransactionRecordsCollectionName(currencyType: CurrencyType): String {
        return getTransactionRecordsCollectionName(currencyType.getId())
    }

    fun close() {
        mongoClient.close()
    }
}