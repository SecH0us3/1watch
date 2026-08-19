# 1watch ProGuard / R8 Rules

# Keep View classes referenced in layout XML
-keep public class com.watch1.app.WatchClockView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Wallpaper Service and AppWidget Provider
-keep public class com.watch1.app.WatchWallpaperService { *; }
-keep public class com.watch1.app.WatchAppWidgetProvider { *; }

# Keep Enums and their values / valueOf methods
-keepclassmembers enum com.watch1.app.* {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Data classes and model helpers
-keep class com.watch1.app.ClockConfig { *; }
-keep class com.watch1.app.SunTimes { *; }
-keep class com.watch1.app.UvDataPoint { *; }
-keep class com.watch1.app.BezelStyle { *; }
-keep class com.watch1.app.DialTheme { *; }
-keep class com.watch1.app.HandStyle { *; }
-keep class com.watch1.app.NumeralStyle { *; }
-keep class com.watch1.app.NumeralFont { *; }
-keep class com.watch1.app.NumeralOrientation { *; }
-keep class com.watch1.app.NumeralDisplayMode { *; }
-keep class com.watch1.app.BackgroundMode { *; }
-keep class com.watch1.app.AppLanguage { *; }
