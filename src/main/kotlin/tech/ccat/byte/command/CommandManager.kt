package tech.ccat.byte.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

class CommandManager(private val currency: AbstractCurrency) : TabExecutor {
    private val commands = mutableMapOf<String, AbstractCommand>()

    fun registerCommand(command: AbstractCommand) {
        commands[command.name] = command
    }
    
    fun clearCommands() {
        commands.clear()
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if(sender is Player){
                return commands[""]?.execute(sender, arrayOf()) != false
            }
            sendSenderHelp(sender)
            return true
        }

        val subCommand = commands[args[0].lowercase()] ?: return sendSenderHelp(sender)

        if (subCommand.playerOnly && sender !is Player) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.PLAYER_COMMAND))
            return true
        }

        if (subCommand.permission != null && !sender.hasPermission(subCommand.permission)) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.NO_PERMISSION))
            return true
        }

        if (args.size < subCommand.minArgs) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.COMMAND_USAGE, subCommand.usage))
            return true
        }

        return subCommand.execute(sender, args)
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return getAvailableCommands(sender)
                .map { it.name }
                .filter { it.startsWith(args[0], true) }
        }

        val subCommand = commands[args[0].lowercase()] ?: return emptyList()

        if ((subCommand.playerOnly && sender !is Player) ||
            (subCommand.permission != null && !sender.hasPermission(subCommand.permission))) {
            return emptyList()
        }

        return subCommand.onTabComplete(sender, args)
    }

    private fun sendSenderHelp(sender: CommandSender): Boolean {
        val available = getAvailableCommands(sender)

        if (available.isEmpty()) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.NO_AVAILABLE_COMMAND))
        } else {
            val helpText = available.joinToString("\n") { it.usage }
            sender.sendMessage(MessageFormatter.format(MessageKeys.COMMAND_LIST_HEADER, currency.commandEntrance) + "\n$helpText")
        }
        return true
    }

    private fun getAvailableCommands(sender: CommandSender): List<AbstractCommand> {
        return commands.values.filter { command ->
            (!command.playerOnly || sender is Player) &&
                    (command.permission == null || sender.hasPermission(command.permission))
        }
    }
}