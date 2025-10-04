package tech.ccat.byte

import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.config.ConfigManager
import tech.ccat.byte.core.ServiceLocator
import tech.ccat.byte.economy.EconomyManager
import tech.ccat.byte.service.ByteService
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
    
    // 通过ServiceLocator暴露服务
    val configManager: ConfigManager get() = serviceLocator.configManager
    val mongoDBManager: MongoDBManager get() = serviceLocator.mongoDBManager
    val byteService: ByteService get() = serviceLocator.byteService
    val economyManager: EconomyManager get() = serviceLocator.economyManager
    val commandManager: CommandManager get() = serviceLocator.commandManager

    lateinit var commandEntrance: String

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        try {
            // 初始化服务定位器
            serviceLocator = ServiceLocator(this).apply { initialize() }

            // 注册服务
            server.servicesManager.register(
                ByteService::class.java,
                byteService,
                this,
                ServicePriority.Normal
            )

            // 注册经济系统
            economyManager.registerEconomy()

            // 注册命令
            reloadCommand()

            LoggerUtil.info("经济系统已启动.")
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "经济系统初始化失败")
            
            // 根据配置决定是否关闭服务器
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
        commandEntrance = serviceLocator.commandEntrance
        this.getCommand(commandEntrance)?.setExecutor(commandManager)
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