package avogadri.marco.localshare.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composable del logo dell'applicazione
 */
@Composable
fun LocalShareLogo(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val radius = this.size.minDimension / 3.2f
            val centerY = this.size.height / 2f
            val offsetX = radius * 0.55f
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = radius,
                center = Offset(this.size.width / 2f - offsetX, centerY),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = radius,
                center = Offset(this.size.width / 2f + offsetX, centerY),
            )
        }
    }
}
