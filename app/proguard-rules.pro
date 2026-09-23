# DrawOnMe Proguard / R8 Configuration

# Preserve line numbers and source file names for readable stack traces
-keepattributes SourceFile,LineNumberTable

# Preserve domain models
-keep class com.draw.onme.domain.model.** { *; }

# Compose rules
-dontwarn androidx.compose.**
