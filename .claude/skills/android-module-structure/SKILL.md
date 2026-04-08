---
name: android-module-structure
description: |
  Module layout, dependency rules, and Gradle convention plugins for Android and Kotlin Multiplatform (KMP) projects. Use this skill whenever setting up a new Android/KMP project, deciding where a new module should live, asking "how should I structure this", creating a new feature module, adding a core submodule, configuring Gradle convention plugins, working with version catalogs, or making any decision about project-level architecture. Trigger on phrases like "set up the project", "add a module", "create a feature", "how should I structure", "project structure", "convention plugin", "build-logic", or "where does X live".
---

# Android / KMP Module Structure

## Core Philosophy

- **Feature-layered modularization**: split by feature first, then by layer within each feature.
- **Clean Architecture layers**: `presentation` → `domain` ← `data`. Domain is innermost and depends on nothing.
- **Code lives in a feature module unless it is needed by more than one feature** — then it moves to the appropriate `core` submodule.
- Features **never depend on each other**. Cross-feature shared data belongs in `core`.

---

## Module Layout (Universal)

Agent reads `settings.gradle.kts` to determine the project's module structure and follows it.

### Flat structure (smaller projects, single developer)
```
:app
:core
:domain
:data
:feature:auth
:feature:home
:feature:admin
```

### Sub-module structure (larger projects, multiple developers)
```
:app
:build-logic
:core:domain
:core:data
:core:presentation
:core:design-system
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
```

**For new projects:** Ask about expected scale:
- Single developer, < 5 features → flat structure
- Team of 2+, 5+ features, build time matters → sub-module structure

**Advantages of sub-module structure:**
- Faster builds (Gradle only rebuilds changed modules)
- Compiler enforces architecture (domain cannot import data)
- Faster unit tests (domain = pure Kotlin, runs on JVM)

For standalone concerns with meaningful complexity (multiple classes, non-trivial API), create a dedicated `:core` submodule (e.g., `:core:location`, `:core:analytics`). Do not create a module for a single class.

---

## Dependency Rules

| Layer | May depend on |
|---|---|
| `presentation` | `domain` (own feature), `core:domain`, `core:presentation`, `core:design-system` |
| `data` | `domain` (own feature), `core:domain`, `core:data` |
| `domain` | `core:domain` only — never `data` or `presentation` |
| `:app` | everything (wires all modules) |

Every layer may access `core:domain`.

---

## Convention Plugins (`:build-logic`)

Agent adds only the plugins that match the project's dependencies:

| Plugin | Purpose |
|---|---|
| `android-application` | App module config (applicationId, versionCode, etc.) |
| `android-library` | Base Android library config |
| `android-feature` | Android library + Compose + DI + shared feature deps |
| `domain-module` | Pure Kotlin/KMP module, no Android deps |
| `compose` | Compose compiler + BOM |
| `hilt` | Hilt DI block (Android projects) |
| `koin` | Koin DI block (KMP projects) |
| `firebase` | Firebase BOM + dependencies (if project uses Firebase) |
| `ktor` | Ktor client + serialization (if project uses Ktor) |
| `room` | Room + KSP config (if project uses Room) |
| `kotlinx-serialization` | KotlinX Serialization plugin + dep |

Use **version catalogs** (`libs.versions.toml`) for all dependency and version management. No hardcoded versions in build files.

---

## Key Libraries (Universal)

Agent uses the libraries found in the project. Common options:

| Concern | Library |
|---|---|
| DI | **Hilt** (Android) / **Koin** (KMP) — agent detects from dependencies |
| Networking | **Ktor** / **Retrofit** — agent detects from dependencies |
| Firebase | Firebase BOM (Firestore, Auth, Storage) — if present |
| Local DB | Room |
| Preferences | DataStore |
| Navigation | Compose Navigation (type-safe) |
| Serialization | KotlinX Serialization |
| Image loading | Coil |
| Async | Coroutines + Flow |
| Background tasks | WorkManager |
| Secrets | `local.properties` + `BuildConfig` |
| Testing | JUnit5, Turbine, AssertK, `kotlinx-coroutines-test` |
| UI testing | `ComposeTestRule` |

---

## Checklist: Adding a New Feature Module

- [ ] Read `settings.gradle.kts` — determine flat or sub-module structure
- [ ] Create modules with appropriate structure
- [ ] Apply convention plugins to each module (`domain-module`, `android-library`/`android-feature`)
- [ ] Verify no cross-feature dependencies are introduced
- [ ] If logic is shared across 2+ features, extract to appropriate `core` submodule
- [ ] Add all dependencies via `libs.versions.toml`, no hardcoded versions
- [ ] Verify `domain` module has no Android imports
