---
name: android-presentation-mvi
description: |
  MVI presentation layer for Android/KMP - State, Action/ViewIntent, Event/ViewSideEffect, ViewModel, Screen/Content composable split, UI models, UiText error mapping, and process death with SavedStateHandle. Use this skill whenever creating or reviewing a ViewModel, defining screen state, actions, or events, structuring composables, mapping errors to UI strings, or handling process death. Trigger on phrases like "add a ViewModel", "create a screen", "MVI", "state", "ViewIntent", "Action", "ViewSideEffect", "Event", "screen composable", "UiText", "SavedStateHandle", "ObserveAsEvents", "UI model", "hiltViewModel", "koinViewModel", "BaseViewModel".
---

# Android / KMP Presentation Layer (MVI)

## DI Rule
Before writing any code, check the project dependencies:
- Found `hilt` → use `@HiltViewModel`, `hiltViewModel()`
- Found `koin` → use `koinViewModel()`
- Not found → ask the user

## Naming Rule (Action / ViewIntent)
Check the project for existing ViewModel base classes:
- Found `ViewIntent` or `BaseViewModel<S, I, E>` → use `ViewIntent`
- Found `Action` pattern → use `Action`
- Not found → ask the user

## Naming Rule (Event / ViewSideEffect)
Check the project for existing patterns:
- Found `ViewSideEffect` → use `ViewSideEffect`
- Found `Event` → use `Event`
- Not found → ask the user

---

## State

Choose the State structure based on the screen's UI:

### Data class — states coexist (forms, pull-to-refresh)
```kotlin
data class NoteEditorState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false,
    val titleError: UiText? = null
)
```

### Sealed — screen fully changes between phases
```kotlin
sealed interface NoteListState {
    data object Loading : NoteListState
    data class Content(val notes: List<NoteUi>) : NoteListState
    data class Error(val message: UiText) : NoteListState
}
```

### Hybrid — exclusive phases + combined fields inside
```kotlin
sealed interface NoteListState {
    data object Loading : NoteListState
    data class Content(
        val notes: List<NoteUi>,
        val isRefreshing: Boolean = false,
        val searchQuery: String = ""
    ) : NoteListState
    data class Error(val message: UiText) : NoteListState
}
```

**Decision rule:** Look at the UI — does the screen fully change between states?
- Yes → sealed
- No → data class
- First load changes fully, then inside one state there are variations → hybrid

Always update state with `.update { }` — never replace the entire flow:
```kotlin
_state.update { it.copy(isLoading = true) }
```

---

## Action / ViewIntent

```kotlin
sealed interface NoteListIntent {
    data object OnRefreshClick : NoteListIntent
    data class OnNoteClick(val noteId: String) : NoteListIntent
    data class OnDeleteNote(val noteId: String) : NoteListIntent
}
```

---

## Event / ViewSideEffect

```kotlin
sealed interface NoteListSideEffect {
    data class NavigateToDetail(val noteId: String) : NoteListSideEffect
    data class ShowSnackbar(val message: UiText) : NoteListSideEffect
}
```

---

## ViewModel

```kotlin
@HiltViewModel // or no annotation for Koin
class NoteListViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<NoteListSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    fun onIntent(intent: NoteListIntent) {
        when (intent) {
            is NoteListIntent.OnRefreshClick -> loadNotes()
            is NoteListIntent.OnNoteClick -> {
                viewModelScope.launch {
                    _sideEffects.send(NoteListSideEffect.NavigateToDetail(intent.noteId))
                }
            }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            noteRepository.getNotes()
                .onSuccess { notes ->
                    _state.update { it.copy(notes = notes.map { it.toNoteUi() }, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _sideEffects.send(NoteListSideEffect.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
```

---

## Coroutine Dispatchers

Do not inject unless the class is unit-tested and dispatches to a non-main dispatcher.

```kotlin
suspend fun compressImage(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
    // blocking compression logic
}
```

Only inject `CoroutineDispatcher` when:
1. The class dispatches to a non-main dispatcher (e.g., `IO`), AND
2. That class is directly unit-tested.

---

## Mapping Errors to UI Strings

`UiText` wraps strings from string resources or dynamic values:

```kotlin
sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class StringResource(val id: Int, val args: Array<Any> = emptyArray()) : UiText
}
```

- Use `UiText` for error messages that map to `R.string.*`
- Use plain `String` for always-dynamic values (user name, formatted date)

```kotlin
data class NoteListState(val error: UiText? = null)
data class NoteUi(val authorName: String, val formattedDate: String)
```

---

## UI Model

```kotlin
data class NoteUi(
    val id: String,
    val title: String,
    val formattedDate: String
)

fun Note.toNoteUi(): NoteUi = NoteUi(
    id = id,
    title = title,
    formattedDate = date.format(...)
)
```

UI models are always suffixed with `Ui`.

---

## Composable Structure

### Screen composable (*Screen.kt) — stateful
Receives ViewModel (via `hiltViewModel()` or `koinViewModel()`), observes side effects, passes state and `onIntent` down.

### Content composable (*Content.kt) — stateless
Receives only `state` and `onIntent`. No ViewModel reference. Can be previewed independently.

```kotlin
// NoteListScreen.kt — stateful
@Composable
fun NoteListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel() // or koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.sideEffects) { effect ->
        when (effect) {
            is NoteListSideEffect.NavigateToDetail -> onNavigateToDetail(effect.noteId)
            is NoteListSideEffect.ShowSnackbar -> { /* show snackbar */ }
        }
    }

    NoteListContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}
```

```kotlin
// NoteListContent.kt — stateless
@Composable
fun NoteListContent(
    state: NoteListState,
    onIntent: (NoteListIntent) -> Unit
) { ... }

@Preview
@Composable
private fun NoteListContentPreview() {
    AppTheme {
        NoteListContent(state = NoteListState(), onIntent = {})
    }
}
```

### Component Decomposition Rules
- SRP: one logical role per composable file
- Reusable components → `:core` module, grouped by type (`buttons/`, `inputs/`, `cards/`)
- Feature components → always in `feature/ui/<screen>/components/` subpackage — no exceptions, regardless of count

---

## Process Death

Use `SavedStateHandle` only for forms with user input:

```kotlin
@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        NoteEditorState(
            title = savedStateHandle["title"] ?: "",
            body = savedStateHandle["body"] ?: ""
        )
    )

    fun onIntent(intent: NoteEditorIntent) {
        when (intent) {
            is NoteEditorIntent.OnTitleChange -> {
                savedStateHandle["title"] = intent.title
                _state.update { it.copy(title = intent.title) }
            }
        }
    }
}
```

Only save what truly matters after process death — not the entire state.

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| ViewModel | `<Screen>ViewModel` | `NoteListViewModel` |
| State | `<Screen>State` | `NoteListState` |
| Action/Intent | `<Screen>Intent` or `<Screen>Action` | `NoteListIntent` |
| Event/SideEffect | `<Screen>SideEffect` or `<Screen>Event` | `NoteListSideEffect` |
| Screen composable | `<Screen>Screen` (stateful) | `NoteListScreen` |
| Content composable | `<Screen>Content` (stateless) | `NoteListContent` |
| UI model | `<Model>Ui` | `NoteUi` |

---

## Checklist: Adding a New Screen

- [ ] Detect DI framework (Hilt/Koin) and naming conventions (Intent/Action, SideEffect/Event) from project
- [ ] Choose State type: data class / sealed / hybrid based on UI
- [ ] Define `State`, `Intent/Action`, `SideEffect/Event`
- [ ] Implement `ViewModel`
- [ ] Create `<Screen>Screen.kt` (stateful — holds ViewModel, observes side effects)
- [ ] Create `<Screen>Content.kt` (stateless — pure state + onIntent, previewable)
- [ ] Decompose components by SRP, group by screen → section
- [ ] Map domain errors to `UiText`
- [ ] Add `SavedStateHandle` for form fields that must survive process death
