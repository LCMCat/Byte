// MongoPlayerDataDao.kt
package tech.ccat.byte.storage.dao

import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import com.mongodb.client.model.Filters.eq

class MongoPlayerDataDao(
    private val collection: MongoCollection<PlayerData>
) : PlayerDataDao {

    override fun load(uuid: UUID): PlayerData? {
        return collection.find(eq("_id", uuid)).first()
    }

    override fun save(data: PlayerData) {
        collection.replaceOne(
            eq("_id", data.uuid),
            data,
            com.mongodb.client.model.ReplaceOptions().upsert(true)
        )
    }

    override fun delete(uuid: UUID) {
        collection.deleteOne(eq("_id", uuid))
    }
}