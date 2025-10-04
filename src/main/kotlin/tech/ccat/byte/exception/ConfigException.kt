package tech.ccat.byte.exception

/**
 * 配置相关异常
 */
class ConfigException : ByteException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}