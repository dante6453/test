package com.example.data

import com.example.model.FormulaPreset
import com.example.model.FormulaVariable

object FormulaRepository {

    val formulas: List<FormulaPreset> = listOf(
        FormulaPreset(
            id = "quadratic",
            category = "Mathematics",
            name = "Quadratic Formula",
            formula = "x = (-b ± √(b² - 4ac)) / (2a)",
            description = "Find roots of quadratic polynomial ax² + bx + c = 0",
            variables = listOf(
                FormulaVariable("a", "Coefficient a", "", 1.0),
                FormulaVariable("b", "Coefficient b", "", -5.0),
                FormulaVariable("c", "Constant c", "", 6.0)
            ),
            exampleProblem = "Solve x² - 5x + 6 = 0"
        ),
        FormulaPreset(
            id = "kinetic_energy",
            category = "Physics",
            name = "Kinetic Energy",
            formula = "KE = ½ · m · v²",
            description = "Kinetic energy of a body in motion",
            variables = listOf(
                FormulaVariable("m", "Mass", "kg", 60.0),
                FormulaVariable("v", "Velocity", "m/s", 15.0)
            ),
            exampleProblem = "Calculate kinetic energy for a 60 kg scout moving at 15 m/s"
        ),
        FormulaPreset(
            id = "ohms_law",
            category = "Physics",
            name = "Ohm's Law & Power",
            formula = "V = I · R  |  P = V · I",
            description = "Voltage, current, electrical resistance & power dissipation",
            variables = listOf(
                FormulaVariable("I", "Current", "A", 2.5),
                FormulaVariable("R", "Resistance", "Ω", 48.0)
            ),
            exampleProblem = "Find voltage and power across a 48 Ω resistor drawing 2.5 A"
        ),
        FormulaPreset(
            id = "ideal_gas",
            category = "Chemistry",
            name = "Ideal Gas Law",
            formula = "P · V = n · R · T",
            description = "State equation for ideal gas pressure, volume, and temperature",
            variables = listOf(
                FormulaVariable("n", "Moles", "mol", 2.0),
                FormulaVariable("T", "Temperature", "K", 298.15),
                FormulaVariable("V", "Volume", "L", 22.4)
            ),
            exampleProblem = "Compute pressure of 2 moles gas in 22.4 L at 298.15 K (R=0.0821 L·atm/(mol·K))"
        ),
        FormulaPreset(
            id = "molarity",
            category = "Chemistry",
            name = "Molar Solution Concentration",
            formula = "M = n / V",
            description = "Molarity of solute dissolved in liters of solution",
            variables = listOf(
                FormulaVariable("mass", "Solute Mass", "g", 58.44),
                FormulaVariable("molarMass", "Molar Mass", "g/mol", 58.44),
                FormulaVariable("volume", "Solution Volume", "L", 0.5)
            ),
            exampleProblem = "Find molarity of 58.44g NaCl dissolved in 0.5 Liters of water"
        ),
        FormulaPreset(
            id = "pythagorean",
            category = "Mathematics",
            name = "Pythagorean Theorem",
            formula = "c = √(a² + b²)",
            description = "Hypotenuse length in right-angled triangles",
            variables = listOf(
                FormulaVariable("a", "Leg a", "units", 9.0),
                FormulaVariable("b", "Leg b", "units", 12.0)
            ),
            exampleProblem = "Find hypotenuse c given perpendicular legs 9 and 12"
        )
    )
}
