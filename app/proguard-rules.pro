# 1watch ProGuard / R8 Rules

# Keep View classes referenced in layout XML
-keep public class com.uno24.wallpaper.Uno24ClockView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Wallpaper Service and AppWidget Provider
-keep public class com.uno24.wallpaper.Uno24WallpaperService { *; }
-keep public class com.uno24.wallpaper.Uno24AppWidgetProvider { *; }

# Keep Enums and their values / valueOf methods
-keepclassmembers enum com.uno24.wallpaper.* {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Data classes and model helpers
-keep class com.uno24.wallpaper.ClockConfig { *; }
-keep class com.uno24.wallpaper.SunTimes { *; }
-keep class com.uno24.wallpaper.UvDataPoint { *; }
-keep class com.uno24.wallpaper.BezelStyle { *; }
-keep class com.uno24.wallpaper.DialTheme { *; }
-keep class com.uno24.wallpaper.HandStyle { *; }
-keep class com.uno24.wallpaper.NumeralStyle { *; }
-keep class com.uno24.wallpaper.NumeralFont { *; }
-keep class com.uno24.wallpaper.NumeralOrientation { *; }
-keep class com.uno24.wallpaper.NumeralDisplayMode { *; }
-keep class com.uno24.wallpaper.BackgroundMode { *; }
-keep class com.uno24.wallpaper.AppLanguage { *; }
