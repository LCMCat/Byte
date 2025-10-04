package tech.ccat.byte.exception

/**
 * Byte插件自定义异常基类
 */
open class ByteException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}