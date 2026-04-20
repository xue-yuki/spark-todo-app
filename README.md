# Spark Todo App - Android (Jetpack Compose)

To-do list app dengan Jetpack Compose berdasarkan design dari Claude Design. App ini butuh Laravel API backend untuk fitur lengkapnya.

## 🎨 Design System

App ini menggunakan design system **Spark** dengan:
- ✅ Light & Dark mode support
- ✅ Custom color scheme (earthy/warm tones)
- ✅ Space Grotesk typography
- ✅ Responsive spacing & density options

## 📱 Screens Implemented

### ✅ Sudah Jadi:
1. **Auth Screen** - Login/Register UI
2. **Home Screen** - Dashboard dengan bento layout, next task card, dan quick access widgets
3. **Tasks Screen** - List semua tasks dengan filter dan toggle complete
4. **Add Task Screen** - Form untuk create task baru
5. **Pomodoro Screen** - Timer pomodoro (placeholder)
6. **Focus Screen** - Focus mode (placeholder)
7. **Analytics Screen** - Stats & productivity analytics (placeholder)
8. **Profile Screen** - User profile (placeholder)
9. **Notification Screen** - Notifikasi (placeholder)
10. **Widget Screen** - Widget config (placeholder)

### 🔄 Status:
- ✅ Navigation setup complete
- ✅ Design system implemented
- ✅ Mock data working (local state)
- ⏳ API integration (butuh Laravel backend dulu)

## 🚀 How to Run

### 1. Sync Gradle
Di Android Studio, tunggu Gradle sync selesai (otomatis jalan setelah buka project).

### 2. Run App
- Klik tombol Run (▶️) atau tekan `Shift + F10`
- Pilih emulator atau device fisik
- App akan launch dengan Auth screen

### 3. Test Navigation
- Klik "Continue" di Auth screen → masuk ke Home
- Dari Home bisa navigate ke semua screen lain
- Swipe gestures supported

## 🔧 Next Steps - Yang Harus Dikerjakan

### 1. Build Laravel API
File `API_SPEC.md` sudah ada dan contains:
- ✅ All endpoint specifications
- ✅ Request/Response formats
- ✅ Database schema
- ✅ Validation rules

**Langkah:**
1. Buat Laravel project baru
2. Install Laravel Sanctum untuk auth
3. Buat migrations sesuai schema di `API_SPEC.md`
4. Buat controllers & routes sesuai endpoints
5. Test dengan Postman/Thunder Client

### 2. Integrate API ke Android

**Update file:** `app/src/main/java/com/example/erlangga/data/api/RetrofitClient.kt`

```kotlin
private const val BASE_URL = "http://your-laravel-url.com/api/"
```

**Untuk testing local:**
- Emulator: `http://10.0.2.2:8000/api/`
- Real device: `http://your-local-ip:8000/api/`

### 3. Implement ViewModels

Buat ViewModel untuk setiap screen, contoh:

```kotlin
// TasksViewModel.kt
class TasksViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun loadTasks() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTasks()
                if (response.success) {
                    _tasks.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
```

### 4. Add Features

**Priority tinggi:**
- [ ] DataStore untuk save auth token
- [ ] Pull-to-refresh di Tasks screen
- [ ] Search functionality
- [ ] Task filtering by tag/priority
- [ ] Error handling & loading states

**Priority medium:**
- [ ] Pomodoro timer implementation
- [ ] Focus mode (block apps/notifications)
- [ ] Analytics charts
- [ ] Push notifications

**Nice to have:**
- [ ] Widgets for home screen
- [ ] Offline support (Room database)
- [ ] Task reminders
- [ ] Dark mode toggle in settings

## 📁 Project Structure

```
app/src/main/java/com/example/erlangga/
├── data/
│   ├── api/
│   │   ├── ApiService.kt         # Retrofit interface
│   │   └── RetrofitClient.kt     # Retrofit setup
│   └── models/
│       └── Task.kt               # Data models
├── navigation/
│   ├── Screen.kt                 # Screen routes
│   └── NavGraph.kt               # Navigation setup
├── ui/
│   ├── screens/                  # All screen composables
│   │   ├── AuthScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── TasksScreen.kt
│   │   └── ...
│   ├── components/               # Reusable components
│   └── theme/                    # Design system
│       ├── Color.kt              # Spark colors
│       ├── Type.kt               # Typography
│       ├── Theme.kt              # Theme setup
│       └── Spacing.kt            # Spacing values
└── MainActivity.kt               # Entry point
```

## 🎓 Learning Resources

### Jetpack Compose
- [Official Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Compose Basics](https://developer.android.com/jetpack/compose/layouts/basics)

### API Integration
- [Retrofit + Compose](https://developer.android.com/jetpack/compose/libraries#retrofit)
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)

### State Management
- [ViewModel in Compose](https://developer.android.com/jetpack/compose/state#viewmodel-state)
- [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)

## 🐛 Troubleshooting

### Gradle sync failed
```bash
# Di terminal Android Studio:
./gradlew clean
./gradlew build
```

### API connection failed (emulator)
- Pastikan Laravel running di `php artisan serve`
- Use `10.0.2.2:8000` bukan `localhost:8000`
- Check CORS di Laravel (`cors.php`)

### Dark mode not working
- Phone settings → Display → Dark mode
- Atau force di `MainActivity.kt`: `SparkTodoAppTheme(darkTheme = true)`

## 📝 Notes

- Package name: `com.example.erlangga`
- Min SDK: Android 10.0 (API 29)
- Target SDK: Android 14 (API 36)
- Language: Kotlin
- Build: Gradle (Kotlin DSL)

## 🤝 Collaboration

**Untuk guru:**
File `API_SPEC.md` contains complete API documentation yang harus di-implement di Laravel. Database schema, validation rules, dan response format sudah defined.

**Progress tracking:**
- ✅ Android UI: 90% done (needs polish)
- ⏳ API Integration: 0% (waiting Laravel backend)
- ⏳ Advanced features: 0%

---

**Built with ❤️ using Jetpack Compose**

Project for SMK Telkom Purwokerto - MK-4
