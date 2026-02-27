<!-- TODO: Temporary readme -->
# 🎮 2048

The famous 2048 game made with **Kotlin** for Android.

## Tech stack

| Tool                | Purpose         |
|---------------------|-----------------|
| Kotlin              | Language        |
| Android SDK 36      | Target platform |
| Gradle (Kotlin DSL) | Build system    |
| ktlint              | Code formatting |
| detekt              | Static analysis |

## Getting started

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

## Code quality

```bash
# Check formatting
./gradlew ktlintCheck

# Auto-fix formatting
./gradlew ktlintFormat

# Static analysis
./gradlew detekt
```

## License

This project is licensed under the [MIT License](LICENSE).
