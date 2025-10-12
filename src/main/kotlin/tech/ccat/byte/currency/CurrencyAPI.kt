package tech.ccat.byte.currency

import org.bukkit.OfflinePlayer
import tech.ccat.byte.storage.model.TransactionRecord
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * 统一货币API接口，对外提供所有货币相关的操作
 *
 * 该类作为所有货币操作的统一入口，内部使用CurrencyManager来管理具体的货币实现。
 */
class CurrencyAPI {
    private val currencyManager = CurrencyManager.getInstance()
    
    /**
     * 获取默认货币（Byte）
     *
     * @return 默认货币实现
     */
    fun getDefaultCurrency(): AbstractCurrency {
        return currencyManager.getDefaultCurrency()
    }
    
    /**
     * 根据货币ID获取货币实现
     *
     * @param currencyId 货币ID
     * @return 货币实现，如果不存在则返回null
     */
    fun getCurrency(currencyId: String): AbstractCurrency? {
        return currencyManager.getCurrency(currencyId)
    }
    
    /**
     * 根据货币类型获取货币实现
     *
     * @param currencyType 货币类型
     * @return 货币实现，如果不存在则返回null
     */
    fun getCurrency(currencyType: CurrencyType): AbstractCurrency? {
        return currencyManager.getCurrency(currencyType.getId())
    }
    
    /**
     * 获取所有已注册的货币实现
     *
     * @return 所有已注册的货币实现列表
     */
    fun getAllCurrencies(): Collection<AbstractCurrency> {
        return currencyManager.getAllCurrencies()
    }
    
    // ==================== 以下是默认货币（Byte）的便捷方法 ====================
    
    /**
     * 获取玩家在默认货币中的余额
     *
     * @param uuid 玩家的唯一标识符
     * @return 玩家当前的余额
     */
    fun getBalance(uuid: UUID): Double {
        return getDefaultCurrency().getBalance(uuid)
    }
    
    /**
     * 获取玩家在默认货币中的余额
     *
     * @param playerName 玩家名称
     * @return 玩家当前的余额，如果玩家不存在则返回null
     */
    fun getBalance(playerName: String): Double? {
        return getDefaultCurrency().getBalance(playerName)
    }
    
    /**
     * 设置玩家在默认货币中的余额
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要设置的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getDefaultCurrency().setBalance(uuid, amount)
    }
    
    /**
     * 给玩家在默认货币中增加指定数量的货币
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要增加的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getDefaultCurrency().addBalance(uuid, amount)
    }
    
    /**
     * 从玩家在默认货币中的账户中扣除指定数量的货币
     *
     * @param uuid 玩家的唯一标识符
     * @param amount 要扣除的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当余额不足时抛出此异常
     */
    fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getDefaultCurrency().subtractBalance(uuid, amount)
    }
    
    /**
     * 从一个玩家在默认货币中转账指定数量的货币到另一个玩家
     *
     * @param fromUuid 发送方玩家的唯一标识符
     * @param toUuid 接收方玩家的唯一标识符
     * @param amount 要转账的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当发送方余额不足时抛出此异常
     */
    fun transferBalance(fromUuid: UUID, toUuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getDefaultCurrency().transferBalance(fromUuid, toUuid, amount)
    }
    
    /**
     * 获取服务器中默认货币的总量
     *
     * @return 异步返回服务器总货币量
     */
    fun getTotalMoney(): CompletableFuture<Double> {
        return getDefaultCurrency().getTotalMoney()
    }
    
    /**
     * 获取服务器中默认货币最富有的玩家排行榜
     *
     * @param limit 排行榜条目数量限制
     * @return 异步返回玩家排行榜列表，每个元素包含玩家对象和其余额
     */
    fun getRichestPlayers(limit: Int): CompletableFuture<List<Pair<OfflinePlayer, Double>>> {
        return getDefaultCurrency().getRichestPlayers(limit)
    }
    
    /**
     * 根据玩家UUID获取其在默认货币中的交易记录（分页）
     *
     * @param playerUuid 玩家UUID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getTransactionRecords(playerUuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        return getDefaultCurrency().getTransactionRecords(playerUuid, page, pageSize)
    }
    
    /**
     * 获取默认货币的所有交易记录（分页）
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getAllTransactionRecords(page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>> {
        return getDefaultCurrency().getAllTransactionRecords(page, pageSize)
    }
    
    // ==================== 以下是特定货币的便捷方法 ====================
    
    /**
     * 获取玩家在指定货币中的余额
     *
     * @param currencyId 货币ID
     * @param uuid 玩家的唯一标识符
     * @return 玩家当前的余额，如果货币不存在则返回0.0
     */
    fun getBalance(currencyId: String, uuid: UUID): Double {
        return getCurrency(currencyId)?.getBalance(uuid) ?: 0.0
    }
    
    /**
     * 根据货币类型获取玩家余额
     *
     * @param currencyType 货币类型
     * @param uuid 玩家UUID
     * @return 玩家余额
     */
    fun getBalance(currencyType: CurrencyType, uuid: UUID): Double {
        return getCurrency(currencyType)?.getBalance(uuid) ?: 0.0
    }

    /**
     * 获取玩家在指定货币中的余额
     *
     * @param currencyId 货币ID
     * @param playerName 玩家名称
     * @return 玩家当前的余额，如果玩家不存在则返回null
     */
    fun getBalance(currencyId: String, playerName: String): Double? {
        return getCurrency(currencyId)?.getBalance(playerName)
    }
    
    /**
     * 根据货币类型和玩家名称获取玩家余额
     *
     * @param currencyType 货币类型
     * @param playerName 玩家名称
     * @return 玩家余额，如果玩家不存在则返回null
     */
    fun getBalance(currencyType: CurrencyType, playerName: String): Double? {
        return getCurrency(currencyType)?.getBalance(playerName)
    }

    /**
     * 设置玩家在指定货币中的余额
     *
     * @param currencyId 货币ID
     * @param uuid 玩家的唯一标识符
     * @param amount 要设置的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun setBalance(currencyId: String, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyId)?.setBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }
    
    /**
     * 设置玩家指定货币的余额
     *
     * @param currencyType 货币类型
     * @param uuid 玩家UUID
     * @param amount 金额
     * @return 异步操作结果
     */
    fun setBalance(currencyType: CurrencyType, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyType)?.setBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }

    /**
     * 给玩家在指定货币中增加指定数量的货币
     *
     * @param currencyId 货币ID
     * @param uuid 玩家的唯一标识符
     * @param amount 要增加的金额
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun addBalance(currencyId: String, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyId)?.addBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }
    
    /**
     * 给玩家增加指定货币
     *
     * @param currencyType 货币类型
     * @param uuid 玩家UUID
     * @param amount 金额
     * @return 异步操作结果
     */
    fun addBalance(currencyType: CurrencyType, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyType)?.addBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }

    /**
     * 从玩家在指定货币中的账户中扣除指定数量的货币
     *
     * @param currencyId 货币ID
     * @param uuid 玩家的唯一标识符
     * @param amount 要扣除的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当余额不足时抛出此异常
     */
    fun subtractBalance(currencyId: String, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyId)?.subtractBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }
    
    /**
     * 从玩家扣除指定货币
     *
     * @param currencyType 货币类型
     * @param uuid 玩家UUID
     * @param amount 金额
     * @return 异步操作结果
     */
    fun subtractBalance(currencyType: CurrencyType, uuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyType)?.subtractBalance(uuid, amount) ?: CompletableFuture.completedFuture(false)
    }

    /**
     * 从一个玩家在指定货币中转账指定数量的货币到另一个玩家
     *
     * @param currencyId 货币ID
     * @param fromUuid 发送方玩家的唯一标识符
     * @param toUuid 接收方玩家的唯一标识符
     * @param amount 要转账的金额
     * @return 异步操作结果，成功返回true，失败返回false
     * @throws IllegalStateException 当发送方余额不足时抛出此异常
     */
    fun transferBalance(currencyId: String, fromUuid: UUID, toUuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyId)?.transferBalance(fromUuid, toUuid, amount) ?: CompletableFuture.completedFuture(false)
    }
    
    /**
     * 在指定货币之间进行转账
     *
     * @param currencyType 货币类型
     * @param fromUuid 发送方UUID
     * @param toUuid 接收方UUID
     * @param amount 金额
     * @return 异步操作结果
     */
    fun transferBalance(currencyType: CurrencyType, fromUuid: UUID, toUuid: UUID, amount: Double): CompletableFuture<Boolean> {
        return getCurrency(currencyType)?.transferBalance(fromUuid, toUuid, amount) ?: CompletableFuture.completedFuture(false)
    }
}