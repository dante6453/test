package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.QuizData
import com.example.model.StudyMode
import com.example.ui.components.CharacterAvatar
import com.example.ui.theme.AmberBurnout
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseComfort
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.UiState

@Composable
fun ChatSessionScreen(
    uiState: UiState,
    onSendMessage: (String) -> Unit,
    onSwitchMode: (StudyMode) -> Unit,
    onQuizAnswer: (messageId: String, optionIndex: Int) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showModeMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val char = uiState.selectedCharacter
    val accentColor = Color(char.accentColor)

    // Auto scroll to bottom on new message
    LaunchedEffect(uiState.messages.size, uiState.isCharacterTyping) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CharacterAvatar(character = char, size = 44.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = char.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${uiState.selectedSubject.name} • ${uiState.selectedSubject.gradeLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Mode Selector Dropdown
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (uiState.currentMode) {
                                    StudyMode.ANTI_BURNOUT -> RoseComfort.copy(alpha = 0.2f)
                                    StudyMode.PRACTICE_EXAM -> AmberBurnout.copy(alpha = 0.2f)
                                    StudyMode.DEBATE_MODE -> CyanAccent.copy(alpha = 0.2f)
                                    else -> VioletPrimary.copy(alpha = 0.2f)
                                }
                            )
                            .clickable { showModeMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = uiState.currentMode.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.currentMode.title.take(14),
                                style = MaterialTheme.typography.labelMedium,
                                color = when (uiState.currentMode) {
                                    StudyMode.ANTI_BURNOUT -> RoseComfort
                                    StudyMode.PRACTICE_EXAM -> AmberBurnout
                                    StudyMode.DEBATE_MODE -> CyanAccent
                                    else -> VioletPrimary
                                }
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showModeMenu,
                        onDismissRequest = { showModeMenu = false }
                    ) {
                        StudyMode.values().forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(mode.icon)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(mode.title)
                                    }
                                },
                                onClick = {
                                    showModeMenu = false
                                    onSwitchMode(mode)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                when (msg.sender) {
                    MessageSender.USER -> UserMessageBubble(msg)
                    MessageSender.CHARACTER -> CharacterMessageBubble(
                        msg = msg,
                        character = char,
                        onQuizOptionClick = { optIndex ->
                            onQuizAnswer(msg.id, optIndex)
                        }
                    )
                    MessageSender.SYSTEM -> SystemMessageBanner(msg)
                }
            }

            if (uiState.isCharacterTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        CharacterAvatar(character = char, size = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${char.name} is formulating response...",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor
                        )
                    }
                }
            }
        }

        // Suggested Replies Chips
        val lastMsgWithSuggestions = uiState.messages.lastOrNull { it.suggestedReplies.isNotEmpty() }
        if (lastMsgWithSuggestions != null) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lastMsgWithSuggestions.suggestedReplies) { reply ->
                    SuggestionChip(
                        onClick = {
                            onSendMessage(reply)
                        },
                        label = {
                            Text(text = reply, style = MaterialTheme.typography.labelSmall)
                        }
                    )
                }
            }
        }

        // Input Field Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Slump Shield Button
                IconButton(
                    onClick = {
                        onSwitchMode(StudyMode.ANTI_BURNOUT)
                        onSendMessage("I'm feeling really stressed and burnt out right now 🛡️")
                    }
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "Slump Shield",
                        tint = RoseComfort
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            when (uiState.currentMode) {
                                StudyMode.ANTI_BURNOUT -> "Vent to ${char.name} freely..."
                                StudyMode.PRACTICE_EXAM -> "Rehearse answer out loud / in text..."
                                StudyMode.DEBATE_MODE -> "Argue your thesis counterpoint..."
                                else -> "Ask ${char.name} any theory, problem, or quiz..."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            onSendMessage(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) VioletPrimary else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                .background(VioletPrimary)
                .padding(14.dp)
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun CharacterMessageBubble(
    msg: ChatMessage,
    character: com.example.model.PersonaCharacter,
    onQuizOptionClick: (Int) -> Unit
) {
    val accentColor = Color(character.accentColor)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CharacterAvatar(character = character, size = 38.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth(0.92f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = msg.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Step breakdowns if available
                    if (msg.stepBreakdowns.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        msg.stepBreakdowns.forEach { step ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text(
                                    text = "•",
                                    color = accentColor,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Interactive Quiz Card
                    if (msg.quizData != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        QuizInteractiveCard(
                            quiz = msg.quizData,
                            accentColor = accentColor,
                            onOptionClick = onQuizOptionClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizInteractiveCard(
    quiz: QuizData,
    accentColor: Color,
    onOptionClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "QUIZ CHALLENGE",
                style = MaterialTheme.typography.labelSmall,
                color = AmberBurnout
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quiz.question,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            quiz.options.forEachIndexed { index, option ->
                val isAnswered = quiz.answeredIndex != null
                val isSelected = quiz.answeredIndex == index
                val isCorrect = index == quiz.correctIndex

                val cardBg = when {
                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                    isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                    isSelected -> RoseComfort.copy(alpha = 0.25f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(cardBg)
                        .clickable(enabled = !isAnswered) { onOptionClick(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + index)}. $option",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isAnswered && isCorrect) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isAnswered && isSelected && !isCorrect) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Incorrect",
                                tint = RoseComfort,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemMessageBanner(msg: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
