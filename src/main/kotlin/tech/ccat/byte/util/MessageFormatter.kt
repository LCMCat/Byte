package tech.ccat.byte.util

import tech.ccat.byte.BytePlugin

object MessageFormatter {
    fun format(key: String, vararg args: Any): String{
        val raw = BytePlugin.instance.configManager.messageConfig.getMessage(key)
        return raw.format(*args)
    }
}