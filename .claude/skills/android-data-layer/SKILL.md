---
name: android-data-layer
description: |
  Data layer patterns for Android/KMP - data sources, repositories, DTOs, mappers, Room entities, Firebase Firestore, safe call helpers, token storage, and offline-first. Use this skill whenever writing or reviewing a data source or repository, creating DTOs or Room entities, writing mappers, setting up Firebase Firestore or Ktor HttpClient, handling network errors, or implementing token refresh. Trigger on phrases like "create a repository", "create a data source", "add a DAO", "Firestore", "Ktor client", "write a mapper", "DTO", "NoteDocument", "Room entity", "network call", "token storage", or "offline-first".
---

# Android / KMP Data Layer

## Error Handling

This skill uses `Result<T, E>`, `DataError`, and the extension helpers defined in the **android-error-handling** skill.

---

## Data Source vs Repository

- **Data source** — accesses a single data source (local DB, remote API, Firestore). Most classes are data sources.
- **Repository** — coordinates multiple data sources. Only use "repository" when the class genuinely coordinates multiple sources.

A project can have multiple remote data sources simultaneously (e.g., Firestore + REST API server). Each is a separate data source class.

```kotlin
// Single source → data source
interface NoteLocalDataSource {
    suspend fun getNotes(): Result<List<Note>, DataError.Local>
    suspend fun insertNote(note: Note): EmptyResult<DataError.Local>
}

interface NoteFirestoreDataSource {
    suspend fun fetchNotes(): Result<List<Note>, DataError.Firestore>
    fun observeNotes(): Flow<List<Note>>
}

interface NoteApiDataSource {
    suspend fun fetchNotes(): Result<List<Note>, DataError.Network>
}

// Multiple sources → repository
interface NoteRepository {
    suspend fun getNotes(): Result<List<Note>, DataError>
    fun observeNotes(): Flow<List<Note>>
    suspend fun sync(): EmptyResult<DataError>
}
```

---

## Domain Layer Contracts

- Pure Kotlin — no Android/framework imports
- Contains: domain models, data source/repository **interfaces**, error types
- **Every data source or repository used by a ViewModel must have an interface in `domain`**

---

## DTOs and Domain Models

Always separate: DTOs (data layer) ↔ Domain Models (domain layer).

| Source | DTO type | Naming |
|---|---|---|
| Firestore | Firestore document | `NoteDocument` |
| Room | Room entity | `NoteEntity` |
| REST API | Data Transfer Object | `NoteDto` |

```kotlin
// Firestore document — default values required for Firestore deserialization
data class NoteDocument(
    val id: String = "",
    val title: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

// Room entity
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long
)
```

Mappers are simple extension functions in the data layer:

```kotlin
fun NoteDocument.toNote(): Note = Note(id = id, title = title)
fun Note.toDocument(): NoteDocument = NoteDocument(id = id, title = title)
fun NoteEntity.toNote(): Note = Note(id = id, title = title)
fun Note.toEntity(): NoteEntity = NoteEntity(id = id, title = title)
```

---

## Implementations

Name implementations for what makes them unique — never suffix with `Impl`.

### Firestore data source
```kotlin
class FirestoreNoteDataSource(
    private val firestore: FirebaseFirestore
) : NoteFirestoreDataSource {

    override suspend fun fetchNotes(): Result<List<Note>, DataError.Firestore> {
        return firestoreSafeCall {
            firestore.collection("notes")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(NoteDocument::class.java)?.toNote() }
        }
    }

    // Realtime updates via callbackFlow
    override fun observeNotes(): Flow<List<Note>> = callbackFlow {
        val listener = firestore.collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val notes = snapshot?.documents
                    ?.mapNotNull { it.toObject(NoteDocument::class.java)?.toNote() }
                    ?: emptyList()
                trySend(notes)
            }
        awaitClose { listener.remove() }
    }
}
```

### Room data source
```kotlin
class RoomNoteDataSource(private val dao: NoteDao) : NoteLocalDataSource {
    override suspend fun getNotes(): Result<List<Note>, DataError.Local> {
        return try {
            Result.Success(dao.getAllNotes().map { it.toNote() })
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
```

### Repository (multiple sources)
```kotlin
class OfflineFirstNoteRepository(
    private val localDataSource: NoteLocalDataSource,
    private val firestoreDataSource: NoteFirestoreDataSource
) : NoteRepository {

    // ViewModel always reads from Room (single source of truth)
    override fun observeNotes(): Flow<List<Note>> =
        localDataSource.observeNotes()

    // Sync runs separately (WorkManager or manual trigger)
    override suspend fun sync(): EmptyResult<DataError> {
        return firestoreDataSource.fetchNotes()
            .onSuccess { notes ->
                localDataSource.deleteAll()
                localDataSource.insertAll(notes)
            }
    }
}
```

---

## Token Storage (Universal)

Agent detects auth mechanism from project dependencies:

**Firebase Auth** (found `firebase-auth`) → tokens managed automatically by SDK:
```kotlin
val currentUser = FirebaseAuth.getInstance().currentUser

fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.getInstance()
        .addAuthStateListener { auth -> trySend(auth.currentUser) }
    awaitClose { FirebaseAuth.getInstance().removeAuthStateListener(listener) }
}
```

**REST API with JWT** (found `ktor` or `retrofit`) → store tokens manually in DataStore:
```kotlin
class TokenStorage(private val dataStore: DataStore<Preferences>) {
    suspend fun saveTokens(access: String, refresh: String) { ... }
    suspend fun getAccessToken(): String? { ... }
    suspend fun getRefreshToken(): String? { ... }
}
```

**Multiple sources** → Firebase manages its tokens, DataStore for REST separately.

**If unclear** → ask the user which auth mechanism is used.

---

## Room Migrations

Prefer `AutoMigration` for simple schema changes:
```kotlin
@Database(
    entities = [NoteEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase()
```

Use manual `Migration` when:
- Renaming a table or column
- Changing a column type
- Merging or splitting tables
- Any complex data transformation

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes RENAME TO employee_notes")
    }
}
```

Rule: Added/removed field → AutoMigration. Renamed/changed existing → manual Migration.

---

## Offline-First (Universal)

Agent detects the project's sync strategy:

**Firebase Firestore + WorkManager sync:**
```kotlin
// Room as single source of truth
override fun observeNotes(): Flow<List<Note>> = localDataSource.observeNotes()

override suspend fun sync(): EmptyResult<DataError> {
    return firestoreDataSource.fetchNotes()
        .onSuccess { notes -> localDataSource.insertAll(notes) }
}
```

**Firebase Firestore realtime (onSnapshot):**
```kotlin
firestoreDataSource.observeNotes()
    .onEach { notes -> localDataSource.insertAll(notes) }
    .launchIn(scope)
```

**REST API:**
```kotlin
override suspend fun sync(): EmptyResult<DataError> {
    return apiDataSource.fetchNotes()
        .onSuccess { notes -> localDataSource.insertAll(notes) }
}
```

**No offline support needed** → Repository reads directly from remote, Room not used.

**Multiple remote sources** → Repository decides priority and sync strategy. Agent asks which source is primary if unclear.

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Data source interface | `<Entity><Local/Remote/Firestore/Api>DataSource` | `NoteLocalDataSource`, `NoteFirestoreDataSource` |
| Data source impl | describes what it wraps | `RoomNoteDataSource`, `FirestoreNoteDataSource`, `ApiNoteDataSource` |
| Repository interface | `<Entity>Repository` (multi-source only) | `NoteRepository` |
| Repository impl | describes behavior | `OfflineFirstNoteRepository` |
| Firestore document | `<Model>Document` | `NoteDocument` |
| Room entity | `<Model>Entity` | `NoteEntity` |
| REST DTO | `<Model>Dto` | `NoteDto` |
| Mapper | extension fun on source type | `fun NoteDocument.toNote()` |

---

## Checklist: Adding a New Data Source or Repository

- [ ] Define domain model(s) in `:domain`
- [ ] For each data source — define a separate interface in `:domain`
- [ ] If multiple sources — define `Repository` interface in `:domain` as coordinator
- [ ] Define error types per source in `:domain` (`DataError.Local`, `DataError.Firestore`, `DataError.Network`)
- [ ] For each source define the appropriate DTO in `:data`:
  - Firestore → `NoteDocument`
  - Room → `NoteEntity`
  - REST API → `NoteDto`
- [ ] Write mappers for each DTO → Domain model
- [ ] Implement each data source as a separate class named for its source
- [ ] If multiple remote sources — implement Repository as coordinator, define sync strategy
- [ ] If offline-first — Room as single source of truth, ViewModel reads only from Room
