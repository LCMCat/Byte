package tech.ccat.byte.util

import tech.ccat.byte.BytePlugin

object MessageFormatter {
    fun format(key: String, vararg args: Any): String{
        return BytePlugin.instance.configManager.messageConfig.getMessage(key, *args)
    }
}