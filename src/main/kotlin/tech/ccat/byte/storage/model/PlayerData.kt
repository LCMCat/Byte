package tech.ccat.byte.storage.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

data class PlayerData @BsonCreator constructor(
    @BsonProperty("uuid")
    val uuid: UUID,

    @BsonProperty("balance")
    var balance: Double,

    // 添加版本字段用于乐观锁
    @BsonProperty("version")
    var version: Long = 1
)