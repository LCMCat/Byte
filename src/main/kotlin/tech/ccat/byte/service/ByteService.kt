package tech.ccat.byte.service

import org.bukkit.OfflinePlayer
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Byte插件的核心服务接口，提供玩家经济系统的操作方法
 *
 * 该接口定义了所有与玩家货币相关的操作，包括查询余额、修改余额、
 * 获取服务器总货币量以及获取财富排行榜等功能。
 */
interface ByteService {
    /**
     * 根据玩家UUID获取其当前余额
     *
     * @param uuid 玩家的唯一标识符
     * @return 玩家当前的余额
     */
    fun getBalance(uuid: UUID): Double
    
    /**
     * 根据玩家名称获取其当前余额
     *
     * @param playerName 玩家名称
     * @return 玩家当前的余额，如果玩家不存在则返回null
     */
    fun getBalance(playerName: String): Double?
    
    /**
     * 设置玩家的余额
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要设置的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    
    /**
     * 给玩家增加指定数量的货币
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要增加的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    
    /**
     * 从玩家账户中扣除指定数量的货币
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要扣除的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当余额不足时抛出此异常
     */
    fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    
    /**
     * 获取服务器中所有玩家的货币总量
     *
     * @return 异步返回服务器总货币量
     */
    fun getTotalMoney(): CompletableFuture<Double>
    
    /**
     * 获取服务器中最富有的玩家排行榜
     *
     * @param limit 排行榜条目数量限制
     * @return 异步返回玩家排行榜列表，每个元素包含玩家对象和其余额
     */
    fun getRichestPlayers(limit: Int): CompletableFuture<List<Pair<OfflinePlayer, Double>>>
}