package tech.ccat.byte.storage.dao

import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import java.util.concurrent.CompletableFuture

interface PlayerDataDao {
    fun load(uuid: UUID): PlayerData?
    fun loadAsync(uuid: UUID): CompletableFuture<PlayerData?>

    fun create(data: PlayerData): Boolean
    fun createAsync(data: PlayerData): CompletableFuture<Boolean>

    // 原子更新方法
    fun atomicUpdate(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): Boolean

    fun atomicUpdateAsync(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): CompletableFuture<Boolean>
}