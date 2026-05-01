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