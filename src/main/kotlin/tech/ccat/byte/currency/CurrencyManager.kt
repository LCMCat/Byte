package tech.ccat.byte.currency

import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.config.PluginConfig
import tech.ccat.byte.storage.MongoDBManager
import java.util.concurrent.ConcurrentHashMap

/**
 * 货币管理器
 *
 * 该类负责管理所有已注册的货币实现，提供了注册、获取货币的方法。
 * 使用单例模式确保全局唯一实例。
 */
class CurrencyManager private constructor() {
    private val currencies = ConcurrentHashMap<String, AbstractCurrency>()
    private val commandManagerMap = ConcurrentHashMap<String, CommandManager>()
    
    companion object {
        @Volatile
        private var INSTANCE: CurrencyManager? = null
        
        fun getInstance(): CurrencyManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CurrencyManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 注册一个新的货币实现
     *
     * @param currency 货币实现类实例
     */
    fun registerCurrency(currency: AbstractCurrency) {
        currencies[currency.currencyId] = currency
    }
    
    /**
     * 注册一个新的货币实现并注册其相关命令
     *
     * @param currency 货币实现类实例
     * @param config 插件配置
     */
    fun registerCurrency(currency: AbstractCurrency, config: PluginConfig) {
        currencies[currency.currencyId] = currency
        
        // 创建并注册专属命令管理器
        val commandManager = currency.createCommandManager(config)
        commandManagerMap[currency.currencyId] = commandManager
    }
    
    /**
     * 获取指定ID的货币实现
     *
     * @param currencyId 货币ID
     * @return 对应的货币实现，如果不存在则返回null
     */
    fun getCurrency(currencyId: String): AbstractCurrency? {
        return currencies[currencyId]
    }
    
    /**
     * 获取指定类型的货币实现
     *
     * @param currencyType 货币类型
     * @return 对应的货币实现，如果不存在则返回null
     */
    fun getCurrency(currencyType: CurrencyType): AbstractCurrency? {
        return getCurrency(currencyType.getId())
    }
    
    /**
     * 根据货币ID获取货币实现（别名方法）
     *
     * @param currencyId 货币ID
     * @return 对应的货币实现，如果不存在则返回null
     */
    fun getCurrencyById(currencyId: String): AbstractCurrency? {
        return currencies[currencyId]
    }
    
    /**
     * 根据货币类型获取货币实现（别名方法）
     *
     * @param currencyType 货币类型
     * @return 对应的货币实现，如果不存在则返回null
     */
    fun getCurrencyById(currencyType: CurrencyType): AbstractCurrency? {
        return getCurrencyById(currencyType.getId())
    }
    
    /**
     * 根据货币ID获取命令管理器
     *
     * @param currencyId 货币ID
     * @return 对应的命令管理器，如果不存在则返回null
     */
    fun getCommandManager(currencyId: String): CommandManager? {
        return commandManagerMap[currencyId]
    }
    
    /**
     * 根据货币类型获取命令管理器
     *
     * @param currencyType 货币类型
     * @return 对应的命令管理器，如果不存在则返回null
     */
    fun getCommandManager(currencyType: CurrencyType): CommandManager? {
        return getCommandManager(currencyType.getId())
    }
    
    /**
     * 获取所有已注册的货币实现
     *
     * @return 包含所有货币实现的集合
     */
    fun getAllCurrencies(): Collection<AbstractCurrency> {
        return currencies.values
    }
    
    /**
     * 初始化默认货币
     *
     * 创建并注册byte、coin和point三种默认货币实现。
     */
    fun initializeDefaultCurrencies(mongoDBManager: MongoDBManager, config: PluginConfig) {
        registerCurrency(ByteCurrency(config, mongoDBManager.getPlayerDataDao(CurrencyType.BYTE), mongoDBManager.getTransactionRecordDao(CurrencyType.BYTE)), config)
        registerCurrency(CoinCurrency(config, mongoDBManager.getPlayerDataDao(CurrencyType.COIN), mongoDBManager.getTransactionRecordDao(CurrencyType.COIN)), config)
        registerCurrency(PointCurrency(config, mongoDBManager.getPlayerDataDao(CurrencyType.POINT), mongoDBManager.getTransactionRecordDao(CurrencyType.POINT)), config)
    }
    
    /**
     * 获取默认货币（byte）
     *
     * @return byte货币实现
     */
    fun getDefaultCurrency(): AbstractCurrency {
        return getCurrency(CurrencyType.BYTE) ?: throw IllegalStateException("Default currency 'byte' not found")
    }
}