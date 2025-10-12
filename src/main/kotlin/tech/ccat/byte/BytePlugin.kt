package tech.ccat.byte

import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import tech.ccat.byte.config.ConfigManager
import tech.ccat.byte.core.ServiceLocator
import tech.ccat.byte.currency.CurrencyAPI
import tech.ccat.byte.currency.CurrencyType
import tech.ccat.byte.economy.EconomyManager
import tech.ccat.byte.storage.MongoDBManager
import tech.ccat.byte.util.LoggerUtil
import tech.ccat.byte.util.ExceptionHandler
import tech.ccat.byte.exception.ConfigException

class BytePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: BytePlugin
            private set
    }

    private lateinit var serviceLocator: ServiceLocator

    val configManager: ConfigManager get() = serviceLocator.configManager
    val mongoDBManager: MongoDBManager get() = serviceLocator.mongoDBManager
    val economyManager: EconomyManager get() = serviceLocator.economyManager

    lateinit var commandEntrance: String

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        try {
            serviceLocator = ServiceLocator(this).apply { initialize() }

            val currencyAPI = CurrencyAPI()
            server.servicesManager.register(
                CurrencyAPI::class.java,
                currencyAPI,
                this,
                ServicePriority.Normal
            )

            economyManager.registerEconomy()

            reloadCommand()
            
            

            LoggerUtil.info("经济系统已启动.")
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "经济系统初始化失败")

            if (serviceLocator.shutdownOnFailure) {
                LoggerUtil.severe("根据配置，服务器将在初始化失败后关闭.")
                server.shutdown()
            }
        }
    }

    override fun onDisable() {
        mongoDBManager.close()
        economyManager.unregisterEconomy()
        LoggerUtil.info("经济系统已关闭!")
    }

    fun reloadCommand(){
        var currencyManager = serviceLocator.currencyManager
        this.getCommand("byte")?.setExecutor(currencyManager.getCommandManager(CurrencyType.BYTE))
        this.getCommand("coin")?.setExecutor(currencyManager.getCommandManager(CurrencyType.COIN))
        this.getCommand("point")?.setExecutor(currencyManager.getCommandManager(CurrencyType.POINT))
    }

    fun reload() {
        try {
            serviceLocator.reload()
            reloadCommand()
            LoggerUtil.info("配置已重新加载.")
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "重新加载配置失败")
            throw ConfigException("重新加载配置失败", e)
        }
    }
}