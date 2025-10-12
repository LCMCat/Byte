package tech.ccat.byte.currency

import tech.ccat.byte.command.CommandManager
import tech.ccat.byte.command.AddCommand
import tech.ccat.byte.command.PayCommand
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
 * Coin货币实现类
 *
 * 该类继承自AbstractCurrencyService，实现了Coin货币的具体功能。
 */
class CoinCurrency(
    private val config: PluginConfig,
    dao: PlayerDataDao,
    transactionRecordDao: TransactionRecordDao
) : AbstractCurrencyService(dao, transactionRecordDao, CurrencyType.COIN, "金币") {
    
    override val currencySymbol: String
        get() = CURRENCY_SYMBOL
    
    override val currencyColor: String
        get() = CURRENCY_COLOR
    
    override val commandEntrance: String
        get() = this.config.coinCurrencyConfig.command

    override fun createCommandManager(config: PluginConfig): CommandManager {
        return CommandManager(this).apply {
            registerCommand(SelfCheckCommand(commandEntrance, this@CoinCurrency))
            registerCommand(ShowCommand(commandEntrance, this@CoinCurrency))
            registerCommand(PayCommand(commandEntrance, this@CoinCurrency))
            registerCommand(AddCommand(commandEntrance, this@CoinCurrency))
            registerCommand(SetCommand(commandEntrance, this@CoinCurrency))
            registerCommand(TakeCommand(commandEntrance, this@CoinCurrency))
            registerCommand(TotalCommand(commandEntrance, this@CoinCurrency))
            registerCommand(RichestCommand(commandEntrance, this@CoinCurrency))
            registerCommand(TransactionHistoryCommand(commandEntrance, this@CoinCurrency))
        }
    }
    
    companion object {
        const val CURRENCY_SYMBOL = "＄"
        const val CURRENCY_COLOR = "&e"
    }
}