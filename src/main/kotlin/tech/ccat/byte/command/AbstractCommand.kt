package tech.ccat.byte.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tech.ccat.byte.util.MessageFormatter
import tech.ccat.byte.util.MessageKeys

abstract class AbstractCommand(
    val name: String,
    val permission: String? = null,
    val usage: String = "",
    val minArgs: Int = 0,
    val playerOnly: Boolean = false
) {
    abstract fun execute(sender: CommandSender, args: Array<out String>): Boolean
    open fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> = emptyList()

    fun amountError(sender: CommandSender): Boolean {
        sender.sendMessage(MessageFormatter.format(MessageKeys.INVALID_AMOUNT))
        return false
    }

    /**
     * 检查参数长度是否足够
     * @param args 参数数组
     * @param requiredLength 所需的最小参数长度（不包括命令名称）
     * @return 如果参数长度不足则发送错误消息并返回true，否则返回false
     */
    protected fun checkArgsLength(sender: CommandSender, args: Array<out String>, requiredLength: Int): Boolean {
        if (args.size <= requiredLength) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.COMMAND_USAGE, usage))
            return true
        }
        return false
    }

    /**
     * 检查玩家参数是否有效
     * @param playerName 玩家名称
     * @return 如果玩家无效则发送错误消息并返回null，否则返回玩家对象
     */
    protected fun validatePlayer(sender: CommandSender, playerName: String): org.bukkit.OfflinePlayer? {
        val target = Bukkit.getOfflinePlayerIfCached(playerName)
        if (target == null || target.name == null) {
            sender.sendMessage(MessageFormatter.format(MessageKeys.PLAYER_NOT_FOUND))
            return null
        }
        return target
    }
}