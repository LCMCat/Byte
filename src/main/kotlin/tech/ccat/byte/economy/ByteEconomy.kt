package tech.ccat.byte.economy

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import tech.ccat.byte.storage.cache.CacheManager
import tech.ccat.byte.storage.model.PlayerData
import java.text.DecimalFormat

class ByteEconomy(
    private val currencySymbol: String,
    private val cacheManager: CacheManager
) : Economy {
    private val formatter = DecimalFormat("#,##0.00")
    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getName() = "Byte Economy"
    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return 1
    }

    override fun currencyNamePlural() = "Bytes"
    override fun currencyNameSingular() = "Byte"
    override fun hasAccount(p0: String?): Boolean {
        return true
    }

    override fun format(amount: Double) = "$currencySymbol${formatter.format(amount)}"
    override fun getBalance(player: OfflinePlayer) = cacheManager.getData(player.uniqueId).balance
    override fun getBalance(p0: String?, p1: String?): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: OfflinePlayer?, p1: String?): Double {
        TODO("Not yet implemented")
    }

    override fun has(p0: String?, p1: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer?, p1: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(p0: String?, p1: String?, p2: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer?, p1: String?, p2: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(p0: String?, p1: Double): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: Double
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(p0: String?, p1: Double): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun hasAccount(player: OfflinePlayer) = true
    override fun hasAccount(p0: String?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: String?): Double {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(player: OfflinePlayer): Boolean {
        cacheManager.getData(player.uniqueId)
        return true
    }

    override fun createPlayerAccount(p0: String?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        if (amount <= 0) return EconomyResponse(0.0, 0.0,
            EconomyResponse.ResponseType.FAILURE, "Amount must be positive")

        val data = cacheManager.getData(player.uniqueId)
        data.balance += amount
        data.dirty = true
        return EconomyResponse(amount, data.balance,
            EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun depositPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun createBank(p0: String?, p1: String?): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun createBank(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun deleteBank(p0: String?): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun bankBalance(p0: String?): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun bankHas(p0: String?, p1: Double): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun bankWithdraw(p0: String?, p1: Double): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun bankDeposit(p0: String?, p1: Double): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(p0: String?, p1: String?): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun isBankMember(p0: String?, p1: String?): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun isBankMember(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse? {
        TODO("Not yet implemented")
    }

    override fun getBanks(): List<String?>? {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

}