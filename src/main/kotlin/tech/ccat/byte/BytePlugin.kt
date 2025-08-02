package tech.ccat.byte

import org.bukkit.plugin.java.JavaPlugin
import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.command.*
import tech.ccat.byte.config.ConfigManager
import tech.ccat.byte.economy.ByteEconomy
import tech.ccat.byte.economy.EconomyManager
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.service.ByteServiceImpl
import tech.ccat.byte.storage.MongoDBManager
import tech.ccat.byte.storage.dao.MongoPlayerDataDao

class BytePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: BytePlugin
            private set
    }

    lateinit var mongoDBManager: MongoDBManager
    lateinit var economyManager: EconomyManager
    lateinit var configManager: ConfigManager
    lateinit var commandManager: CommandManager
    lateinit var byteService: ByteService

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        // 初始化配置
        configManager = ConfigManager(this).apply { setup() }

        // 初始化MongoDB
        mongoDBManager = MongoDBManager(configManager.pluginConfig)
        mongoDBManager.connect()


        // 初始化服务层
        byteService = ByteServiceImpl(mongoDBManager.getPlayerDataDao())

        // 注册经济系统
        economyManager = EconomyManager(
            ByteEconomy(configManager.pluginConfig.symbol)
        )
        economyManager.registerEconomy()

        // 注册命令
        commandManager = CommandManager(this).apply{
            registerCommand(SelfCheckCommand(byteService))
            registerCommand(AddCommand(byteService))
            registerCommand(SetCommand(byteService))
            registerCommand(TakeCommand(byteService))
            registerCommand(ReloadCommand())
        }
        this.getCommand("byte")?.setExecutor(commandManager)

        logger.info("Byte economy system enabled!")
    }

    override fun onDisable() {
        mongoDBManager.close()
        economyManager.unregisterEconomy()
        logger.info("Byte economy system disabled!")
    }

    fun reload() {
        configManager.reloadAll()
        mongoDBManager.reconnect(configManager.pluginConfig)
    }
}