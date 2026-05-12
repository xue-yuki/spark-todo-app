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
import kotlin.math.roundToInt

class SparkWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val tasks       = WidgetCacheManager.getTasks(context)
        val totalActive = WidgetCacheManager.getTaskCount(context)
        val totalDone   = WidgetCacheManager.getCompletedCount(context)
        provideContent {
            WidgetContent(tasks = tasks, totalActive = totalActive, totalDone = totalDone)
        }
    }
}

@Composable
fun WidgetContent(tasks: List<CachedTask>, totalActive: Int, totalDone: Int) {
    val bg       = ColorProvider(R.color.widget_bg)
    val dark     = ColorProvider(R.color.widget_dark)
    val muted    = ColorProvider(R.color.widget_muted)
    val cardSoft = ColorProvider(R.color.widget_card)
    // widget_tag_bg (#E0D9CC) is visibly darker than bg (#F5F0E8) — use for empty segments
    val divider  = ColorProvider(R.color.widget_tag_bg)

    val total      = totalActive + totalDone
    val shownTasks = tasks.take(3)
    val moreCount  = totalActive - shownTasks.size

    // Progress: normalize to 7 segments, always render even when done=0
    val progressSegs = if (total > 0)
        ((totalDone.toFloat() / total) * 7).roundToInt().coerceIn(0, 7)
    else 0

    val subtitle = when {
        total == 0      -> "nothing pending"
        totalDone == 0  -> "today · $totalActive left"
        else            -> "today · $totalDone of $total done"
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bg)
            .cornerRadius(24.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // ── Header ──────────────────────────────────────────────
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(20.dp)
                        .background(dark)
                        .cornerRadius(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✦",
                        style = TextStyle(
                            color = ColorProvider(R.color.widget_bg),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.width(8.dp))

                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = "SPARK",
                        style = TextStyle(
                            color = dark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(1.dp))
                    Text(
                        text = subtitle,
                        style = TextStyle(
                            color = muted,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Box(
                    modifier = GlanceModifier
                        .background(cardSoft)
                        .cornerRadius(99.dp)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Add",
                        style = TextStyle(
                            color = dark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // ── Progress bar — always visible, empty segs use divider color ──
            Row(modifier = GlanceModifier.fillMaxWidth().height(3.dp)) {
                for (i in 0 until 7) {
                    if (i > 0) Spacer(modifier = GlanceModifier.width(2.dp))
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .height(3.dp)
                            .background(if (i < progressSegs) dark else divider)
                            .cornerRadius(2.dp)
                    ) {}
                }
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            // ── Divider ──────────────────────────────────────────────
            Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(divider)) {}
            Spacer(modifier = GlanceModifier.height(8.dp))

            if (tasks.isEmpty()) {
                // ── Empty state ──────────────────────────────────────
                Text(
                    text = "All done for today.",
                    style = TextStyle(color = muted, fontSize = 12.sp)
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
            } else {
                // ── Task rows — natural height, no stretch ────────────
                shownTasks.forEachIndexed { index, task ->
                    WidgetTaskRow(task = task, dark = dark, muted = muted, cardSoft = cardSoft)
                    if (index < shownTasks.lastIndex) {
                        Box(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(divider)
                        ) {}
                    }
                }

                // Push footer to bottom
                Spacer(modifier = GlanceModifier.defaultWeight())

                // ── Footer ───────────────────────────────────────────
                Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(divider)) {}
                Spacer(modifier = GlanceModifier.height(7.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (moreCount > 0) "+$moreCount more today" else "All shown",
                        style = TextStyle(color = muted, fontSize = 10.5.sp)
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "See all ›",
                        style = TextStyle(
                            color = dark,
                            fontSize = 10.5.sp,
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
    dark: ColorProvider,
    muted: ColorProvider,
    cardSoft: ColorProvider
) {
    val priorityColor = when (task.priority.uppercase()) {
        "HIGH"   -> ColorProvider(R.color.widget_priority_high)
        "MEDIUM" -> ColorProvider(R.color.widget_priority_med)
        else     -> ColorProvider(R.color.widget_priority_low)
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(6.dp)
                .background(priorityColor)
                .cornerRadius(3.dp)
        ) {}

        Spacer(modifier = GlanceModifier.width(10.dp))

        Text(
            text = task.title,
            style = TextStyle(
                color = dark,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight()
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        Box(
            modifier = GlanceModifier
                .background(cardSoft)
                .cornerRadius(4.dp)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = task.tag.uppercase(),
                style = TextStyle(
                    color = muted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.width(8.dp))

        Text(
            text = task.time,
            style = TextStyle(
                color = muted,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

class SparkWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SparkWidget()
}
