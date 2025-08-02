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

class BytePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: BytePlugin
            private set
    }

    private lateinit var mongoDBManager: MongoDBManager
    lateinit var economyManager: EconomyManager
    lateinit var configManager: ConfigManager
    lateinit var commandManager: CommandManager
    lateinit var byteService: ByteService

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        // 初始化配置
        configManager = ConfigManager().apply { setup() }

        // 初始化MongoDB
        mongoDBManager = MongoDBManager()
        mongoDBManager.connect()


        // 初始化服务层
        byteService = ByteServiceImpl(mongoDBManager.getPlayerDataDao())

        // 注册经济系统
        economyManager = EconomyManager(
            ByteEconomy()
        )
        economyManager.registerEconomy()

        // 注册命令
        commandManager = CommandManager().apply{
            registerCommand(SelfCheckCommand())
            registerCommand(ShowCommand())
            registerCommand(AddCommand())
            registerCommand(SetCommand())
            registerCommand(TakeCommand())
            registerCommand(ReloadCommand())
        }
        this.getCommand("byte")?.setExecutor(commandManager)

        logger.info("字节经济系统已启动.")
    }

    override fun onDisable() {
        mongoDBManager.close()
        economyManager.unregisterEconomy()
        logger.info("字节经济系统已关闭!")
    }

    fun reload() {
        configManager.reloadAll()
        mongoDBManager.reconnect()
    }
}