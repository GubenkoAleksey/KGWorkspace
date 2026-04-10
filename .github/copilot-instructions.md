# Copilot Instructions — FirestoreApp

This is an Android project using **Clean Architecture**, **MVI**, **Hilt**, **Jetpack Compose**, and **Firebase Firestore**.

---

## ⚠️ Refactoring Plan

**Before starting any refactoring work, read [`REFACTORING_PLAN.md`](../REFACTORING_PLAN.md).**
It contains the full analysis of current violations, a step-by-step checklist with `[ ]`/`[x]` progress tracking, and the recommended execution order. Continue from the first unchecked item.

---

## How to use these instructions

1. **Always-apply rules** (section below) — apply to every response without exception.
2. **Skill files** — detailed patterns live in `.claude/skills/`. Before writing or reviewing code in a domain listed below, **read the corresponding SKILL.md first** and follow it precisely.

| Domain | Skill file to read |
|---|---|
| Compose UI (composables, previews, animations, modifiers) | `.claude/skills/android-compose-ui/SKILL.md` |
| Data layer (DataSource, Repository, DTOs, mappers, Firestore, Room) | `.claude/skills/android-data-layer/SKILL.md` |
| Dependency injection (Hilt modules, scoping, wiring) | `.claude/skills/android-di/SKILL.md` |
| Error handling (`Result`, `DataError`, safe calls) | `.claude/skills/android-error-handling/SKILL.md` |
| Module structure (Gradle, convention plugins, where code lives) | `.claude/skills/android-module-structure/SKILL.md` |
| Navigation (routes, nav graphs, cross-feature callbacks) | `.claude/skills/android-navigation/SKILL.md` |
| Presentation / MVI (State, Intent, SideEffect, ViewModel, Screen/Content split) | `.claude/skills/android-presentation-mvi/SKILL.md` |
| Testing (JUnit5, Turbine, AssertK, fakes, Robot Pattern) | `.claude/skills/android-testing/SKILL.md` |

---

## Always-apply rules

### Architecture
- Flat modules: `:app`, `:core`, `:domain`, `:data`, `:feature:auth`, `:feature:home`, `:feature:admin`, `:feature:status`
- `domain` → `core` only. `data` → `domain`, `core`. `presentation` → `domain`, `core`. Features never depend on each other.
- All versions in `libs.versions.toml` — no hardcoded versions in build files.

### MVI
- State: `data class` / `sealed interface` / hybrid — choose based on whether states coexist or are exclusive.
- `sealed interface <Screen>Intent` dispatched via `onIntent()`.
- `sealed interface <Screen>SideEffect` via `Channel`.
- `@HiltViewModel` + `MutableStateFlow` + `_state.update { }`.
- `*Screen.kt` stateful (holds ViewModel) · `*Content.kt` stateless (state + onIntent, previewable).
- UI models suffixed `Ui` (e.g. `NoteUi`), mapped in ViewModel.
- `UiText.StringResource` / `UiText.DynamicString` for error strings.

### Compose
- Zero business logic in composables — render state, forward intents only.
- `collectAsStateWithLifecycle()` for state collection.
- `@Stable` only when class has unstable fields (`List`, `Map`, interfaces).
- Lazy lists: `key = { it.id }` always when unique ID exists.
- Animations: `animateFloatAsState` + `graphicsLayer { }` — never `Modifier.alpha(animated)`.
- `contentDescription` via `stringResource()` on all interactive/informational elements.

### Data layer
- One `DataSource` per backing system. No `Impl` suffix.
- `Repository` only when coordinating multiple sources.
- Interfaces in `:domain`. Implementations in `:data`.
- DTOs: `NoteDocument` (Firestore) · `NoteEntity` (Room) · `NoteDto` (REST).
- Mappers: extension functions (e.g. `fun NoteDocument.toNote()`).
- Offline-first: Room as single source of truth; ViewModel reads only from Room.

### Error handling
- `Result<T, E>` typed — never throw for expected failures.
- **Never catch `CancellationException`** — always rethrow.
- `firestoreSafeCall { }` for all Firestore calls.
- Map `DataError` → `UiText` in `:core:presentation`.

### DI (Hilt)
- One `@Module @InstallIn(SingletonComponent::class)` per feature layer.
- `@Binds` for interface→impl · `@Provides` for third-party/factories.
- `@Singleton` for repos, Firestore, DB. `hiltViewModel()` in `*Screen` composables only.

### Navigation
- `@Serializable object/data class` routes. One `NavGraphBuilder.<feature>Graph(...)` per feature.
- Cross-feature: lambda callbacks only — never import another feature's route.
- Pass IDs only; load data in destination ViewModel via `savedStateHandle.toRoute<Route>()`.

### Testing
- JUnit5 + AssertK + Turbine + `UnconfinedTestDispatcher`.
- Fakes over mocks. `SavedStateHandle(mapOf(...))` — no mocking.
- UI tests on `*Content` composables. Robot Pattern for 3+ test cases.
