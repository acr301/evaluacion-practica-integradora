package ni.edu.uam.telecatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.telecatalog.ui.screens.ProgramListScreen
import ni.edu.uam.telecatalog.ui.theme.TelecatalogTheme
import ni.edu.uam.telecatalog.viewmodel.TeleCatalogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelecatalogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: TeleCatalogViewModel = viewModel()
                    ProgramListScreen(viewModel = viewModel)
                }
            }
        }
    }
}