package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.util.MessageFormatter

class SelfCheckCommand(private val service: ByteService) : AbstractCommand(
    name = "",
    usage = "/byte",
    playerOnly = true
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val player = sender as Player
        val balance = service.getBalance(player.uniqueId)
        sender.sendMessage(MessageFormatter.format("self-balance", balance.toString()))
        return true
    }
}