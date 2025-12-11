package id.rezyfr.quiet

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import id.rezyfr.quiet.di.appModule
import id.rezyfr.quiet.di.persistenceModule
import id.rezyfr.quiet.di.repositoryModule
import id.rezyfr.quiet.di.viewModelModule
import id.rezyfr.quiet.navigation.navigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class QuietApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QuietApp)
            modules(
                listOf(
                    viewModelModule,
                    navigationModule,
                    repositoryModule,
                    persistenceModule,
                    appModule
                )
            )
        }
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val batchChannel = NotificationChannel(
                CHANNEL_BATCH_DELIVERY,
                "Quiet Batch Delivery",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications delivered after batching windows"
                setShowBadge(true)
                enableLights(false)
                enableVibration(true)
            }

            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(batchChannel)
        }
    }
}
const val CHANNEL_BATCH_DELIVERY = "quiet_batch_delivery"