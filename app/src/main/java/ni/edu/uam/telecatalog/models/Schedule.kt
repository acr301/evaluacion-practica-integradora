package ni.edu.uam.telecatalog.models

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val programId: String,
    val channelId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isRepeated: Boolean = false,
    val notes: String = ""
) {
    fun getDuration(): Long = ChronoUnit.MINUTES.between(startTime, endTime)

    fun isValid(): Boolean = endTime.isAfter(startTime)

    fun getFormattedStartTime(pattern: String = "dd/MM/yyyy HH:mm"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return startTime.format(formatter)
    }

    fun getFormattedEndTime(pattern: String = "dd/MM/yyyy HH:mm"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return endTime.format(formatter)
    }
}

// DateUtils actualizado sin referencia a Calendar
object DateUtils {
    fun now(): LocalDateTime = LocalDateTime.now()

    fun of(year: Int, month: Int, day: Int, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime.of(year, month, day, hour, minute)

    fun parse(dateString: String, pattern: String = "yyyy-MM-dd HH:mm"): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateString, formatter)
    }
}