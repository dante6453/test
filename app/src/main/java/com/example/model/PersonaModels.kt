package com.example.model

enum class CharacterArchetype(val label: String, val badgeColor: Long) {
    CHARISMATIC_GENIUS("Playful Sensei", 0xFF00E5FF),
    STOIC_CAPTAIN("Stoic Disciplinarian", 0xFF81C784),
    TSUNDERE_SCHOLAR("Tsundere Scientist", 0xFFFF4081),
    ENCYCLOPEDIC_WIZARD("Warm Perfectionist", 0xFFFFB300),
    DARK_STRATEGIST("Cold Tactician", 0xFF9E7BFF),
    CUSTOM("Custom Persona", 0xFFE040FB)
}

enum class StudyMode(val title: String, val icon: String, val description: String) {
    ACADEMIC_TUTOR("Academic Tutor", "📚", "Master difficult theories with custom grade-level breakdowns"),
    ANTI_BURNOUT("Slump Shield & Vent", "🛡️", "Emotional comfort, slump recovery, and genuine motivation"),
    PRACTICE_EXAM("Oral & Practice Rehearsal", "🎙️", "Interactive roleplay, viva voce interrogation, & mock oral exams"),
    DEBATE_MODE("Intellectual Debate Arena", "⚔️", "Defend thesis arguments & counter-points against your companion"),
    SMART_CALCULATOR("Smart Step Calculator", "🧮", "Equation solving with conversational character explanation")
}

enum class MessageSender {
    USER,
    CHARACTER,
    SYSTEM
}

data class QuizData(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    var answeredIndex: Int? = null
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: StudyMode = StudyMode.ACADEMIC_TUTOR,
    val stepBreakdowns: List<String> = emptyList(),
    val suggestedReplies: List<String> = emptyList(),
    val quizData: QuizData? = null
)

data class PersonaCharacter(
    val id: String,
    val name: String,
    val title: String,
    val franchise: String,
    val archetype: CharacterArchetype,
    val avatarDrawableName: String? = null,
    val avatarEmoji: String = "✨",
    val accentColor: Long,
    val catchphrase: String,
    val quote: String,
    val tutorTone: String,
    val burnoutComfortMessage: String,
    val strictness: Int, // 1 to 10
    val empathy: Int,    // 1 to 10
    val intellect: Int,  // 1 to 10
    val energy: Int,     // 1 to 10
    val isCustom: Boolean = false,
    val systemPromptSnippet: String
)

data class StudySubject(
    val id: String,
    val name: String,
    val emoji: String,
    val gradeLevel: String,
    val popularTopics: List<String>
)

data class FormulaPreset(
    val id: String,
    val category: String,
    val name: String,
    val formula: String,
    val description: String,
    val variables: List<FormulaVariable>,
    val exampleProblem: String
)

data class FormulaVariable(
    val symbol: String,
    val label: String,
    val unit: String,
    val defaultValue: Double
)
