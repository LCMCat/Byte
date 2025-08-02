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

class MongoDBManager(private val config: PluginConfig) {private var mongoClient = MongoClients.create()
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
            .build()

        mongoClient = MongoClients.create(settings)
        database = mongoClient.getDatabase(config.mongoDatabase)
            .withCodecRegistry(codecRegistry)
    }

    fun getPlayerDataDao(): PlayerDataDao {
        return MongoPlayerDataDao(
            database.getCollection("BytesCollection", PlayerData::class.java)
                .withCodecRegistry(codecRegistry) // 应用编解码器到集合
        )
    }


    fun reconnect(config: PluginConfig) {
        close()
        connect()
    }

    fun close() {
        mongoClient.close()
    }
}