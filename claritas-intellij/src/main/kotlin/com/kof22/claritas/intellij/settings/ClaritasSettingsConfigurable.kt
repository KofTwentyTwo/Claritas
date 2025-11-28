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
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JRadioButton

/**
 * Claritas settings UI.
 *
 * Provides configuration for:
 * - Enable/disable plugin
 * - Rebalancing behavior
 * - Maximum line length
 * - Comment block style
 */
class ClaritasSettingsConfigurable : Configurable {
   private var mainPanel: JPanel? = null
   private var enableClaritasCheckbox: JBCheckBox? = null
   private var rebalanceBlocksCheckbox: JBCheckBox? = null
   private var maxLineLengthField: JBTextField? = null
   private var javadocStyleRadio: JRadioButton? = null
   private var blockStyleRadio: JRadioButton? = null

   override fun getDisplayName(): String = "Claritas"

   override fun createComponent(): JComponent {
      // /////////////////////////////////////////
      // Create UI components                  //
      // /////////////////////////////////////////
      val enableCheckbox = JBCheckBox("Enable Claritas Plugin")
      val rebalanceCheckbox = JBCheckBox("Rebalance Blocks (rewrap long lines)")
      val maxLengthField = JBTextField(10)
      val javadocRadio = JRadioButton("Javadoc Style (/** ... */)")
      val blockRadio = JRadioButton("Block Style (/* ... */)")

      // /////////////////////////////////////////
      // Group radio buttons                   //
      // /////////////////////////////////////////
      val styleGroup = ButtonGroup()
      styleGroup.add(javadocRadio)
      styleGroup.add(blockRadio)

      // /////////////////////////////////////////
      // Create style panel                    //
      // /////////////////////////////////////////
      val stylePanel = JPanel(BorderLayout())
      val styleSubPanel = JPanel()
      styleSubPanel.layout = java.awt.FlowLayout(java.awt.FlowLayout.LEFT)
      styleSubPanel.add(javadocRadio)
      styleSubPanel.add(blockRadio)
      stylePanel.add(styleSubPanel, BorderLayout.WEST)

      // /////////////////////////////////////////
      // Build form layout                     //
      // /////////////////////////////////////////
      val panel =
         FormBuilder
            .createFormBuilder()
            .addComponent(enableCheckbox)
            .addSeparator()
            .addComponent(rebalanceCheckbox)
            .addLabeledComponent(
               "Max Line Length:",
               maxLengthField
            ).addSeparator()
            .addComponent(stylePanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel

      // /////////////////////////////////////////
      // Store references                      //
      // /////////////////////////////////////////
      mainPanel = panel
      enableClaritasCheckbox = enableCheckbox
      rebalanceBlocksCheckbox = rebalanceCheckbox
      maxLineLengthField = maxLengthField
      javadocStyleRadio = javadocRadio
      blockStyleRadio = blockRadio

      return panel
   }

   override fun isModified(): Boolean {
      val settings = ClaritasSettings.getInstance()
      val state = settings.state

      return enableClaritasCheckbox?.isSelected != state.enableClaritas ||
         rebalanceBlocksCheckbox?.isSelected != state.rebalanceBlocks ||
         maxLineLengthField?.text?.toIntOrNull() != state.maxLineLength ||
         javadocStyleRadio?.isSelected != (state.commentBlockStyle == ClaritasSettings.CommentBlockStyle.JAVADOC)
   }

   override fun apply() {
      val settings = ClaritasSettings.getInstance()
      val state = settings.state

      state.enableClaritas = enableClaritasCheckbox?.isSelected ?: true
      state.rebalanceBlocks = rebalanceBlocksCheckbox?.isSelected ?: true
      state.maxLineLength = maxLineLengthField?.text?.toIntOrNull() ?: 70
      state.commentBlockStyle =
         if (javadocStyleRadio?.isSelected == true) {
            ClaritasSettings.CommentBlockStyle.JAVADOC
         } else {
            ClaritasSettings.CommentBlockStyle.BLOCK
         }
   }

   override fun reset() {
      val settings = ClaritasSettings.getInstance()
      val state = settings.state

      enableClaritasCheckbox?.isSelected = state.enableClaritas
      rebalanceBlocksCheckbox?.isSelected = state.rebalanceBlocks
      maxLineLengthField?.text = state.maxLineLength.toString()

      when (state.commentBlockStyle) {
         ClaritasSettings.CommentBlockStyle.JAVADOC -> javadocStyleRadio?.isSelected = true
         ClaritasSettings.CommentBlockStyle.BLOCK -> blockStyleRadio?.isSelected = true
      }
   }

   override fun disposeUIResources() {
      mainPanel = null
      enableClaritasCheckbox = null
      rebalanceBlocksCheckbox = null
      maxLineLengthField = null
      javadocStyleRadio = null
      blockStyleRadio = null
   }
}
