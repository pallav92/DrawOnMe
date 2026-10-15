# DrawOnMe Proguard / R8 Configuration

# Preserve line numbers and source file names for readable stack traces
-keepattributes SourceFile,LineNumberTable

# Preserve domain models
-keep class com.draw.onme.domain.model.** { *; }

# Compose rules
-dontwarn androidx.compose.**

# AndroidX Startup
-keep class androidx.startup.** { *; }
-keep class * extends androidx.startup.Initializer {
    public <init>();
}

# Room Database implementations generated classes & reflection
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
}
-dontwarn androidx.room.paging.**

# WorkManager (used transitively by ML Kit for model management)
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}
-keep class androidx.work.impl.** { *; }
-dontwarn androidx.work.impl.**

# ML Kit Digital Ink Recognition
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
