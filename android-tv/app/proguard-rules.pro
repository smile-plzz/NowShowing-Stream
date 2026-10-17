# Proguard rules for NowShowing TV
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.google.gson.** { *; }
-keep class com.nowshowing.tv.data.model.** { *; }
-keep class com.nowshowing.tv.domain.model.** { *; }
