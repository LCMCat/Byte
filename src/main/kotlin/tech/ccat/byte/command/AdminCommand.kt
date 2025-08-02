package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.util.MessageFormatter

abstract class AdminCommand(
    name: String,
    minArgs: Int
) : AbstractCommand(
    name = name,
    permission = "byte.admin",
    usage = "/byte $name <玩家> <数量>",
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

class AddCommand(private val service: ByteService) : AdminCommand("add", 2) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getPlayer(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.addBalance(target.uniqueId, amount)
        target.name?.let { sender.sendMessage(MessageFormatter.format("balance-added", it, amount.toString())) }
        return true
    }
}

class SetCommand(private val service: ByteService) : AdminCommand("set", 2) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getPlayer(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.setBalance(target.uniqueId, amount)
        target.name?.let { sender.sendMessage(MessageFormatter.format("balance-set", it, amount.toString())) }
        return true
    }
}

class TakeCommand(private val service: ByteService) : AdminCommand("take", 2) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getPlayer(args[1])
        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        if(target == null){
            sender.sendMessage(MessageFormatter.format("player-not-found"))
            return true
        }

        service.subtractBalance(target.uniqueId, amount)
        target.name?.let { sender.sendMessage(MessageFormatter.format("balance-taken", it, amount.toString())) }
        return true
    }
}