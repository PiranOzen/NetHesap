# Project specific ProGuard rules

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep @interface retrofit2.http.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# SimpleXML
-keep @org.simpleframework.xml.** class * { *; }
-keep class org.simpleframework.xml.** { *; }
-dontwarn org.simpleframework.xml.**

# Keep DTOs (Models used for JSON/XML parsing)
-keep class com.example.nethesap.data.remote.dto.** { *; }

# Hilt / Dagger
-keep class dagger.hilt.android.internal.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$ComponentManager
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.EntryPoint class *

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext$HandlerPost {
    private void <init>(java.lang.Runnable, android.os.Handler);
}

# Preserve line numbers for debugging
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
