package tech.ccat.byte.service

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

import tech.ccat.byte.BytePlugin.Companion.instance
import tech.ccat.byte.util.LoggerUtil

/**
 * Byte插件的核心服务实现类
 *
 * 该类实现了ByteService接口，提供了玩家经济系统的完整实现。
 * 包括余额查询、修改、并发安全更新、排行榜等功能。
 */
class ByteServiceImpl(private val dao: PlayerDataDao): ByteService{

    private val plugin = instance

    /**
     * 获取或创建玩家数据(同步)
     *
     * 如果数据库中存在该玩家数据则直接返回，否则创建一个新的玩家数据对象。
     *
     * @param uuid 玩家的唯一标识符
     * @return 玩家数据对象
     */
    fun getOrCreate(uuid: UUID): PlayerData {
        val new = PlayerData(uuid, 0.0)
        return dao.load(uuid) ?: new.also {
            dao.create(new)
        }
    }

    /**
     * 异步获取或创建玩家数据
     *
     * 如果数据库中存在该玩家数据则直接返回，否则创建一个新的玩家数据对象。
     *
     * @param uuid 玩家的唯一标识符
     * @return 异步返回玩家数据对象
     */
    fun getOrCreateAsync(uuid: UUID): CompletableFuture<PlayerData> {
        return dao.loadAsync(uuid).thenComposeAsync { data ->
            data?.let { CompletableFuture.completedFuture(it) }
                ?: run {
                    val new = PlayerData(uuid, 0.0)
                    dao.createAsync(new).thenApply { new }
                }
        }
    }

    /**
     * 异步更新余额
     *
     * 使用带重试机制的原子更新操作来确保并发安全。
     *
     * @param uuid 玩家的唯一标识符
     * @param update 余额更新函数，接收当前余额并返回新余额
     * @param maxRetries 最大重试次数，默认为5次
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun updateBalanceAsync(
        uuid: UUID,
        update: (Double) -> Double,
        maxRetries: Int = 5
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        attemptUpdate(uuid, update, maxRetries, 0, future)
        return future
    }

    /**
     * 尝试更新玩家余额
     *
     * 内部实现方法，使用指数退避策略处理并发冲突。
     *
     * @param uuid 玩家的唯一标识符
     * @param update 余额更新函数
     * @param maxRetries 最大重试次数
     * @param attempt 当前尝试次数
     * @param future 用于返回异步结果的CompletableFuture
     */
    private fun attemptUpdate(
        uuid: UUID,
        update: (Double) -> Double,
        maxRetries: Int,
        attempt: Int,
        future: CompletableFuture<Boolean>
    ) {
        val plugin = this.plugin

        getOrCreateAsync(uuid).thenComposeAsync { currentData ->
            val newBalance = update(currentData.balance)
            val updatedData = currentData.copy(balance = newBalance, version = currentData.version + 1)

            dao.atomicUpdateAsync(uuid) { updatedData }
        }.handleAsync { success, _ ->
            if (success == true) {
                future.complete(true)
                return@handleAsync
            }

            if (attempt < maxRetries - 1) {
                val waitTime = (10L shl attempt) + (0..10L).random()

                Bukkit.getScheduler().runTaskLater(plugin, Runnable{
                    attemptUpdate(uuid, update, maxRetries, attempt + 1, future)
                }, waitTime)
            } else {
                future.completeExceptionally(ConcurrentModificationException("账户更新冲突: $uuid"))
            }
        }
    }

    /**
     * 安全更新余额（已弃用）
     *
     * 使用同步阻塞方式更新余额，已弃用，请使用attemptUpdate替代。
     *
     * @param uuid 玩家的唯一标识符
     * @param update 余额更新函数
     * @param maxRetries 最大重试次数，默认为5次
     * @deprecated 使用attemptUpdate替代
     */
    @Deprecated("use attemptUpdate instead.")
    fun updateBalance(
        uuid: UUID,
        update: (Double) -> Double,
        maxRetries: Int = 5
    ) {
        var retries = 0
        var success = false

        while (!success && retries < maxRetries) {
            LoggerUtil.debug("Attempting to update balance for UUID: $uuid")
            val data = getOrCreate(uuid)
            LoggerUtil.debug("Retrieved data for UUID: ${data.uuid}")
            val newBalance = update(data.balance)

            success = dao.atomicUpdate(uuid) {
                it.copy(balance = newBalance)
            }

            if (!success) {
                retries++
                // 指数退避策略
                val waitTime = (10L shl retries) + Random.nextLong(10)
                Thread.sleep(waitTime)
            }
        }

        if (!success) {
            throw ConcurrentModificationException("账户更新冲突: $uuid")
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getBalance(uuid: UUID): Double {
        // 直接查询数据库获取最新余额
        return getOrCreate(uuid).balance
    }

    /**
     * {@inheritDoc}
     */
    override fun getBalance(playerName: String): Double? {
        return Bukkit.getOfflinePlayerIfCached(playerName)?.let {
            getBalance(it.uniqueId)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { amount })
    }

    /**
     * {@inheritDoc}
     */
    override fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { it + amount })
    }

    /**
     * {@inheritDoc}
     */
    override fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { current ->
            if (current < amount) throw IllegalStateException("余额不足")
            current - amount
        })
    }

    /**
     * {@inheritDoc}
     */
    override fun getTotalMoney(): CompletableFuture<Double> {
        return dao.getAllPlayersAsync().thenApply { players ->
            players.sumOf { it.balance }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getRichestPlayers(limit: Int): CompletableFuture<List<Pair<OfflinePlayer, Double>>> {
        return dao.getAllPlayersAsync().thenApply { players ->
            
            if (players.isEmpty()) {
                return@thenApply emptyList()
            }
            
            val sortedPlayers = players
                .sortedByDescending { it.balance }
                .take(limit)
            
            if (sortedPlayers.isEmpty()) {
                return@thenApply emptyList()
            }
            
            val result = mutableListOf<Pair<OfflinePlayer, Double>>()
            
            sortedPlayers.forEach { playerData ->
                try {
                    val offlinePlayer = Bukkit.getOfflinePlayer(playerData.uuid)
                    // 尝试获取玩家名称，如果获取不到则使用UUID作为显示名称
//                    val playerName = offlinePlayer.name ?: playerData.uuid.toString().substring(0, 8) + "..."
                    result.add(Pair(offlinePlayer, playerData.balance))
                } catch (e: Exception) {
                    LoggerUtil.severe("[Byte] 错误: 处理玩家数据时出错 - UUID: ${playerData.uuid}, 错误: ${e.message}")
                }
            }
            
            result
        }
    }
}