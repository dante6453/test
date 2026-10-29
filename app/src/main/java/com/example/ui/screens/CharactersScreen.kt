package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.CharacterArchetype
import com.example.model.PersonaCharacter
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.StatBar
import com.example.ui.theme.AmberBurnout
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseComfort
import com.example.ui.theme.VioletPrimary

@Composable
fun CharactersScreen(
    characters: List<PersonaCharacter>,
    selectedCharacter: PersonaCharacter,
    onSelectCharacter: (PersonaCharacter) -> Unit,
    onCreateCharacterClick: () -> Unit
) {
    var selectedArchetypeFilter by remember { mutableStateOf<CharacterArchetype?>(null) }

    val filteredCharacters = remember(characters, selectedArchetypeFilter) {
        if (selectedArchetypeFilter == null) characters
        else characters.filter { it.archetype == selectedArchetypeFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Study Companions",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Pick or build your ideal anime & fictional mentor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onCreateCharacterClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Archetype Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedArchetypeFilter == null,
                    onClick = { selectedArchetypeFilter = null },
                    label = { Text("All Archetypes") }
                )
            }
            items(CharacterArchetype.values()) { archetype ->
                FilterChip(
                    selected = selectedArchetypeFilter == archetype,
                    onClick = {
                        selectedArchetypeFilter = if (selectedArchetypeFilter == archetype) null else archetype
                    },
                    label = { Text(archetype.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCharacters, key = { it.id }) { character ->
                val isSelected = character.id == selectedCharacter.id
                val accentColor = Color(character.accentColor)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(listOf(accentColor, CyanAccent)),
                                    shape = RoundedCornerShape(18.dp)
                                )
                            } else Modifier
                        )
                        .clickable { onSelectCharacter(character) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            CharacterAvatar(character = character, size = 64.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = character.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldSuccess
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${character.title} • ${character.franchise}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = character.archetype.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quote Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "\"${character.catchphrase}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Teaching Persona: ${character.tutorTone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Radar Stats
                        StatBar(
                            label = "Strictness",
                            value = character.strictness,
                            color = Color(0xFFFF5252)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatBar(
                            label = "Empathy",
                            value = character.empathy,
                            color = RoseComfort
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatBar(
                            label = "Intellect",
                            value = character.intellect,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatBar(
                            label = "Energy",
                            value = character.energy,
                            color = AmberBurnout
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!isSelected) {
                            OutlinedButton(
                                onClick = { onSelectCharacter(character) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Select ${character.name} as Companion")
                            }
                        }
                    }
                }
            }
        }
    }
}
