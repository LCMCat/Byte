package tech.ccat.byte.service

import java.util.*
import java.util.concurrent.CompletableFuture

interface ByteService {
    fun getBalance(uuid: UUID): Double
    fun getBalance(playerName: String): Double?
    fun setBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    fun addBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
    fun subtractBalance(uuid: UUID, amount: Double): CompletableFuture<Boolean>
}