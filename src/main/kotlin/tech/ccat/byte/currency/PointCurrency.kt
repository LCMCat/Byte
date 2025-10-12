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
 * Point货币实现类
 *
 * 该类继承自AbstractCurrencyService，实现了点券货币的具体功能。
 */
class PointCurrency(
    private val config: PluginConfig,
    dao: PlayerDataDao,
    transactionRecordDao: TransactionRecordDao
) : AbstractCurrencyService(dao, transactionRecordDao, CurrencyType.POINT, "点券") {
    
    override val currencySymbol: String
        get() = CURRENCY_SYMBOL
    
    override val currencyColor: String
        get() = CURRENCY_COLOR
    
    override val commandEntrance: String
        get() = config.pointCurrencyConfig.command

    override fun createCommandManager(config: PluginConfig): CommandManager {
        return CommandManager(this).apply {
            registerCommand(SelfCheckCommand(commandEntrance, this@PointCurrency))
            registerCommand(ShowCommand(commandEntrance, this@PointCurrency))
            registerCommand(PayCommand(commandEntrance, this@PointCurrency))
            registerCommand(AddCommand(commandEntrance, this@PointCurrency))
            registerCommand(SetCommand(commandEntrance, this@PointCurrency))
            registerCommand(TakeCommand(commandEntrance, this@PointCurrency))
            registerCommand(ReloadCommand(commandEntrance))
            registerCommand(TotalCommand(commandEntrance, this@PointCurrency))
            registerCommand(RichestCommand(commandEntrance, this@PointCurrency))
            registerCommand(TransactionHistoryCommand(commandEntrance, this@PointCurrency))
        }
    }
    
    companion object {
        const val CURRENCY_SYMBOL = "■"
        const val CURRENCY_COLOR = "&3"
    }
}