package ni.edu.uam.telecatalog

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class TeleCatalogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}