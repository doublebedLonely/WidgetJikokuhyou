package com.example.directjikokuhyou

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.RemoteViews
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 複数のウィジェットがある場合に対応
        for (appWidgetId in appWidgetIds) {
            // ウィジェットレイアウトを取得
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // ボタンのクリックイベントを設定
            val intent = Intent(context, WidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)

            // 現在の時刻を取得
            val currentTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            // 取得した現在時刻をwidget_textに設定
            views.setTextViewText(R.id.widget_text, "Current Time: $currentTime")

            // Bitmapを作成
            val bitmap = createBitmapWithCustomFont(
                context,
                currentTime,
                R.font.pixelfont, // フォントリソース
                50f // テキストサイズ
            )

            // BitmapをImageViewに設定
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // ウィジェットを更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
            println("update widget")
        }
    }

    fun createBitmapWithCustomFont(
        context: Context,
        text: String,
        fontResId: Int,
        textSize: Float
    ): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.LEFT
            this.textSize = textSize

            // フォントを設定
            typeface = Typeface.create(context.resources.getFont(fontResId), Typeface.NORMAL)
        }

        // テキストの幅と高さを計算
        val textWidth = paint.measureText(text).toInt()
        val textHeight = (paint.descent() - paint.ascent()).toInt()

        // Bitmapを生成
        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, -paint.ascent(), paint)
        return bitmap
    }
}