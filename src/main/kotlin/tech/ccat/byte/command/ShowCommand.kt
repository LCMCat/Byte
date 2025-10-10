package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

class ShowCommand(
    private val commandEntrance: String,
    private val service: ByteService
) : AbstractCommand(
    name = "show",
    permission = "$commandEntrance.admin",
    usage = "/$commandEntrance show <玩家名>",
    minArgs = 1
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getOfflinePlayerIfCached(args[1])

        if (target == null || target.name == null) {
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        val balance = service.getBalance(target.uniqueId)
        sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_SHOW, target.name!!, balance.toString()))
        return true
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return if (args.size == 2) {
            sender.server.onlinePlayers.map { it.name }
                .filter { it.startsWith(args[1], ignoreCase = true) }
        } else {
            emptyList()
        }
    }
}