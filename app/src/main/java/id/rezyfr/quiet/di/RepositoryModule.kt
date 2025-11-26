package id.rezyfr.quiet.di

import id.rezyfr.quiet.data.repository.NotificationRepository
import id.rezyfr.quiet.data.repository.NotificationRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    factory<NotificationRepository> { NotificationRepositoryImpl(get()) }
}