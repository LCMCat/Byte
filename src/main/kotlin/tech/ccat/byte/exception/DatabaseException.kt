package tech.ccat.byte.exception

/**
 * 数据库操作异常
 */
class DatabaseException : ByteException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}