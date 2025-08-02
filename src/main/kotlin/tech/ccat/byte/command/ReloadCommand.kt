package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import tech.ccat.byte.util.MessageFormatter

import tech.ccat.byte.BytePlugin.Companion.instance

class ReloadCommand : AbstractCommand(
    name = "reload",
    permission = "${instance.commandEntrance}.admin",
    usage = "/${instance.commandEntrance} reload"
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        instance.reload()
        sender.sendMessage(MessageFormatter.format("config-reloaded"))
        return true
    }
}