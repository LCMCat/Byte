package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

class PayCommand(
    commandEntrance: String,
    private val currency: AbstractCurrency
) : AbstractCommand(
    name = "pay",
    permission = "$commandEntrance.pay",
    usage = "/$commandEntrance pay <玩家> <数量>",
    minArgs = 2,
    playerOnly = true
) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val player = sender as Player
        val target = validatePlayer(sender, args[1]) ?: return true
        
        if (player.uniqueId == target.uniqueId) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.TRANSFER_TO_SELF))
            return true
        }
        
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        val balance = currency.getBalance(player.uniqueId)
        if (balance < amount) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.INSUFFICIENT_BALANCE))
            return true
        }

        currency.transferBalance(player.uniqueId, target.uniqueId, amount)
            .whenCompleteAsync { success, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.PAYMENT_FAILED, error.message ?: "未知错误"))
                } else if (success) {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.PAYMENT_SUCCESS, target.name!!, currency.format(amount)))
                    val targetPlayer = target.player
                    if (targetPlayer != null && targetPlayer.isOnline) {
                        targetPlayer.sendMessage(MessageFormatter.format(MessageKeys.PAYMENT_RECEIVED, player.name, currency.format(amount)))
                    }
                } else {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.PAYMENT_FAILED, "未知错误"))
                }
            }
        return true
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return if (args.size == 2) {
            Bukkit.getOnlinePlayers().map { it.name }
        } else {
            emptyList()
        }
    }
}