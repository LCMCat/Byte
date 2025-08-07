package tech.ccat.byte.service

import kotlinx.coroutines.Runnable
import org.bukkit.Bukkit
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

import tech.ccat.byte.BytePlugin.Companion.instance

class ByteServiceImpl(private val dao: PlayerDataDao): ByteService{

    private val plugin = instance

    // 获取或创建玩家数据(同步)
    fun getOrCreate(uuid: UUID): PlayerData {
        val new = PlayerData(uuid, 0.0)
        return dao.load(uuid) ?: new.also {
            dao.create(new)
        }
    }

    // 异步获取或创建玩家数据
    fun getOrCreateAsync(uuid: UUID): CompletableFuture<PlayerData> {
        return dao.loadAsync(uuid).thenComposeAsync { data ->
            data?.let { CompletableFuture.completedFuture(it) }
                ?: run {
                    val new = PlayerData(uuid, 0.0)
                    dao.createAsync(new).thenApply { new }
                }
        }
    }

    // 异步更新余额
    fun updateBalanceAsync(
        uuid: UUID,
        update: (Double) -> Double,
        maxRetries: Int = 5
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        attemptUpdate(uuid, update, maxRetries, 0, future)
        return future
    }

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
        }.handleAsync { success, throwable ->
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

    // 安全更新余额
    @Deprecated("use attemptUpdate instead.")
    fun updateBalance(
        uuid: UUID,
        update: (Double) -> Double,
        maxRetries: Int = 5
    ) {
        var retries = 0
        var success = false

        while (!success && retries < maxRetries) {
            print(uuid)
            val data = getOrCreate(uuid)
            print(data.uuid)
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

    override fun getBalance(uuid: UUID): Double {
        // 直接查询数据库获取最新余额
        return getOrCreate(uuid).balance
    }

    override fun getBalance(playerName: String): Double? {
        return Bukkit.getOfflinePlayerIfCached(playerName)?.let {
            getBalance(it.uniqueId)
        }
    }

    override fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { amount })
    }

    override fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { it + amount })
    }

    override fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return updateBalanceAsync(uuid, { current ->
            if (current < amount) throw IllegalStateException("余额不足")
            current - amount
        })
    }
}