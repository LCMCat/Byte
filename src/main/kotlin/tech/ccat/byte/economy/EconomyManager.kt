package tech.ccat.byte.economy

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import tech.ccat.byte.BytePlugin

class EconomyManager(private val economies: List<Economy>) {

    fun registerEconomy() {
        economies.forEach { economy ->
            Bukkit.getServicesManager().register(
                Economy::class.java,
                economy,
                BytePlugin.instance,
                ServicePriority.Highest
            )
        }
    }

    fun unregisterEconomy() {
        economies.forEach { economy ->
            Bukkit.getServicesManager().unregister(Economy::class.java, economy)
        }
    }
}