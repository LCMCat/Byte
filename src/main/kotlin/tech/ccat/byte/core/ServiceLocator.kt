package tech.ccat.byte.core

import tech.ccat.byte.BytePlugin
import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.config.ConfigManager
import tech.ccat.byte.economy.EconomyManager
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.storage.MongoDBManager

/**
 * 服务定位器，用于管理插件中的所有服务实例
 */
class ServiceLocator(private val plugin: BytePlugin) {
    lateinit var configManager: ConfigManager
    lateinit var mongoDBManager: MongoDBManager
    lateinit var byteService: ByteService
    lateinit var economyManager: EconomyManager
    lateinit var commandManager: CommandManager
    
    // 配置属性的直接访问
    val commandEntrance: String get() = configManager.pluginConfig.commandEntrance
    val shutdownOnFailure: Boolean get() = configManager.pluginConfig.shutdownOnFailure
    
    fun initialize() {
        // 初始化配置管理器
        configManager = ConfigManager().apply { setup() }
        
        // 初始化MongoDB管理器
        mongoDBManager = MongoDBManager(configManager.pluginConfig).apply { 
            connect() 
        }
        
        // 初始化核心服务
        byteService = createByteService()
        
        // 初始化经济系统管理器
        economyManager = createEconomyManager()
        
        // 初始化命令管理器
        commandManager = createCommandManager()
    }
    
    private fun createByteService(): ByteService {
        return tech.ccat.byte.service.ByteServiceImpl(mongoDBManager.getPlayerDataDao())
    }
    
    private fun createEconomyManager(): EconomyManager {
        return EconomyManager(
            tech.ccat.byte.economy.ByteEconomy(configManager.pluginConfig)
        )
    }
    
    private fun createCommandManager(): CommandManager {
        return CommandManager().apply {
            registerCommand(tech.ccat.byte.command.SelfCheckCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.ShowCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.AddCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.SetCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.TakeCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.ReloadCommand(commandEntrance))
            registerCommand(tech.ccat.byte.command.TotalCommand(commandEntrance, byteService))
            registerCommand(tech.ccat.byte.command.RichestCommand(commandEntrance, byteService))
        }
    }
    
    fun reload() {
        // 重新加载配置
        configManager.reloadAll()
        
        // 重新创建MongoDB管理器并重新连接数据库
        mongoDBManager = MongoDBManager(configManager.pluginConfig).apply { 
            connect() 
        }
        
        // 重新创建服务
        byteService = createByteService()
    }
}