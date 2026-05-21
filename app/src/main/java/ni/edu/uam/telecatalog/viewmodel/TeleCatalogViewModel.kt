package ni.edu.uam.telecatalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ni.edu.uam.telecatalog.data.repository.TeleCatalogRepository
import ni.edu.uam.telecatalog.models.*

class TeleCatalogViewModel(
    private val repository: TeleCatalogRepository = TeleCatalogRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeleCatalogUiState())
    val uiState: StateFlow<TeleCatalogUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.programs.collect { programs ->
                _uiState.update { it.copy(programs = programs) }
            }
        }
    }

    fun addProgram(program: Program) {
        viewModelScope.launch {
            repository.addProgram(program)
        }
    }

    fun updateProgram(program: Program) {
        viewModelScope.launch {
            repository.updateProgram(program)
        }
    }

    fun deleteProgram(programId: String) {
        viewModelScope.launch {
            repository.deleteProgram(programId)
        }
    }

    fun filterByCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
        viewModelScope.launch {
            val filtered = if (category != null) {
                repository.getProgramsByCategory(category)
            } else {
                repository.programs.value
            }
            _uiState.update { it.copy(programs = filtered) }
        }
    }
}

data class TeleCatalogUiState(
    val programs: List<Program> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)