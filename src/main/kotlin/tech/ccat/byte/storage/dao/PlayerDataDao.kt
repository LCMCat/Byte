package tech.ccat.byte.storage.dao

import tech.ccat.byte.storage.model.PlayerData
import java.util.*

interface PlayerDataDao {
    fun load(uuid: UUID): PlayerData?
    fun save(data: PlayerData)
    fun delete(uuid: UUID)
}