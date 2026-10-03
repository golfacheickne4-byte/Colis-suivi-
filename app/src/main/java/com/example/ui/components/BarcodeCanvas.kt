package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import kotlin.math.abs

@Composable
fun BarcodeCanvas(
    code: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Generate deterministic bar widths based on code characters
            val hashPattern = generateBarcodePattern(code)
            val totalUnits = hashPattern.sumOf { it }
            if (totalUnits == 0) return@Canvas

            val unitWidth = canvasWidth / totalUnits
            var currentX = 0f

            hashPattern.forEachIndexed { index, barWeight ->
                val barWidth = barWeight * unitWidth
                val isBlackBar = index % 2 == 0
                if (isBlackBar) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(currentX, 0f),
                        size = Size(barWidth, canvasHeight)
                    )
                }
                currentX += barWidth
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatTrackingNumberDisplay(code),
            color = Slate900,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
    }
}

private fun generateBarcodePattern(text: String): List<Int> {
    // Standard pseudo-barcode line alternation: black, white, black, white
    val pattern = mutableListOf<Int>()
    pattern.addAll(listOf(2, 1, 2, 1)) // Start guard bars

    val seed = if (text.isNotBlank()) text else "COLISTRACK"
    for (ch in seed) {
        val v = abs(ch.code)
        val w1 = (v % 3) + 1
        val s1 = ((v / 3) % 2) + 1
        val w2 = ((v / 6) % 3) + 1
        val s2 = 1
        pattern.add(w1)
        pattern.add(s1)
        pattern.add(w2)
        pattern.add(s2)
    }

    pattern.addAll(listOf(2, 1, 3)) // Stop guard bars
    return pattern
}

private fun formatTrackingNumberDisplay(num: String): String {
    // Insert spacing every 4 characters for clean scanning readability
    return num.chunked(4).joinToString(" ")
}
