package tech.ccat.byte.currency

import org.bukkit.OfflinePlayer
import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.config.PluginConfig
import tech.ccat.byte.storage.model.TransactionRecord
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * 抽象货币系统接口，定义所有货币类型应实现的基本功能
 *
 * 该接口为不同类型的货币系统提供了统一的API，使得插件可以支持多种货币类型。
 */
interface AbstractCurrency {
    /**
     * 获取货币系统的唯一标识符
     *
     * @return 货币系统的唯一标识符
     */
    val currencyId: String
        get() = currencyType.getId()
    
    /**
     * 获取货币类型枚举
     *
     * @return 货币类型枚举
     */
    val currencyType: CurrencyType
    
    /**
     * 获取货币系统的名称
     *
     * @return 货币系统的名称
     */
    val currencyName: String
    
    /**
     * 获取货币系统的符号
     *
     * @return 货币系统的符号
     */
    val currencySymbol: String
    
    /**
     * 获取货币系统的颜色代码
     *
     * @return 货币系统的颜色代码
     */
    val currencyColor: String
    
    /**
     * 创建该货币专属的命令管理器
     */
    fun createCommandManager(config: PluginConfig): CommandManager

    /**
     * 获取该货币的命令入口点
     */
    val commandEntrance: String
    
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
     * 从一个玩家转账指定数量的货币到另一个玩家
     *
     * @param fromUuid 发送方玩家的唯一标识符
     * @param toUuid 接收方玩家的唯一标识符
     * @param amount 要转账的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当发送方余额不足时抛出此异常
     */
    fun transferBalance(fromUuid: UUID, toUuid: UUID, amount: Double): CompletableFuture<Boolean>
    
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
    
    /**
     * 根据玩家UUID获取其相关的交易记录（分页）
     *
     * @param playerUuid 玩家UUID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getTransactionRecords(playerUuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>
    
    /**
     * 获取所有交易记录（分页）
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getAllTransactionRecords(page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>
    fun format(amount: Double): String
}