package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

import tech.ccat.byte.BytePlugin.Companion.instance

class ReloadCommand(private val commandEntrance: String) : AbstractCommand(
    name = "reload",
    permission = "$commandEntrance.admin",
    usage = "/$commandEntrance reload"
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        instance.reload()
        sender.sendMessage(MessageFormatter.format(MessageKeys.RELOAD_SUCCESS))
        return true
    }
}