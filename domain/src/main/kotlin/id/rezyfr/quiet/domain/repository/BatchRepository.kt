package id.rezyfr.quiet.domain.repository

import id.rezyfr.quiet.domain.model.BatchModel

interface BatchRepository {
    suspend fun addBatch(batch: BatchModel)
    suspend fun getBatch(ruleId: Long) : List<BatchModel>
    suspend fun clear(ruleId: Long)
}