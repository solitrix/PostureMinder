
# Project: Kotlin Compose Multiplatform — Clean Architecture + MVI

## Platforms
- Android, iOS, Desktop (JVM), Web (Wasm/JS)
- Shared code lives in `shared/commonMain`; platform specifics in
  `androidMain`, `iosMain`, `desktopMain`, `wasmJsMain`

## Architecture: Clean Architecture + MVI

### Layer rules (strict — no skipping layers)

UI (Compose)
└── ViewModel  (MVI: sends Actions, exposes StateFlow<UiState> +
SharedFlow<SideEffect>)
└── UseCase  (one public function, returns Flow or suspend)
└── Repository interface  (defined in domain, implemented in
data)
└── DataSource  (remote, local, or both)

- **Domain** (`domain/`) — pure Kotlin only. No Android, Compose, or platform
  imports. No coroutine framework imports beyond `kotlinx.coroutines.flow`.
- **Data** (`data/`) — implements domain interfaces. Room, Ktor, DataStore
  live here.
- **UI** (`ui/`) — Compose screens, ViewModels, UiState, Action, SideEffect.
  No business logic.
- **DI** (`di/`) — Koin modules only. No manual instantiation in UI or domain.

### MVI contracts (enforce for every feature)

Each feature has exactly these three types, co-located in the ViewModel file
or a `contract/` subfolder:

  ```kotlin
  data class UiState(...)          // immutable, rendered by Compose
  sealed interface Action          // user intents dispatched to ViewModel
  sealed interface SideEffect      // one-shot events (navigation, toasts)

  ViewModel shape:
  class FooViewModel(useCase: FooUseCase) : ViewModel() {
      private val _state = MutableStateFlow(UiState())
      val state: StateFlow<UiState> = _state.asStateFlow()

      private val _effects = MutableSharedFlow<SideEffect>(extraBufferCapacity =
  1)
      val effects: SharedFlow<SideEffect> = _effects.asSharedFlow()

      fun onAction(action: Action) { ... }
  }
  ```
 
  ---
  File & Package Structure

  shared/src/commonMain/kotlin/com/<org>/<app>/
    domain/
      model/          # pure data classes, no annotations
      repository/     # interfaces only
      usecase/        # one class per use case, one public operator fun invoke()
    data/
      repository/     # implementations of domain interfaces
      local/          # Room DAOs, entities, DB
      remote/         # Ktor clients, DTOs, mappers
      mapper/         # extension funs: DtoToModel, EntityToModel
    ui/
      <feature>/
        <Feature>Screen.kt
        <Feature>ViewModel.kt      # contains UiState / Action / SideEffect
        components/
    di/
      DomainModule.kt
      DataModule.kt
      UiModule.kt
  shared/src/commonMain/composeResources/values/strings.xml

  ---
  Coding rules

  General

  - All UI strings go in strings.xml (composeResources). Never hardcode display
  strings in .kt files.
  - Use stringResource(Res.string.xxx) — never raw string literals in Text(),
  button labels, content descriptions, or dialog titles.
  - One class per file. File name matches class name.
  - No object singletons outside DI modules. Use Koin for lifecycle-scoped
  instances.
  - Prefer sealed interface over sealed class for Action and SideEffect
  hierarchies.
  - Domain models are data class with no framework annotations.
  - Data entities (Room) and DTOs (Ktor) are separate classes from domain
  models; map between them explicitly.

  Coroutines & Flow

  - ViewModels use viewModelScope. UseCases return Flow or are suspend fun.
  - Never collect flows in non-UI layers. Pass Flow up, let the ViewModel
  collect.
  - Use stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
  initialValue) for UI-facing StateFlows.

  Compose

  - Screens are stateless: accept state: UiState + onAction: (Action) -> Unit.
  - Never call ViewModel directly from a child composable — thread actions up.
  - LaunchedEffect for side-effect collection only. No business logic in
  composables.
  - Preview every screen composable with @Preview using a fake UiState.

  Multiplatform

  - expect/actual only for genuine platform differences (file I/O,
  notifications, permissions). Prefer commonMain implementations wherever
  possible.
  - Do not use @Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
  wholesale — fix the underlying structure instead.

  ---
  Testing: comprehensive, one test class per production class

  Layer coverage requirements

  ┌─────────────────┬──────────────────┬────────────────────────────────────┐
  │      Layer      │       Tool       │            What to test            │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ UseCase         │ kotlin.test +    │ All Flow emissions, error paths,   │
  │                 │ Turbine          │ edge cases                         │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ Repository      │ kotlin.test +    │ Delegates correctly to             │
  │ (impl)          │ fakes            │ DataSources; maps correctly        │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ ViewModel       │ kotlin.test +    │ State transitions for every        │
  │                 │ Turbine          │ Action; SideEffects emitted        │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ DataSource/DAO  │ Room in-memory   │ CRUD correctness, FK constraints,  │
  │                 │ DB               │ query filters                      │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ Mapper          │ kotlin.test      │ Every field, null/default handling │
  ├─────────────────┼──────────────────┼────────────────────────────────────┤
  │ Composables     │ Compose UI test  │ Key states: loading, success,      │
  │                 │                  │ empty, error                       │
  └─────────────────┴──────────────────┴────────────────────────────────────┘

  Test conventions

  - Fakes over mocks: write Fake<Interface> implementations in testFixtures/ or
  commonTest/fake/.
  - Test class name: <ClassName>Test.
  - Test function name: `given X when Y then Z`() (backtick style).
  - Each test: one arrange / act / assert block, no shared mutable state between
  tests.
  - Use TestCoroutineScheduler + UnconfinedTestDispatcher for all ViewModel and
  UseCase tests.
  - Use app.cash.turbine.test { } for Flow assertions.
  - Target 100% branch coverage on UseCases and ViewModels; minimum 80% on data
  layer.

  Example ViewModel test shape

  class FooViewModelTest {
      private val fakeUseCase = FakeFooUseCase()
      private val viewModel get() = FooViewModel(fakeUseCase)

      @Test
      fun `given data loads when init then state is Success`() = runTest {
          fakeUseCase.emit(Result.success(listOf(fakeItem)))
          viewModel.state.test {
              assertEquals(UiState(items = listOf(fakeItem)), awaitItem())
          }
      }
  }

  ---
  Dependencies (do not add without justification)

  ┌───────────────────┬──────────────────────────┐
  │      Purpose      │         Library          │
  ├───────────────────┼──────────────────────────┤
  │ DI                │ Koin KMP                 │
  ├───────────────────┼──────────────────────────┤
  │ Networking        │ Ktor                     │
  ├───────────────────┼──────────────────────────┤
  │ Local DB          │ Room KMP                 │
  ├───────────────────┼──────────────────────────┤
  │ Preferences       │ DataStore KMP            │
  ├───────────────────┼──────────────────────────┤
  │ Serialization     │ kotlinx.serialization    │
  ├───────────────────┼──────────────────────────┤
  │ Date/Time         │ kotlinx.datetime         │
  ├───────────────────┼──────────────────────────┤
  │ Flow testing      │ Turbine                  │
  ├───────────────────┼──────────────────────────┤
  │ Coroutine testing │ kotlinx-coroutines-test  │
  ├───────────────────┼──────────────────────────┤
  │ Navigation        │ Compose Navigation (KMP) │
  └───────────────────┴──────────────────────────┘

  Justify any addition with a comment in build.gradle.kts. Do not add a library
  if stdlib or an existing dependency already covers the need.

  ---
  What NOT to do

  - No logic in composables — move it to ViewModel or UseCase.
  - No repository calls directly from ViewModel — always go through a UseCase.
  - No platform-specific imports in commonMain domain or use-case code.
  - No skipping the mapper layer (no Room entity leaking into the UI layer).
  - No runBlocking in tests — use runTest.
  - No hardcoded strings in UI code — use strings.xml.
  - Do not suppress lint/compiler warnings with @Suppress without a comment
  explaining why.

## Access Rules

- Never read, search, write, or otherwise access files outside this project's
working directory (`./code`). This includes other home-directory locations, and
any other absolute path outside this repo.

- If you need to verify a third-party library/API (e.g. a Gradle dependency's
class shape), do NOT extract or inspect jars. Instead: check vendored sources or docs within this repo,
rely on documented/well-known API knowledge, or ask the user.
