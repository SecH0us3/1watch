package com.watch1.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.min

class WatchAppWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE_WIDGETS = "com.watch1.app.ACTION_UPDATE_WIDGETS"
        private const val ALARM_REQUEST_CODE = 2001
        private val renderer = WatchDialRenderer()

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, WatchAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                for (appWidgetId in appWidgetIds) {
                    updateSingleWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }

        fun updateSingleWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 150)
            val minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 150)

            val displayMetrics = context.resources.displayMetrics
            val density = displayMetrics.density

            val widthPx = (max(minWidthDp, 100) * density).toInt().coerceIn(200, 1080)
            val heightPx = (max(minHeightDp, 100) * density).toInt().coerceIn(200, 1080)
            val sizePx = min(widthPx, heightPx)

            val bitmap = renderWidgetBitmap(context, sizePx, sizePx)

            val views = RemoteViews(context.packageName, R.layout.widget_watch_layout)
            views.setImageViewBitmap(R.id.widgetClockImage, bitmap)

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, pendingIntentFlags)
            views.setOnClickPendingIntent(R.id.widgetRootContainer, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun renderWidgetBitmap(context: Context, width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val config = LocationHelper.loadConfig(context)
            val now = LocalTime.now()
            val date = LocalDate.now()
            val hourFraction = now.hour + now.minute / 60.0 + now.second / 3600.0
            val zoneOffsetHours = ZoneId.systemDefault().rules.getOffset(java.time.Instant.now()).totalSeconds / 3600.0

            val sunTimes = SolarCalculator.calculateSunTimes(config.lat, config.lon, date, zoneOffsetHours)
            val uvData = if (config.showUv || config.showUvIndex) {
                UvRepository.getCachedOrFallbackUv(context, config.lat, config.lon, date)
            } else {
                null
            }

            val bgBitmap = if (config.bgMode == BackgroundMode.CUSTOM_IMAGE) {
                BackgroundImageHelper.loadBitmap(context)
            } else {
                null
            }

            renderer.draw(
                canvas = canvas,
                width = width,
                height = height,
                timeHourFraction = hourFraction,
                sunTimes = sunTimes,
                theme = config.theme,
                showUv = config.showUv,
                showDate = config.showDate,
                showUvIndex = config.showUvIndex,
                date = date,
                uvData = uvData,
                numeralStyle = config.numeralStyle,
                numeralOrientation = config.numeralOrientation,
                numeralDisplayMode = config.numeralDisplayMode,
                fontSizeScale = config.fontSizeScale,
                numeralFont = config.numeralFont,
                handStyle = config.handStyle,
                bgMode = config.bgMode,
                customColor = config.customColor,
                bgBitmap = bgBitmap,
                isWallpaper = false,
                bezelStyle = config.bezelStyle,
                showBrandLogo = config.showBrandLogo,
                gradientDayNight = config.gradientDayNight,
                showMoonPhase = config.showMoonPhase,
                showGoldenHour = config.showGoldenHour,
                showSolarNoon = config.showSolarNoon,
                redNightMode = config.redNightMode
            )

            return bitmap
        }

        fun scheduleNextMinuteAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, WatchAppWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGETS
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, flags)

            val now = System.currentTimeMillis()
            val millisUntilNextMinute = 60000L - (now % 60000L)
            val triggerAtMillis = SystemClock.elapsedRealtime() + millisUntilNextMinute

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.ELAPSED_REALTIME,
                            triggerAtMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.ELAPSED_REALTIME,
                            triggerAtMillis,
                            pendingIntent
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        }

        fun cancelMinuteAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, WatchAppWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGETS
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, flags)
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateSingleWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleNextMinuteAlarm(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateSingleWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleNextMinuteAlarm(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelMinuteAlarm(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_UPDATE_WIDGETS,
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_USER_PRESENT -> {
                updateAllWidgets(context)
                scheduleNextMinuteAlarm(context)
            }
        }
    }
}
