# FirestoreApp — Refactoring Plan

> **Мета:** Привести проєкт у повну відповідність із правилами, описаними у `.claude/skills/` та `CLAUDE.md`.
>
> **Як використовувати:** На початку кожної сесії Claude/Copilot — прочитай цей файл (`REFACTORING_PLAN.md`), щоб зрозуміти поточний стан рефакторингу та продовжити з наступного незавершеного пункту. Позначай `[x]` кожен завершений крок.

---

## Поточний стан проєкту (до рефакторингу)

### Виявлені порушення

| Категорія | Що порушено | Де |
|---|---|---|
| Error Handling | Використовується `kotlin.Result` замість типізованого `Result<T, E : Error>` | Усі repository інтерфейси та імплементації |
| Error Handling | Відсутні `DataError`, `firestoreSafeCall`, `UiText` | Проєкт в цілому |
| Error Handling | `CancellationException` перехоплюється (не перекидується) | `AuthRepositoryImpl`, `StatusRepositoryImpl`, `EmployeeRepositoryImpl`, `RoleRepositoryImpl`, `StatusTypeRepositoryImpl`, `ReminderRepositoryImpl` |
| Data Layer | Суфікс `Impl` на всіх імплементаціях | `data/.../repository/*Impl.kt` |
| Data Layer | Немає Firestore DTO-класів (`*Document`) | Ручний маппінг через `doc.getString()` |
| Data Layer | Витоки `CoroutineScope(Dispatchers.IO)` | `EmployeeRepositoryImpl:25`, `RoleRepositoryImpl:28`, `StatusTypeRepositoryImpl:31,38` |
| Data Layer | Маппери `ReminderSettings` приватні всередині repository | `ReminderRepositoryImpl:66-90` |
| Data Layer | Немає розділення DataSource vs Repository | `RoleRepositoryImpl` та `StatusTypeRepositoryImpl` (одне джерело) назвaні "Repository" |
| DI | `@Provides` замість `@Binds` для interface→impl | `DataModule.kt:72-101` (7 біндінгів) |
| DI | Один монолітний `DataModule` | `data/.../di/DataModule.kt` |
| Navigation | Рядкові маршрути (`"login"`, `"home"`) | `MainActivity.kt:50-116` |
| Navigation | Немає `@Serializable` routes | Проєкт в цілому |
| Navigation | Немає `NavGraphBuilder.<feature>Graph()` | `MainActivity.kt` — все inline |
| MVI | `sealed class` замість `sealed interface` | Усі `*Contract.kt` файли |
| MVI | `object` замість `data object` | `AuthIntent.Submit`, `StatusIntent.LoadActiveStatus` тощо |
| MVI | Помилки як `String` замість `UiText` | `HomeEffect.ShowToast`, `AuthEffect.ShowError`, `StatusEffect.ShowError` |
| MVI | Доменні моделі прямо у State | `StatusState.activeStatus: EmployeeStatus`, `EmployeesState.employees: List<Employee>` |
| MVI | `isDarkTheme`/`onThemeToggle` пробросюється через усі features | `HomeScreen`, `StatusScreen`, `AdminScreen`, `RegisterEmployeeScreen`, `ReminderSettingsScreen` |
| Compose | `collectAsState()` замість `collectAsStateWithLifecycle()` | `MainActivity.kt:39`, `ReminderSettingsScreen.kt:20` |
| Compose | `contentDescription` — хардкодовані рядки | ≈20 місць по всьому проєкту |
| Compose | `stringResource()` не використовується | Проєкт в цілому |
| Compose | Немає `@Stable` на State з `List` полями | `StatusState`, `EmployeesState`, `StatusesState` |
| Compose | `LaunchedEffect + collectLatest` замість `ObserveAsEvents` | Усі `*Screen.kt` |
| Compose | Подвійне `CoreTheme` обгортання | `HomeScreen.kt:66` |
| Module | Дублікати `SyncWorker`, `SyncManager` | Існують і в `:app`, і в `:data` |
| Module | Порожні stub-файли | `core/.../utils/AlarmScheduler.kt`, `core/.../utils/ReminderReceiver.kt` |
| Module | Hardcoded версії | `domain/build.gradle.kts` → `javax.inject:1`, `app/build.gradle.kts` → `firebase-analytics`, `libs.versions.toml:93` → `hilt-navigation-compose` |
| Module | Зайві залежності в `:app` | Retrofit, OkHttp, Moshi, Camera, Accompanist, Coil, PlayServicesLocation |
| Testing | Немає тестів (лише `ExampleUnitTest`) | Проєкт в цілому |
| Testing | JUnit4 замість JUnit5 | `libs.versions.toml`, `build.gradle.kts` |

---

## Етап 1 — Error Handling інфраструктура

> **Скіл:** `android-error-handling` (`.claude/skills/android-error-handling/SKILL.md`)
>
> **Файли для читання перед початком:** `domain/src/main/java/com/hubenko/domain/`, `core/src/main/java/com/hubenko/core/`

- [x] **1.1** Створити `domain/src/main/java/com/hubenko/domain/util/Result.kt`:
  ```
  - interface Error
  - sealed interface Result<out D, out E : Error>
    - data class Success<out D>(val data: D)
    - data class Error<out E : com.hubenko.domain.util.Error>(val error: E)
  - typealias EmptyResult<E> = Result<Unit, E>
  ```
- [x] **1.2** Створити `domain/src/main/java/com/hubenko/domain/util/ResultExtensions.kt`:
  ```
  - fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E>
  - fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E>
  - fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E>
  - fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E>
  ```
- [x] **1.3** Створити `domain/src/main/java/com/hubenko/domain/error/DataError.kt`:
  ```
  sealed interface DataError : Error {
      enum class Firestore : DataError { NOT_FOUND, PERMISSION_DENIED, UNAVAILABLE, UNAUTHENTICATED, ALREADY_EXISTS, UNKNOWN }
      enum class Local : DataError { DISK_FULL, NOT_FOUND, UNKNOWN }
  }
  ```
- [x] **1.4** Створити `data/src/main/java/com/hubenko/data/util/FirestoreSafeCall.kt`:
  ```
  suspend fun <T> firestoreSafeCall(execute: suspend () -> T): Result<T, DataError.Firestore>
  - catch FirebaseFirestoreException → map code → DataError.Firestore.*
  - catch Exception → if (e is CancellationException) throw e → DataError.Firestore.UNKNOWN
  ```
- [x] **1.4b** Створити `data/src/main/java/com/hubenko/data/util/LocalSafeCall.kt`:
  ```
  suspend fun <T> localSafeCall(execute: suspend () -> T): Result<T, DataError.Local>
  - catch SQLiteFullException → DataError.Local.DISK_FULL
  - catch SQLiteException (NOT_FOUND) → DataError.Local.NOT_FOUND
  - catch Exception → if (e is CancellationException) throw e → DataError.Local.UNKNOWN
  ```
  Потрібен для Room-операцій (2D.1: `updateStatusEndTime` → `EmptyResult<DataError.Local>`).
- [x] **1.5** Перейменувати пакет `core.ui` → `core.presentation` (конвенція скілу, відповідає `core:presentation` у sub-module структурі):
  - Перемістити `core/src/main/java/com/hubenko/core/ui/` → `core/src/main/java/com/hubenko/core/presentation/`
  - Перемістити `core/src/main/java/com/hubenko/core/base/` → `core/src/main/java/com/hubenko/core/presentation/`
  - Оновити `package` декларації та всі `import` у проєкті.
- [x] **1.6** Створити `core/src/main/java/com/hubenko/core/presentation/UiText.kt`:
  ```
  sealed interface UiText {
      data class DynamicString(val value: String) : UiText
      class StringResource(@StringRes val id: Int, val args: Array<Any> = emptyArray()) : UiText
  }
  // Extension: fun UiText.asString(context: Context): String
  ```
- [x] **1.7** Додати `implementation(project(":domain"))` у `core/build.gradle.kts` (архітектурно валідно: `core:presentation` → `core:domain`).
- [x] **1.8** Створити `core/src/main/java/com/hubenko/core/presentation/DataErrorToUiText.kt`:
  ```
  fun DataError.toUiText(): UiText — маппінг кожного DataError → UiText.StringResource(R.string.error_*)
  ```
- [x] **1.9** Створити `core/src/main/res/values/strings.xml` з ресурсами:
  ```xml
  error_unknown, error_permission_denied, error_unavailable, error_unauthenticated,
  error_not_found, error_already_exists, error_disk_full, error_no_internet
  ```
- [x] **1.10** Створити `core/src/main/java/com/hubenko/core/presentation/ObserveAsEvents.kt`:
  ```kotlin
  @Composable
  fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(flow, lifecycleOwner.lifecycle) {
          lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
              flow.collect(onEvent)
          }
      }
  }
  ```
  > Інфраструктура `core.presentation`, потрібна до Етапу 5. Перенесено з 5D.1.
- [x] **1.11** Перемістити `core/src/main/java/com/hubenko/core/utils/NotificationHelper.kt` → `core/src/main/java/com/hubenko/core/presentation/utils/NotificationHelper.kt`. Оновити `package` та всі `import` у проєкті.
- [x] **1.12** Створити feature-специфічні типи помилок (реалізують `domain.util.Error`):
  - `feature/auth` → `AuthValidationError.kt`: `enum class AuthValidationError : Error { EMPTY_EMAIL, INVALID_EMAIL, EMPTY_PASSWORD, PASSWORD_TOO_SHORT }`
  - `feature/admin` → `EmployeeValidationError.kt`: `enum class EmployeeValidationError : Error { EMPTY_NAME, EMPTY_EMAIL, INVALID_EMAIL, EMPTY_PHONE }`
  - Маппери `XxxValidationError.toUiText()` — у відповідному `feature/.../ui/` пакеті.
  - String resources для помилок валідації — у `feature/.../res/values/strings.xml`.

---

## Етап 2 — Data Layer рефакторинг

> **Скіл:** `android-data-layer` (`.claude/skills/android-data-layer/SKILL.md`)
>
> **Файли для читання перед початком:** всі файли в `data/src/main/java/com/hubenko/data/repository/`, `domain/src/main/java/com/hubenko/domain/repository/`

### 2A — Перейменування та поділ DataSource / Repository

- [x] **2A.1** Оновити інтерфейси в `:domain`:

  | Поточне ім'я | Нове ім'я | Причина |
  |---|---|---|
  | `AuthRepository` | `AuthDataSource` | Одне джерело (Firebase Auth + Firestore) |
  | `RoleRepository` | `RoleDataSource` | Одне джерело (Firestore) |
  | `StatusTypeRepository` | `StatusTypeDataSource` | Одне джерело (Firestore + Room-кеш) |
  | `SettingsRepository` | `SettingsDataSource` | Одне джерело (DataStore) |
  | `EmployeeRepository` | `EmployeeRepository` | ✅ Координує Room + Firestore |
  | `StatusRepository` | `StatusRepository` | ✅ Координує Room + Firestore + WorkManager |
  | `ReminderRepository` | `ReminderRepository` | ✅ Координує Room + Firestore |

- [x] **2A.2** Перейменувати імплементації в `:data` (без суфікса `Impl`):

  | Поточне ім'я | Нове ім'я |
  |---|---|
  | `AuthRepositoryImpl` | `FirebaseAuthDataSource` |
  | `RoleRepositoryImpl` | `FirestoreRoleDataSource` |
  | `StatusTypeRepositoryImpl` | `FirestoreStatusTypeDataSource` |
  | `SettingsRepositoryImpl` | `DataStoreSettingsDataSource` |
  | `EmployeeRepositoryImpl` | `OfflineFirstEmployeeRepository` |
  | `StatusRepositoryImpl` | `OfflineFirstStatusRepository` |
  | `ReminderRepositoryImpl` | `OfflineFirstReminderRepository` |

- [x] **2A.3** Оновити всі use case та DI-модулі після перейменування.
  > ⚠️ Зачіпає ~40 файлів: 15 use cases у `:domain`, `DataModule.kt` у `:data`, ViewModels у feature-модулях. Виконувати через sed або IDE Rename, не вручну:
  > ```
  > sed -i 's/RoleRepository/RoleDataSource/g; s/StatusTypeRepository/StatusTypeDataSource/g;
  >         s/AuthRepository/AuthDataSource/g; s/SettingsRepository/SettingsDataSource/g' **/*.kt
  > ```

### 2B — Firestore DTOs

- [x] **2B.1** Створити `data/src/main/java/com/hubenko/data/remote/document/EmployeeDocument.kt`:
  ```kotlin
  data class EmployeeDocument(
      @DocumentId val id: String = "", val lastName: String = "", val firstName: String = "",
      val middleName: String = "", val phoneNumber: String = "", val role: String = "USER", val email: String = ""
  )
  ```
  > ⚠️ `@DocumentId` обов'язковий — `toObject()` не заповнює поле `id` з document ID без цієї анотації.
- [x] **2B.2** Створити `EmployeeStatusDocument.kt`:
  ```kotlin
  data class EmployeeStatusDocument(
      @DocumentId val id: String = "", val employeeId: String = "", val status: String = "",
      val note: String? = null, val startTime: Long = 0L, val endTime: Long? = null
  )
  ```
  > ⚠️ `@DocumentId` — аналогічно.
- [x] **2B.3** Створити `RoleDocument.kt`, `StatusTypeDocument.kt`, `ReminderSettingsDocument.kt` — з default-значеннями та `@DocumentId` де поле = document ID.
  > ⚠️ `ReminderSettingsDocument.daysOfWeek` має бути `List<Long>` (не `Int`) — Firestore SDK десеріалізує числові масиви як `Long`. Маппер: `.map { it.toInt() }`.

### 2C — Маппери

- [x] **2C.1** Додати маппери `*Document ↔ Domain` у `data/.../mapper/`:
  ```
  EmployeeDocument.toEmployee(), Employee.toDocument()
  EmployeeStatusDocument.toEmployeeStatus(), EmployeeStatus.toDocument()
  RoleDocument.toRole(), StatusTypeDocument.toStatusType()
  ReminderSettingsDocument.toReminderSettings(), ReminderSettings.toDocument()
  ```
- [x] **2C.2** Винести приватні маппери `ReminderSettingsEntity ↔ ReminderSettings` з `ReminderRepositoryImpl` у `data/.../mapper/ReminderSettingsMapper.kt` як public extension functions.
- [x] **2C.3** Замінити ручний `doc.getString()` маппінг на `doc.toObject(XxxDocument::class.java)?.toXxx()` у всіх data source.
  > ⚠️ Виконувати тільки після виправлення `@DocumentId` у 2B — без нього `toObject()` дасть `id = ""` скрізь.

### 2D — Типізований Result та firestoreSafeCall

- [x] **2D.1** Замінити `kotlin.Result` на `com.hubenko.domain.util.Result<T, DataError.*>` у всіх domain-інтерфейсах:
  - `AuthDataSource.signIn()` → `Result<String, DataError.Firestore>`
  - `AuthDataSource.signUp()` → `Result<String, DataError.Firestore>`
  - `EmployeeRepository.saveEmployee()` → `EmptyResult<DataError.Firestore>` (зараз `Unit`, impl ковтає помилку через `throw e`)
  - `EmployeeRepository.deleteEmployee()` → `EmptyResult<DataError.Firestore>` (аналогічно)
  - `StatusRepository.saveStatusLocally()` → `EmptyResult<DataError.Local>` (Room-insert, зараз `Unit`)
  - `StatusRepository.fetchStatusesFromRemote()` → `EmptyResult<DataError.Firestore>` (зараз `Unit`, тихо ковтає через `e.printStackTrace()`)
  - `StatusRepository.syncStatuses()` → `EmptyResult<DataError.Firestore>`
  - `StatusRepository.deleteAllStatuses()` → `EmptyResult<DataError.Firestore>`
  - `StatusRepository.updateStatusEndTime()` → `EmptyResult<DataError.Local>`
  - `RoleDataSource.saveRole()` → `EmptyResult<DataError.Firestore>`
  - `RoleDataSource.deleteRole()` → `EmptyResult<DataError.Firestore>`
  - `StatusTypeDataSource.saveStatusType()` → `EmptyResult<DataError.Firestore>`
  - `StatusTypeDataSource.deleteStatusType()` → `EmptyResult<DataError.Firestore>`
  - `ReminderRepository.saveReminderSettings()` → `EmptyResult<DataError.Firestore>`

- [x] **2D.2** Обгорнути всі Firestore-виклики у `firestoreSafeCall { }` в data source імплементаціях.

- [x] **2D.3** Перевірити `catch (e: Exception)` блоки, що **не** обгорнуті у `firestoreSafeCall`/`localSafeCall` після 2D.2 — додати `if (e is CancellationException) throw e`:
  - `FirebaseAuthDataSource.getUserRole()` — повертає `String` з fallback, не мігрує на Result, тому safe call не застосовується
  - Будь-які інші catch-блоки поза wrappers, що залишились після 2D.2

### 2E — Виправити витоки CoroutineScope

- [x] **2E.1** `OfflineFirstEmployeeRepository` — видалити `private val syncScope = CoroutineScope(Dispatchers.IO)`. Зробити `syncEmployeesFromFirestore()` suspend-функцією. У `getAllEmployees()` замінити `onStart { syncScope.launch { } }` на `onStart { launch { syncEmployeesFromFirestore() } }` де `launch` — з `CoroutineScope(coroutineContext)`, щоб зберегти фоновий запуск (Room-дані починають емітуватись одразу, синхронізація йде паралельно).
  > ⚠️ `coroutineScope { }` (blocking) тут неправильний — він затримує емісію Room-даних до завершення синхронізації. Потрібен саме `launch` у поточному coroutine context.
- [x] **2E.2** `FirestoreRoleDataSource` — видалити `CoroutineScope(Dispatchers.IO).launch { seedDefaults() }` (рядок 28). Використовувати `launch { seedDefaults() }` у контексті `ProducerScope` від `callbackFlow`.
- [x] **2E.3** `FirestoreStatusTypeDataSource` — аналогічно (рядки 31, 38). Видалити `CoroutineScope(Dispatchers.IO).launch`. Також перенести `dao.insertAll(...)` у той самий `launch { }` блок ProducerScope.

### 2F — SyncWorker дублікат та сумісність з типізованим Result

- [x] **2F.1** Видалити дублікат `app/src/main/java/com/hubenko/firestoreapp/worker/SyncWorker.kt` — залишити тільки `data/src/main/java/com/hubenko/data/worker/SyncWorker.kt`. Оновити реєстрацію у `AndroidManifest.xml` якщо потрібно.
- [x] **2F.2** Оновити `SyncWorker.doWork()` після 2D.1 — `syncStatuses()` більше не повертає `kotlin.Result`, тому `.isSuccess` перестане компілюватись. Замінити:
  ```kotlin
  // Було
  if (syncResult.isSuccess) Result.success() else Result.retry()

  // Стало
  when (syncResult) {
      is Result.Success -> Result.success()
      is Result.Error -> Result.retry()
  }
  ```
  > ⚠️ Виконувати одночасно з 2D.1, інакше проєкт не збереться.

---

## Етап 3 — DI (Hilt) рефакторинг

> **Скіл:** `android-di` (`.claude/skills/android-di/SKILL.md`)
>
> **Файл для читання перед початком:** `data/src/main/java/com/hubenko/data/di/DataModule.kt`

- [x] **3.1** Розділити `DataModule` на **2** модулі (7 модулів — надлишок для single-dev flat-project):

  | Клас | Тип | Що надає |
  |---|---|---|
  | `CoreDataModule` | `object` | `@Provides @Singleton`: `FirebaseFirestore`, `FirebaseAuth`, `AppDatabase`, DAOs |
  | `RepositoryModule` | `abstract class` | `@Binds @Singleton`: всі 8 interface→impl (AuthDataSource, EmployeeRepository, StatusRepository, StatusTypeDataSource, RoleDataSource, ReminderRepository, ReminderManager, SettingsDataSource) |

  > **Чому 2, а не 7:** `@Provides` (factory-методи) потребують `object`, `@Binds` потребує `abstract class` — але поєднувати їх в одному файлі не потрібно. Розбивка по одному модулю на інтерфейс — бюрократія без реальної користі для проєкту такого масштабу.

- [x] **3.2** Використовувати `@Binds` (abstract fun) замість `@Provides` для всіх interface→impl біндінгів у `RepositoryModule`.

- [x] **3.3** Видалити старий `DataModule.kt` після створення нових модулів.

### 3B — Hardcoded версії

- [x] **3.4** Перенести `"javax.inject:javax.inject:1"` з `domain/build.gradle.kts` → `libs.versions.toml`:
  ```toml
  [versions]
  javaxInject = "1"

  [libraries]
  javax-inject = { group = "javax.inject", name = "javax.inject", version.ref = "javaxInject" }
  ```
- [x] **3.5** Перенести `"com.google.firebase:firebase-analytics"` з `app/build.gradle.kts:98` → `libs.versions.toml`:
  ```toml
  firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics" }
  ```
- [x] **3.6** Винести hardcoded `version = "1.2.0"` для `hilt-navigation-compose` з `libs.versions.toml:93` у `[versions]`:
  ```toml
  [versions]
  hiltNavigationCompose = "1.2.0"

  [libraries]
  androidx-hilt-navigation-compose = { ..., version.ref = "hiltNavigationCompose" }
  ```

- [x] **3.7** Видалити `@Singleton` з класів `DataStoreSettingsDataSource` та `AlarmScheduler` — скоуп оголошується **на місці прив'язки** (`@Binds @Singleton` у `RepositoryModule`), а не на класі. Дублювання `@Singleton` на класі є false signal: читач коду бачить скоуп, але не бачить до якого компонента він прив'язаний.

---

## Етап 4 — Навігація

> **Скіл:** `android-navigation` (`.claude/skills/android-navigation/SKILL.md`)
>
> **Файл для читання перед початком:** `app/src/main/java/com/hubenko/firestoreapp/MainActivity.kt`

### 4A — Підготовка

- [x] **4A.1** Додати `kotlinx-serialization` плагін до `libs.versions.toml`:
  ```toml
  [plugins]
  kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
  ```
- [x] **4A.2** Застосувати плагін у feature modules що визначають `@Serializable` route objects (`feature:auth`, `feature:home`, `feature:status`, `feature:admin`) та `:app`:
  ```kotlin
  plugins {
      alias(libs.plugins.kotlin.serialization)
  }
  ```
  > ⚠️ НЕ додавати до `:core` та `:data` — route objects там не потрібні.
- [x] **4A.3** Перевірити чи `kotlinx-serialization-core` доступний транзитивно через `navigation-compose:2.8.9` (type-safe навігація використовує `serialization-core`, не `serialization-json`). Якщо збірка не проходить без явної залежності — додати у `libs.versions.toml` та feature modules:
  ```toml
  [libraries]
  kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.7.3" }
  ```

### 4B — Route Objects

- [x] **4B.1** `:feature:auth` → `AuthRoute.kt`:
  ```kotlin
  @Serializable object AuthRoute
  ```
- [x] **4B.2** `:feature:home` → `HomeRoute.kt`:
  ```kotlin
  @Serializable object HomeRoute
  ```
- [x] **4B.3** `:feature:status` → `StatusRoute.kt`:
  ```kotlin
  @Serializable object StatusRoute
  ```
- [x] **4B.4** `:feature:admin` → `AdminRoutes.kt`:
  ```kotlin
  @Serializable object AdminRoute
  @Serializable object RegisterEmployeeRoute
  @Serializable data class ReminderSettingsRoute(val employeeId: String)
  ```

### 4C — Feature Nav Graphs

- [x] **4C.0** Додати `implementation(libs.androidx.navigation.compose)` до `build.gradle.kts` у `feature:auth`, `feature:home`, `feature:status`, `feature:admin` — необхідно для визначення `NavGraphBuilder` extension functions.
- [x] **4C.1** `:feature:auth` → `fun NavGraphBuilder.authGraph(onNavigateToHome: () -> Unit)`.
- [x] **4C.2** `:feature:home` → `fun NavGraphBuilder.homeGraph(onNavigateToStatus: () -> Unit, onNavigateToAdmin: () -> Unit, onNavigateToAuth: () -> Unit)`.
- [x] **4C.3** `:feature:status` → `fun NavGraphBuilder.statusGraph(onNavigateBack: () -> Unit)`.
- [x] **4C.4** `:feature:admin` → використовувати **callbacks**, не `navController` — feature не повинен керувати стеком навігації напряму:
  ```kotlin
  fun NavGraphBuilder.adminGraph(
      onNavigateBack: () -> Unit,
      onNavigateToRegisterEmployee: () -> Unit,
      onNavigateToReminderSettings: (String) -> Unit
  )
  ```
  > ⚠️ Логіка `navController.navigate(RegisterEmployeeRoute)` залишається у `:app`. Feature module лише оголошує destinations та викликає callbacks.

### 4D — Wiring in :app

- [x] **4D.1** Перенести перевірку стану авторизації у `MainViewModel`:
  ```kotlin
  // MainViewModel.kt
  val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
  ```
  У `MainActivity` читати через `viewModel.isLoggedIn`. Пряме звернення до `FirebaseAuth` в Activity — логіка не на своєму місці.

- [x] **4D.2** Переписати `MainActivity.kt` NavHost:
  > ⚠️ Виконувати **після 5E** (видалення `isDarkTheme`/`onThemeToggle` з Screen composables), інакше compile error — screens ще вимагають ці параметри, а новий NavHost їх не передає.
  ```kotlin
  NavHost(navController, startDestination = if (viewModel.isLoggedIn) HomeRoute else AuthRoute) {
      authGraph(
          onNavigateToHome = { navController.navigate(HomeRoute) { popUpTo(AuthRoute) { inclusive = true } } }
      )
      homeGraph(
          onNavigateToStatus = { navController.navigate(StatusRoute) },
          onNavigateToAdmin = { navController.navigate(AdminRoute) },
          onNavigateToAuth = { navController.navigate(AuthRoute) { popUpTo(HomeRoute) { inclusive = true } } }
      )
      statusGraph(onNavigateBack = { navController.popBackStack() })
      adminGraph(
          onNavigateBack = { navController.popBackStack() },
          onNavigateToRegisterEmployee = { navController.navigate(RegisterEmployeeRoute) },
          onNavigateToReminderSettings = { employeeId -> navController.navigate(ReminderSettingsRoute(employeeId)) }
      )
  }
  ```
- [x] **4D.3** `ReminderSettingsViewModel` — отримувати `employeeId` через `savedStateHandle.toRoute<ReminderSettingsRoute>().employeeId` замість параметра конструктора.

---

## Етап 5 — Presentation / MVI рефакторинг

> **Скіл:** `android-presentation-mvi` (`.claude/skills/android-presentation-mvi/SKILL.md`)
>
> **Файли для читання перед початком:** всі `*Contract.kt`, `*ViewModel.kt`, `*Screen.kt`

### 5A — sealed interface + data object

- [x] **5A.1** Замінити `sealed class` → `sealed interface` у всіх Intent:
  - `HomeIntent`, `AuthIntent`, `StatusIntent`, `AdminIntent`
  - `EmployeesIntent`, `StatusesIntent`, `ScheduleIntent`, `DirectoriesIntent`, `RegisterEmployeeIntent`

- [x] **5A.2** Замінити `sealed class` → `sealed interface` у всіх Effect:
  - `HomeEffect`, `AuthEffect`, `StatusEffect`, `AdminEffect`
  - `EmployeesEffect`, `StatusesEffect`, `ScheduleEffect`, `DirectoriesEffect`, `RegisterEmployeeEffect`

- [x] **5A.3** Замінити `object` → `data object` у singleton variants:
  - `AuthIntent.Submit` → `data object Submit`
  - `StatusIntent.LoadActiveStatus`, `ConfirmSubmit`, `DismissConfirmDialog`, `DismissDialog`, `OnBackClick`
  - `HomeIntent.LoadAdminStatus`, `OnAdminPanelClick`, `OnSendStatusClick`, `OnLogoutClick`, `OnThemeToggle`
  - `AuthEffect.NavigateToHome`
  - `StatusEffect.NavigateBack`
  - `AdminIntent.OnBackClick`
  - `AdminEffect.NavigateBack`
  - тощо по всіх contract файлах

### 5B — UiText у Effect та State

- [x] **5B.1** Замінити у Effects:
  ```
  HomeEffect.ShowToast(message: String)        → ShowSnackbar(message: UiText)
  AuthEffect.ShowError(message: String)        → ShowError(message: UiText)
  StatusEffect.ShowError(message: String)      → ShowError(message: UiText)
  EmployeesEffect.ShowToast(message: String)   → ShowSnackbar(message: UiText)
  StatusesEffect.ShowToast(message: String)    → ShowSnackbar(message: UiText)
  DirectoriesEffect.ShowToast(message: String) → ShowSnackbar(message: UiText)
  RegisterEmployeeEffect.ShowToast(message: String) → ShowSnackbar(message: UiText)
  ```
- [x] **5B.2** Замінити у State:
  ```
  AuthState.error: String?              → error: UiText?
  StatusState.error: String?            → error: UiText?
  ReminderSettingsState.error: String?  → error: UiText?
  ```
- [x] **5B.3** Оновити ViewModels — використовувати `DataError.toUiText()` та `UiText.StringResource(R.string.*)` замість хардкодованих рядків.

### 5C — UI-моделі (суфікс `Ui`)

- [x] **5C.1** Створити UI-моделі у feature-модулях:

  | Feature | Модель | Файл |
  |---|---|---|
  | `:feature:status` | `StatusTypeUi(type: String, label: String)` | `StatusTypeUi.kt` |
  | `:feature:status` | `EmployeeStatusUi(id, employeeId, fullName, status, note, startTime, endTime, isSynced)` | `EmployeeStatusUi.kt` |
  | `:feature:admin` | `EmployeeUi(id, fullName, phoneNumber, role, email)` | `EmployeeUi.kt` |
  | `:feature:admin` | `RoleUi(id: String, label: String)` | `RoleUi.kt` |
  | `:feature:admin` | `StatusTypeUi` — повторно використовувати з `:feature:status` або дублювати якщо немає між-feature залежності | — |

- [x] **5C.2** Створити маппери `Domain → Ui` у feature-модулях:
  ```
  fun StatusType.toStatusTypeUi(): StatusTypeUi
  fun EmployeeStatus.toEmployeeStatusUi(): EmployeeStatusUi
  fun Employee.toEmployeeUi(): EmployeeUi
  fun Role.toRoleUi(): RoleUi
  ```

- [x] **5C.3** Замінити **всі** доменні моделі на Ui-моделі в State (повний список):
  ```
  StatusState.activeStatus: EmployeeStatus?          → EmployeeStatusUi?
  StatusState.statusTypes: List<StatusType>           → List<StatusTypeUi>

  EmployeesState.employees: List<Employee>            → List<EmployeeUi>
  EmployeesState.roles: List<Role>                    → List<RoleUi>
  EmployeesState.editingEmployee: Employee?           → EmployeeUi?
  EmployeesState.employeePendingDelete: Employee?     → EmployeeUi?

  StatusesState.statuses: List<EmployeeStatus>        → List<EmployeeStatusUi>
  StatusesState.availableStatusTypes: List<StatusType>→ List<StatusTypeUi>
  StatusesState.EmployeeStatusesGroup.statuses        → List<EmployeeStatusUi>

  ScheduleState.employees: List<Employee>             → List<EmployeeUi>

  DirectoriesState.statusTypes: List<StatusType>      → List<StatusTypeUi>
  DirectoriesState.roles: List<Role>                  → List<RoleUi>
  DirectoryDialog.EditStatusType(item: StatusType?)   → item: StatusTypeUi?
  DirectoryDialog.EditRole(item: Role?)               → item: RoleUi?

  RegisterEmployeeState.roles: List<Role>             → List<RoleUi>
  ```

- [x] **5C.4** Intent-и що несуть доменні моделі — замінити на Ui-моделі або ID:
  ```
  EmployeesIntent.OnEditEmployeeClick(val employee: Employee)   → EmployeeUi або employeeId: String
  EmployeesIntent.OnDeleteEmployeeClick(val employee: Employee) → EmployeeUi або employeeId: String
  EmployeesIntent.OnSaveEmployee(val employee: Employee)        → EmployeeUi
  ```
  > Для `OnEditEmployeeClick`/`OnDeleteEmployeeClick` достатньо `employeeId: String` якщо ViewModel може знайти модель у поточному State. Для `OnSaveEmployee` — `EmployeeUi` з усіма полями.

### 5D — ObserveAsEvents

- [x] **5D.1** `ObserveAsEvents.kt` — виконано у **1.10**.
- [x] **5D.2** Замінити `LaunchedEffect(viewModel.effect) { effect.collectLatest { } }` на `ObserveAsEvents(viewModel.effect) { }` у всіх Screen composables:
  - `HomeScreen`, `AuthScreen`, `StatusScreen`, `AdminScreen`
  - `EmployeesScreen`, `StatusesScreen`, `ScheduleScreen`, `DirectoriesScreen`, `RegisterEmployeeScreen`

### 5E — Видалити isDarkTheme/onThemeToggle пробросювання

- [x] **5E.1** Тема вже обробляється на рівні `MainActivity` через `MainViewModel`. Видалити `isDarkTheme: Boolean` та `onThemeToggle: () -> Unit` параметри з **усіх** screens та contents (перевірено по коду):
  - `HomeScreen`, `HomeContent`, `HomeState.isDarkTheme`
  - `StatusScreen`, `StatusContent`
  - `AdminScreen`, `AdminContent`
  - `ScheduleScreen`, `ScheduleContent` *(також має ці параметри — не було у плані)*
  - `RegisterEmployeeScreen`, `RegisterEmployeeContent`
  - `ReminderSettingsScreen`, `ReminderSettingsContent`
  > ⚠️ Виконати **до 4D.2** — новий NavHost в 4D.2 не передає ці параметри, compile error якщо screens їх ще вимагають.
- [x] **5E.2** Видалити `HomeIntent.OnThemeToggle` та відповідну логіку з `HomeViewModel`.
- [x] **5E.3** Видалити `observeTheme()` з `HomeViewModel` та залежність від `SettingsRepository`.

### 5F — @Stable на State з List

- [x] **5F.1** Додати `@Stable` до всіх State з нестабільними полями (повний список):
  - `StatusState` (має `statusTypes: List<StatusTypeUi>`, `activeStatus: EmployeeStatusUi?`)
  - `EmployeesState` (має `employees: List<EmployeeUi>`, `roles: List<RoleUi>`, `editingEmployee: EmployeeUi?`)
  - `StatusesState` (має `statuses`, `employeeGroups`, `availableStatusTypes`, `filterEmployeeIds: Set`, `filterStatusTypes: Set`)
  - `ScheduleState` (має `employees: List<EmployeeUi>`)
  - `DirectoriesState` (має `statusTypes: List<StatusTypeUi>`, `roles: List<RoleUi>`)
  - `RegisterEmployeeState` (має `roles: List<RoleUi>`)

---

## Етап 6 — Compose UI

> **Скіл:** `android-compose-ui` (`.claude/skills/android-compose-ui/SKILL.md`)

### 6A — collectAsStateWithLifecycle

- [x] **6A.1** `MainActivity.kt:39` — замінити `collectAsState()` → `collectAsStateWithLifecycle()`.
- [x] **6A.2** `ReminderSettingsScreen.kt:20` — замінити `collectAsState()` → `collectAsStateWithLifecycle()`.

### 6B — contentDescription через stringResource

- [x] **6B.1** Створити string resources для всіх `contentDescription` у кожному feature-модулі:
  ```xml
  <!-- core/src/main/res/values/strings.xml -->
  cd_navigate_back, cd_toggle_theme
  <!-- feature/home/src/main/res/values/strings.xml -->
  cd_logout
  <!-- feature/status/src/main/res/values/strings.xml -->
  cd_success_icon
  <!-- feature/admin/src/main/res/values/strings.xml -->
  cd_delete_all_statuses, cd_export_csv, cd_filters, cd_clear_filter,
  cd_add_employee, cd_edit, cd_delete, cd_expand, cd_collapse, cd_app_logo
  ```
  > Повний список місць (20): `AppTopBar` (Назад, Змінити тему), `HomeContent` (Вийти), `DirectoriesContent` (Додати), `DirectoryItemRow` (Редагувати, Видалити), `EmployeeItem` (Редагувати, Видалити), `EmployeesContent` FAB (Зареєструвати), `StatusesContent` (Видалити всі, Експортувати CSV, Фільтри, Скинути фільтр), `EmployeeStatusesItem` (Розгорнути, Згорнути), `AuthHeader` (App Logo), `StatusConfirmationDialog` (Success).

- [x] **6B.2** Замінити хардкодовані рядки → `stringResource(R.string.cd_*)` у всіх 20 місцях.
  > ⚠️ `AppTopBar` у `:core` — використовувати `R.string` з `:core` модуля. Feature-компоненти — з відповідного feature `R.string`.

### 6C — Видалити подвійний CoreTheme

- [x] **6C.1** Подвійний `CoreTheme` у `HomeScreen.kt` — усунуто у **5E** (видалення `isDarkTheme` параметра зробило обгортку непотрібною). `CoreTheme` у `@Preview` блоках `HomeContent.kt` — **правильно**, previews зобов'язані мати тему.

### 6D — Previews (доповнення)

- [x] **6D.1** Додати відсутні `@Preview` варіанти (більшість Content composables вже мають previews):
  - `StatusContent` — відсутній **"Active status"** стан (коли `activeStatus != null`, кнопки "Завершити")
  - `AdminContent` — відсутній **Employees tab** preview (тільки Dashboard є)
  > Решта Content composables (`AuthContent`, `EmployeesContent`, `StatusesContent`, `ScheduleContent`, `DirectoriesContent`, `RegisterEmployeeContent`, `ReminderSettingsContent`) — вже мають повні preview набори.

### 6E — Domain моделі у компонентах (пробіл 5C.3)

> **Проблема:** State і Contract файли оновлено на Ui-моделі, але самі компоненти (`components/`) все ще приймають domain моделі напряму — це витік domain у presentation layer.

- [x] **6E.1** `feature/admin/ui/statuses/components/StatusItem.kt` — замінити параметр `status: EmployeeStatus` → `status: EmployeeStatusUi`. Оновити preview.
- [x] **6E.2** `feature/admin/ui/statuses/components/EmployeeStatusesItem.kt` — оновити preview з `List<EmployeeStatus>` → `List<EmployeeStatusUi>` (сам компонент приймає `EmployeeStatusesGroup` — група вже містить `List<EmployeeStatusUi>` після 5C.3, але `previewStatuses()` функція у файлі все ще використовує domain `EmployeeStatus`).
- [x] **6E.3** `feature/admin/ui/employees/components/EmployeeItem.kt` — замінити `employee: Employee` → `employee: EmployeeUi`. Оновити preview.
- [x] **6E.4** `feature/admin/ui/employees/components/DeleteEmployeeDialog.kt` — замінити `employee: Employee` → `employee: EmployeeUi`. Оновити preview.
- [x] **6E.5** `feature/admin/ui/employees/components/EmployeeDialog.kt` — замінити `employee: Employee?` → `employee: EmployeeUi?`, `roles: List<Role>` → `roles: List<RoleUi>`. Оновити preview та `RoleDropdown`.
- [x] **6E.6** `feature/admin/ui/employees/components/RoleDropdown.kt` — замінити `roles: List<Role>` → `roles: List<RoleUi>`. Оновити preview.
- [x] **6E.7** `feature/admin/ui/register/components/RegisterEmployeeForm.kt` — файл не існує, `RegisterEmployeeContent.kt` вже використовує `RoleUi` (виконано в 5D). — замінити `roles: List<Role>` → `roles: List<RoleUi>`. Оновити preview.
  > ⚠️ Після виконання 6E — видалити `import com.hubenko.domain.model.*` з усіх оновлених файлів компонентів.

### 6F — Lazy list keys

> **Правило скілу:** `key = { it.id }` обов'язковий коли є унікальний ID — дозволяє Compose відстежувати елементи під час реорганізації списку.

- [x] **6F.1** `EmployeesContent.kt` — `items(state.employees)` → `items(state.employees, key = { it.id })`.
- [x] **6F.2** `ScheduleContent.kt` — `items(state.employees)` → `items(state.employees, key = { it.id })`.
  > `StatusesContent.kt` вже має `key = { it.employeeId }`. `DirectoriesContent.kt` вже має `key = { it.type }` та `key = { it.id }`. ✅

### 6G — Expand/collapse анімація

- [x] **6G.1** `EmployeeStatusesItem.kt:73` — замінити `if (group.isExpanded)` миттєве показати/сховати → `AnimatedVisibility(visible = group.isExpanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut())`.
  > Поточний підхід: миттєве перемикання без анімації. `AnimatedVisibility` відповідає патерну, вже використаному в `StatusContent.kt`.

---

## Етап 7 — Очистка структури модулів

> **Скіл:** `android-module-structure` (`.claude/skills/android-module-structure/SKILL.md`)

### 7A — Видалити дублікати

- [x] **7A.1** Видалити `app/src/main/java/com/hubenko/firestoreapp/worker/SyncWorker.kt` — вже видалений (git status: `D`).
- [x] **7A.2** Видалити `app/src/main/java/com/hubenko/firestoreapp/worker/SyncManager.kt` — мертвий код: `SyncReceiver` вже імпортує `SyncManager` з `:data` напряму, тому `app`-версія ніколи не викликається.
- [x] **7A.3** `app/.../worker/SyncReceiver.kt:6` — вже імпортує `import com.hubenko.data.worker.SyncManager`. Оновлення не потрібне.

### 7B — Видалити stub-файли

- [x] **7B.1** `core/src/main/java/com/hubenko/core/utils/AlarmScheduler.kt` — вже видалений (файл не існує).
- [x] **7B.2** `core/src/main/java/com/hubenko/core/utils/ReminderReceiver.kt` — вже видалений (файл не існує).

### 7C — Зайві залежності в `app/build.gradle.kts`

> Перевірка підтвердила: жоден з нижчеперелічених імпортів не зустрічається в жодному `.kt` файлі `:app` модуля.

- [x] **7C.1** Видалити невикористовувані `implementation` залежності:
  - `libs.coil.compose` (рядок 69)
  - `libs.retrofit` (рядок 70)
  - `libs.converter.moshi` (рядок 71)
  - `libs.accompanist.permissions` (рядок 74)
  - `libs.play.services.location` (рядок 75)
  - `libs.androidx.camera.camera2` (рядок 76)
  - `libs.androidx.camera.lifecycle` (рядок 77)
  - `libs.androidx.camera.view` (рядок 78)
  - `libs.androidx.camera.core` (рядок 79)
  - `libs.logging.interceptor` (рядок 80)
  - `libs.okhttp` (рядок 81)
  - `libs.moshi.kotlin` (рядок 82)
  - `libs.androidx.room.runtime` (рядок 64) — Room живе в `:data`
  - `libs.androidx.room.ktx` (рядок 65) — Room живе в `:data`

- [x] **7C.2** Видалити невикористовувані `ksp` залежності:
  - `"ksp"(libs.androidx.room.compiler)` (рядок 114) — немає Room-анотацій у `:app`
  - `"ksp"(libs.moshi.kotlin.codegen)` (рядок 115) — немає Moshi-анотацій у `:app`

- [x] **7C.3** Перевірити `alias(libs.plugins.kotlin.android)` у `app/build.gradle.kts`:
  Kotlin 2.2.10 + AGP 9.1.0 — `kotlin.compose` є окремим плагіном, але `:core` та всі feature-модулі також не мають `kotlin.android` і компілюються. Патерн послідовний, додавати не потрібно.

---

## Етап 8 — Тестування (фундамент)

> **Скіл:** `android-testing` (`.claude/skills/android-testing/SKILL.md`)

### 8A — Налаштування стеку

- [x] **8A.1** Додати до `libs.versions.toml` (junit5 = "5.11.4", assertk = "0.28.1", turbine = "1.2.0"):
  `junit5-api`, `junit5-engine`, `assertk`, `turbine` — додано до `[libraries]`.

- [x] **8A.2** Додати test-залежності до `:feature:auth`, `:feature:home`, `:feature:status`, `:feature:admin` + `useJUnitPlatform()` у `testOptions`. Додано також до `:domain` (pure JVM) з `tasks.withType<Test> { useJUnitPlatform() }`.
  > JUnit5 для ViewModel unit-тестів (JVM) — без додаткового плагіна. Compose UI тести (instrumented) — залишаються на JUnit4.

- [ ] **8A.3** (новий) Fake реалізації — розмістити в test-source того модуля де використовуються. `FakeAuthDataSource` потрібен у кількох модулях — дублювати або винести у `testFixtures {}` блок `:domain`.

### 8B — Fake реалізації

- [ ] **8B.1** Створити `FakeAuthDataSource` у test-source `:feature:auth`:
  ```kotlin
  class FakeAuthDataSource : AuthDataSource {
      var shouldReturnError = false
      var signInResult: Result<String, DataError.Firestore> = Result.Success("test-uid")
      // ...
  }
  ```
- [ ] **8B.2** Створити `FakeStatusRepository` у test-source `:feature:status`.
- [ ] **8B.3** Створити `FakeEmployeeRepository` у test-source `:feature:admin`.

### 8C — ViewModel тести

- [ ] **8C.1** `AuthViewModelTest` — тест signIn success/failure, стейт зміни.
- [ ] **8C.2** `HomeViewModelTest` — тест admin status loading, logout effect.
- [ ] **8C.3** `StatusViewModelTest` — тест submit status, active status loading.
- [ ] **8C.4** `EmployeesViewModelTest` — тест load employees, save, delete.

---

## Порядок виконання

```
Етап 1 (Error Handling)  ──┐
                           ├──► Етап 2 (Data Layer) ──► Етап 3 (DI) ──┐
                           │                                           │
                           │    Етап 4 (Navigation) ◄─────────────────┘
                           │         │
                           │         ▼
                           │    Етап 5 (MVI/Presentation)
                           │         │
                           │         ▼
                           │    Етап 6 (Compose UI)
                           │
                           └──► Етап 7 (Cleanup) — можна в будь-який момент
                           
                           Етап 8 (Testing) — після стабілізації API
```

**Рекомендований порядок:** 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8

---

## Корисні посилання на скіли

| Скіл | Шлях |
|---|---|
| Compose UI | `.claude/skills/android-compose-ui/SKILL.md` |
| Data Layer | `.claude/skills/android-data-layer/SKILL.md` |
| DI (Hilt) | `.claude/skills/android-di/SKILL.md` |
| Error Handling | `.claude/skills/android-error-handling/SKILL.md` |
| Module Structure | `.claude/skills/android-module-structure/SKILL.md` |
| Navigation | `.claude/skills/android-navigation/SKILL.md` |
| Presentation (MVI) | `.claude/skills/android-presentation-mvi/SKILL.md` |
| Testing | `.claude/skills/android-testing/SKILL.md` |

---

## Етап 9 — Залишки після аудиту

> Знайдено під час фінального аудиту після Етапів 1-8.

- [x] **9A** `DirectoriesContract.kt:19` — `sealed class DirectoryDialog` → `sealed interface DirectoryDialog`.
- [x] **9B** `RegisterEmployeeForm.kt` — `roles: List<Role>` → `roles: List<RoleUi>` (6E.7 було помилково позначено як виконане, файл існував).
- [x] **9C** `ReminderSettingsViewModel` — не розширював `BaseViewModel`, використовував `isSaved: Boolean` у State замість Effect. Виправлено:
  - Розширює `BaseViewModel<ReminderSettingsState, ReminderSettingsIntent, ReminderSettingsEffect>`
  - Додано `ReminderSettingsEffect` (`NavigateBack`, `ShowSnackbar`)
  - `isSaved: Boolean` видалено зі State
  - `ReminderSettingsScreen` — `LaunchedEffect(state.isSaved)` замінено на `ObserveAsEvents`
- [x] **9D** `ReminderSettings` domain модель у presentation — створено `ReminderSettingsUi` з мапперами `toReminderSettingsUi()` / `toDomain()`. `ReminderSettingsState`, `ReminderSettingsContent`, `ReminderSettingsViewModel` оновлено.
- [x] **9E** `try/catch Exception` замість `Result` у ViewModels:
  - `AuthDataSource.getUserRole()` → `Result<String, DataError.Firestore>`; `FirebaseAuthDataSource` оновлено через `firestoreSafeCall`
  - `CheckAdminStatusUseCase` → `Result<Boolean, DataError.Firestore>` з `.map { role.uppercase() == "ADMIN" }`
  - `HomeViewModel.loadAdminStatus()` — замінено try/catch на `.onSuccess`/`.onFailure`
  - `HomeViewModel.syncReminders()` — додано явний `catch (e: CancellationException) { throw e }`
  - `EmployeesViewModel.saveEmployee()` / `confirmDeleteEmployee()` — замінено try/catch на `.onSuccess`/`.onFailure` (use cases вже повертали `EmptyResult`)
  - `StatusesViewModel.exportStatusesToCsv()` — файловий I/O залишено з try/catch, але додано явний `catch (e: CancellationException) { throw e }`

---

## Нотатки

- `BaseViewModel<S, I, E>` та `MviContract` — поточна реалізація вже відповідає конвенціям. Маркерні інтерфейси `ViewState`, `ViewIntent`, `ViewSideEffect` залишити для backward compatibility.
- UseCase-шар — Use cases що просто делегують (напр. `GetAllEmployeesUseCase`) залишити як контракт для спрощення тестування ViewModels.
- При роботі над кожним етапом — **спочатку прочитати відповідний SKILL.md**, потім виконувати зміни.

