# FirestoreApp — Claude Code Instructions

Android project: **Clean Architecture · MVI · Hilt · Jetpack Compose · Firebase Firestore**

---

## ⚠️ Refactoring Plan

**Before starting any refactoring work, read [`REFACTORING_PLAN.md`](./REFACTORING_PLAN.md).**
It contains the full analysis of current violations, a step-by-step checklist with `[ ]`/`[x]` progress tracking, and the recommended execution order. Continue from the first unchecked item.

---

## Skill Auto-Activation

Before writing or reviewing code in these domains, invoke the corresponding skill:

| Domain | Skill | Trigger keywords |
|---|---|---|
| Compose UI — composables, previews, animations, modifiers | `android-compose-ui` | composable, LazyColumn, animation, Modifier, preview, recomposition |
| MVI presentation — State, Intent, SideEffect, ViewModel | `android-presentation-mvi` | ViewModel, screen, state, intent, action, sideEffect, event, MVI |
| Data layer — DataSource, Repository, DTOs, Firestore, Room | `android-data-layer` | repository, dataSource, DTO, Firestore, Room, mapper, NoteDocument |
| Dependency injection — Hilt modules, scoping, wiring | `android-di` | Hilt, @Module, @Inject, @Provides, @Binds, hiltViewModel |
| Error handling — Result, DataError, safe calls | `android-error-handling` | Result, DataError, onSuccess, onFailure, EmptyResult, error type |
| Module structure — Gradle, convention plugins, where code lives | `android-module-structure` | add module, project structure, build.gradle, where does X live |
| Navigation — routes, nav graphs, cross-feature callbacks | `android-navigation` | navigation, route, NavController, nav graph, navigate between |
| Testing — JUnit5, Turbine, fakes, Robot Pattern | `android-testing` | test, ViewModel test, fake repository, Turbine, Robot Pattern |

---

## Always-Apply Rules

### Architecture
- Flat modules: `:app` · `:core` · `:domain` · `:data` · `:feature:auth` · `:feature:home` · `:feature:admin` · `:feature:status`
- Dependency direction: `presentation` → `domain` ← `data`. Domain has no Android imports.
- Features **never depend on each other**. Shared code goes to `:core`.
- All versions in `libs.versions.toml` — no hardcoded versions.

### MVI (use `android-presentation-mvi` for full patterns)
- `*Screen.kt` — stateful, holds ViewModel via `hiltViewModel()`, observes side effects.
- `*Content.kt` — stateless, receives only `state` + `onIntent`, previewable.
- State: `data class` (coexisting fields) / `sealed interface` (exclusive phases) / hybrid.
- `_state.update { it.copy(...) }` — never replace the entire flow.
- Side effects via `Channel<SideEffect>`, observed with `ObserveAsEvents`.
- UI models suffixed `Ui` (e.g. `NoteUi`), mapped in ViewModel.
- `UiText.StringResource` / `UiText.DynamicString` for all user-visible error strings.

### Compose (use `android-compose-ui` for full patterns)
- Zero business logic in composables — render state, forward intents only.
- `collectAsStateWithLifecycle()` for state collection.
- `@Stable` only when class has unstable fields (`List`, `Map`, interfaces).
- Lazy lists: `key = { it.id }` always when unique ID exists.
- Animations: `animateFloatAsState` + `graphicsLayer { }` — never `Modifier.alpha(animated)`.
- `contentDescription` via `stringResource()` on all interactive/informational elements.

### Data Layer (use `android-data-layer` for full patterns)
- One `DataSource` per backing system. No `Impl` suffix — name for what makes it unique.
- `Repository` only when coordinating multiple sources.
- Firestore docs: `NoteDocument` (default values required). Room: `NoteEntity`. REST: `NoteDto`.
- Mappers as extension functions: `fun NoteDocument.toNote()`.
- Domain interfaces for every DataSource/Repository used by a ViewModel.
- Never catch `CancellationException` — always rethrow it.

### Error Handling (use `android-error-handling` for full patterns)
- `Result<T, E : Error>` / `EmptyResult<E>` — never throw for expected failures.
- `DataError.Firestore` / `DataError.Network` / `DataError.Local` per source.
- Feature errors implement `Error` directly (e.g. `PasswordValidationError`).
- Use `firestoreSafeCall { }` wrapper for all Firestore calls.
- `DataError.toUiText()` in `:core:presentation`, feature errors in `feature:presentation`.

### DI — Hilt (use `android-di` for full patterns)
- Detect DI framework from `build.gradle.kts` before writing any DI code.
- `@Binds` for interface→implementation. `@Provides` for factory/third-party.
- `@Singleton` for repositories, Firestore, DB. `@HiltViewModel` for ViewModels.
- Inject ViewModels only in `*Screen` composables — never pass down the tree.

### Navigation (use `android-navigation` for full patterns)
- `@Serializable` route objects per screen in `feature:presentation`.
- One `NavGraphBuilder` extension function per feature, assembled in `:app`.
- Cross-feature navigation via lambda callbacks — never import routes from other features.
- Pass only IDs via routes — load data in destination ViewModel.

### Testing (use `android-testing` for full patterns)
- JUnit5 + AssertK + Turbine + `UnconfinedTestDispatcher`.
- Fakes over mocks — `FakeNoteRepository` with `shouldReturnError` flag.
- `*Content` composables for UI tests (stateless, no ViewModel).
- Robot Pattern for screens with 3+ UI test cases or shared interactions.
- Inject `CoroutineDispatcher` only when class uses non-main dispatcher AND is unit-tested.