package tech.ccat.byte.currency

import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.command.AddCommand
import tech.ccat.byte.command.PayCommand
import tech.ccat.byte.command.ReloadCommand
import tech.ccat.byte.command.RichestCommand
import tech.ccat.byte.command.SelfCheckCommand
import tech.ccat.byte.command.SetCommand
import tech.ccat.byte.command.ShowCommand
import tech.ccat.byte.command.TakeCommand
import tech.ccat.byte.command.TotalCommand
import tech.ccat.byte.command.TransactionHistoryCommand
import tech.ccat.byte.config.PluginConfig
import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.dao.TransactionRecordDao

/**
 * Byte货币实现类
 *
 * 该类继承自AbstractCurrencyService，实现了Byte货币的具体功能。
 */
class ByteCurrency(
    private val config: PluginConfig,
    dao: PlayerDataDao,
    transactionRecordDao: TransactionRecordDao
) : AbstractCurrencyService(dao, transactionRecordDao, CurrencyType.BYTE, "字节") {
    
    override val currencySymbol: String
        get() = CURRENCY_SYMBOL
    
    override val currencyColor: String
        get() = CURRENCY_COLOR
    
    override val commandEntrance: String
        get() = config.byteCurrencyConfig.command

    override fun createCommandManager(config: PluginConfig): CommandManager {
        return CommandManager(this).apply {
            registerCommand(SelfCheckCommand(commandEntrance, this@ByteCurrency))
            registerCommand(ShowCommand(commandEntrance, this@ByteCurrency))
            registerCommand(PayCommand(commandEntrance, this@ByteCurrency))
            registerCommand(AddCommand(commandEntrance, this@ByteCurrency))
            registerCommand(SetCommand(commandEntrance, this@ByteCurrency))
            registerCommand(TakeCommand(commandEntrance, this@ByteCurrency))
            registerCommand(ReloadCommand(commandEntrance))
            registerCommand(TotalCommand(commandEntrance, this@ByteCurrency))
            registerCommand(RichestCommand(commandEntrance, this@ByteCurrency))
            registerCommand(TransactionHistoryCommand(commandEntrance, this@ByteCurrency))
        }
    }
    
    companion object {
        const val CURRENCY_SYMBOL = "§9§k|||§r"
        const val CURRENCY_COLOR = "&9"
    }
}