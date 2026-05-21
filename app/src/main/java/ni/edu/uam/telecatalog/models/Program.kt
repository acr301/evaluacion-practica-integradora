package ni.edu.uam.telecatalog.models

import java.time.LocalDateTime
import java.util.UUID

data class Program(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: Category,
    val duration: Int, // duración en minutos
    val rating: Double = 0.0, // rating 0-10
    val imageUrl: String? = null,
    val isLive: Boolean = false,
    val season: Int? = null,    // para series
    val episode: Int? = null,    // para series
    val year: Int? = null        // para películas
)

data class ProgramSchedule(
    val programId: String,
    val channelId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)