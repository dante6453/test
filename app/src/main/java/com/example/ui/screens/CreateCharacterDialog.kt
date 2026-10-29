package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CharacterArchetype
import com.example.ui.theme.VioletPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        franchise: String,
        archetype: CharacterArchetype,
        catchphrase: String,
        tutorTone: String,
        burnoutComfort: String,
        strictness: Int,
        empathy: Int,
        intellect: Int,
        energy: Int,
        emoji: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var franchise by remember { mutableStateOf("") }
    var selectedArchetype by remember { mutableStateOf(CharacterArchetype.CHARISMATIC_GENIUS) }
    var catchphrase by remember { mutableStateOf("") }
    var tutorTone by remember { mutableStateOf("") }
    var burnoutComfort by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("⭐") }

    var strictness by remember { mutableFloatStateOf(6f) }
    var empathy by remember { mutableFloatStateOf(8f) }
    var intellect by remember { mutableFloatStateOf(9f) }
    var energy by remember { mutableFloatStateOf(8f) }

    val emojiOptions = listOf("⭐", "🔥", "⚡", "🧠", "🗡️", "🌸", "👑", "🔮", "🌙", "🎯")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Create Custom Persona",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Channel any anime, movie, or game character",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Emoji Selector
                Text(
                    text = "Character Icon Badge",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    emojiOptions.take(6).forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedEmoji == emoji) VioletPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Character Name (e.g. Kakashi, 2B, Batman)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = franchise,
                    onValueChange = { franchise = it },
                    label = { Text("Anime / Movie / Game Franchise") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Personality Archetype",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CharacterArchetype.values().forEach { arch ->
                        val isSelected = selectedArchetype == arch
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) VioletPrimary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedArchetype = arch }
                        ) {
                            Text(
                                text = arch.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = catchphrase,
                    onValueChange = { catchphrase = it },
                    label = { Text("Signature Catchphrase") },
                    placeholder = { Text("e.g. 'Believe it!' or 'I am vengeance!'") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tutorTone,
                    onValueChange = { tutorTone = it },
                    label = { Text("Tutor Voice & Teaching Style") },
                    placeholder = { Text("How do they teach difficult concepts?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = burnoutComfort,
                    onValueChange = { burnoutComfort = it },
                    label = { Text("Anti-Burnout / Comfort Persona") },
                    placeholder = { Text("What do they say when the user feels like giving up?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Personality Attributes Balance",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Sliders
                Text("Strictness: ${strictness.toInt()}/10", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = strictness,
                    onValueChange = { strictness = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = VioletPrimary, activeTrackColor = VioletPrimary)
                )

                Text("Empathy: ${empathy.toInt()}/10", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = empathy,
                    onValueChange = { empathy = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF4081), activeTrackColor = Color(0xFFFF4081))
                )

                Text("Intellect: ${intellect.toInt()}/10", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = intellect,
                    onValueChange = { intellect = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                )

                Text("Energy / Intensity: ${energy.toInt()}/10", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = energy,
                    onValueChange = { energy = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFFB300), activeTrackColor = Color(0xFFFFB300))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(
                                name,
                                franchise.ifBlank { "Original" },
                                selectedArchetype,
                                catchphrase,
                                tutorTone,
                                burnoutComfort,
                                strictness.toInt(),
                                empathy.toInt(),
                                intellect.toInt(),
                                energy.toInt(),
                                selectedEmoji
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                    enabled = name.isNotBlank()
                ) {
                    Text("Summon & Start Studying with $name", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
