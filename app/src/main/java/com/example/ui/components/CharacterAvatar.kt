package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PersonaCharacter

@Composable
fun CharacterAvatar(
    character: PersonaCharacter,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    showBorder: Boolean = true
) {
    val context = LocalContext.current
    val accentColor = Color(character.accentColor)

    val resId = character.avatarDrawableName?.let { name ->
        context.resources.getIdentifier(name, "drawable", context.packageName)
    } ?: 0

    val borderModifier = if (showBorder) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.sweepGradient(
                listOf(accentColor, Color.White.copy(alpha = 0.8f), accentColor)
            ),
            shape = CircleShape
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(borderModifier)
            .clip(CircleShape)
            .background(Color(0xFF1E2235)),
        contentAlignment = Alignment.Center
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = "${character.name} avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
        } else {
            Text(
                text = character.avatarEmoji,
                fontSize = (size.value * 0.48f).sp
            )
        }
    }
}
