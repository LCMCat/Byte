package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.byte.util.MessageFormatter

import tech.ccat.byte.BytePlugin.Companion.instance

class SelfCheckCommand() : AbstractCommand(
    name = "",
    usage = "/byte",
    playerOnly = true
) {

    val service = instance.byteService

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val player = sender as Player
        val balance = service.getBalance(player.uniqueId)
        sender.sendMessage(MessageFormatter.format("self-balance", balance.toString()))
        return true
    }
}