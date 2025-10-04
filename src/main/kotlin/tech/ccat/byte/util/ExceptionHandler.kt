package tech.ccat.byte.util

import tech.ccat.byte.exception.ByteException
import tech.ccat.byte.exception.DatabaseException
import tech.ccat.byte.exception.ConfigException
import tech.ccat.byte.exception.EconomyException
import java.util.concurrent.CompletableFuture

/**
 * 统一异常处理工具类
 */
object ExceptionHandler {
    
    /**
     * 处理异常并记录日志
     */
    fun handleException(throwable: Throwable, context: String = "") {
        val message = if (context.isNotEmpty()) "$context: ${throwable.message}" else throwable.message ?: "未知错误"
        
        when (throwable) {
            is DatabaseException -> {
                LoggerUtil.severe("数据库错误 - $message", throwable)
            }
            is ConfigException -> {
                LoggerUtil.severe("配置错误 - $message", throwable)
            }
            is EconomyException -> {
                LoggerUtil.severe("经济系统错误 - $message", throwable)
            }
            is ByteException -> {
                LoggerUtil.severe("插件错误 - $message", throwable)
            }
            else -> {
                LoggerUtil.severe("未预期的错误 - $message", throwable)
            }
        }
    }
    
    /**
     * 处理CompletableFuture中的异常
     */
    fun <T> handleFutureException(future: CompletableFuture<T>, context: String = "") {
        future.exceptionally { throwable ->
            handleException(throwable, context)
            null
        }
    }
    
    /**
     * 包装可能抛出异常的操作
     */
    inline fun <T> wrap(context: String, operation: () -> T): T? {
        return try {
            operation()
        } catch (e: Exception) {
            handleException(e, context)
            null
        }
    }
    
    /**
     * 包装可能抛出异常的操作，返回默认值
     */
    inline fun <T> wrapOrDefault(context: String, defaultValue: T, operation: () -> T): T {
        return try {
            operation()
        } catch (e: Exception) {
            handleException(e, context)
            defaultValue
        }
    }
}