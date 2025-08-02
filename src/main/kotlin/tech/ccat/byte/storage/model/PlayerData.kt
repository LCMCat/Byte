package tech.ccat.byte.storage.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

data class PlayerData @BsonCreator constructor(
    @BsonProperty("_id")
    val uuid: UUID,

    @BsonProperty("balance")
    var balance: Double,

    @BsonProperty("dirty")
    var dirty: Boolean = false
)