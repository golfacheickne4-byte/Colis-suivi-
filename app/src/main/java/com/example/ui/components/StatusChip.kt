package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PackageStatus

@Composable
fun StatusChip(
    status: PackageStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = Color(status.colorHex)
    val bgColor = Color(status.lightBgColorHex)

    val icon = when (status) {
        PackageStatus.ORDER_CONFIRMED -> Icons.Default.Schedule
        PackageStatus.INFO_RECEIVED -> Icons.Default.Inventory2
        PackageStatus.IN_TRANSIT -> Icons.Default.LocalShipping
        PackageStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
        PackageStatus.AVAILABLE_FOR_PICKUP -> Icons.Default.Place
        PackageStatus.DELIVERED -> Icons.Default.CheckCircle
        PackageStatus.EXCEPTION -> Icons.Default.ErrorOutline
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.label,
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
