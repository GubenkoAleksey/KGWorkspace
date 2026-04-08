---
name: android-di
description: |
  Dependency injection setup for Android/KMP - Hilt for Android projects, Koin for KMP projects. Module definitions per layer, ViewModel injection, assembling modules, and injecting in composables. Use this skill whenever setting up DI, defining a module, providing a repository or ViewModel, injecting a dependency, or wiring modules. Trigger on phrases like "set up Hilt", "set up Koin", "add a DI module", "inject a dependency", "DI module", "hiltViewModel", "koinViewModel", "provide a ViewModel", "@HiltViewModel", "startKoin", "single/viewModel/factory", "@Inject", "@Provides", "@Binds".
---

# Android / KMP Dependency Injection

## DI Framework Detection

Before writing any DI code, check project dependencies:
- Found `hilt` in `build.gradle.kts` → use **Hilt** (Android only)
- Found `koin` in `build.gradle.kts` → use **Koin** (Android/KMP)
- Not found → ask the user: "Is this an Android-only or KMP project?"
  - Android only → recommend Hilt (compile-time safety)
  - KMP → recommend Koin (multiplatform support)

---

## Principles

**Hilt (Android):**
- One `@Module` per feature layer
- `@HiltAndroidApp` in Application class
- `@AndroidEntryPoint` in Activity/Fragment
- `hiltViewModel()` in `*Screen` composables

**Koin (KMP):**
- One Koin module per feature layer — create only if there are dependencies to provide
- Modules assembled in `:app`, never in feature modules themselves
- `koinViewModel()` in `*Screen` composables

---

## Module Definitions

### Hilt — Data layer
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class NoteDataModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        impl: OfflineFirstNoteRepository
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindNoteLocalDataSource(
        impl: RoomNoteDataSource
    ): NoteLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore =
            FirebaseFirestore.getInstance()

        @Provides
        @Singleton
        fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    }
}
```

### Hilt — Presentation layer
```kotlin
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() { ... }
```

### Koin — Data layer
```kotlin
val noteDataModule = module {
    singleOf(::OfflineFirstNoteRepository) { bind<NoteRepository>() }
    singleOf(::RoomNoteDataSource) { bind<NoteLocalDataSource>() }
    single { FirebaseFirestore.getInstance() }
}
```

### Koin — Presentation layer
```kotlin
val notePresentationModule = module {
    viewModelOf(::NoteListViewModel)
    viewModelOf(::NoteDetailViewModel)
}
```

### Core data module
```kotlin
// Hilt
@Module
@InstallIn(SingletonComponent::class)
object CoreDataModule {
    @Provides @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()
}

// Koin
val coreDataModule = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app.db").build() }
    single { createDataStore(get()) }
}
```

---

## Assembly in `:app`

### Hilt
```kotlin
@HiltAndroidApp
class App : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

Hilt automatically discovers all `@Module` classes via KSP — no manual registration needed.

### Koin
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                coreDataModule,
                noteDataModule,
                notePresentationModule,
                authDataModule,
                authPresentationModule
            )
        }
    }
}
```

Koin requires explicit registration of all modules in `:app`.

---

## Injecting in Composables

Always inject ViewModels in `*Screen` composables (stateful). Never pass ViewModels down the composable tree.

```kotlin
// Hilt
@Composable
fun NoteListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) { ... }

// Koin
@Composable
fun NoteListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: NoteListViewModel = koinViewModel()
) { ... }
```

---

## Scoping Rules

### Hilt

| Scope | Annotation | When to use |
|---|---|---|
| Singleton | `@Singleton` | One instance for app lifetime (repositories, Firestore, DB) |
| ViewModel | `@HiltViewModel` | ViewModel scoped to lifecycle |
| Activity | `@ActivityScoped` | One instance per Activity (rare) |

Use `@Binds` for interface → implementation binding (abstract module method).
Use `@Provides` for factory methods, third-party classes, or complex construction.

### Koin

| Scope | Form | When to use |
|---|---|---|
| Singleton | `singleOf(::Impl) { bind<Interface>() }` | One instance for app lifetime |
| ViewModel | `viewModelOf(::VM)` | ViewModel scoped to lifecycle |
| Factory | `factoryOf(::Impl)` | New instance on every injection (rare) |

Prefer `*Of` constructor-reference forms. Use lambda forms only when constructor injection is insufficient (factory methods, named qualifiers).

---

## Naming Conventions

### Hilt
| Thing | Convention | Example |
|---|---|---|
| Hilt module | `<Feature><Layer>Module` | `NoteDataModule`, `NotePresentationModule` |
| ViewModel | `@HiltViewModel` annotation | `@HiltViewModel class NoteListViewModel` |

### Koin
| Thing | Convention | Example |
|---|---|---|
| Koin module | `<feature><Layer>Module` | `noteDataModule`, `notePresentationModule` |

---

## Checklist: Adding DI for a New Feature

### Hilt
- [ ] Define `@Module @InstallIn(SingletonComponent::class)` for data layer
- [ ] Use `@Binds` for interface → implementation binding
- [ ] Use `@Provides` for factory methods (Firestore, Room, etc.)
- [ ] Add `@HiltViewModel` + `@Inject constructor` to each ViewModel
- [ ] Use `hiltViewModel()` in `*Screen` composables

### Koin
- [ ] Define `val <feature>DataModule = module { ... }` in `feature:data`
- [ ] Define `val <feature>PresentationModule = module { ... }` in `feature:presentation`
- [ ] Register both modules in `:app`'s `startKoin { modules(...) }`
- [ ] Use `koinViewModel()` in `*Screen` composables
