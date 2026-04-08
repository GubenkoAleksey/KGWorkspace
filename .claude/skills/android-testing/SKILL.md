---
name: android-testing
description: |
  Testing patterns for Android/KMP - ViewModel unit tests with JUnit5, Turbine, AssertK, UnconfinedTestDispatcher, fake repositories, SavedStateHandle, Robot Pattern, and Compose UI tests. Use this skill whenever writing or reviewing tests for ViewModels, repositories, use cases, or Compose screens. Trigger on phrases like "write a test", "unit test the ViewModel", "test a repository", "Turbine", "fake repository", "UnconfinedTestDispatcher", "runTest", "ComposeTestRule", "JUnit5", or "Robot Pattern".
---

# Android / KMP Testing

## Stack

| Concern | Library |
|---|---|
| Test framework | JUnit5 |
| Assertions | AssertK |
| Flow / StateFlow testing | Turbine |
| Coroutine testing | `kotlinx-coroutines-test` + `UnconfinedTestDispatcher` |
| UI testing | `ComposeTestRule` |

**Why this stack:**
- **JUnit5** — `@BeforeEach/@AfterEach`, parametrized tests, better Kotlin integration
- **AssertK** — readable assertions with clear failure messages (`assertThat(x).isTrue()`)
- **Turbine** — synchronous-style API for testing async Flow/StateFlow emissions
- **UnconfinedTestDispatcher** — replaces Main dispatcher in tests, runs coroutines synchronously
- **ComposeTestRule** — renders Compose UI in tests, find and assert on nodes

---

## ViewModel Unit Tests

Agent detects naming conventions from project (Intent/Action, SideEffect/Event):

```kotlin
class NoteListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() { Dispatchers.setMain(testDispatcher) }

    @AfterEach
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `loading notes emits correct state`() = runTest {
        val viewModel = NoteListViewModel(FakeNoteRepository())

        viewModel.state.test {
            viewModel.onIntent(NoteListIntent.OnRefreshClick) // or onAction
            assertThat(awaitItem().isLoading).isTrue()
            assertThat(awaitItem().notes).isNotEmpty()
        }
    }

    @Test
    fun `clicking note sends NavigateToDetail side effect`() = runTest {
        val viewModel = NoteListViewModel(FakeNoteRepository())

        viewModel.sideEffects.test { // or .events
            viewModel.onIntent(NoteListIntent.OnNoteClick("123"))
            assertThat(awaitItem())
                .isEqualTo(NoteListSideEffect.NavigateToDetail("123"))
        }
    }
}
```

---

## Fake Repositories

Prefer **fakes** over mocks. A fake is a simple in-memory implementation:

```kotlin
class FakeNoteRepository : NoteRepository {
    private val notes = mutableListOf<Note>()
    var shouldReturnError = false

    override suspend fun getNotes(): Result<List<Note>, DataError> {
        return if (shouldReturnError) Result.Error(DataError.Firestore.UNKNOWN)
        else Result.Success(notes.toList())
    }

    override suspend fun insertNote(note: Note): EmptyResult<DataError> {
        notes.add(note)
        return Result.Success(Unit)
    }
}
```

**Why fakes over mocks:**
- Mocks verify HOW a method is called — brittle tests
- Fakes verify WHAT is returned — more realistic tests
- Fakes are simpler to read and maintain

---

## SavedStateHandle in Tests

Instantiate directly — no mocking needed:

```kotlin
val savedStateHandle = SavedStateHandle(mapOf("noteId" to "123"))
val viewModel = NoteDetailViewModel(savedStateHandle, FakeNoteRepository())
```

---

## When to Inject Dispatchers

Only inject `CoroutineDispatcher` when:
1. The class dispatches to a non-main dispatcher (e.g., `IO`), AND
2. That class is directly unit-tested.

ViewModels using only `viewModelScope` don't need injected dispatchers:

```kotlin
// FirestoreSyncHelper uses withContext(IO) and is unit-tested → inject dispatcher
class FirestoreSyncHelper(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun sync() = withContext(ioDispatcher) { ... }
}

// In test:
val helper = FirestoreSyncHelper(ioDispatcher = UnconfinedTestDispatcher())
```

---

## Integration and E2E Tests

Use `*Content` composables (stateless) for UI tests — they accept pure state with no ViewModel dependency:

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun noteList_displaysNotes_afterLoad() {
    composeTestRule.setContent {
        NoteListContent( // *Content — stateless, no ViewModel
            state = NoteListState.Content(
                notes = listOf(NoteUi("1", "Hello", "Mar 15"))
            ),
            onIntent = {}
        )
    }
    composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
}
```

---

## Robot Pattern

Use Robot Pattern for complex UI tests with 3+ test cases or shared interaction sequences. Robots encapsulate all `composeTestRule` interactions, keeping tests readable and DRY.

```kotlin
// Robot — owns all UI interactions for the screen
class NoteListRobot(private val composeTestRule: ComposeContentTestRule) {

    fun setContent(
        state: NoteListState,
        onIntent: (NoteListIntent) -> Unit = {} // or onAction
    ) = apply {
        composeTestRule.setContent {
            NoteListContent(state = state, onIntent = onIntent) // *Content
        }
    }

    fun assertNoteVisible(title: String) = apply {
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    fun clickNote(title: String) = apply {
        composeTestRule.onNodeWithText(title).performClick()
    }

    fun assertEmptyState() = apply {
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }
}

// Test reads like a user scenario
class NoteListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { NoteListRobot(composeTestRule) }

    @Test
    fun displaysNotes_afterLoad() {
        robot
            .setContent(NoteListState.Content(notes = listOf(NoteUi("1", "Hello", "Mar 15"))))
            .assertNoteVisible("Hello")
    }

    @Test
    fun showsEmptyState_whenNoNotes() {
        robot
            .setContent(NoteListState.Content(notes = emptyList()))
            .assertEmptyState()
    }
}
```

**When to use Robot Pattern:**
- Screen has 3+ UI test cases
- Multiple tests share the same setup/assertion sequences
- Testing complex multi-step user flows

---

## What to Test

- Unit-test every ViewModel and non-trivial domain/data logic
- Unit-test any logic that is likely to change
- Use fakes over mocks — fakes catch more real bugs
- Write integration tests where DB/network interactions are non-trivial
- Write E2E Compose tests for critical user flows using `*Content` composables
- Use Robot Pattern for complex UI/E2E tests with 3+ cases or shared interactions
