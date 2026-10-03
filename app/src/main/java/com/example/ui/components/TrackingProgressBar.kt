package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PackageStatus
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400

data class TrackingStage(
    val title: String,
    val stageIndex: Int
)

@Composable
fun TrackingProgressBar(
    status: PackageStatus,
    modifier: Modifier = Modifier
) {
    val stages = listOf(
        TrackingStage("Préparé", 1),
        TrackingStage("En transit", 2),
        TrackingStage(
            if (status == PackageStatus.AVAILABLE_FOR_PICKUP) "En relais" else "En livraison",
            3
        ),
        TrackingStage("Livré", 4)
    )

    val currentStep = if (status == PackageStatus.EXCEPTION) 2 else status.stepIndex

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            stages.forEachIndexed { index, stage ->
                val isCompleted = currentStep >= stage.stageIndex
                val isCurrent = currentStep == stage.stageIndex

                val circleColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> EmeraldSuccess
                        isCurrent -> MaterialTheme.colorScheme.primary
                        else -> Slate200
                    },
                    label = "circleColor"
                )

                val iconTint = if (isCompleted || isCurrent) Color.White else Slate400

                // Circle Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(circleColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(iconTint)
                        )
                    }
                }

                // Connecting line between steps
                if (index < stages.size - 1) {
                    val nextStage = stages[index + 1]
                    val isLineFilled = currentStep >= nextStage.stageIndex
                    val lineColor = if (isLineFilled) EmeraldSuccess else Slate200

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .padding(horizontal = 2.dp)
                            .background(lineColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Step labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stages.forEach { stage ->
                val isCompleted = currentStep >= stage.stageIndex
                val isCurrent = currentStep == stage.stageIndex

                Text(
                    text = stage.title,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.primary
                    } else if (isCompleted) {
                        EmeraldSuccess
                    } else {
                        Slate400
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(72.dp)
                )
            }
        }
    }
}
