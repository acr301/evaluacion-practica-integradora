package ni.edu.uam.telecatalog.models

enum class Category {
    NEWS,        // Noticias
    SERIES,      // Series
    MOVIES,      // Películas
    SPORTS,      // Deportes
    REALITY,     // Reality shows
    CHILDREN,    // Infantil
    DOCUMENTARY, // Documentales
    MUSIC,       // Música
    OTHER;

    fun getDisplayName(): String = when(this) {
        NEWS -> "Noticias"
        SERIES -> "Series"
        MOVIES -> "Películas"
        SPORTS -> "Deportes"
        REALITY -> "Reality"
        CHILDREN -> "Infantil"
        DOCUMENTARY -> "Documentales"
        MUSIC -> "Música"
        OTHER -> "Otros"
    }
}