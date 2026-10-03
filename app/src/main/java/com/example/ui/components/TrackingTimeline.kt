package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PackageStatus
import com.example.data.model.TrackingEventEntity
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun TrackingTimeline(
    events: List<TrackingEventEntity>,
    modifier: Modifier = Modifier
) {
    val sortedEvents = events.sortedByDescending { it.timestamp }

    if (sortedEvents.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucune étape d'acheminement enregistrée pour l'instant.",
                    color = Slate500,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        sortedEvents.forEachIndexed { index, event ->
            val isFirst = index == 0
            val isLast = index == sortedEvents.size - 1

            TimelineItem(
                event = event,
                isLatest = isFirst,
                isLast = isLast
            )
        }
    }
}

@Composable
private fun TimelineItem(
    event: TrackingEventEntity,
    isLatest: Boolean,
    isLast: Boolean
) {
    val nodeColor = if (isLatest) {
        Color(event.status.colorHex)
    } else {
        Slate400
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Date/Time Column on Left
        Column(
            modifier = Modifier
                .width(68.dp)
                .padding(end = 8.dp, top = 2.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = event.timeFormatted.ifBlank { "--:--" },
                fontSize = 13.sp,
                fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Medium,
                color = if (isLatest) MaterialTheme.colorScheme.primary else Slate700
            )
            Text(
                text = event.dateFormatted,
                fontSize = 10.sp,
                color = Slate400
            )
        }

        // Timeline spine & node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            // Node circle
            Box(
                modifier = Modifier
                    .size(if (isLatest) 22.dp else 16.dp)
                    .clip(CircleShape)
                    .background(nodeColor),
                contentAlignment = Alignment.Center
            ) {
                if (isLatest) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Vertical line connector
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(Slate200)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Content Card
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isLatest) {
                        Color(event.status.lightBgColorHex)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isLatest) 2.dp else 0.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = event.title,
                        fontSize = 14.sp,
                        fontWeight = if (isLatest) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isLatest) Color(event.status.colorHex) else Slate900
                    )

                    if (event.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.location,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate600
                            )
                        }
                    }

                    if (event.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = event.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Slate600
                        )
                    }
                }
            }
        }
    }
}
