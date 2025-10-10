package tech.ccat.byte.storage.model

/**
 * 交易类型枚举
 */
enum class TransactionType {
    /**
     * 玩家转账给另一个玩家
     */
    PLAYER_TO_PLAYER,

    /**
     * 管理员添加货币
     */
    ADMIN_ADD,

    /**
     * 管理员扣除货币
     */
    ADMIN_SUBTRACT,

    /**
     * 管理员设置货币
     */
    ADMIN_SET,

    /**
     * 系统操作（如利息、奖励等）
     */
    SYSTEM
}