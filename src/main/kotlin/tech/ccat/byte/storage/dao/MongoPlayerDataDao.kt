package tech.ccat.byte.storage.dao

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.Runnable
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

import tech.ccat.byte.BytePlugin.Companion.instance

class MongoPlayerDataDao(
    private val collection: MongoCollection<PlayerData>
) : PlayerDataDao {

    private val plugin = instance

    override fun load(uuid: UUID): PlayerData? {
        return collection.find(eq("uuid", uuid)).first()
    }

    override fun create(data: PlayerData): Boolean {
        return try {
            collection.insertOne(data)
            true
        } catch (e: MongoWriteException) {
            // 已经存在的情况不算错误
            e.code == 11000
        }
    }

    override fun atomicUpdate(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): Boolean {
        return try {
            // 读取当前数据
            val current = collection.find(eq("uuid", uuid)).first()
                ?: return false

            // 创建更新后对象
            val updated = update(current).apply {
                version = current.version + 1  // 增加版本号
            }

            // 原子条件更新
            val result = collection.replaceOne(
                and(eq("uuid", uuid), eq("version", current.version)),
                updated
            )

            result.modifiedCount == 1L
        } catch (_: Exception) {
            false
        }
    }

    override fun loadAsync(uuid: UUID): CompletableFuture<PlayerData?> {
        val future = CompletableFuture<PlayerData?>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            future.complete(load(uuid))
        })
        return future
    }

    override fun createAsync(data: PlayerData): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            future.complete(create(data))
        })
        return future
    }

    override fun atomicUpdateAsync(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            future.complete(atomicUpdate(uuid, update))
        })
        return future
    }
}