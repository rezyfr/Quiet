package id.rezyfr.quiet.di

import androidx.room.Room
import id.rezyfr.quiet.data.dao.NotificationDao
import id.rezyfr.quiet.data.dao.RuleDao
import id.rezyfr.quiet.data.database.QuietDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistenceModule = module {
    single<QuietDatabase> {
        Room.databaseBuilder(androidContext(), QuietDatabase::class.java, "quiet_db").build()
    }

    single<NotificationDao> { get<QuietDatabase>().notificationDao() }
    single<RuleDao> { get<QuietDatabase>().ruleDao() }
}
