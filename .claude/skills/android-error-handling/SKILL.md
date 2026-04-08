---
name: android-error-handling
description: |
  Generic Result wrapper, error types, and extension helpers for Android/KMP - Result<T, E>, DataError, EmptyResult, map, onSuccess, onFailure. Use this skill whenever defining error types, creating a Result wrapper, handling success/failure flows, mapping errors, or working with typed errors anywhere in the app (not just data layer — also validation, auth, domain logic). Trigger on phrases like "Result wrapper", "error handling", "DataError", "onSuccess", "onFailure", "EmptyResult", "map result", "error type", "validation error", "typed errors", "FirebaseFirestoreException", or "safe call".
---

# Android / KMP Error Handling

## Result Wrapper (`core:domain`)

A generic, typed Result that works across all layers:

```kotlin
interface Error

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : com.example.Error>(val error: E) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
```

---

## Extension Helpers (`core:domain`)

```kotlin
inline fun <T, E : Error, R> Result<T, E>.map(
    map: (T) -> R
): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(this.data))
    }
}

inline fun <T, E : Error> Result<T, E>.onSuccess(
    action: (T) -> Unit
): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> { action(this.data); this }
    }
}

inline fun <T, E : Error> Result<T, E>.onFailure(
    action: (E) -> Unit
): Result<T, E> {
    return when (this) {
        is Result.Error -> { action(error); this }
        is Result.Success -> this
    }
}

fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> = map { }
```

All helpers return `Result` so they can be chained:
```kotlin
repository.saveNote(note)
    .onSuccess { /* update UI */ }
    .onFailure { /* show error */ }
    .asEmptyResult()
```

---

## Shared Error Types (`core:domain`)

Add only the error types that exist in the project:

```kotlin
sealed interface DataError : Error {

    // For REST API / HTTP calls (add if project uses Ktor/Retrofit)
    enum class Network : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        SERIALIZATION,
        UNKNOWN
    }

    // For Firebase Firestore (add if project uses Firestore)
    enum class Firestore : DataError {
        NOT_FOUND,
        PERMISSION_DENIED,
        UNAVAILABLE,
        UNAUTHENTICATED,
        ALREADY_EXISTS,
        UNKNOWN
    }

    // For Room / local DB
    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}
```

---

## Feature-Specific Errors

Features define their own error types by implementing `Error`:

```kotlin
enum class PasswordValidationError : Error {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_DIGIT
}

// Always a single error, not a list:
fun validatePassword(pw: String): EmptyResult<PasswordValidationError>
```

---

## Exception Handling Philosophy

Never throw exceptions for expected failures — always return `Result.Error`. Each layer catches its own exceptions:

| Exception origin | Catch in | Example |
|---|---|---|
| HTTP / network | Data layer | `UnresolvedAddressException` → `DataError.Network.NO_INTERNET` |
| Firestore | Data layer | `FirebaseFirestoreException` → `DataError.Firestore.*` |
| Database / disk | Data layer | `SQLiteFullException` → `DataError.Local.DISK_FULL` |
| Business logic | Domain layer | Invalid input → `Result.Error(ValidationError.TOO_SHORT)` |

**CRITICAL:** Never catch `CancellationException` — always rethrow it:

```kotlin
} catch (e: Exception) {
    if (e is CancellationException) throw e // always rethrow!
    Result.Error(DataError.Firestore.UNKNOWN)
}
```

`CancellationException` signals that a coroutine was intentionally cancelled (e.g., user left the screen). Catching it prevents the coroutine from stopping, causing memory leaks.

---

## Mapping Errors to UiText

- **Shared errors** (`DataError`) → `core:presentation`
- **Feature-specific errors** → `feature:presentation`
- **Internal errors** never shown to user → no `toUiText()` needed

```kotlin
// core:presentation
fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
        DataError.Firestore.PERMISSION_DENIED -> UiText.StringResource(R.string.error_permission_denied)
        DataError.Firestore.UNAVAILABLE -> UiText.StringResource(R.string.error_unavailable)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        else -> UiText.StringResource(R.string.error_unknown)
    }
}
```

---

## Safe Call Helpers (`core:data`)

### Firebase Firestore safe call
```kotlin
suspend fun <T> firestoreSafeCall(
    execute: suspend () -> T
): Result<T, DataError.Firestore> {
    return try {
        Result.Success(execute())
    } catch (e: FirebaseFirestoreException) {
        Result.Error(when (e.code) {
            FirebaseFirestoreException.Code.NOT_FOUND -> DataError.Firestore.NOT_FOUND
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> DataError.Firestore.PERMISSION_DENIED
            FirebaseFirestoreException.Code.UNAVAILABLE -> DataError.Firestore.UNAVAILABLE
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> DataError.Firestore.UNAUTHENTICATED
            FirebaseFirestoreException.Code.ALREADY_EXISTS -> DataError.Firestore.ALREADY_EXISTS
            else -> DataError.Firestore.UNKNOWN
        })
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(DataError.Firestore.UNKNOWN)
    }
}
```

### REST API safe call (Ktor)
```kotlin
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        return Result.Error(DataError.Network.UNKNOWN)
    }
    return responseToResult(response)
}
```

Agent adds only the safe call helpers that match the project's dependencies.

---

## When to Use What

| Scenario | Error type | Example return |
|---|---|---|
| Firestore call | `DataError.Firestore` | `Result<List<NoteDocument>, DataError.Firestore>` |
| Network/HTTP call | `DataError.Network` | `Result<List<NoteDto>, DataError.Network>` |
| Local DB access | `DataError.Local` | `Result<Note, DataError.Local>` |
| Repository (multi-source) | `DataError` (supertype) | `Result<List<Note>, DataError>` |
| Domain validation | Custom `Error` enum | `EmptyResult<PasswordValidationError>` |
| Auth logic | Custom `Error` enum | `Result<User, AuthError>` |
