package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

class ShowCommand(
    commandEntrance: String,
    private val currency: AbstractCurrency
) : AbstractCommand(
    name = "show",
    permission = "$commandEntrance.admin",
    usage = "/$commandEntrance show <玩家名>",
    minArgs = 1
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (checkArgsLength(sender, args, 1)) return true
        val target = validatePlayer(sender, args[1]) ?: return true

        val balance = currency.getBalance(target.uniqueId)
        sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_SHOW, target.name!!, currency.currencyName, currency.format(balance)))
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