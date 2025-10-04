package tech.ccat.byte.command

import org.bukkit.command.CommandSender
import tech.ccat.byte.BytePlugin.Companion.instance
import tech.ccat.byte.service.ByteService
import tech.ccat.byte.util.MessageFormatter
import java.text.DecimalFormat

class TotalCommand(private val commandEntrance: String, service: ByteService) : AdminCommand("total", 0, commandEntrance, service) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        sender.sendMessage(MessageFormatter.format("calculating-total-money"))
        
        service.getTotalMoney().whenCompleteAsync { total, error ->
            if (error != null) {
                sender.sendMessage(MessageFormatter.format("total-money-failed", error.message ?: "未知错误"))
                return@whenCompleteAsync
            }
            
            val formatter = DecimalFormat("#,##0.00")
            val currencyFlag = instance.configManager.pluginConfig.currencyFlag
            val currencyName = instance.configManager.pluginConfig.currencyName
            
            sender.sendMessage(MessageFormatter.format("total-money-success", 
                "$currencyFlag${formatter.format(total)}", currencyName))
        }
        
        return true
    }
}

class RichestCommand(private val commandEntrance: String, service: ByteService) : AdminCommand("richest", 0, commandEntrance, service) {
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
        
        sender.sendMessage(MessageFormatter.format("fetching-richest-players", limit.toString()))
        
        service.getRichestPlayers(limit).whenCompleteAsync { richestPlayers, error ->
            if (error != null) {
                sender.sendMessage(MessageFormatter.format("richest-players-failed", error.message ?: "未知错误"))
                return@whenCompleteAsync
            }
            
            if (richestPlayers.isEmpty()) {
                sender.sendMessage(MessageFormatter.format("no-players-found"))
                return@whenCompleteAsync
            }
            
            val formatter = DecimalFormat("#,##0.00")
            val currencyFlag = instance.configManager.pluginConfig.currencyFlag
            
            // 计算当前页的玩家范围
            val startIndex = (page - 1) * PLAYERS_PER_PAGE
            val endIndex = minOf(startIndex + PLAYERS_PER_PAGE, richestPlayers.size)
            
            // 获取当前页的玩家列表
            val playersOnCurrentPage = richestPlayers.subList(startIndex, endIndex)
            
            // 发送页眉信息
            sender.sendMessage(MessageFormatter.format("richest-players-header-paginated", 
                page.toString(), 
                ((richestPlayers.size - 1) / PLAYERS_PER_PAGE + 1).toString(), 
                richestPlayers.size.toString()))
            
            // 显示当前页的玩家
            playersOnCurrentPage.forEachIndexed { index, (player, balance) ->
                val rank = startIndex + index + 1
                // 尝试获取玩家名称，如果获取不到则使用UUID的部分作为显示名称
                val playerName = player.name ?: (player.uniqueId.toString().substring(0, 8) + "...")
                sender.sendMessage(MessageFormatter.format("richest-player-entry", 
                    rank, playerName, "$currencyFlag${formatter.format(balance)}"))
            }
            
            // 如果还有更多页面，提示用户如何翻页
            if (page * PLAYERS_PER_PAGE < richestPlayers.size) {
                sender.sendMessage(MessageFormatter.format("richest-players-next-page", (page + 1).toString()))
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