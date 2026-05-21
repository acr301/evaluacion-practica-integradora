package ni.edu.uam.telecatalog.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ni.edu.uam.telecatalog.model.Canal

class ControladorEstado : ViewModel() {

    //1. Datos ingresados por el usuario
    var nombreInput by mutableStateOf("")
    var categoriaInput by mutableStateOf("")

    //2. Listas dinámicas
    var listaCanales = mutableStateListOf<Canal>()
        private set

    // Estado interno auxiliar para la edición
    private var indiceEdicion by mutableStateOf(-1)

    //3.Actualización de información
    fun guardarCanal() {
        if (nombreInput.isNotBlank() && categoriaInput.isNotBlank()) {
            if (indiceEdicion == -1) {
                // Agregar nuevo registro
                listaCanales.add(Canal(nombre = nombreInput, categoria = categoriaInput))
            } else {
                // Actualizar registro existente
                listaCanales[indiceEdicion] = Canal(nombre = nombreInput, categoria = categoriaInput)
                indiceEdicion = -1 // Resetear modo edición
            }
            // Limpieza de campos
            nombreInput = ""
            categoriaInput = ""
        }
    }
        //para eliminar un canal de la lista
    fun eliminarCanal(canal: Canal) {
        listaCanales.remove(canal)
    }
 // para preparar la edición de un canal, llenando los campos de entrada con los datos del canal seleccionado
    fun prepararEdicion(canal: Canal) {
        val index = listaCanales.indexOf(canal)
        if (index != -1) {
            nombreInput = canal.nombre
            categoriaInput = canal.categoria
            indiceEdicion = index
        }
    }


}