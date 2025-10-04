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
import tech.ccat.byte.storage.dao.MongoPlayerDataDao
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.model.PlayerData
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


    fun close() {
        mongoClient.close()
    }
}