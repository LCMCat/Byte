package tech.ccat.byte.storage.cache

import tech.ccat.byte.storage.dao.PlayerDataDao
import tech.ccat.byte.storage.model.PlayerData
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

class CacheManager(private val dao: PlayerDataDao) {
    private val cache: ConcurrentMap<UUID, PlayerData> = ConcurrentHashMap()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val isFlushing = AtomicBoolean(false)

    init {
        executor.scheduleAtFixedRate(::flushDirtyData, 1, 1, TimeUnit.MINUTES)
    }

    fun getData(uuid: UUID): PlayerData {
        return cache.computeIfAbsent(uuid) {
            dao.load(it) ?: PlayerData(uuid, 0.0).also { data ->
                dao.save(data)
            }
        }
    }

    fun updateData(data: PlayerData, async: Boolean = true) {
        data.dirty = true
        cache[data.uuid] = data
        if (async) {
            executor.execute { dao.save(data) }
        } else {
            dao.save(data)
        }
    }

    fun flushAll() {
        if (isFlushing.compareAndSet(false, true)) {
            try {
                cache.values.forEach { dao.save(it) }
            } finally {
                isFlushing.set(false)
            }
        }
    }

    private fun flushDirtyData() {
        cache.values.filter { it.dirty }.forEach {
            dao.save(it)
            it.dirty = false
        }
    }

    fun shutdown() {
        flushAll()
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }
}