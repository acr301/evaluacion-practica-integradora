package ni.edu.uam.telecatalog.data.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.telecatalog.models.*
import org.threeten.bp.LocalDateTime

class InMemoryDatabase {
    // Stores
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _programs = MutableStateFlow<List<Program>>(emptyList())
    val programs: StateFlow<List<Program>> = _programs.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    init {
        loadMockData()
    }

    fun addChannel(channel: Channel) {
        _channels.value = _channels.value + channel
    }

    fun addProgram(program: Program) {
        _programs.value = _programs.value + program
    }

    fun updateProgram(program: Program) {
        _programs.value = _programs.value.map {
            if (it.id == program.id) program else it
        }
    }

    fun deleteProgram(programId: String) {
        _programs.value = _programs.value.filter { it.id != programId }
        // Also delete related schedules
        _schedules.value = _schedules.value.filter { it.programId != programId }
    }

    fun addSchedule(schedule: Schedule) {
        _schedules.value = _schedules.value + schedule
    }

    fun updateSchedule(schedule: Schedule) {
        _schedules.value = _schedules.value.map {
            if (it.id == schedule.id) schedule else it
        }
    }

    fun deleteSchedule(scheduleId: String) {
        _schedules.value = _schedules.value.filter { it.id != scheduleId }
    }

    private fun loadMockData() {
        // Add mock channels
        _channels.value = listOf(
            Channel("1", "Canal 2", 2, description = "Noticias y variedades"),
            Channel("2", "Tele 7", 7, description = "Entretenimiento y series"),
            Channel("3", "Deportes TV", 10, description = "Deportes 24/7"),
            Channel("4", "Cine Max", 5, description = "Las mejores películas")
        )

        // Add mock programs usando LocalDateTime de ThreeTen
        _programs.value = listOf(
            Program("p1", "Noticias 360", "Noticias locales e internacionales",
                Category.NEWS, 60, 4.5),
            Program("p2", "Stranger Things", "Serie de ciencia ficción",
                Category.SERIES, 50, 4.8, season = 4, episode = 1),
            Program("p3", "La Casa de Papel", "Serie española de atracos",
                Category.SERIES, 70, 4.7, season = 5, episode = 1),
            Program("p4", "Fútbol: Final Copa", "Partido de vuelta de la final",
                Category.SPORTS, 120, 4.9, isLive = true)
        )

        // Add mock schedules usando LocalDateTime (sin Calendar)
        val now = LocalDateTime.now()

        _schedules.value = listOf(
            Schedule(
                id = "s1",
                programId = "p1",
                channelId = "1",
                startTime = now.withHour(20).withMinute(0),
                endTime = now.withHour(21).withMinute(0),
                notes = "Noticiero central"
            ),
            Schedule(
                id = "s2",
                programId = "p2",
                channelId = "2",
                startTime = now.withHour(21).withMinute(0),
                endTime = now.withHour(21).withMinute(50),
                notes = "Estreno nuevo episodio"
            ),
            Schedule(
                id = "s3",
                programId = "p4",
                channelId = "3",
                startTime = now.withHour(15).withMinute(0),
                endTime = now.withHour(17).withMinute(0),
                notes = "Transmisión en vivo"
            )
        )
    }
}