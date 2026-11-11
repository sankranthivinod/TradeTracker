# ProGuard / R8 Keep Rules for TradeTracker

# Room Database keep rules
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# Keep Entities, DAOs, and Models
-keep class com.sankranthivinod.tradetracker.data.local.** { *; }
-keep class com.sankranthivinod.tradetracker.domain.model.** { *; }

# Keep ViewModel & UI States
-keep class com.sankranthivinod.tradetracker.presentation.** { *; }
