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
            // Combina los Flows en uno solo
            repository.programs.combine(repository.channels) { programs, channels ->
                _uiState.value = _uiState.value.copy(
                    programs = programs,
                    channels = channels
                )
            }.collect()
        }

        viewModelScope.launch {
            repository.schedules.collect { schedules ->
                _uiState.update { it.copy(schedules = schedules) }
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
        viewModelScope.launch {
            val allPrograms = repository.programs.first()
            val filtered = if (category != null) {
                allPrograms.filter { it.category == category }
            } else {
                allPrograms
            }
            _uiState.update {
                it.copy(
                    programs = filtered,
                    selectedCategory = category
                )
            }
        }
    }

    fun searchPrograms(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                filterByCategory(_uiState.value.selectedCategory)
            } else {
                val allPrograms = repository.programs.first()
                val filtered = allPrograms.filter { program ->
                    program.title.contains(query, ignoreCase = true) ||
                            program.description.contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(programs = filtered) }
            }
        }
    }
}

data class TeleCatalogUiState(
    val programs: List<Program> = emptyList(),
    val channels: List<Channel> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)