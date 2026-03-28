# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew build

# Test
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
./gradlew :<module>:test          # Single module unit tests

# Lint
./gradlew lint

# Clean
./gradlew clean
```

On Windows use `gradlew.bat` instead of `./gradlew`.

After any code change, verify the build compiles: `.\gradlew.bat build`

## Architecture

**Clean Architecture + MVI with Unidirectional Data Flow**

Employee status tracking app with Firebase backend. Employees submit daily status updates; admins manage employees and configure reminder notifications.

### Module Layout

| Module | Purpose |
|---|---|
| `:app` | NavHost, Hilt `@HiltAndroidApp`, Activity |
| `:core` | Shared theme, base MVI classes, reusable UI components |
| `:domain` | Pure Kotlin — models, use cases, repository interfaces |
| `:data` | Repository implementations, Firebase Firestore + Room sync, mappers, WorkManager |
| `:feature:auth` | Login/signup screens |
| `:feature:home` | Home/dashboard |
| `:feature:status` | Employee status submission |
| `:feature:admin` | Admin panel, reminder settings |

Root package: `com.hubenko`; features under `com.hubenko.feature.{featurename}`.

### MVI Data Flow

```
*Screen.kt (Stateful)
  — collects StateFlow<ViewState> from ViewModel
  — handles SideEffect (navigation, toasts)
  — passes ViewIntent down
*Content.kt (Stateless)
  — receives state + intent lambdas only
  — no ViewModel reference
ViewModel : BaseViewModel<S, I, E>
  — processes intents, emits new state
  — emits one-shot SideEffects via Channel
```

`Screen` and `Content` files are **always** in separate files — never combine them.

### Layer Dependencies

`:feature:*` → `:domain` ← `:data`
`:feature:*` → `:core`
`:app` → all modules
`:domain` has no Android dependencies.

### Key Classes

- `BaseViewModel<S: ViewState, I: ViewIntent, E: ViewSideEffect>` — `core/base/BaseViewModel.kt`
- `AppDatabase` — Room DB with `EmployeeEntity`, `EmployeeStatusEntity`, `ReminderSettingsEntity`
- `DataModule` — Hilt bindings for all repositories
- `MainActivity` — single activity, hosts Compose `NavHost`
- `ReminderReceiver` + `BootReceiver` — AlarmManager-based reminders
- `SyncWorker` — WorkManager background Firestore sync

## UI Component Rules

1. **One file per component** — no god-files. Each independent UI element gets its own file under `ui/components/`.
2. **Use `:core` base components** (`AppTopBar`, `AppTextField`, `PrimaryActionButton`) instead of raw Material components.
3. **Every composable must have `@Preview`** covering multiple states (Loading, Error, Empty, Content).
4. **Semantic naming** — name by business role (`AddEmployeeButton`), not visual style (`BlueBorderButton`).
5. **Feature-level shared components** go in `feature:*/ui/components/`; app-wide components go in `:core`.

## Tech Stack

- Kotlin 2.2.10, Compose BOM 2024.09.00, Material3
- Dagger Hilt 2.59.2 (KSP)
- Firebase BOM 34.10.0 (Firestore + Auth)
- Room 2.7.0
- Coroutines + StateFlow + Channel
- WorkManager 2.10.0
- Navigation Compose
- compileSdk/targetSdk 36, minSdk 24, Java 11
