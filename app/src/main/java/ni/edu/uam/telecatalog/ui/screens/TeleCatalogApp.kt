package ni.edu.uam.telecatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ni.edu.uam.telecatalog.models.Category
import ni.edu.uam.telecatalog.models.Channel
import ni.edu.uam.telecatalog.models.Program
import ni.edu.uam.telecatalog.models.Schedule
import ni.edu.uam.telecatalog.ui.components.ChannelCard
import ni.edu.uam.telecatalog.ui.components.LiveBadge
import ni.edu.uam.telecatalog.ui.components.ProgramCard
import ni.edu.uam.telecatalog.ui.components.SectionHeader
import ni.edu.uam.telecatalog.ui.components.StatCard
import ni.edu.uam.telecatalog.viewmodel.TeleCatalogUiState
import ni.edu.uam.telecatalog.viewmodel.TeleCatalogViewModel

private enum class Screen {
    HOME,
    PROGRAMS,
    CHANNELS,
    DETAIL,
    FORM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleCatalogApp(
    viewModel: TeleCatalogViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var selectedProgramId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedProgram = uiState.programs.firstOrNull { it.id == selectedProgramId }

    val topBarTitle = when (currentScreen) {
        Screen.HOME -> "Tele Catalog"
        Screen.PROGRAMS -> "Programación"
        Screen.CHANNELS -> "Canales"
        Screen.DETAIL -> "Detalle"
        Screen.FORM -> if (selectedProgram == null) "Nuevo programa" else "Editar programa"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    if (currentScreen == Screen.DETAIL || currentScreen == Screen.FORM) {
                        TextButton(
                            onClick = { currentScreen = Screen.PROGRAMS }
                        ) {
                            Text("Atrás")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (
                currentScreen == Screen.HOME ||
                currentScreen == Screen.PROGRAMS ||
                currentScreen == Screen.CHANNELS
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { currentScreen = Screen.HOME },
                        icon = { Text("⌂") },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.PROGRAMS,
                        onClick = { currentScreen = Screen.PROGRAMS },
                        icon = { Text("▤") },
                        label = { Text("Programas") }
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.CHANNELS,
                        onClick = { currentScreen = Screen.CHANNELS },
                        icon = { Text("▣") },
                        label = { Text("Canales") }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.PROGRAMS) {
                FloatingActionButton(
                    onClick = {
                        selectedProgramId = null
                        currentScreen = Screen.FORM
                    }
                ) {
                    Text("+")
                }
            }
        }
    ) { padding ->
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                uiState = uiState,
                padding = padding,
                onSeePrograms = { currentScreen = Screen.PROGRAMS },
                onProgramClick = {
                    selectedProgramId = it
                    currentScreen = Screen.DETAIL
                }
            )

            Screen.PROGRAMS -> ProgramsScreen(
                uiState = uiState,
                padding = padding,
                onSearch = viewModel::searchPrograms,
                onFilter = viewModel::filterByCategory,
                onProgramClick = {
                    selectedProgramId = it
                    currentScreen = Screen.DETAIL
                }
            )

            Screen.CHANNELS -> ChannelsScreen(
                uiState = uiState,
                padding = padding
            )

            Screen.DETAIL -> ProgramDetailScreen(
                program = selectedProgram,
                uiState = uiState,
                padding = padding,
                onEdit = { currentScreen = Screen.FORM },
                onDelete = { programId ->
                    viewModel.deleteProgram(programId)
                    selectedProgramId = null
                    currentScreen = Screen.PROGRAMS
                },
                onBack = { currentScreen = Screen.PROGRAMS }
            )

            Screen.FORM -> ProgramFormScreen(
                editingProgram = selectedProgram,
                padding = padding,
                onCancel = { currentScreen = Screen.PROGRAMS },
                onSave = { program ->
                    if (selectedProgram == null) {
                        viewModel.addProgram(program)
                    } else {
                        viewModel.updateProgram(program)
                    }

                    selectedProgramId = program.id
                    currentScreen = Screen.PROGRAMS
                }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    uiState: TeleCatalogUiState,
    padding: PaddingValues,
    onSeePrograms: () -> Unit,
    onProgramClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Televisión nacional en una sola guía",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Consulta canales, administra programas y controla la parrilla televisiva desde una interfaz clara y moderna.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    ElevatedButton(onClick = onSeePrograms) {
                        Text("Ver programación")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Programas",
                    value = uiState.programs.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Canales",
                    value = uiState.channels.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "En vivo",
                    value = uiState.programs.count { it.isLive }.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Horarios",
                    value = uiState.schedules.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(
                title = "Programas destacados",
                subtitle = "Selecciona un programa para ver su detalle, editarlo o eliminarlo."
            )
        }

        items(
            items = uiState.programs.take(3),
            key = { it.id }
        ) { program ->
            ProgramCard(
                program = program,
                schedule = uiState.findSchedule(program.id),
                channel = uiState.findChannelByProgram(program.id),
                onClick = { onProgramClick(program.id) }
            )
        }
    }
}

@Composable
private fun ProgramsScreen(
    uiState: TeleCatalogUiState,
    padding: PaddingValues,
    onSearch: (String) -> Unit,
    onFilter: (Category?) -> Unit,
    onProgramClick: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Parrilla de programación",
                subtitle = "Aquí se evidencia búsqueda, filtros y CRUD sobre programas."
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onSearch(it)
                },
                label = { Text("Buscar programa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            CategoryFilters(
                selectedCategory = uiState.selectedCategory,
                onFilter = onFilter
            )
        }

        if (uiState.programs.isEmpty()) {
            item {
                EmptyProgramsCard()
            }
        } else {
            items(
                items = uiState.programs,
                key = { it.id }
            ) { program ->
                ProgramCard(
                    program = program,
                    schedule = uiState.findSchedule(program.id),
                    channel = uiState.findChannelByProgram(program.id),
                    onClick = { onProgramClick(program.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    selectedCategory: Category?,
    onFilter: (Category?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onFilter(null) },
                label = { Text("Todos") }
            )

            Category.entries.take(3).forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onFilter(category) },
                    label = { Text(category.getDisplayName()) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Category.entries.drop(3).take(3).forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onFilter(category) },
                    label = { Text(category.getDisplayName()) }
                )
            }
        }
    }
}

@Composable
private fun EmptyProgramsCard() {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No hay programas para mostrar",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Prueba otra búsqueda, cambia el filtro o agrega un nuevo programa.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChannelsScreen(
    uiState: TeleCatalogUiState,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Canales disponibles",
                subtitle = "Información tomada desde el repositorio y la base de datos en memoria."
            )
        }

        items(
            items = uiState.channels,
            key = { it.id }
        ) { channel ->
            ChannelCard(channel = channel)
        }
    }
}

@Composable
private fun ProgramDetailScreen(
    program: Program?,
    uiState: TeleCatalogUiState,
    padding: PaddingValues,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (program == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No se encontró el programa seleccionado.")

            Button(onClick = onBack) {
                Text("Volver")
            }
        }

        return
    }

    val schedule = uiState.findSchedule(program.id)
    val channel = uiState.findChannelByProgram(program.id)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = program.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )

                        if (program.isLive) {
                            LiveBadge()
                        }
                    }

                    DetailItem("Categoría", program.category.getDisplayName())
                    DetailItem("Canal", channel?.name ?: "Sin canal asignado")
                    DetailItem("Duración", "${program.duration} minutos")
                    DetailItem("Rating", "${program.rating}/10")

                    DetailItem(
                        "Horario",
                        schedule?.let {
                            "${it.getFormattedStartTime("dd/MM/yyyy HH:mm")} - ${it.getFormattedEndTime("HH:mm")}"
                        } ?: "Sin horario asignado"
                    )

                    if (program.season != null || program.episode != null) {
                        DetailItem(
                            "Temporada / Episodio",
                            "T${program.season ?: "-"} · E${program.episode ?: "-"}"
                        )
                    }

                    if (program.year != null) {
                        DetailItem("Año", program.year.toString())
                    }

                    Text(
                        text = program.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar programa") },
            text = {
                Text("¿Deseas eliminar '${program.title}'? También se eliminarán sus horarios asociados en memoria.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(program.id)
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProgramFormScreen(
    editingProgram: Program?,
    padding: PaddingValues,
    onCancel: () -> Unit,
    onSave: (Program) -> Unit
) {
    var title by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.title.orEmpty())
    }

    var description by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.description.orEmpty())
    }

    var durationText by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.duration?.toString().orEmpty())
    }

    var ratingText by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.rating?.toString().orEmpty())
    }

    var category by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.category ?: Category.NEWS)
    }

    var isLive by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.isLive ?: false)
    }

    var seasonText by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.season?.toString().orEmpty())
    }

    var episodeText by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.episode?.toString().orEmpty())
    }

    var yearText by rememberSaveable(editingProgram?.id) {
        mutableStateOf(editingProgram?.year?.toString().orEmpty())
    }

    val duration = durationText.toIntOrNull()
    val rating = ratingText.toDoubleOrNull()

    val isValid = title.isNotBlank() &&
            description.isNotBlank() &&
            duration != null &&
            duration > 0 &&
            rating != null &&
            rating in 0.0..10.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = if (editingProgram == null) "Agregar programa" else "Editar programa",
                subtitle = "Completa los datos principales. Los campos de temporada, episodio y año son opcionales."
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter(Char::isDigit) },
                    label = { Text("Duración") },
                    suffix = { Text("min") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = ratingText,
                    onValueChange = { ratingText = it },
                    label = { Text("Rating") },
                    suffix = { Text("/10") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categoría", fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Category.entries.take(3).forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item.getDisplayName()) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Category.entries.drop(3).take(3).forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item.getDisplayName()) }
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Transmisión en vivo", fontWeight = FontWeight.Bold)

                    Text(
                        text = "Activa esta opción si el programa está al aire.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isLive,
                    onCheckedChange = { isLive = it }
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = seasonText,
                    onValueChange = { seasonText = it.filter(Char::isDigit) },
                    label = { Text("Temporada") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = episodeText,
                    onValueChange = { episodeText = it.filter(Char::isDigit) },
                    label = { Text("Episodio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        item {
            OutlinedTextField(
                value = yearText,
                onValueChange = { yearText = it.filter(Char::isDigit) },
                label = { Text("Año de estreno") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    enabled = isValid,
                    onClick = {
                        val program = Program(
                            id = editingProgram?.id ?: java.util.UUID.randomUUID().toString(),
                            title = title.trim(),
                            description = description.trim(),
                            category = category,
                            duration = duration ?: 0,
                            rating = rating ?: 0.0,
                            imageUrl = editingProgram?.imageUrl,
                            isLive = isLive,
                            season = seasonText.toIntOrNull(),
                            episode = episodeText.toIntOrNull(),
                            year = yearText.toIntOrNull()
                        )

                        onSave(program)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

private fun TeleCatalogUiState.findSchedule(programId: String): Schedule? {
    return schedules.firstOrNull { it.programId == programId }
}

private fun TeleCatalogUiState.findChannelByProgram(programId: String): Channel? {
    val schedule = findSchedule(programId)
    return channels.firstOrNull { it.id == schedule?.channelId }
}

