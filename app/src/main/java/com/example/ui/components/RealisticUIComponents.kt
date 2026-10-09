package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun RealisticAppBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // High-definition 8K photorealistic ambient backdrop
        Image(
            painter = painterResource(id = R.drawable.bg_realistic_ambient_1791508557088),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Frosted glass atmospheric scrim
        val scrimGradient = if (isDarkTheme) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xEA091512),
                    Color(0xF60C1B17),
                    Color(0xFA08120F)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xE6F5F9F7),
                    Color(0xF2F0F7F4),
                    Color(0xF6EAF2EE)
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimGradient)
        )

        content()
    }
}

@Composable
fun Realistic3DCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    elevation: Dp = 6.dp,
    borderStroke: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val defaultBorder = borderStroke ?: BorderStroke(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = if (isDark) {
                listOf(
                    Color(0x55FFFFFF),
                    Color(0x15FFFFFF),
                    Color(0x3516987C)
                )
            } else {
                listOf(
                    Color(0xCCFFFFFF),
                    Color(0x40FFFFFF),
                    Color(0x250F5747)
                )
            }
        )
    )

    val surfaceColor = if (isDark) {
        Color(0xCC11231F)
    } else {
        Color(0xEEFFFFFF)
    }

    val cardModifier = if (onClick != null) {
        modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x220B3B30),
                spotColor = if (isDark) Color(0x8806231C) else Color(0x3316987C)
            )
            .clip(shape)
            .background(surfaceColor)
            .clickable(onClick = onClick)
    } else {
        modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x220B3B30),
                spotColor = if (isDark) Color(0x8806231C) else Color(0x3316987C)
            )
            .clip(shape)
            .background(surfaceColor)
    }

    Surface(
        modifier = cardModifier,
        shape = shape,
        color = Color.Transparent,
        border = defaultBorder
    ) {
        // Inner subtle realistic bevel gloss overlay
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(Color(0x18FFFFFF), Color(0x00FFFFFF), Color(0x14000000))
                            } else {
                                listOf(Color(0x60FFFFFF), Color(0x00FFFFFF), Color(0x08000000))
                            }
                        )
                    )
            )
            Column(
                modifier = Modifier.padding(1.dp),
                content = content
            )
        }
    }
}

@Composable
fun Realistic3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primaryColor: Color = Emerald600,
    shape: Shape = RoundedCornerShape(18.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateFloatAsState(targetValue = if (isPressed) 2f else 6f, label = "btn_elevation")

    val buttonGradient = Brush.verticalGradient(
        colors = if (enabled) {
            listOf(
                primaryColor.copy(alpha = 0.95f),
                primaryColor,
                primaryColor.copy(red = (primaryColor.red * 0.85f), green = (primaryColor.green * 0.85f), blue = (primaryColor.blue * 0.85f))
            )
        } else {
            listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.3f))
        }
    )

    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation.dp,
                shape = shape,
                ambientColor = primaryColor.copy(alpha = 0.4f),
                spotColor = primaryColor.copy(alpha = 0.6f)
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick
            ),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x88FFFFFF), Color(0x11FFFFFF), Color(0x44000000))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(buttonGradient)
                .padding(vertical = 12.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

@Composable
fun RealisticIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    backgroundColor: Color = Emerald700,
    size: Dp = 46.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateFloatAsState(targetValue = if (isPressed) 1f else 4f, label = "icon_elevation")

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = elevation.dp,
                shape = CircleShape,
                spotColor = backgroundColor.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.9f),
                        backgroundColor,
                        backgroundColor.copy(red = backgroundColor.red * 0.8f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Realistic 3D glass highlight ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x55FFFFFF), Color(0x00FFFFFF), Color(0x33000000))
                    )
                )
        )
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}
