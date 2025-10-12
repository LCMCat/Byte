package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.storage.model.TransactionType
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys
import tech.ccat.byte.BytePlugin
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryCommand(
    commandEntrance: String,
    private val currency: AbstractCurrency
) : AbstractCommand(
    name = "transactionhistory",
    permission = "byte.command.transactionhistory",
    usage = "/$commandEntrance transactionhistory [页码]",
    minArgs = 0
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val plugin = BytePlugin.instance

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        val page = if (args.isNotEmpty()) {
            try {
                args[0].toInt().coerceAtLeast(1)
            } catch (e: NumberFormatException) {
                sender.sendMessage(MessageFormatter.format(
                    MessageKeys.TRANSACTION_HISTORY_INVALID_PAGE
                ))
                return true
            }
        } else {
            1
        }

        val pageSize = 10

        val recordsFuture = if (sender is Player) {
            currency.getTransactionRecords(sender.uniqueId, page, pageSize)
        } else {
            currency.getAllTransactionRecords(page, pageSize)
        }

        recordsFuture.thenAccept { records ->
            sender.server.scheduler.runTask(plugin, Runnable {
                if (records.isEmpty()) {
                    sender.sendMessage(MessageFormatter.format(
                        MessageKeys.TRANSACTION_HISTORY_EMPTY
                    ))
                    return@Runnable
                }

                sender.sendMessage(MessageFormatter.format(
                    MessageKeys.TRANSACTION_HISTORY_HEADER,
                    page.toString()
                ))

                records.forEach { record ->
                    val timestamp = dateFormat.format(Date(record.timestamp))
                    val amount = formatCurrency(record.amount)
                    
                    when (record.type) {
                        TransactionType.PLAYER_TO_PLAYER -> {
                            val fromPlayer = sender.server.getOfflinePlayer(record.fromPlayerUuid!!)
                            val toPlayer = sender.server.getOfflinePlayer(record.toPlayerUuid!!)
                            val fromName = fromPlayer.name ?: record.fromPlayerUuid.toString().substring(0, 8)
                            val toName = toPlayer.name ?: record.toPlayerUuid.toString().substring(0, 8)

                            if (sender is Player && (record.fromPlayerUuid == sender.uniqueId || record.toPlayerUuid == sender.uniqueId)) {
                                if (record.fromPlayerUuid == sender.uniqueId) {
                                    sender.sendMessage(
                                        MessageFormatter.format(
                                            MessageKeys.TRANSACTION_HISTORY_ENTRY_TRANSFER_SEND,
                                            timestamp,
                                            amount,
                                            toName
                                        )
                                    )
                                } else {
                                    sender.sendMessage(
                                        MessageFormatter.format(
                                            MessageKeys.TRANSACTION_HISTORY_ENTRY_TRANSFER_RECEIVE,
                                            timestamp,
                                            amount,
                                            fromName
                                        )
                                    )
                                }
                            } else {
                                sender.sendMessage(
                                    MessageFormatter.format(
                                        MessageKeys.TRANSACTION_HISTORY_ENTRY_TRANSFER_SEND,
                                        timestamp,
                                        amount,
                                        "$fromName -> $toName"
                                    )
                                )
                            }
                        }
                        TransactionType.ADMIN_ADD -> {
                            sender.sendMessage(
                                MessageFormatter.format(
                                    MessageKeys.TRANSACTION_HISTORY_ENTRY_ADMIN_ADD,
                                    timestamp,
                                    amount
                                )
                            )
                        }
                        TransactionType.ADMIN_SUBTRACT -> {
                            sender.sendMessage(
                                MessageFormatter.format(
                                    MessageKeys.TRANSACTION_HISTORY_ENTRY_ADMIN_TAKE,
                                    timestamp,
                                    amount
                                )
                            )
                        }
                        TransactionType.ADMIN_SET -> {
                            sender.sendMessage(
                                MessageFormatter.format(
                                    MessageKeys.TRANSACTION_HISTORY_ENTRY_ADMIN_SET,
                                    timestamp,
                                    amount
                                )
                            )
                        }
                        TransactionType.SYSTEM -> {
                            sender.sendMessage(
                                MessageFormatter.format(
                                    MessageKeys.TRANSACTION_HISTORY_ENTRY_SYSTEM,
                                    timestamp,
                                    amount,
                                    record.description
                                )
                            )
                        }
                    }
                }

                sender.sendMessage(MessageFormatter.format(
                    MessageKeys.TRANSACTION_HISTORY_NEXT_PAGE,
                    (page + 1).toString()
                ))
            })
        }.exceptionally { ex ->
            sender.server.scheduler.runTask(plugin, Runnable {
                sender.sendMessage(MessageFormatter.format(
                    MessageKeys.TRANSACTION_HISTORY_FETCH_FAILED,
                    ex.message ?: "未知错误"
                ))
            })
            null
        }

        return true
    }

    fun formatCurrency(amount: Double): String{
        return currency.format(amount)
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        if (args.isEmpty() || args.size == 1) {
            return listOf("1", "2", "3")
        }
        
        return emptyList()
    }
}