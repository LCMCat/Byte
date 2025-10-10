package tech.ccat.byte.storage.dao

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

import tech.ccat.byte.BytePlugin.Companion.instance
import tech.ccat.byte.exception.DatabaseException
import tech.ccat.byte.util.ExceptionHandler
import tech.ccat.byte.util.LoggerUtil

class MongoPlayerDataDao(
    private val collection: MongoCollection<PlayerData>
) : PlayerDataDao {

    private val plugin = instance

    override fun load(uuid: UUID): PlayerData? {
        return ExceptionHandler.wrap("加载玩家数据") {
            collection.find(eq("uuid", uuid)).first()
        }
    }

    override fun create(data: PlayerData): Boolean {
        return ExceptionHandler.wrapOrDefault("创建玩家数据", false) {
            try {
                collection.insertOne(data)
                true
            } catch (e: MongoWriteException) {
                // 已经存在的情况不算错误
                if (e.code == 11000) {
                    true
                } else {
                    throw DatabaseException("创建玩家数据失败: ${data.uuid}", e)
                }
            }
        } ?: false
    }

    override fun atomicUpdate(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): Boolean {
        return ExceptionHandler.wrapOrDefault("原子更新玩家数据", false) {
            val current = collection.find(eq("uuid", uuid)).first()
                ?: return@wrapOrDefault false

            val updated = update(current).apply {
                version = current.version + 1  // 增加版本号
            }

            val result = collection.replaceOne(
                and(eq("uuid", uuid), eq("version", current.version)),
                updated
            )

            result.modifiedCount == 1L
        } ?: false
    }

    override fun loadAsync(uuid: UUID): CompletableFuture<PlayerData?> {
        val future = CompletableFuture<PlayerData?>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            try {
                future.complete(load(uuid))
            } catch (e: Exception) {
                LoggerUtil.severe("异步加载玩家数据失败: $uuid", e)
                future.completeExceptionally(e)
            }
        })
        ExceptionHandler.handleFutureException(future, "异步加载玩家数据")
        return future
    }

    override fun createAsync(data: PlayerData): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            try {
                future.complete(create(data))
            } catch (e: Exception) {
                LoggerUtil.severe("异步创建玩家数据失败: ${data.uuid}", e)
                future.completeExceptionally(e)
            }
        })
        ExceptionHandler.handleFutureException(future, "异步创建玩家数据")
        return future
    }

    override fun atomicUpdateAsync(
        uuid: UUID,
        update: (PlayerData) -> PlayerData
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            try {
                future.complete(atomicUpdate(uuid, update))
            } catch (e: Exception) {
                LoggerUtil.severe("异步原子更新玩家数据失败: $uuid", e)
                future.completeExceptionally(e)
            }
        })
        ExceptionHandler.handleFutureException(future, "异步原子更新玩家数据")
        return future
    }

    override fun getAllPlayers(): List<PlayerData> {
        return ExceptionHandler.wrapOrDefault("获取所有玩家数据", mutableListOf()) {
            collection.find().into(mutableListOf())
        }
    }

    override fun getAllPlayersAsync(): CompletableFuture<List<PlayerData>> {
        val future = CompletableFuture<List<PlayerData>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable{
            try {
                val players = getAllPlayers()
                future.complete(players)
            } catch (e: Exception) {
                LoggerUtil.severe("异步获取所有玩家数据失败", e)
                future.completeExceptionally(e)
            }
        })
        ExceptionHandler.handleFutureException(future, "异步获取所有玩家数据")
        return future
    }
}