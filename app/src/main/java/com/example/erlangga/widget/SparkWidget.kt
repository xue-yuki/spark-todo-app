package com.example.erlangga.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import com.example.erlangga.MainActivity
import com.example.erlangga.R
import com.example.erlangga.data.CachedTask
import com.example.erlangga.data.WidgetCacheManager

class SparkWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val tasks = WidgetCacheManager.getTasks(context)
        val totalActive = WidgetCacheManager.getTaskCount(context)
        provideContent {
            WidgetContent(tasks = tasks, totalActive = totalActive)
        }
    }
}

@Composable
fun WidgetContent(tasks: List<CachedTask>, totalActive: Int) {
    val bg     = ColorProvider(R.color.widget_bg)
    val cardBg = ColorProvider(R.color.widget_card)
    val dark   = ColorProvider(R.color.widget_dark)
    val muted  = ColorProvider(R.color.widget_muted)
    val tagBg  = ColorProvider(R.color.widget_tag_bg)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bg)
            .cornerRadius(28.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = "⚡ Spark",
                        style = TextStyle(
                            color = dark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = if (totalActive == 0) "You're all caught up!" else "$totalActive task${if (totalActive > 1) "s" else ""} remaining",
                        style = TextStyle(color = muted, fontSize = 12.sp)
                    )
                }

                Spacer(modifier = GlanceModifier.width(12.dp))

                // Badge
                Box(
                    modifier = GlanceModifier
                        .background(dark)
                        .cornerRadius(99.dp)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (totalActive > 0) "$totalActive" else "✓",
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_bg),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(16.dp))

            // Divider
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(tagBg)
            ) {}

            Spacer(modifier = GlanceModifier.height(14.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Nothing pending 🎉",
                            style = TextStyle(
                                color = dark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(6.dp))
                        Text(
                            text = "Tap to add a new task",
                            style = TextStyle(color = muted, fontSize = 12.sp)
                        )
                    }
                }
            } else {
                // Task list — fills available space
                Column(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    verticalAlignment = Alignment.Top
                ) {
                    tasks.forEachIndexed { index, task ->
                        WidgetTaskRow(task = task, cardBg = cardBg, dark = dark, muted = muted, tagBg = tagBg)
                        if (index < tasks.lastIndex) {
                            Spacer(modifier = GlanceModifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Open app button — full width
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(dark)
                        .cornerRadius(14.dp)
                        .padding(vertical = 12.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open Spark →",
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_bg),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetTaskRow(
    task: CachedTask,
    cardBg: ColorProvider,
    dark: ColorProvider,
    muted: ColorProvider,
    tagBg: ColorProvider
) {
    val priorityColor = when (task.priority) {
        "HIGH"   -> ColorProvider(R.color.widget_priority_high)
        "MEDIUM" -> ColorProvider(R.color.widget_priority_med)
        else     -> ColorProvider(R.color.widget_priority_low)
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(cardBg)
            .cornerRadius(14.dp)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Priority dot
        Box(
            modifier = GlanceModifier
                .size(9.dp)
                .background(priorityColor)
                .cornerRadius(5.dp)
        ) {}

        Spacer(modifier = GlanceModifier.width(12.dp))

        // Title
        Text(
            text = task.title,
            style = TextStyle(
                color = dark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight()
        )

        Spacer(modifier = GlanceModifier.width(10.dp))

        // Tag pill
        Box(
            modifier = GlanceModifier
                .background(tagBg)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = task.tag,
                style = TextStyle(
                    color = muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

class SparkWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SparkWidget()
}
