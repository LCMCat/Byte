package tech.ccat.byte.util

/**
 * 消息键定义类
 */
object MessageKeys {
    // 通用消息
    const val PLAYER_COMMAND = "player-command"
    const val RELOAD_SUCCESS = "config-reloaded"
    const val INVALID_AMOUNT = "invalid-amount"
    const val PLAYER_NOT_FOUND = "player-not-found"
    const val COMMAND_USAGE = "command-usage"
    const val NO_AVAILABLE_COMMAND = "no-available-command"
    const val COMMAND_LIST_HEADER = "command-list-header"
    const val NO_PERMISSION = "no-permission"
    
    // 余额相关消息
    const val BALANCE_CHECK = "self-balance"
    const val BALANCE_SET = "balance-set"
    const val BALANCE_ADD = "balance-added"
    const val BALANCE_TAKE = "balance-taken"
    const val BALANCE_SHOW = "balance-show"
    const val BALANCE_UPDATE_FAILED = "balance-update-failed"
    const val INSUFFICIENT_BALANCE = "insufficient-balance"
    const val PAYMENT_SUCCESS = "payment-success"
    const val PAYMENT_FAILED = "payment-failed"
    const val PAYMENT_RECEIVED = "payment-received"
    const val TRANSFER_TO_SELF = "transfer-to-self"
    
    // 总金额相关消息
    const val TOTAL_MONEY = "calculating-total-money"
    const val TOTAL_MONEY_SUCCESS = "total-money-success"
    const val TOTAL_MONEY_FAILED = "total-money-failed"
    
    // 富豪榜相关消息
    const val RICHEST_PLAYERS_HEADER = "richest-players-header-paginated"
    const val RICHEST_PLAYERS_ENTRY = "richest-player-entry"
    const val RICHEST_PLAYERS_EMPTY = "no-players-found"
    const val RICHEST_PLAYERS_FAILED = "richest-players-failed"
    const val RICHEST_PLAYERS_NEXT_PAGE = "richest-players-next-page"
    const val FETCHING_RICHEST_PLAYERS = "fetching-richest-players"
    
    // 交易历史相关消息
    const val TRANSACTION_HISTORY_HEADER = "transaction-history-header"
    const val TRANSACTION_HISTORY_HEADER_PAGINATED = "transaction-history-header-paginated"
    const val TRANSACTION_HISTORY_EMPTY = "transaction-history-empty"
    const val TRANSACTION_HISTORY_NEXT_PAGE = "transaction-history-next-page"
    const val TRANSACTION_HISTORY_INVALID_PAGE = "transaction-history-invalid-page"
    const val TRANSACTION_HISTORY_FETCH_FAILED = "transaction-history-fetch-failed"
    const val TRANSACTION_HISTORY_FAILED = "transaction-history-failed"
    
    // 交易历史条目消息
    const val TRANSACTION_HISTORY_ENTRY_TRANSFER_SEND = "transaction-history-entry-transfer-send"
    const val TRANSACTION_HISTORY_ENTRY_TRANSFER_RECEIVE = "transaction-history-entry-transfer-receive"
    const val TRANSACTION_HISTORY_ENTRY_ADMIN_ADD = "transaction-history-entry-admin-add"
    const val TRANSACTION_HISTORY_ENTRY_ADMIN_SUBTRACT = "transaction-history-entry-admin-subtract"
    const val TRANSACTION_HISTORY_ENTRY_ADMIN_SET = "transaction-history-entry-admin-set"
    const val TRANSACTION_HISTORY_ENTRY_ADMIN_TAKE = "transaction-history-entry-admin-take"
    const val TRANSACTION_HISTORY_ENTRY_SYSTEM = "transaction-history-entry-system"
    
    // 货币符号配置
    const val CURRENCY_SYMBOLS = "currency-symbols"
    const val CURRENCY_SYMBOL_BYTE = "currency-symbols.byte"
    const val CURRENCY_SYMBOL_COIN = "currency-symbols.coin"
    const val CURRENCY_SYMBOL_POINT = "currency-symbols.point"
}