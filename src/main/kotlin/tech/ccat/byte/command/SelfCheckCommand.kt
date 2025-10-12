package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

class SelfCheckCommand(
    commandEntrance: String,
    private val currency: AbstractCurrency
) : AbstractCommand(
    name = "",
    usage = "/$commandEntrance",
    playerOnly = true
) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val player = sender as Player
        val balance = currency.getBalance(player.uniqueId)
        sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_CHECK, currency.currencyName, currency.format(balance)))
        return true
    }
}