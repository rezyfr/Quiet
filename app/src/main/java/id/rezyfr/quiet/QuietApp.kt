package id.rezyfr.quiet

import android.app.Application
import id.rezyfr.quiet.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class QuietApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QuietApp)
            modules(
                listOf(
                    viewModelModule
                )
            )
        }
    }
}