package tech.ccat.byte.service.impl

import org.bukkit.Bukkit
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.storage.cache.CacheManager
import java.util.*

class ByteServiceImpl(private val cacheManager: CacheManager) : ByteService {
    override fun getBalance(uuid: UUID): Double {
        return cacheManager.getData(uuid).balance
    }

    override fun getBalance(playerName: String): Double? {
        return Bukkit.getOfflinePlayer(playerName)?.let {
            cacheManager.getData(it.uniqueId).balance
        }
    }

    override fun setBalance(uuid: UUID, amount: Double) {
        cacheManager.getData(uuid).let {
            it.balance = amount
            it.dirty = true
        }
    }

    override fun addBalance(uuid: UUID, amount: Double) {
        cacheManager.getData(uuid).let {
            it.balance += amount
            it.dirty = true
        }
    }

    override fun subtractBalance(uuid: UUID, amount: Double) {
        cacheManager.getData(uuid).let {
            it.balance -= amount
            it.dirty = true
        }
    }

    override fun reload() {
        cacheManager.flushAll()
    }
}