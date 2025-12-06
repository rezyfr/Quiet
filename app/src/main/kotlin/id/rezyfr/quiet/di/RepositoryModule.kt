package id.rezyfr.quiet.di

import id.rezyfr.quiet.data.repository.NotificationRepositoryImpl
import id.rezyfr.quiet.data.repository.RuleRepositoryImpl
import id.rezyfr.quiet.domain.repository.NotificationRepository
import id.rezyfr.quiet.domain.repository.RuleRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<NotificationRepository> { NotificationRepositoryImpl(get()) }
    factory<RuleRepository> { RuleRepositoryImpl(get()) }
}
