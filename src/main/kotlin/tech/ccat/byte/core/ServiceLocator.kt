package tech.ccat.byte.core

import tech.ccat.byte.BytePlugin
import tech.ccat.byte.config.ConfigManager
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.currency.CurrencyManager
import tech.ccat.byte.currency.CurrencyType
import tech.ccat.byte.economy.EconomyManager
import tech.ccat.byte.economy.vault.CurrencyEconomy
import tech.ccat.byte.storage.MongoDBManager

/**
 * 服务定位器，用于管理插件中的所有服务实例
 */
class ServiceLocator(private val plugin: BytePlugin) {
    companion object {
        private lateinit var instance: ServiceLocator
        
        fun getInstance(): ServiceLocator {
            return instance
        }
    }
    lateinit var configManager: ConfigManager
    lateinit var mongoDBManager: MongoDBManager
    lateinit var currencyManager: CurrencyManager
    lateinit var defaultCurrency: AbstractCurrency
    lateinit var economyManager: EconomyManager

    val shutdownOnFailure: Boolean get() = configManager.pluginConfig.shutdownOnFailure
    
    fun initialize() {
        instance = this

        configManager = ConfigManager().apply { setup() }

        mongoDBManager = MongoDBManager(configManager.pluginConfig).apply { 
            connect() 
        }

        currencyManager = CurrencyManager.getInstance()
        currencyManager.initializeDefaultCurrencies(
            mongoDBManager,
            configManager.pluginConfig
        )
        defaultCurrency = currencyManager.getDefaultCurrency()

        val economyInstances = CurrencyType.entries.map { CurrencyEconomy(it) }
        economyManager = EconomyManager(economyInstances)
    }

    fun reload() {
        configManager.reloadAll()

        mongoDBManager = MongoDBManager(configManager.pluginConfig).apply { 
            connect() 
        }

        currencyManager.initializeDefaultCurrencies(
            mongoDBManager,
            configManager.pluginConfig
        )
        defaultCurrency = currencyManager.getDefaultCurrency()

    }
}