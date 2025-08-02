package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import tech.ccat.byte.BytePlugin
import tech.ccat.byte.util.MessageFormatter

class ReloadCommand : AbstractCommand(
    name = "reload",
    permission = "byte.admin",
    usage = "/byte reload"
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        BytePlugin.instance.reload()
        sender.sendMessage(MessageFormatter.format("config-reloaded"))
        return true
    }
}