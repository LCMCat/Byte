package tech.ccat.byte.command

import org.bukkit.command.CommandSender

abstract class AbstractCommand(
    val name: String,
    val permission: String? = null,
    val usage: String = "",
    val minArgs: Int = 0,
    val playerOnly: Boolean = false
) {
    abstract fun execute(sender: CommandSender, args: Array<out String>): Boolean
    open fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> = emptyList()
}