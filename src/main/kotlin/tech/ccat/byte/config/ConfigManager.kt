package tech.ccat.byte.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

import tech.ccat.byte.BytePlugin.Companion.instance

class ConfigManager() {
    private val configs = mutableMapOf<String, YamlConfiguration>()
    private val configFiles = mutableMapOf<String, File>()

    lateinit var pluginConfig: PluginConfig
    lateinit var messageConfig: MessageConfig

    fun setup() {
        saveDefaultConfig("config.yml")
        saveDefaultConfig("messages.yml")

        reloadAll()
    }

    private fun saveDefaultConfig(fileName: String) {
        val file = File(instance.dataFolder, fileName)
        if (!file.exists()) {
            instance.saveResource(fileName, false)
        }
        configFiles[fileName] = file
        configs[fileName] = YamlConfiguration.loadConfiguration(file)
    }

    fun reloadAll() {
        configFiles.forEach { (name, file) ->
            configs[name] = YamlConfiguration.loadConfiguration(file)
        }

        pluginConfig = PluginConfig(configs["config.yml"]!!)
        messageConfig = MessageConfig(configs["messages.yml"]!!)
    }
}