package tech.ccat.byte.util

import tech.ccat.byte.BytePlugin
import java.util.logging.Level
import java.util.logging.Logger

/**
 * 统一日志工具类
 */
object LoggerUtil {
    private val logger: Logger = BytePlugin.instance.logger
    
    /**
     * 记录信息级别日志
     */
    fun info(message: String) {
        logger.info("[Byte] $message")
    }
    
    /**
     * 记录警告级别日志
     */
    fun warn(message: String) {
        logger.warning("[Byte] $message")
    }
    
    /**
     * 记录错误级别日志
     */
    fun severe(message: String) {
        logger.severe("[Byte] $message")
    }
    
    /**
     * 记录带异常的错误日志
     */
    fun severe(message: String, throwable: Throwable) {
        logger.log(Level.SEVERE, "[Byte] $message", throwable)
    }
    
    /**
     * 记录调试级别日志
     */
    fun debug(message: String) {
        logger.fine("[Byte] $message")
    }
    
    /**
     * 记录详细级别日志
     */
    fun trace(message: String) {
        logger.finest("[Byte] $message")
    }
}