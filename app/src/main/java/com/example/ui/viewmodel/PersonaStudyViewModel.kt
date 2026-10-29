package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CharacterRepository
import com.example.data.FormulaRepository
import com.example.model.CharacterArchetype
import com.example.model.ChatMessage
import com.example.model.FormulaPreset
import com.example.model.MessageSender
import com.example.model.PersonaCharacter
import com.example.model.QuizData
import com.example.model.StudyMode
import com.example.model.StudySubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class UiState(
    val characters: List<PersonaCharacter> = CharacterRepository.defaultCharacters,
    val selectedCharacter: PersonaCharacter = CharacterRepository.defaultCharacters[0],
    val subjects: List<StudySubject> = CharacterRepository.subjects,
    val selectedSubject: StudySubject = CharacterRepository.subjects[0],
    val currentMode: StudyMode = StudyMode.ACADEMIC_TUTOR,
    val messages: List<ChatMessage> = emptyList(),
    val isCharacterTyping: Boolean = false,
    val burnoutLevel: Int = 18, // 0 - 100%
    val studyStreakDays: Int = 4,
    // Calculator State
    val formulaPresets: List<FormulaPreset> = FormulaRepository.formulas,
    val selectedFormula: FormulaPreset = FormulaRepository.formulas[0],
    val formulaInputs: Map<String, Double> = mapOf("a" to 1.0, "b" to -5.0, "c" to 6.0),
    val calculatorDisplay: String = "x² - 5x + 6 = 0",
    val calcResult: String? = "x₁ = 3.0,  x₂ = 2.0",
    val calcSteps: List<String> = listOf(
        "Standard quadratic form: ax² + bx + c = 0 (a=1, b=-5, c=6)",
        "Discriminant Δ = b² - 4ac = (-5)² - 4(1)(6) = 25 - 24 = 1",
        "Square root of discriminant: √1 = 1",
        "Roots: x = (-(-5) ± 1) / (2 · 1) = (5 ± 1) / 2",
        "Result: x₁ = (5 + 1)/2 = 3.0,  x₂ = (5 - 1)/2 = 2.0"
    ),
    val characterCalcExplanation: String = "Gojo's Note: Easy work! The discriminant was positive, so you get two clean real roots without even breaking a sweat."
)

class PersonaStudyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Welcome message from first character
        seedInitialChat(_uiState.value.selectedCharacter, _uiState.value.selectedSubject)
    }

    private fun seedInitialChat(character: PersonaCharacter, subject: StudySubject) {
        val welcome = ChatMessage(
            sender = MessageSender.CHARACTER,
            text = "Yo! ${character.name} here. We're tackling ${subject.name} today. ${character.catchphrase} What topic are we breaking down first, or are you ready for a test?",
            mode = StudyMode.ACADEMIC_TUTOR,
            suggestedReplies = listOf(
                "Explain the fundamental theorem",
                "Give me a practice quiz!",
                "I'm feeling overwhelmed and burned out 🛡️",
                "Rehearse an oral exam with me"
            )
        )
        _uiState.update { it.copy(messages = listOf(welcome)) }
    }

    fun selectCharacter(character: PersonaCharacter) {
        _uiState.update {
            it.copy(
                selectedCharacter = character,
                characterCalcExplanation = generateCharacterCalcNote(character, it.selectedFormula)
            )
        }
        val switchMsg = ChatMessage(
            sender = MessageSender.SYSTEM,
            text = "Companion switched to ${character.name} (${character.title} • ${character.franchise})"
        )
        val characterGreeting = ChatMessage(
            sender = MessageSender.CHARACTER,
            text = "${character.catchphrase} I've taken over your study session for ${_uiState.value.selectedSubject.name}. Let's get to work.",
            suggestedReplies = listOf(
                "Teach me a core concept",
                "Test my knowledge with a quiz",
                "Help, I'm stressed out",
                "Let's practice oral questions"
            )
        )
        _uiState.update { it.copy(messages = it.messages + switchMsg + characterGreeting) }
    }

    fun selectSubject(subject: StudySubject) {
        _uiState.update { it.copy(selectedSubject = subject) }
        val char = _uiState.value.selectedCharacter
        val msg = ChatMessage(
            sender = MessageSender.CHARACTER,
            text = "Subject updated to ${subject.name} [${subject.gradeLevel}]. Topics on deck: ${subject.popularTopics.take(3).joinToString(", ")}. What are we solving first?",
            suggestedReplies = subject.popularTopics.take(3)
        )
        _uiState.update { it.copy(messages = it.messages + msg) }
    }

    fun switchMode(mode: StudyMode) {
        _uiState.update { it.copy(currentMode = mode) }
        val char = _uiState.value.selectedCharacter
        val promptText = when (mode) {
            StudyMode.ACADEMIC_TUTOR ->
                "Tutor Mode active. Ready to break down any theory, formula, or textbook page step-by-step."
            StudyMode.ANTI_BURNOUT ->
                "${char.burnoutComfortMessage}\n\n(🛡️ Slump Shield Activated: No pressure, no quizzes. Tell me what's stressing you out, vent freely, or ask for a decompression routine.)"
            StudyMode.PRACTICE_EXAM ->
                "🎙️ Oral Exam Rehearsal initialized! I'll take the role of your lead examiner or viva defense board. Answer out loud or in text—I will scrutinize your logic!"
            StudyMode.DEBATE_MODE ->
                "⚔️ Debate Arena initialized! Present your thesis, theorem interpretation, or historical argument. I will counter-argue vigorously."
            StudyMode.SMART_CALCULATOR ->
                "🧮 Smart Calculator active. Feed me equations or pick a formula below—I'll compute the values and explain every derivation."
        }

        val modeMsg = ChatMessage(
            sender = MessageSender.CHARACTER,
            text = promptText,
            mode = mode,
            suggestedReplies = when (mode) {
                StudyMode.ANTI_BURNOUT -> listOf(
                    "I failed my test today and feel hopeless",
                    "I have too much homework and can't focus",
                    "Can we do a 5-minute breathing reset?",
                    "Remind me why I'm doing this"
                )
                StudyMode.PRACTICE_EXAM -> listOf(
                    "Ask me the first oral exam question!",
                    "Test my defense on core fundamentals",
                    "What is a common trap on this exam?"
                )
                StudyMode.DEBATE_MODE -> listOf(
                    "I argue that classical determinism fails at quantum scale",
                    "Let's debate the primary cause of the Industrial Revolution",
                    "Counter my stance on computational limits"
                )
                else -> listOf("Explain this theory", "Quiz me", "Give me a summary")
            }
        )
        _uiState.update { it.copy(messages = it.messages + modeMsg) }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = userText,
            mode = _uiState.value.currentMode
        )

        // Check if user text triggers emotional slump/burnout
        val lower = userText.lowercase()
        val isBurnoutTrigger = lower.contains("burnout") || lower.contains("burned out") ||
                lower.contains("give up") || lower.contains("fail") || lower.contains("tired") ||
                lower.contains("hopeless") || lower.contains("stressed") || lower.contains("overwhelm") ||
                lower.contains("cry") || lower.contains("can't do this")

        _uiState.update {
            it.copy(
                messages = it.messages + userMsg,
                isCharacterTyping = true,
                burnoutLevel = if (isBurnoutTrigger) (it.burnoutLevel + 25).coerceAtMost(100) else it.burnoutLevel
            )
        }

        viewModelScope.launch {
            delay(800) // realistic persona response cadence
            val char = _uiState.value.selectedCharacter
            val subject = _uiState.value.selectedSubject

            val reply = if (isBurnoutTrigger || _uiState.value.currentMode == StudyMode.ANTI_BURNOUT) {
                generateBurnoutComfortResponse(char, userText)
            } else if (lower.contains("quiz") || lower.contains("test me")) {
                generateQuizResponse(char, subject)
            } else if (_uiState.value.currentMode == StudyMode.PRACTICE_EXAM) {
                generateOralExamResponse(char, subject, userText)
            } else if (_uiState.value.currentMode == StudyMode.DEBATE_MODE) {
                generateDebateResponse(char, subject, userText)
            } else {
                generateTutorExplanationResponse(char, subject, userText)
            }

            _uiState.update {
                it.copy(
                    messages = it.messages + reply,
                    isCharacterTyping = false,
                    burnoutLevel = if (isBurnoutTrigger) (it.burnoutLevel - 15).coerceAtLeast(5) else it.burnoutLevel
                )
            }
        }
    }

    fun handleQuizAnswer(messageId: String, selectedIndex: Int) {
        val currentMsgs = _uiState.value.messages.map { msg ->
            if (msg.id == messageId && msg.quizData != null) {
                msg.copy(quizData = msg.quizData.copy(answeredIndex = selectedIndex))
            } else msg
        }
        _uiState.update { it.copy(messages = currentMsgs) }

        val targetMsg = currentMsgs.find { it.id == messageId } ?: return
        val quiz = targetMsg.quizData ?: return
        val isCorrect = selectedIndex == quiz.correctIndex
        val char = _uiState.value.selectedCharacter

        val feedbackText = when (char.id) {
            "gojo_satoru" -> if (isCorrect) {
                "BAM! Correct! 🎉 See? You've got top-tier talent. Infinity approved!"
            } else {
                "Ah-ah! Not quite! But don't sweat it. Even special-grade sorcerers miss once in a while. ${quiz.explanation}"
            }
            "levi_ackerman" -> if (isCorrect) {
                "Tch. Correct. You didn't make a mess of it. Keep this standard up."
            } else {
                "Wrong. Sloppy deduction. Don't look away from your mistakes—burn the right answer into your memory: ${quiz.explanation}"
            }
            "makise_kurisu" -> if (isCorrect) {
                "Hmph! Correct answer! N-Not that I was worried you'd get it wrong or anything! Good job."
            } else {
                "Baka! Did you even read the premises? The empirical truth is clear: ${quiz.explanation}"
            }
            "hermione_granger" -> if (isCorrect) {
                "Ten points to you! Brilliant application of the principles!"
            } else {
                "Oh, close, but not quite! Let's examine why: ${quiz.explanation}. Review chapter three!"
            }
            else -> if (isCorrect) {
                "Correct analysis! Well done."
            } else {
                "Incorrect. Re-evaluate the parameters: ${quiz.explanation}"
            }
        }

        val feedbackMsg = ChatMessage(
            sender = MessageSender.CHARACTER,
            text = feedbackText,
            mode = StudyMode.ACADEMIC_TUTOR,
            suggestedReplies = listOf("Give me another question!", "Explain this deeper", "Back to notes")
        )
        _uiState.update { it.copy(messages = it.messages + feedbackMsg) }
    }

    fun selectFormula(preset: FormulaPreset) {
        val defaultMap = preset.variables.associate { it.symbol to it.defaultValue }
        _uiState.update {
            it.copy(
                selectedFormula = preset,
                formulaInputs = defaultMap,
                calculatorDisplay = preset.formula
            )
        }
        recomputeFormula(preset, defaultMap)
    }

    fun updateFormulaInput(symbol: String, value: Double) {
        val updated = _uiState.value.formulaInputs.toMutableMap()
        updated[symbol] = value
        _uiState.update { it.copy(formulaInputs = updated) }
        recomputeFormula(_uiState.value.selectedFormula, updated)
    }

    private fun recomputeFormula(preset: FormulaPreset, inputs: Map<String, Double>) {
        val char = _uiState.value.selectedCharacter
        when (preset.id) {
            "quadratic" -> {
                val a = inputs["a"] ?: 1.0
                val b = inputs["b"] ?: -5.0
                val c = inputs["c"] ?: 6.0
                val disc = b * b - 4 * a * c
                val steps = mutableListOf<String>()
                steps.add("Standard form: ($a)x² + ($b)x + ($c) = 0")
                steps.add("Discriminant Δ = b² - 4ac = (${b})² - 4($a)($c) = $disc")

                val resultStr: String
                val note: String
                if (disc > 0) {
                    val rootDisc = sqrt(disc)
                    val x1 = (-b + rootDisc) / (2 * a)
                    val x2 = (-b - rootDisc) / (2 * a)
                    resultStr = "x₁ = ${"%.3f".format(x1)},  x₂ = ${"%.3f".format(x2)}"
                    steps.add("Square root of Δ: √$disc = ${"%.3f".format(rootDisc)}")
                    steps.add("Roots: x = (-($b) ± ${"%.3f".format(rootDisc)}) / (2 · $a)")
                    note = when (char.id) {
                        "gojo_satoru" -> "Gojo's Take: Two real distinct roots! You cleaved right through that polynomial."
                        "levi_ackerman" -> "Levi's Directive: Clean solution. No messy complex numbers. Record the two roots and move to the next target."
                        "makise_kurisu" -> "Kurisu's Analysis: Positive discriminant confirms two distinct real eigenvalues in 1D phase space."
                        else -> "Result derived with clean algebraic roots."
                    }
                } else if (disc == 0.0) {
                    val x = -b / (2 * a)
                    resultStr = "Repeated Root: x = ${"%.3f".format(x)}"
                    steps.add("Δ is exactly zero. Parabola touches the x-axis at a single vertex.")
                    note = "Single tangent root. Perfectly balanced equation."
                } else {
                    val realPart = -b / (2 * a)
                    val imgPart = sqrt(-disc) / (2 * a)
                    resultStr = "Complex: ${"%.2f".format(realPart)} ± ${"%.2f".format(imgPart)}i"
                    steps.add("Δ < 0 indicates conjugate complex roots in the Argand plane.")
                    note = "Imaginary roots! A higher-dimensional outcome."
                }

                _uiState.update {
                    it.copy(
                        calcResult = resultStr,
                        calcSteps = steps,
                        characterCalcExplanation = note
                    )
                }
            }
            "kinetic_energy" -> {
                val m = inputs["m"] ?: 60.0
                val v = inputs["v"] ?: 15.0
                val ke = 0.5 * m * v * v
                val steps = listOf(
                    "Formula: Kinetic Energy = ½ · mass · velocity²",
                    "Mass m = $m kg, Velocity v = $v m/s",
                    "v² = ($v)² = ${v * v} m²/s²",
                    "KE = 0.5 · $m · ${v * v} = $ke Joules"
                )
                val note = when (char.id) {
                    "levi_ackerman" -> "Levi's Review: At $v m/s with Omni-Directional Mobility gear, that $ke J impact is more than enough to slice through a nape. Keep your trajectory tight."
                    "gojo_satoru" -> "Gojo's Sensei Wisdom: Look at that energy output! Velocity is squared, so if you double speed, your kinetic impact quadruples. Physics is basically real-world Jujutsu."
                    "makise_kurisu" -> "Kurisu's Calculation: Exactly $ke J. Remember, kinetic energy is scalar, conserved in perfectly elastic collisions."
                    else -> "Total kinetic energy calculated: $ke Joules."
                }
                _uiState.update {
                    it.copy(
                        calcResult = "KE = ${"%,.1f".format(ke)} Joules",
                        calcSteps = steps,
                        characterCalcExplanation = note
                    )
                }
            }
            "ohms_law" -> {
                val current = inputs["I"] ?: 2.5
                val resistance = inputs["R"] ?: 48.0
                val voltage = current * resistance
                val power = voltage * current
                val steps = listOf(
                    "Ohm's Law: Voltage V = I · R",
                    "Current I = $current A, Resistance R = $resistance Ω",
                    "Voltage V = $current · $resistance = $voltage Volts",
                    "Joule Heating Power: P = V · I = $voltage · $current = $power Watts"
                )
                _uiState.update {
                    it.copy(
                        calcResult = "V = $voltage V,  P = $power W",
                        calcSteps = steps,
                        characterCalcExplanation = "${char.name}'s Note: $voltage Volts pushing through $resistance Ohms generates $power Watts of thermal power. Efficient and verified."
                    )
                }
            }
            "ideal_gas" -> {
                val n = inputs["n"] ?: 2.0
                val t = inputs["T"] ?: 298.15
                val v = inputs["V"] ?: 22.4
                val r = 0.08206
                val p = (n * r * t) / v
                val steps = listOf(
                    "Ideal Gas Law: P = (n · R · T) / V",
                    "R (gas constant) = 0.08206 L·atm/(mol·K)",
                    "Numerator: ($n mol) · (0.08206) · ($t K) = ${"%.4f".format(n * r * t)}",
                    "Dividing by Volume ($v L): P = ${"%.3f".format(p)} atmospheres"
                )
                _uiState.update {
                    it.copy(
                        calcResult = "P = ${"%.3f".format(p)} atm",
                        calcSteps = steps,
                        characterCalcExplanation = "${char.name}'s Breakdown: Ideal gas pressure is ${"%.2f".format(p)} atm. Thermodynamics respects precision!"
                    )
                }
            }
            "pythagorean" -> {
                val a = inputs["a"] ?: 9.0
                val b = inputs["b"] ?: 12.0
                val c = sqrt(a * a + b * b)
                val steps = listOf(
                    "Theorem: c = √(a² + b²)",
                    "Leg a = $a, a² = ${a * a}",
                    "Leg b = $b, b² = ${b * b}",
                    "Sum of squares: ${a * a} + ${b * b} = ${a * a + b * b}",
                    "Hypotenuse: √${a * a + b * b} = $c"
                )
                _uiState.update {
                    it.copy(
                        calcResult = "c = $c",
                        calcSteps = steps,
                        characterCalcExplanation = "${char.name}'s Note: Classic Pythagorean triple! Hypotenuse is exact: $c."
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        calcResult = "Solved",
                        calcSteps = listOf("Formula verified: ${preset.formula}"),
                        characterCalcExplanation = "${char.name}: Calculated successfully."
                    )
                }
            }
        }
    }

    private fun generateCharacterCalcNote(char: PersonaCharacter, formula: FormulaPreset): String {
        return when (char.id) {
            "gojo_satoru" -> "Gojo: Ready to compute! Show me any complex equation—my Six Eyes break it down in a millisecond."
            "levi_ackerman" -> "Levi: Precision matters in calculations. A single decimal error can get your entire derivation slaughtered."
            "makise_kurisu" -> "Kurisu: Let's calculate with empirical rigor. Don't forget your units, baka!"
            "hermione_granger" -> "Hermione: Double-check every factor! Arithmancy and algebra reward disciplined patience."
            else -> "${char.name}: Let's solve this formula together."
        }
    }

    fun createCustomCharacter(
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
    ) {
        val newChar = PersonaCharacter(
            id = "custom_" + System.currentTimeMillis(),
            name = name,
            title = "Custom Companion",
            franchise = franchise,
            archetype = archetype,
            avatarDrawableName = null,
            avatarEmoji = emoji.ifBlank { "⭐" },
            accentColor = 0xFFE040FB,
            catchphrase = catchphrase.ifBlank { "Let's conquer this subject together!" },
            quote = "Knowledge is power, and we have plenty of it.",
            tutorTone = tutorTone.ifBlank { "Passionate and encouraging mentor." },
            burnoutComfortMessage = burnoutComfort.ifBlank { "Take a deep breath and rest. You are capable of amazing things." },
            strictness = strictness,
            empathy = empathy,
            intellect = intellect,
            energy = energy,
            isCustom = true,
            systemPromptSnippet = "You are $name from $franchise. Tone: $tutorTone. Comfort: $burnoutComfort."
        )

        _uiState.update {
            it.copy(
                characters = it.characters + newChar
            )
        }
        selectCharacter(newChar)
    }

    private fun generateTutorExplanationResponse(
        char: PersonaCharacter,
        subject: StudySubject,
        query: String
    ): ChatMessage {
        val steps = when (subject.id) {
            "calc_math" -> listOf(
                "Step 1: Identify the outer and inner functions f(g(x)).",
                "Step 2: Differentiate the outer function with respect to the inner: f'(g(x)).",
                "Step 3: Differentiate the inner function: g'(x).",
                "Step 4: Multiply both results together: d/dx = f'(g(x)) · g'(x)."
            )
            "physics_mech" -> listOf(
                "Step 1: Draw a pristine Free-Body Diagram (FBD) of all acting forces.",
                "Step 2: Resolve vectors into orthogonal x and y axes.",
                "Step 3: Apply Newton's 2nd Law: ΣFx = m·ax and ΣFy = m·ay.",
                "Step 4: Substitute boundary constraints to solve for target variables."
            )
            "organic_chem" -> listOf(
                "Step 1: Identify the nucleophile, electrophile, and leaving group stability.",
                "Step 2: Check solvent polarity (polar protic favors SN1, polar aprotic favors SN2).",
                "Step 3: Determine steric hindrance around the alpha carbon.",
                "Step 4: Predict stereochemical inversion (Walden inversion) or racemic mixture."
            )
            else -> listOf(
                "Step 1: State the fundamental thesis and historical or conceptual context.",
                "Step 2: Break down the primary causal mechanisms.",
                "Step 3: Trace secondary feedback loops and empirical evidence.",
                "Step 4: Synthesize the conclusion for exam recall."
            )
        }

        val speech = when (char.id) {
            "gojo_satoru" -> "Alright, check this out! You asked about '$query'. Think of it like my Limitless technique—it seems impossible until you realize it's just breaking distance down into infinite little steps! Here's the clean blueprint:"
            "levi_ackerman" -> "Tch. Stop staring blankly at '$query'. Confusion comes from clutter. We cut this problem down into four clean slices. Pay attention to each one:"
            "makise_kurisu" -> "Listen closely! Understanding '$query' is fundamental to your coursework. It's not like I wrote this step-by-step breakdown just to help you or anything, but make sure you take notes:"
            "hermione_granger" -> "Splendid question! '$query' is actually covered in depth in chapter seven. The crucial part is not skipping ahead before you grasp the underlying rule. Here is the proper formulation:"
            else -> "Here is the exact structural breakdown for '$query':"
        }

        return ChatMessage(
            sender = MessageSender.CHARACTER,
            text = speech,
            mode = StudyMode.ACADEMIC_TUTOR,
            stepBreakdowns = steps,
            suggestedReplies = listOf(
                "Can you quiz me on this?",
                "Give me a real-world example",
                "I get it! Next topic",
                "Wait, clarify step 2"
            )
        )
    }

    private fun generateBurnoutComfortResponse(char: PersonaCharacter, query: String): ChatMessage {
        val comfortSpeech = when (char.id) {
            "gojo_satoru" -> "Hey... look at me. Put the pen down. 🩵\n\nStudying at your level is exhausting, and it's totally okay to feel overwhelmed. When you push your brain to its limits, burnout isn't failure—it's just a sign that you cared enough to run out of cursed energy!\n\nYou've got ME in your corner. We're going to pause right now. Drink a tall glass of water, put on your favorite song, or close your eyes for 10 minutes. When you're ready, we'll pick it back up. You're doing amazing, promise."
            "levi_ackerman" -> "Tch. Stop right there. Look at your hands—you're tense and grinding your teeth. ⚔️\n\nA soldier who fights without rest gets killed in the field. Pushing yourself through exhaustion doesn't make you strong; it makes you careless. Rest is tactical.\n\nClose the notebook. Step away from the screen. Go brew something warm and breathe. The assignment will still be here when your head is clear. I'm not letting you quit, but right now your orders are to rest."
            "makise_kurisu" -> "H-Hey! Stop beating yourself up like that! 🧪\n\nListen to the neuroscientist for a second: when your brain's cortisol spikes from chronic academic pressure, your hippocampus literally downregulates memory retrieval! It's pure biology, not a reflection of your intelligence, you baka!\n\nI... I care about how you're feeling, okay? Take twenty minutes away from the desk. Grab a drink, lie down, and let your neurotransmitters rebalance. We will solve this together afterward. You aren't alone in this lab."
            "hermione_granger" -> "Oh, please put your books away for the evening! 📖✨\n\nI remember during our third year when I had the Time-Turner—I pushed myself until I was crying in the library over Arithmancy. Trust me, no grade, no test, and no deadline is worth destroying your peace of mind.\n\nYou have worked so diligently. Be gentle with yourself tonight. Let's make a warm cup of cocoa, take five slow deep breaths, and let your wonderful mind rest. You are far more capable than you realize."
            else -> "${char.burnoutComfortMessage}\n\nTake five deep breaths. You have worked hard, and it is time to recharge."
        }

        return ChatMessage(
            sender = MessageSender.CHARACTER,
            text = comfortSpeech,
            mode = StudyMode.ANTI_BURNOUT,
            suggestedReplies = listOf(
                "Thank you... I needed to hear that",
                "Start 5-minute breathing exercise",
                "Let's just chat about anime/hobbies",
                "Okay, I feel calmer. Let's do a gentle review"
            )
        )
    }

    private fun generateQuizResponse(char: PersonaCharacter, subject: StudySubject): ChatMessage {
        val (q, options, correct, expl) = when (subject.id) {
            "calc_math" -> Quad(
                "What is the derivative of f(x) = ln(x² + 1)?",
                listOf("2x / (x² + 1)", "1 / (x² + 1)", "2x · ln(x)", "x / (x² + 1)"),
                0,
                "Chain rule states d/dx [ln(u)] = u'/u. Here u = x² + 1, so u' = 2x, yielding 2x / (x² + 1)."
            )
            "physics_mech" -> Quad(
                "If velocity is tripled, by what factor does kinetic energy increase?",
                listOf("Factor of 3", "Factor of 6", "Factor of 9", "Remains constant"),
                2,
                "Kinetic Energy is proportional to velocity squared (KE = ½mv²). Tripling v yields 3² = 9 times the energy!"
            )
            "organic_chem" -> Quad(
                "Which mechanism involves a carbocation intermediate and results in racemization?",
                listOf("SN2", "SN1", "E2", "Addition-Elimination"),
                1,
                "SN1 is a two-step mechanism where the leaving group departs first, forming a planar carbocation that can be attacked from either face."
            )
            else -> Quad(
                "Which complexity class represents problems verifiable in polynomial time?",
                listOf("P", "NP", "EXPTIME", "NP-Hard"),
                1,
                "NP (Nondeterministic Polynomial time) contains decision problems whose solutions can be verified in polynomial time."
            )
        }

        val prompt = when (char.id) {
            "gojo_satoru" -> "Quiz time! Let's see if your reflexes are as sharp as mine. Answer this:"
            "levi_ackerman" -> "Checkpoint inspection. Pick your answer without hesitation:"
            "makise_kurisu" -> "Pop quiz! Show me you've actually assimilated the material:"
            else -> "Knowledge verification question:"
        }

        return ChatMessage(
            sender = MessageSender.CHARACTER,
            text = prompt,
            mode = StudyMode.ACADEMIC_TUTOR,
            quizData = QuizData(
                question = q,
                options = options,
                correctIndex = correct,
                explanation = expl
            )
        )
    }

    private fun generateOralExamResponse(char: PersonaCharacter, subject: StudySubject, userText: String): ChatMessage {
        val feedback = when (char.id) {
            "levi_ackerman" -> "Tch. Examiner Levi here. In your thesis on '${subject.name}', how do you justify your primary assumptions when external boundary conditions fluctuate wildly? Defend your position clearly."
            "gojo_satoru" -> "Welcome to the oral exam committee! 🎤 Let's say a student argues that your conclusion on '${subject.name}' violates conservation laws. How do you prove them wrong in 30 seconds?"
            "makise_kurisu" -> "As your primary oral committee member, I must question your experimental methodology in '${subject.name}'. What control variables did you establish to eliminate systemic error?"
            else -> "Oral Exam Question for ${subject.name}: Explain how your chosen theorem holds under extreme limit cases."
        }

        return ChatMessage(
            sender = MessageSender.CHARACTER,
            text = feedback,
            mode = StudyMode.PRACTICE_EXAM,
            suggestedReplies = listOf(
                "By establishing asymptotic boundary bounds",
                "Through rigorous empirical control groups",
                "I reject the premise because variables are normalized"
            )
        )
    }

    private fun generateDebateResponse(char: PersonaCharacter, subject: StudySubject, userText: String): ChatMessage {
        val counter = when (char.id) {
            "makise_kurisu" -> "A bold stance! But you failed to account for entropy. If your argument holds, how do you explain the thermodynamic irreversibility in the system? You have 2 minutes to counter!"
            "gojo_satoru" -> "Haha, nice try! But that thesis has a gaping hole right in the center. What happens when your underlying model encounters a singularity? Convince me!"
            "levi_ackerman" -> "Flawed reasoning. You are relying on idealistic conditions. Real battlefield conditions will shatter that theory. What is your contingency plan?"
            else -> "Interesting thesis. However, empirical counter-examples suggest the opposite under high entropy. How do you defend it?"
        }

        return ChatMessage(
            sender = MessageSender.CHARACTER,
            text = counter,
            mode = StudyMode.DEBATE_MODE,
            suggestedReplies = listOf(
                "The singularity is bypassed via renormalization",
                "Empirical data demonstrates resilience in closed loops",
                "I concede that point; let's adjust the framework"
            )
        )
    }
}

private data class Quad(val q: String, val options: List<String>, val correct: Int, val expl: String)
