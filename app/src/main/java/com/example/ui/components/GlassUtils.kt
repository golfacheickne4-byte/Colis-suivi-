package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Liquid Glass Palette tokens
val GlassWhiteHigh = Color(0xF5FFFFFF)
val GlassWhiteMedium = Color(0xD9FFFFFF)
val GlassWhiteSubtle = Color(0x99FFFFFF)
val GlassWhiteBorderLight = Color(0xE6FFFFFF)
val GlassWhiteBorderDark = Color(0x33FFFFFF)

val LiquidBlueDark = Color(0xFF0F2042)
val LiquidBlueMid = Color(0xFF1E3A8A)
val LiquidCyanGlow = Color(0xFF06B6D4)
val LiquidVioletGlow = Color(0xFF7C3AED)
val LiquidAmberGlow = Color(0xFFF59E0B)

/**
 * Animated liquid fluid background that flows under the frosted glass cards.
 */
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "LiquidMotion")

    val animOffset1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb1"
    )

    val animOffset2 by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb2"
    )

    val animOffset3 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val w = size.width
                val h = size.height

                // Base soft gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F4F8),
                            Color(0xFFE2E8F0),
                            Color(0xFFEDF2F7)
                        )
                    )
                )

                // Liquid Floating Orb 1 (Cyan / Aqua liquid sheen)
                val orb1Center = Offset(
                    x = w * (0.2f + 0.3f * animOffset1),
                    y = h * (0.15f + 0.2f * animOffset2)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LiquidCyanGlow.copy(alpha = 0.28f),
                            LiquidCyanGlow.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = orb1Center,
                        radius = w * 0.75f
                    ),
                    center = orb1Center,
                    radius = w * 0.75f
                )

                // Liquid Floating Orb 2 (Royal Blue depth)
                val orb2Center = Offset(
                    x = w * (0.8f - 0.25f * animOffset2),
                    y = h * (0.45f + 0.25f * animOffset1)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.24f),
                            Color(0xFF1D4ED8).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = orb2Center,
                        radius = w * 0.85f
                    ),
                    center = orb2Center,
                    radius = w * 0.85f
                )

                // Liquid Floating Orb 3 (Violet / Indigo refraction)
                val orb3Center = Offset(
                    x = w * (0.3f + 0.4f * animOffset3),
                    y = h * (0.75f + 0.15f * animOffset1)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LiquidVioletGlow.copy(alpha = 0.22f),
                            LiquidVioletGlow.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = orb3Center,
                        radius = w * 0.8f
                    ),
                    center = orb3Center,
                    radius = w * 0.8f
                )

                // Liquid Accent Orb 4 (Warm Amber logistics glow at bottom right)
                val orb4Center = Offset(
                    x = w * (0.85f - 0.2f * animOffset1),
                    y = h * (0.2f + 0.3f * animOffset3)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LiquidAmberGlow.copy(alpha = 0.18f),
                            LiquidAmberGlow.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = orb4Center,
                        radius = w * 0.55f
                    ),
                    center = orb4Center,
                    radius = w * 0.55f
                )
            }
    ) {
        content()
    }
}

/**
 * Reusable Liquid Glass Container with specular reflections, glass gradient borders, and translucency.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 4.dp,
    tint: Color = GlassWhiteMedium,
    borderAlpha: Float = 0.7f,
    content: @Composable () -> Unit
) {
    val glassBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = borderAlpha),
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = borderAlpha * 0.8f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val specularBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.05f),
            Color.Transparent
        )
    )

    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x1A1E293B),
                spotColor = Color(0x260F172A)
            )
            .border(
                border = BorderStroke(1.2.dp, glassBorderBrush),
                shape = shape
            ),
        shape = shape,
        color = tint
    ) {
        Box(
            modifier = Modifier.drawBehind {
                // Subtle top specular glass highlight reflection
                drawRect(
                    brush = specularBrush,
                    size = size.copy(height = size.height * 0.45f)
                )
            }
        ) {
            content()
        }
    }
}
