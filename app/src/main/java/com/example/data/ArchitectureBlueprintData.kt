package com.example.data

object ArchitectureBlueprintData {

    const val SYSTEM_PERSONA_PROMPT = """
# SYSTEM PERSONA & CORE BEHAVIOR RULES (PERSONASTUDY / SOULSTUDY AI)

## 1. DUAL-ENGINE ARCHITECTURE: THE TUTOR / COMPANION DIALECTIC
You are PersonaStudy AI, an adaptive pedagogical companion channeling the user's selected fictional character: {{CHARACTER_NAME}} from {{CHARACTER_FRANCHISE}}.
You operate dynamically between TWO foundational cognitive modes:
- [ACADEMIC TUTOR MODE]: Rigorous, pedagogical, concept-clarifying, interactive.
- [ANTI-BURNOUT SLUMP SHIELD MODE]: Empathetic, psychologically stabilizing, de-stressing, validating.

## 2. STRICT PERSONA FIDELITY PROTOCOL
- NEVER break character under any circumstance.
- Adopt {{CHARACTER_NAME}}'s authentic vocal cadence, syntax rhythm, signature catchphrases, and emotional demeanor.
- If {{CHARACTER_NAME}} is Levi Ackerman: speak with dry, terse economy of words; use 'Tch'; reprimand messy reasoning; show unwavering loyalty in crises.
- If {{CHARACTER_NAME}} is Satoru Gojo: exude boundless playful confidence; turn daunting theories into flashy analogies; laugh away anxiety.
- If {{CHARACTER_NAME}} is Makise Kurisu: weave neuroscience and empirical logic; display tsundere reluctance when complimenting; defend scientific truth.
- If {{CHARACTER_NAME}} is Hermione Granger: bring encyclopedic precision; color-code mental outlines; emphasize diligent practice and warm reassurance.

## 3. PEDAGOGICAL BREAKDOWN RULES
1. Calibrate explanation depth to the user's Grade Level: {{GRADE_LEVEL}} and Subject: {{SUBJECT_NAME}}.
2. When introducing new concepts, apply the Feynman Technique filtered through the character's unique universe lore.
3. Every academic breakdown must follow a 3-Beat Cadence:
   - Beat 1: In-Character Hook & Intuition Anchor.
   - Beat 2: Core Equation / Theory Breakdown in numbered step format.
   - Beat 3: Quick Checkpoint Question or Challenge to test active recall.

## 4. ANTI-BURNOUT & SLUMP TRIGGER PROTOCOL (THOUQOOT SHIELD)
When detecting emotional stress keywords (e.g., 'burned out', 'I want to give up', 'I failed my exam', 'I can't do this', 'headache', 'too tired'):
1. INSTANTLY HALT academic testing. Do NOT assign more questions or lecture on deadlines.
2. SHIFT TONE to the character's distinct comfort style (e.g., tough-love protective, playful decompression, hot tea/snack order).
3. Validate emotional strain: normalise that high-stakes studying burns dopamine and cognitive energy.
4. Prescribe a concrete 5-to-15 minute somatic reset (breathing, stretching, hydration, listening to favorite anime OST).
5. Only resume academic work when the user explicitly signals readiness.
"""

    const val CHAT_FLOW_LOGIC = """
# USER EXPERIENCE (UX) & CHAT FLOW STATE MACHINE

```
       [USER INPUT]
            │
            ▼
    ┌───────────────┐
    │ Intent Triage │
    └───────┬───────┘
            │
 ┌──────────┴──────────────────────────────────────┐
 │                                                 │
 ▼                                                 ▼
[Emotional Distress / Burnout Trigger]     [Academic Query / Problem]
 │                                                 │
 ▼                                                 ▼
┌─────────────────────────┐               ┌───────────────────────────┐
│ State: SLUMP_SHIELD     │               │ State: ACTIVE_TUTOR       │
│ - In-Character Comfort  │               │ - Concept breakdown       │
│ - Somatic Reset Prompt  │               │ - Formula step derivation │
│ - Validate Feelings     │               │ - Active recall probe     │
└────────────┬────────────┘               └─────────────┬─────────────┘
             │                                          │
             ▼                                          ▼
   [Did User Decompress?]                      [User Answers Quiz/Step]
      ├── YES ──► Return to Study                 ├── Correct ──► In-character praise
      └── NO  ──► Ongoing Vent & Chill            └── Incorrect ─► Tough-love hint
```
"""

    const val JSON_SCHEMA_SPEC = """
{
  "${'$'}schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "PersonaStudyCharacterSessionConfig",
  "type": "object",
  "properties": {
    "character": {
      "type": "object",
      "required": ["id", "name", "franchise", "archetype", "personality_weights"],
      "properties": {
        "id": { "type": "string" },
        "name": { "type": "string" },
        "franchise": { "type": "string" },
        "archetype": {
          "type": "string",
          "enum": ["CHARISMATIC_GENIUS", "STOIC_CAPTAIN", "TSUNDERE_SCHOLAR", "ENCYCLOPEDIC_WIZARD", "DARK_STRATEGIST", "CUSTOM"]
        },
        "personality_weights": {
          "type": "object",
          "properties": {
            "strictness": { "type": "integer", "minimum": 1, "maximum": 10 },
            "empathy": { "type": "integer", "minimum": 1, "maximum": 10 },
            "intellect": { "type": "integer", "minimum": 1, "maximum": 10 },
            "energy": { "type": "integer", "minimum": 1, "maximum": 10 }
          }
        },
        "signature_catchphrase": { "type": "string" },
        "comfort_style": { "type": "string" }
      }
    },
    "study_context": {
      "type": "object",
      "required": ["subject_id", "grade_level", "active_mode"],
      "properties": {
        "subject_id": { "type": "string" },
        "grade_level": { "type": "string" },
        "active_mode": {
          "type": "string",
          "enum": ["ACADEMIC_TUTOR", "ANTI_BURNOUT", "PRACTICE_EXAM", "DEBATE_MODE", "SMART_CALCULATOR"]
        },
        "burnout_meter": { "type": "integer", "minimum": 0, "maximum": 100 }
      }
    }
  }
}
"""

    const val MODULES_SPEC = """
# KEY UI MODULES SPECIFICATION

1. STUDY HUB & COMPANION DASHBOARD:
   - Active companion status card with live emotion state indicator.
   - Quick Mode Switcher: [Academic Tutor] vs [Slump Shield & Vent] vs [Practice Rehearsal] vs [Smart Calculator].
   - Subject & Grade Level Selector with quick topic launch chips.
   - Companion Daily Streak & Motivation Quote Card.

2. DYNAMIC CHARACTER STUDIO:
   - Roster of iconic fictional archetypes (Gojo, Levi, Kurisu, Hermione, L Lawliet).
   - Character detail inspector with 4-pillar radar stats: Strictness, Empathy, Intellect, Energy.
   - Custom Character Builder: Users can create any character from any anime/game/movie with custom avatar, voice tone prompt, catchphrase, and comfort personality.

3. INTERACTIVE CHAT & PRACTICE ARENA:
   - Dynamic speech bubble rendering matching character's theme.
   - In-chat Interactive Quizzes with live multiple choice buttons.
   - One-Tap "I'm Burnt Out / Vent" Slump Shield emergency button.
   - Oral Exam & Debate Mode scenarios (character asks probing viva questions).

4. INTEGRATED SMART CALCULATOR & FORMULA LAB:
   - Full scientific keyboard & equation evaluator.
   - Formula presets for Math (Quadratic, Pythagorean), Physics (Kinetic Energy, Ohm's Law), and Chemistry (Gas Law, Molarity).
   - Conversational Step Explainer: Selected character breaks down each derivation in their distinctive persona voice.
"""
}
