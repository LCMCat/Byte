package tech.ccat.byte.storage.dao

import tech.ccat.byte.storage.model.TransactionRecord
import tech.ccat.byte.storage.model.TransactionType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * 交易记录数据访问接口
 */
interface TransactionRecordDao {
    /**
     * 创建一条新的交易记录
     *
     * @param record 交易记录对象
     * @return 异步操作结果，成功返回true，失败返回false
     */
    fun create(record: TransactionRecord): CompletableFuture<Boolean>

    /**
     * 根据玩家UUID获取其相关的交易记录（分页）
     *
     * @param playerUuid 玩家UUID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getRecordsByPlayer(playerUuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>

    /**
     * 获取所有交易记录（分页）
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getAllRecords(page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>

    /**
     * 根据交易类型获取交易记录（分页）
     *
     * @param type 交易类型
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getRecordsByType(type: TransactionType, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>

    /**
     * 获取指定时间范围内的交易记录（分页）
     *
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getRecordsByTimeRange(startTime: Long, endTime: Long, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>

    /**
     * 获取两个玩家之间的交易记录（分页）
     *
     * @param player1Uuid 玩家1 UUID
     * @param player2Uuid 玩家2 UUID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 异步返回交易记录列表
     */
    fun getRecordsBetweenPlayers(player1Uuid: UUID, player2Uuid: UUID, page: Int, pageSize: Int): CompletableFuture<List<TransactionRecord>>
}