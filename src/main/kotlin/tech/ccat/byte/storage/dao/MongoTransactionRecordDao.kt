package tech.ccat.byte.storage.dao

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bukkit.Bukkit
import tech.ccat.byte.BytePlugin.Companion.instance
import tech.ccat.byte.exception.DatabaseException
import tech.ccat.byte.storage.model.TransactionRecord
import tech.ccat.byte.storage.model.TransactionType
import tech.ccat.byte.util.ExceptionHandler
import tech.ccat.byte.util.LoggerUtil
import java.util.*
import java.util.concurrent.CompletableFuture

class MongoTransactionRecordDao(
    private val collection: MongoCollection<TransactionRecord>
) : TransactionRecordDao {

    private val plugin = instance

    override fun create(record: TransactionRecord): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                ExceptionHandler.wrap("创建交易记录") {
                    collection.insertOne(record)
                }
                future.complete(true)
            } catch (e: Exception) {
                LoggerUtil.severe("创建交易记录失败: ${record.uuid}", e)
                future.completeExceptionally(DatabaseException("创建交易记录失败: ${record.uuid}", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "创建交易记录")
        return future
    }

    override fun getRecordsByPlayer(playerUuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        val future = CompletableFuture<List<TransactionRecord>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val records = ExceptionHandler.wrapOrDefault("根据玩家获取交易记录", mutableListOf()) {
                    collection.find(
                        Filters.or(
                            Filters.eq("fromPlayerUuid", playerUuid),
                            Filters.eq("toPlayerUuid", playerUuid)
                        )
                    )
                        .sort(Sorts.descending("timestamp"))
                        .skip((page - 1) * pageSize)
                        .limit(pageSize)
                        .into(mutableListOf())
                }
                future.complete(records)
            } catch (e: Exception) {
                LoggerUtil.severe("根据玩家获取交易记录失败: $playerUuid", e)
                future.completeExceptionally(DatabaseException("根据玩家获取交易记录失败: $playerUuid", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "根据玩家获取交易记录")
        return future
    }

    override fun getAllRecords(page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        val future = CompletableFuture<List<TransactionRecord>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val records = ExceptionHandler.wrapOrDefault("获取所有交易记录", mutableListOf()) {
                    collection.find()
                        .sort(Sorts.descending("timestamp"))
                        .skip((page - 1) * pageSize)
                        .limit(pageSize)
                        .into(mutableListOf())
                }
                future.complete(records)
            } catch (e: Exception) {
                LoggerUtil.severe("获取所有交易记录失败", e)
                future.completeExceptionally(DatabaseException("获取所有交易记录失败", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "获取所有交易记录")
        return future
    }

    override fun getRecordsByType(type: TransactionType, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        val future = CompletableFuture<List<TransactionRecord>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val records = ExceptionHandler.wrapOrDefault("获取指定类型的交易记录", mutableListOf()) {
                    collection.find(Filters.eq("type", type))
                        .sort(Sorts.descending("timestamp"))
                        .skip((page - 1) * pageSize)
                        .limit(pageSize)
                        .into(mutableListOf())
                }
                future.complete(records)
            } catch (e: Exception) {
                LoggerUtil.severe("获取指定类型的交易记录失败: $type", e)
                future.completeExceptionally(DatabaseException("获取指定类型的交易记录失败: $type", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "获取指定类型的交易记录")
        return future
    }

    override fun getRecordsByTimeRange(startTime: Long, endTime: Long, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        val future = CompletableFuture<List<TransactionRecord>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val records = ExceptionHandler.wrapOrDefault("根据时间范围获取交易记录", mutableListOf()) {
                    collection.find(
                        Filters.and(
                            Filters.gte("timestamp", startTime),
                            Filters.lte("timestamp", endTime)
                        )
                    )
                        .sort(Sorts.descending("timestamp"))
                        .skip((page - 1) * pageSize)
                        .limit(pageSize)
                        .into(mutableListOf())
                }
                future.complete(records)
            } catch (e: Exception) {
                LoggerUtil.severe("根据时间范围获取交易记录失败: $startTime - $endTime", e)
                future.completeExceptionally(DatabaseException("根据时间范围获取交易记录失败: $startTime - $endTime", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "根据时间范围获取交易记录")
        return future
    }

    override fun getRecordsBetweenPlayers(player1Uuid: UUID, player2Uuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        val future = CompletableFuture<List<TransactionRecord>>()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val records = ExceptionHandler.wrapOrDefault("获取两个玩家之间的交易记录", mutableListOf()) {
                    collection.find(
                        Filters.and(
                            Filters.or(
                                Filters.and(
                                    Filters.eq("fromPlayerUuid", player1Uuid),
                                    Filters.eq("toPlayerUuid", player2Uuid)
                                ),
                                Filters.and(
                                    Filters.eq("fromPlayerUuid", player2Uuid),
                                    Filters.eq("toPlayerUuid", player1Uuid)
                                )
                            )
                        )
                    )
                        .sort(Sorts.descending("timestamp"))
                        .skip((page - 1) * pageSize)
                        .limit(pageSize)
                        .into(mutableListOf())
                }
                future.complete(records)
            } catch (e: Exception) {
                LoggerUtil.severe("获取两个玩家之间的交易记录失败: $player1Uuid - $player2Uuid", e)
                future.completeExceptionally(DatabaseException("获取两个玩家之间的交易记录失败: $player1Uuid - $player2Uuid", e))
            }
        })
        ExceptionHandler.handleFutureException(future, "获取两个玩家之间的交易记录")
        return future
    }
}