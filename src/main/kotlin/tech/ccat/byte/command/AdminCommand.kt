package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.currency.AbstractCurrencyService
import tech.ccat.byte.storage.model.TransactionType
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys
import java.util.UUID

abstract class AdminCommand(
    name: String,
    minArgs: Int,
    commandEntrance: String,
    protected val currency: AbstractCurrency
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
}

class AddCommand(
    commandEntrance: String,
    currency: AbstractCurrency
) : AdminCommand("add", 2, commandEntrance, currency) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (checkArgsLength(sender, args, 2)) return true

        val target = validatePlayer(sender, args[1]) ?: return true

        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        currency.addBalance(target.uniqueId, amount)
            .whenCompleteAsync { _, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_UPDATE_FAILED, error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_ADD, target.name!!, currency.format(amount)))
                    
                    (currency as AbstractCurrencyService).recordTransaction(
                        fromUuid = UUID.randomUUID(),
                        toUuid = target.uniqueId,
                        amount = amount,
                        transactionType = TransactionType.ADMIN_ADD,
                        description = "管理员添加货币"
                    )
                }
            }
        return true
    }
}

class SetCommand(commandEntrance: String, currency: AbstractCurrency) : AdminCommand("set", 2, commandEntrance, currency) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (checkArgsLength(sender, args, 2)) return true

        val target = validatePlayer(sender, args[1]) ?: return true

        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        val oldBalance = currency.getBalance(target.uniqueId)
        val difference = amount - oldBalance

        currency.setBalance(target.uniqueId, amount)
            .whenCompleteAsync { _, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_UPDATE_FAILED, error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_SET, target.name!!, currency.format(amount)))
                    
                    val transactionType = if (difference > 0) TransactionType.ADMIN_ADD else TransactionType.ADMIN_SUBTRACT
                    val description = if (difference > 0) "管理员增加货币" else "管理员扣除货币"
                    
                    (currency as AbstractCurrencyService).recordTransaction(
                        fromUuid = UUID.randomUUID(),
                        toUuid = target.uniqueId,
                        amount = kotlin.math.abs(difference),
                        transactionType = transactionType,
                        description = description
                    )
                }
            }
        return true
    }
}

class TakeCommand(commandEntrance: String, currency: AbstractCurrency) : AdminCommand("take", 2, commandEntrance, currency) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (checkArgsLength(sender, args, 2)) return true

        val target = validatePlayer(sender, args[1]) ?: return true

        val amount = args[2].toDoubleOrNull() ?: return amountError(sender)

        currency.subtractBalance(target.uniqueId, amount)
            .whenCompleteAsync { _, error ->
                if (error != null) {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_UPDATE_FAILED, error.message!!))
                } else {
                    sender.sendMessage(MessageFormatter.format(MessageKeys.BALANCE_TAKE, target.name!!, currency.format(amount)))

                    (currency as AbstractCurrencyService).recordTransaction(
                        fromUuid = UUID.randomUUID(),
                        toUuid = target.uniqueId,
                        amount = amount,
                        transactionType = TransactionType.ADMIN_SUBTRACT,
                        description = "管理员扣除货币"
                    )
                }
            }
        return true
    }
}

class TotalCommand(
    commandEntrance: String,
    currency: AbstractCurrency
) : AdminCommand("total", 0, commandEntrance, currency) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        sender.sendMessage(MessageFormatter.format(MessageKeys.TOTAL_MONEY))

        currency.getTotalMoney().whenCompleteAsync { total, error ->
            if (error != null) {
                sender.sendMessage(MessageFormatter.format(MessageKeys.TOTAL_MONEY_FAILED, error.message ?: "未知错误"))
                return@whenCompleteAsync
            }

            val currencyName = currency.currencyName

            sender.sendMessage(MessageFormatter.format(MessageKeys.TOTAL_MONEY_SUCCESS,
                currency.format(total), currencyName))
        }

        return true
    }
}

class RichestCommand(commandEntrance: String, currency: AbstractCurrency) : AdminCommand("richest", 0, commandEntrance, currency) {
    companion object {
        private const val PLAYERS_PER_PAGE = 20
    }

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val page = if (args.size > 1) {
            args[1].toIntOrNull()?.coerceAtLeast(1) ?: 1
        } else {
            1
        }

        val limit = page * PLAYERS_PER_PAGE

        sender.sendMessage(MessageFormatter.format(MessageKeys.FETCHING_RICHEST_PLAYERS, limit.toString()))

        currency.getRichestPlayers(limit).whenCompleteAsync { richestPlayers, error ->
            if (error != null) {
                sender.sendMessage(MessageFormatter.format(MessageKeys.RICHEST_PLAYERS_FAILED, error.message ?: "未知错误"))
                return@whenCompleteAsync
            }

            if (richestPlayers.isEmpty()) {
                sender.sendMessage(MessageFormatter.format(MessageKeys.RICHEST_PLAYERS_EMPTY))
                return@whenCompleteAsync
            }

            val startIndex = (page - 1) * PLAYERS_PER_PAGE
            val endIndex = minOf(startIndex + PLAYERS_PER_PAGE, richestPlayers.size)

            val playersOnCurrentPage = richestPlayers.subList(startIndex, endIndex)

            sender.sendMessage(MessageFormatter.format(MessageKeys.RICHEST_PLAYERS_HEADER,
                page.toString(),
                ((richestPlayers.size - 1) / PLAYERS_PER_PAGE + 1).toString(),
                richestPlayers.size.toString()))

            playersOnCurrentPage.forEachIndexed { index, pair ->
                val player = pair.first
                val balance = pair.second
                val rank = startIndex + index + 1
                val playerName = player.name ?: (player.uniqueId.toString().substring(0, 8) + "...")
                sender.sendMessage(MessageFormatter.format(MessageKeys.RICHEST_PLAYERS_ENTRY,
                    rank.toString(), playerName, currency.format(balance)))
            }

            if (page * PLAYERS_PER_PAGE < richestPlayers.size) {
                sender.sendMessage(MessageFormatter.format(MessageKeys.RICHEST_PLAYERS_NEXT_PAGE, (page + 1).toString()))
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.size == 2) {
            return listOf("1", "2", "3", "4", "5")
        }
        return emptyList()
    }
}