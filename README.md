# Sweet Bites — Offline Cake & Dessert Recipes

A native Android recipe app built with Kotlin, Jetpack Compose, Material 3, MVVM architecture, Room Database with KSP, and Kotlin Coroutines/Flow. All recipes and metadata are stored locally in Room. The app is completely offline and requires no internet permission.

## Features

- **Home Screen**: Featured dessert banner, local search entry, quick category carousel, popular weekly recipes, and pick-up-where-you-left-off recent section.
- **Explore Screen**: Category filtering (Cakes, Cupcakes, Cookies, Brownies, Cheesecakes, Puddings, No Bake, Healthy) with recipe counts and cards.
- **Recipe Details**: High-resolution dessert hero, prep & bake time, difficulty rating, serving size, interactive ingredients preparation checklist, numbered step-by-step instructions, and baker's notes.
- **Baking Mode**: Interactive step-by-step guided baking flow with visual progress bar, large step text, and easy navigation controls.
- **Search**: Fast on-device search across titles, categories, descriptions, and ingredients with instant filtering.
- **Favorites**: Persisted saved recipes in Room database with quick heart toggles.
- **Serving Calculator**: Dynamic serving scaler that automatically recalculates ingredient quantities with factor-based adjustments.
- **Shopping List**: Kitchen ingredient checklist with interactive checkboxes and progress tracking.
- **Recently Viewed**: Fast access to recently viewed recipes persisted via database timestamps.
- **Dark Mode**: Pastel-tuned dark and light Material 3 color schemes.
- **100% Offline**: Requires zero network permissions or cloud dependencies.

## Architecture & Tech Stack

- **Framework**: Jetpack Compose & Material 3
- **Database**: AndroidX Room 2.6.1 with KSP
- **Concurrency**: Kotlin Coroutines & StateFlow with `collectAsStateWithLifecycle`
- **Minimum SDK**: 24 (Android 7.0+)
- **Target SDK**: 35
