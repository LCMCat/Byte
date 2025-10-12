package tech.ccat.byte.currency

/**
 * 货币类型枚举
 *
 * 定义了系统中支持的所有货币类型
 */
enum class CurrencyType {
    BYTE,
    COIN,
    POINT;

    /**
     * 获取货币ID字符串
     */
    fun getId(): String = this.name.lowercase()

    /**
     * 根据货币ID字符串获取对应的枚举值
     */
    companion object {
        fun fromId(id: String): CurrencyType? {
            return try {
                valueOf(id.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}