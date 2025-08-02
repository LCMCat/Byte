package tech.ccat.byte.service

import org.bukkit.Bukkit
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import kotlin.random.Random

class ByteServiceImpl(private val dao: PlayerDataDao): ByteService{

    // 获取或创建玩家数据
    fun getOrCreate(uuid: UUID): PlayerData {
        val new = PlayerData(uuid, 0.0)
        return dao.load(uuid) ?: new.also {
            // 新数据直接创建
            dao.create(new)
        }
    }

    // 安全更新余额
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

            success = dao.atomicUpdate(uuid, data.version) {
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

    override fun setBalance(uuid: UUID, amount: Double) =
        updateBalance(uuid, {amount})

    override fun addBalance(uuid: UUID, amount: Double) {
        updateBalance(uuid, { it + amount })
    }

    override fun subtractBalance(uuid: UUID, amount: Double) {
        updateBalance(uuid, { current ->
            // 确保余额足够扣除
            if (current < amount) throw IllegalStateException("余额不足")
            current - amount
        })
    }
}