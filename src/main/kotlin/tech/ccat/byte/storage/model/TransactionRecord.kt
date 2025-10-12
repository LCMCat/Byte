package tech.ccat.byte.storage.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

/**
 * 交易记录数据模型
 *
 * 用于存储所有货币变动事件的历史记录
 */
data class TransactionRecord @BsonCreator constructor(
    /**
     * 交易记录的唯一标识符
     */
    @param:BsonProperty("uuid")
    val uuid: UUID,

    /**
     * 交易类型
     */
    @param:BsonProperty("type")
    val type: TransactionType,

    /**
     * 发起交易的玩家UUID（对于某些系统操作可能为空）
     */
    @param:BsonProperty("fromPlayerUuid")
    val fromPlayerUuid: UUID?,

    /**
     * 接收交易的玩家UUID（对于某些系统操作可能为空）
     */
    @param:BsonProperty("toPlayerUuid")
    val toPlayerUuid: UUID?,

    /**
     * 交易金额
     */
    @param:BsonProperty("amount")
    val amount: Double,

    /**
     * 交易发生时的服务器时间戳
     */
    @param:BsonProperty("timestamp")
    val timestamp: Long,

    /**
     * 交易的附加信息（如转账备注、操作原因等）
     */
    @param:BsonProperty("description")
    val description: String,

    /**
     * 交易发生前发送方的余额
     */
    @param:BsonProperty("fromPlayerBalanceBefore")
    val fromPlayerBalanceBefore: Double?,

    /**
     * 交易发生后发送方的余额
     */
    @param:BsonProperty("fromPlayerBalanceAfter")
    val fromPlayerBalanceAfter: Double?,

    /**
     * 交易发生前接收方的余额
     */
    @param:BsonProperty("toPlayerBalanceBefore")
    val toPlayerBalanceBefore: Double?,

    /**
     * 交易发生后接收方的余额
     */
    @param:BsonProperty("toPlayerBalanceAfter")
    val toPlayerBalanceAfter: Double?
)