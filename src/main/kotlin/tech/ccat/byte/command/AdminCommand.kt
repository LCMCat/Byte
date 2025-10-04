package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tech.ccat.byte.BytePlugin.Companion.instance
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.util.MessageFormatter

abstract class AdminCommand(
    name: String,
    minArgs: Int,
    private val commandEntrance: String,
    protected val service: ByteService
) : AbstractCommand(
    name = name,
    permission = "$commandEntrance.admin",
    usage = "/$commandEntrance $name <玩家> <数量>",
    minArgs = minArgs
) {

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 2) return Bukkit.getOnlinePlayers().map { it.name }
        return emptyList()
    }

    fun amountError(sender: CommandSender): Boolean {
        sender.sendMessage(MessageFormatter.format("invalid-amount"))
        return false
    }
}

class AddCommand(private val commandEntrance: String, service: ByteService) : AdminCommand("add", 2, commandEntrance, service) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getOfflinePlayerIfCached(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null || target.name == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.addBalance(target.uniqueId, amount)
            .whenCompleteAsync { success, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format("balance-update-failed", error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format("balance-added", target.name!!, amount.toString()))
                }
            }
        return true
    }
}

class SetCommand(private val commandEntrance: String, service: ByteService) : AdminCommand("set", 2, commandEntrance, service) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getOfflinePlayerIfCached(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null || target.name == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.setBalance(target.uniqueId, amount)
            .whenCompleteAsync { success, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format("balance-update-failed", error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format("balance-set", target.name!!, amount.toString()))
                }
            }
        return true
    }
}

class TakeCommand(private val commandEntrance: String, service: ByteService) : AdminCommand("take", 2, commandEntrance, service) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getOfflinePlayerIfCached(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null || target.name == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.subtractBalance(target.uniqueId, amount)
            .whenCompleteAsync { success, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format("balance-update-failed", error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format("balance-taken", target.name!!, amount.toString()))
                }
            }
        return true
    }
}