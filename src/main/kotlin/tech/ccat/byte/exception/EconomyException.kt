package tech.ccat.byte.exception

/**
 * 经济系统相关异常
 */
class EconomyException : ByteException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}