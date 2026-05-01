# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Deine Domain-Modelle (für @Serializable)
-keep class com.ercoding.foodify.domain.model.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Ktor uses java.lang.management for debugger detection - not available on Android
-dontwarn java.lang.management.**

-dontwarn kotlinx.serialization.**
