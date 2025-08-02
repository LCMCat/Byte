package tech.ccat.byte.storage.dao

import tech.ccat.byte.storage.model.PlayerData
import java.util.*

interface PlayerDataDao {
    fun load(uuid: UUID): PlayerData?

    fun create(data: PlayerData): Boolean

    // 原子更新方法
    fun atomicUpdate(
        uuid: UUID,
        currentVersion: Long,
        update: (PlayerData) -> PlayerData
    ): Boolean
}