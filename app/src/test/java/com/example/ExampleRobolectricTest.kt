package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CharacterRepository
import com.example.data.FormulaRepository
import com.example.ui.viewmodel.PersonaStudyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PersonaStudy", appName)
  }

  @Test
  fun `verify character repository loaded`() {
    val characters = CharacterRepository.defaultCharacters
    assertTrue(characters.size >= 5)
    assertNotNull(characters.find { it.id == "gojo_satoru" })
    assertNotNull(characters.find { it.id == "levi_ackerman" })
  }

  @Test
  fun `verify formula repository presets`() {
    val formulas = FormulaRepository.formulas
    assertTrue(formulas.isNotEmpty())
    assertNotNull(formulas.find { it.id == "quadratic" })
  }

  @Test
  fun `verify view model initial state`() {
    val vm = PersonaStudyViewModel()
    val state = vm.uiState.value
    assertEquals("Satoru Gojo", state.selectedCharacter.name)
    assertTrue(state.messages.isNotEmpty())
  }
}
