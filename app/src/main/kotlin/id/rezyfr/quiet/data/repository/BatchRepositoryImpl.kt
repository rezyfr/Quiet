package id.rezyfr.quiet.data.repository

import id.rezyfr.quiet.data.dao.BatchDao
import id.rezyfr.quiet.data.entity.BatchEntry
import id.rezyfr.quiet.domain.model.BatchModel
import id.rezyfr.quiet.domain.repository.BatchRepository

class BatchRepositoryImpl(
    private val batchDao: BatchDao
) : BatchRepository {
    override suspend fun addBatch(batch: BatchModel) {
        batchDao.insert(
            BatchEntry(
                ruleId = batch.ruleId,
                title = batch.title,
                text = batch.text,
                packageName = batch.packageName,
                timestamp = batch.timestamp,
            )
        )
    }
    override suspend fun getBatch(ruleId: Long): List<BatchModel> {
        return batchDao.getBatchByRuleId(ruleId).map {
            BatchModel(
                id = it.id,
                ruleId = it.ruleId,
                title = it.title,
                text = it.text,
                packageName = it.packageName,
                timestamp = it.timestamp
            )
        }
    }
    override suspend fun clear(ruleId: Long) {
        batchDao.deleteBatchByRuleId(ruleId)
    }
}
