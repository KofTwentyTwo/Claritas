/*
 * All Rights Reserved
 *
 * Copyright (c) 2025. Kof22
 *
 * THE CONTENTS OF THIS PROJECT ARE PROPRIETARY AND CONFIDENTIAL.
 * UNAUTHORIZED COPYING, TRANSFERRING, OR REPRODUCTION OF ANY PART OF THIS PROJECT, VIA ANY MEDIUM, IS STRICTLY PROHIBITED.
 *
 * The receipt or possession of the source code and/or any parts thereof does not convey or imply any right to use them
 * for any purpose other than the purpose for which they were provided to you.
 *
 *
 *
 */

package com.kof22.claritas.intellij.settings

import com.intellij.openapi.options.Configurable
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Claritas settings UI.
 *
 * This is a simple POC version with minimal UI.
 * Will be expanded in Phase 2 to include tabbed interface with all options.
 */
class ClaritasSettingsConfigurable : Configurable {
   private var settingsPanel: JPanel? = null
   private var enableClaritasCheckbox: JCheckBox? = null

   override fun getDisplayName(): String = "Claritas"

   override fun createComponent(): JComponent {
      val panel = JPanel(BorderLayout())
      val checkbox = JCheckBox("Enable Claritas Plugin")

      panel.add(checkbox, BorderLayout.NORTH)

      settingsPanel = panel
      enableClaritasCheckbox = checkbox

      return panel
   }

   override fun isModified(): Boolean {
      val settings = ClaritasSettings.getInstance()
      return enableClaritasCheckbox?.isSelected != settings.state.enableClaritas
   }

   override fun apply() {
      val settings = ClaritasSettings.getInstance()
      settings.state.enableClaritas = enableClaritasCheckbox?.isSelected ?: true
   }

   override fun reset() {
      val settings = ClaritasSettings.getInstance()
      enableClaritasCheckbox?.isSelected = settings.state.enableClaritas
   }

   override fun disposeUIResources() {
      settingsPanel = null
      enableClaritasCheckbox = null
   }
}
