# Project-specific R8 rules.
# Most library rules are supplied by the dependencies themselves.

# Gson/TypeToken and annotation-based libraries rely on these attributes.
-keepattributes Signature
-keepattributes *Annotation*

# Ktor/CIO can reference JDK management APIs that are not present on Android.
-dontwarn java.lang.management.**
