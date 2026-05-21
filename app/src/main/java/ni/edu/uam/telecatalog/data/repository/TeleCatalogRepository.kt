package ni.edu.uam.telecatalog.data.repository

import kotlinx.coroutines.flow.Flow
import ni.edu.uam.telecatalog.data.database.InMemoryDatabase
import ni.edu.uam.telecatalog.models.*

class TeleCatalogRepository(
    private val database: InMemoryDatabase = InMemoryDatabase()
) {
    val programs: Flow<List<Program>> = database.programs
    val channels: Flow<List<Channel>> = database.channels
    val schedules: Flow<List<Schedule>> = database.schedules

    suspend fun addProgram(program: Program) {
        database.addProgram(program)
    }

    suspend fun updateProgram(program: Program) {
        database.updateProgram(program)
    }

    suspend fun deleteProgram(programId: String) {
        database.deleteProgram(programId)
    }

    suspend fun getProgramsByCategory(category: Category): List<Program> {
        return database.programs.value.filter { it.category == category }
    }

    suspend fun searchPrograms(query: String): List<Program> {
        return database.programs.value.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }
}