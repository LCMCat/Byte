package tech.ccat.byte.util

import tech.ccat.byte.BytePlugin
import tech.ccat.byte.economy.ByteEconomy

object MessageFormatter {
    fun format(key: String, vararg args: Any): String{
        return BytePlugin.instance.configManager.messageConfig.getMessage(key, *args)
    }
    
    fun formatMessage(key: String, vararg args: Any): String{
        return BytePlugin.instance.configManager.messageConfig.getMessage(key, *args)
    }
    
    fun formatSystemMessage(key: String, vararg args: Any): String{
        return BytePlugin.instance.configManager.messageConfig.getMessage(key, *args)
    }
    
    fun formatCurrency(amount: Double): String{
        val economy = ByteEconomy(BytePlugin.instance.configManager.pluginConfig)
        return economy.format(amount)
    }
}