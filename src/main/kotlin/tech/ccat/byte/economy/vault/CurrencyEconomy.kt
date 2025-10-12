package tech.ccat.byte.economy.vault

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import tech.ccat.byte.core.ServiceLocator
import tech.ccat.byte.currency.AbstractCurrency
import tech.ccat.byte.currency.CurrencyType

class CurrencyEconomy(private val currencyType: CurrencyType) : Economy {
    private val currency: AbstractCurrency
        get() =
            ServiceLocator.getInstance().currencyManager.getCurrencyById(currencyType.getId())
                ?: ServiceLocator.getInstance().defaultCurrency

    override fun getName(): String = currencyType.getId()

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = 2

    override fun format(amount: Double): String {
        return currency.format(amount)
    }

    override fun currencyNamePlural(): String = currency.currencyName

    override fun currencyNameSingular(): String = currency.currencyName

    override fun getBalance(player: OfflinePlayer?): Double {
        if (player == null) return 0.0
        return currency.getBalance(player.uniqueId)
    }

    override fun getBalance(player: OfflinePlayer?, world: String?): Double {
        return getBalance(player)
    }
    
    override fun getBalance(playerName: String?): Double {
        if (playerName == null) return 0.0
        val player = Bukkit.getOfflinePlayerIfCached(playerName)
        return if (player != null) {
            currency.getBalance(player.uniqueId)
        } else {
            0.0
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun getBalance(playerName: String?, worldName: String?): Double {
        return getBalance(playerName)
    }

    override fun has(player: OfflinePlayer?, amount: Double): Boolean {
        if (player == null) return false
        return currency.getBalance(player.uniqueId) >= amount
    }

    override fun has(player: OfflinePlayer?, world: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun has(playerName: String?, amount: Double): Boolean {
        if (playerName == null) return false
        val player = Bukkit.getOfflinePlayerIfCached(playerName)
        return if (player != null) {
            currency.getBalance(player.uniqueId) >= amount
        } else {
            false
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }
        
        if (amount < 0) {
            return EconomyResponse(0.0, currency.getBalance(player.uniqueId), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds")
        }
        
        val balance = currency.getBalance(player.uniqueId)
        if (balance < amount) {
            return EconomyResponse(0.0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds")
        }
        
        currency.subtractBalance(player.uniqueId, amount)
        val newBalance = currency.getBalance(player.uniqueId)
        return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun withdrawPlayer(player: OfflinePlayer?, world: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(player, amount)
    }

    override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
        if (playerName == null) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }

        val player = Bukkit.getOfflinePlayerIfCached(playerName) ?: return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "Player not found"
        )

        return withdrawPlayer(player, amount)
    }
    
    @Deprecated("Deprecated in Java")
    override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

    override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }
        
        if (amount < 0) {
            return EconomyResponse(0.0, currency.getBalance(player.uniqueId), EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds")
        }
        
        currency.addBalance(player.uniqueId, amount)
        val newBalance = currency.getBalance(player.uniqueId)
        return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun depositPlayer(player: OfflinePlayer?, world: String?, amount: Double): EconomyResponse {
        return depositPlayer(player, amount)
    }

    override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
        if (playerName == null) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }

        val player = Bukkit.getOfflinePlayerIfCached(playerName) ?: return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "Player not found"
        )

        return depositPlayer(player, amount)
    }
    
    @Deprecated("Deprecated in Java")
    override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        return depositPlayer(playerName, amount)
    }

    override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }
    
    @Deprecated("Deprecated in Java")
    override fun createBank(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }
    
    @Deprecated("Deprecated in Java")
    override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }
    
    @Deprecated("Deprecated in Java")
    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking not supported")
    }

    override fun getBanks(): List<String> = emptyList()

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean = true

    override fun createPlayerAccount(player: OfflinePlayer?, world: String?): Boolean = true
    
    @Deprecated("Deprecated in Java")
    override fun createPlayerAccount(playerName: String?): Boolean = true
    
    @Deprecated("Deprecated in Java")
    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean = true
    
    override fun isEnabled(): Boolean = true
    
    @Deprecated("Deprecated in Java")
    override fun hasAccount(playerName: String?): Boolean = true
    
    override fun hasAccount(player: OfflinePlayer?): Boolean = true
    @Deprecated("Deprecated in Java")
    override fun hasAccount(p0: String?, p1: String?): Boolean {
        return true
    }

    override fun hasAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        return true
    }
}