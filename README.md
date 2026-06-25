# Foodify

![Android CI](https://github.com/erduan-ramadani/foodify/actions/workflows/android.yml/badge.svg)

AI-powered calorie tracker for Android. Built with Kotlin Multiplatform and Jetpack Compose.

[Available on Google Play](https://play.google.com/store/apps/details?id=com.eddiapps.foodify)

## Features

- **AI-powered nutrition recognition** — describe meals in text, voice, or photo
- **Barcode scanning** — scan packaged foods, data from Open Food Facts
- **Detailed analysis** — weekly progress, weight trends, critical nutrients
- **Privacy-first** — no account, no ads, data stays on device
- **12 languages** — DE, EN, ES, FR, IT, PT-BR, HI, TR, PL, RU, NL, IN

## Tech Stack

- **UI:** Jetpack Compose, Material 3
- **Architecture:** Clean Architecture, MVVM
- **DI:** Koin
- **State:** Kotlin Flow / StateFlow
- **Networking:** Ktor (Anthropic API, Open Food Facts API)
- **Local Storage:** DataStore
- **Image Loading:** Coil 3
- **Barcode:** Google ML Kit
- **Analytics:** Firebase Analytics
- **Testing:** JUnit, Coroutines Test, Compose UI Test

## Architecture

- `data/` — Repositories, remote APIs, local persistence
- `domain/` — Interfaces, models, calculation logic
- `presentation/` — ViewModels, Composables, UI state

ViewModels depend on interfaces, not concrete implementations. Repositories are injected via Koin.
State flows reactively from data layer to UI via StateFlow.

## Testing

- **Unit tests** for calculation logic
- **ViewModel tests** with fake repositories
- **Compose UI tests** for key components
- Tests run automatically on every push via GitHub Actions

## Build

Requires JDK 17.

```bash
./gradlew :composeApp:assembleDebug
```

To run tests:

```bash
./gradlew testDebugUnitTest
```

## Roadmap

- Test coverage reporting (JaCoCo)
- Compose UI tests in CI with emulator
- iOS release (currently Android only)

---

Built by [Eddi Ramadani](https://github.com/erduan-ramadani).