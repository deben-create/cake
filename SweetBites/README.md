# Sweet Bites — Offline Cake & Dessert Recipes

A native Android recipe app starter built with Kotlin, Jetpack Compose, Material 3, MVVM-style state, Room, and Kotlin Coroutines/Flow. All seeded recipes and recipe metadata are stored in the local Room database. The app declares **no INTERNET permission**.

## Included screens

- Home: featured banner, local search entry, category shortcuts, popular recipes, recently viewed recipes
- Explore: category filters and recipe cards
- Recipe detail: prep/bake time, difficulty, servings, ingredients checklist, instructions, baker's note, save-to-favorites action
- Baking mode: step-by-step cooking flow with progress
- Search: searches recipe title, category, description, and ingredients on-device
- Favorites: Room-backed saved recipes
- More: dark mode, shopping list, recent recipes, serving calculator, about
- Shopping list: local tick-off checklist
- Serving calculator: serving count control and ingredient list
- About screen

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room database (`sweet_bites.db`), seeded with 12 sample recipes on first launch
- ViewModel + StateFlow and lifecycle-aware collection
- Min SDK 24 / Target SDK 35 / Java 17
- No login, analytics, remote API, or internet permission

## Open and run

1. Install Android Studio (a recent stable version) and Android SDK Platform 35.
2. Open the `SweetBites` folder in Android Studio.
3. Allow Gradle sync to finish. Use JDK 17.
4. Run the `app` configuration on an emulator or Android device (Android 7.0/API 24 or newer).

This source package intentionally does not bundle the Gradle wrapper JAR. Android Studio can open the Gradle project and configure its installed Gradle tooling; if you prefer command-line builds, add a wrapper using the Gradle version recommended by your installed Android Gradle Plugin.

## Project structure

```
app/src/main/java/com/example/sweetbites/
  MainActivity.kt                 Compose screens and navigation state
  MainViewModel.kt                UI state and repository actions
  data/Recipe.kt                  Room entity and parsing helpers
  data/RecipeDao.kt               Database queries
  data/RecipeRepository.kt        Seed recipes and repository
  data/SweetBitesDatabase.kt      Room database singleton
  ui/theme/Theme.kt               Pastel pink / dark Material 3 color schemes
```

## Customization notes

- Add recipes to `RecipeRepository.seedRecipes`. Separate ingredient and step entries with `|`.
- Recipe illustrations currently use bundled Unicode dessert symbols and locally defined pastel color fields, so they render without network access. Replace `DessertTile` with local drawable resources if you want photographic recipe art; keep those images in `app/src/main/res/drawable` or `assets` to preserve offline behavior.
- Favorites and viewed timestamps persist in Room. The shopping checklist and current cooking-step selection are currently session-local; move them into Room/DataStore if you want those particular states to persist after app restarts.
- The calculator provides serving adjustment UI and a factor-based ingredient view; for production release, add a robust quantity parser for mixed fractions and locale-specific units.

## Notes

This is a complete starter project with functional local recipe browsing and persistence. Please build and test on your intended Android Studio / SDK version before publishing. Recipe timing and quantities are illustrative and should be validated before use.
