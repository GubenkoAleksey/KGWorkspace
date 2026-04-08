---
name: android-compose-ui
description: |
  Compose UI patterns for Android/KMP - stability, recomposition, side effects, lazy lists, animations, previews, accessibility, modifier extensions, and design system composables. Use this skill whenever writing or reviewing composables, optimizing recomposition, adding animations, creating previews, writing custom modifiers, structuring a design system, or making any Compose UI decision beyond the MVI/ViewModel layer. Trigger on phrases like "composable", "recomposition", "LaunchedEffect", "Modifier", "LazyColumn", "preview", "animation", "design system", "stability", "contentDescription", "graphicsLayer", "slot API", or "Compose performance".
---

# Android / KMP Compose UI Patterns

## Core Principle

The UI is dumb. Composables render state and forward user actions — nothing more. All state lives in the ViewModel. All logic lives in the ViewModel, domain, or data layer. Compose code should contain zero business logic, zero data transformation, and minimal side effects.

---

## Stability & Recomposition

Strong skipping mode is enabled by default in modern Compose — no explicit opt-in needed.

Only annotate a state data class with `@Stable` when it contains fields the Compose compiler considers unstable (e.g., `List`, `Map`, `Set`, interfaces, or abstract types). If all fields are primitive types, `String`, or other stable types, no annotation is needed.

```kotlin
// Needs @Stable — contains a List (unstable by default)
@Stable
data class NoteListState(
    val notes: List<NoteUi> = emptyList(),
    val isLoading: Boolean = false
)

// No annotation needed — all fields are stable
data class NoteDetailState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false
)
```

---

## State Ownership

All state lives in the ViewModel. Do not use `remember` or `rememberSaveable` for application state — that belongs in the ViewModel's `StateFlow` and is surfaced via `collectAsStateWithLifecycle()`.

The only exception is Compose-internal state that the framework requires you to hold in composition, such as `LazyListState`, `ScrollState`, or `PagerState`:

```kotlin
// Acceptable — Compose-owned UI state
val lazyListState = rememberLazyListState()

// Reacting to Compose-owned state with derivedStateOf
// derivedStateOf runs on every scroll pixel but only triggers
// recomposition when the RESULT changes (false → true or vice versa)
val showScrollToTop by remember {
    derivedStateOf { lazyListState.firstVisibleItemIndex > 5 }
}
```

`derivedStateOf` should only be used for Compose-internal state driven derived values. If the derivation can happen in the ViewModel, it should.

Always collect ViewModel state with lifecycle awareness:
```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()
```

---

## Side Effects

Side effects should be avoided when possible. If something can be handled by the ViewModel through an Intent/Action, do that instead.

When a side effect is truly necessary (e.g., Android lifecycle APIs), extract it into a dedicated composable:

```kotlin
@Composable
fun ObserveLifecycle(onStart: () -> Unit, onStop: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
```

`LaunchedEffect` is acceptable when genuinely needed, but question whether the work belongs in the ViewModel first. Do not use custom `CompositionLocal`s.

---

## Lazy Layouts

Add `key` to lazy list items when there is an obvious unique identifier:

```kotlin
LazyColumn {
    items(
        items = state.notes,
        key = { it.id }
    ) { note ->
        NoteItem(note = note, onClick = { onIntent(NoteListIntent.OnNoteClick(note.id)) })
    }
}
```

---

## Animations

Prefer approaches that animate below the recomposition layer:

```kotlin
// Good — animates without recomposition
val alpha by animateFloatAsState(if (state.isVisible) 1f else 0f)
Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })

// Bad — causes recomposition on every frame
Box(modifier = Modifier.alpha(animatedAlpha))
```

**Deferred state reads:** Pass animated values as lambdas to defer state reading to layout/draw phase:

```kotlin
// Good — deferred read
fun Modifier.animatedOffset(offsetProvider: () -> IntOffset) = offset { offsetProvider() }
```

---

## Modifier Extensions

Prefer plain `Modifier` extension functions. Do not make modifier extensions `@Composable` — modifiers are transformations, not UI elements. State belongs in the composable that uses the modifier, not inside it.

```kotlin
// Good — plain extension
fun Modifier.shimmerEffect(): Modifier = composed { ... }

// Good — modifier factory
fun Modifier.roundedBackground(color: Color, radius: Dp) =
    background(color, RoundedCornerShape(radius))

// Bad — @Composable modifier owns state, unpredictable recomposition
@Composable
fun Modifier.someEffect(): Modifier { ... }
```

---

## Design System & Slot APIs

The design system lives in `:core` and contains reusable Compose components, colors, theme, and typography. See component decomposition rules in `android-presentation-mvi` skill.

Use slot APIs for design system components that need flexible content areas:

```kotlin
// Slot API — core component
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier) {
        header()
        content()
    }
}
```

Feature-level composables should prefer typed parameters over slots for clarity.

---

## Previews

Every Content composable (*Content.kt) must have at least one meaningful `@Preview` with realistic state. Add separate previews for different states (Loading, Error, Empty, Content):

```kotlin
@Preview
@Composable
private fun NoteListContentPreview() {
    AppTheme {
        NoteListContent(
            state = NoteListState.Content(
                notes = listOf(
                    NoteUi("1", "Meeting notes", "Mar 15"),
                    NoteUi("2", "Shopping list", "Mar 14")
                )
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun NoteListContentLoadingPreview() {
    AppTheme {
        NoteListContent(state = NoteListState.Loading, onIntent = {})
    }
}
```

---

## Accessibility

Use meaningful `contentDescription` on all interactive or informational visual elements via string resources:

```kotlin
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = stringResource(R.string.cd_delete_note)
)
```

For decorative elements that convey no information, set `contentDescription = null`.

---

## TextField

Text input state lives in the ViewModel. Every keystroke dispatches an Intent/Action:

```kotlin
AppTextField(
    value = state.title,
    onValueChange = { onIntent(NoteEditorIntent.OnTitleChange(it)) }
)
```
