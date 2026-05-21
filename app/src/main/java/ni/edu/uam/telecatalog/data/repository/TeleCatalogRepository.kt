// TeleCatalogRepository.kt - Versión con StateFlow
package ni.edu.uam.telecatalog.data.repository

import kotlinx.coroutines.flow.StateFlow
import ni.edu.uam.telecatalog.data.database.InMemoryDatabase
import ni.edu.uam.telecatalog.models.*

class TeleCatalogRepository(
    private val database: InMemoryDatabase = InMemoryDatabase()
) {
    // Cambiar a StateFlow para tener acceso a .value
    val programs: StateFlow<List<Program>> = database.programs
    val channels: StateFlow<List<Channel>> = database.channels
    val schedules: StateFlow<List<Schedule>> = database.schedules

    suspend fun addProgram(program: Program) {
        database.addProgram(program)
    }

    suspend fun updateProgram(program: Program) {
        database.updateProgram(program)
    }

    suspend fun deleteProgram(programId: String) {
        database.deleteProgram(programId)
    }
}