package tech.ccat.byte.economy

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import tech.ccat.byte.BytePlugin

class EconomyManager(private val economy: Economy) {
    fun registerEconomy() {
        Bukkit.getServicesManager().register(
            Economy::class.java,
            economy,
            BytePlugin.instance,
            ServicePriority.Highest
        )
    }

    fun unregisterEconomy() {
        Bukkit.getServicesManager().unregister(Economy::class.java, economy)
    }
}