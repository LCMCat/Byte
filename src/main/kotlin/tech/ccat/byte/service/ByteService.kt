package tech.ccat.byte.service

import org.bukkit.OfflinePlayer
import java.util.*
import java.util.concurrent.CompletableFuture

interface ByteService {
    fun getBalance(uuid: UUID): Double
    fun getBalance(playerName: String): Double?
    fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    
    // 获取服务器货币总量
    fun getTotalMoney(): CompletableFuture<Double>
    
    // 获取最富有玩家排行榜
    fun getRichestPlayers(limit: Int): CompletableFuture<List<Pair<OfflinePlayer, Double>>>
}