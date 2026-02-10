package com.retrocam.app.presentation.camera.components

import androidx.camera.core.AspectRatio
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retrocam.app.R

enum class AspectRatioOption(
    val ratio: Int,
    val label: String,
    val description: String
) {
    RATIO_4_3(AspectRatio.RATIO_4_3, "4:3", "Standard"),
    RATIO_16_9(AspectRatio.RATIO_16_9, "16:9", "Widescreen"),
    RATIO_1_1(AspectRatio.RATIO_4_3, "1:1", "Square"), // Using 4:3 and cropping
    RATIO_FULL(AspectRatio.RATIO_16_9, "Full", "No crop") // Using 16:9
}

@Composable
fun AspectRatioSelector(
    selectedRatio: AspectRatioOption,
    onRatioSelected: (AspectRatioOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AspectRatioOption.values().forEach { option ->
            AspectRatioItem(
                option = option,
                isSelected = option == selectedRatio,
                onClick = { onRatioSelected(option) }
            )
        }
    }
}

@Composable
private fun AspectRatioItem(
    option: AspectRatioOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        label = "backgroundPadding"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}
