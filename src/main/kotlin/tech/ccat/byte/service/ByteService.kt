package tech.ccat.byte.service

import java.util.*

interface ByteService {
    fun getBalance(uuid: UUID): Double
    fun getBalance(playerName: String): Double?
    fun setBalance(uuid: UUID, amount: Double)
    fun addBalance(uuid: UUID, amount: Double)
    fun subtractBalance(uuid: UUID, amount: Double)
}