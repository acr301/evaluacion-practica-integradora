package ni.edu.uam.telecatalog.models

import java.util.UUID

data class Channel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val number: Int,
    val logoUrl: String? = null,
    val description: String = ""
)