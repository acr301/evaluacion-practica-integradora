package ni.edu.uam.telecatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.telecatalog.ui.screens.TeleCatalogApp
import ni.edu.uam.telecatalog.ui.theme.TeleCatalogTheme
import ni.edu.uam.telecatalog.viewmodel.TeleCatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TeleCatalogTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: TeleCatalogViewModel = viewModel()
                    TeleCatalogApp(viewModel = viewModel)
                }
            }
        }
    }
}