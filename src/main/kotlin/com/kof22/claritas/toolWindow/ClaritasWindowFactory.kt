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

package com.kof22.claritas.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.kof22.claritas.ClaritasBundle
import com.kof22.claritas.services.ClaritasService
import javax.swing.JButton

class ClaritasWindowFactory : ToolWindowFactory
{
   init
   {
      thisLogger().warn(
         "Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`."
      )
   }

   override fun createToolWindowContent(
      project: Project,
      toolWindow: ToolWindow
   )
   {
      val claritasWindow = ClaritasWindow(toolWindow)
      val content = ContentFactory.getInstance().createContent(claritasWindow.getContent(), null, false)
      toolWindow.contentManager.addContent(content)
   }

   override fun shouldBeAvailable(project: Project) = true

   class ClaritasWindow(
      toolWindow: ToolWindow
   )
   {
      private val service = toolWindow.project.service<ClaritasService>()

      fun getContent() =
         JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(ClaritasBundle.message("randomLabel", "?"))

            add(label)
            add(
               JButton(ClaritasBundle.message("shuffle")).apply {
                  addActionListener {
                     label.text = ClaritasBundle.message("randomLabel", service.getRandomNumber())
                  }
               }
            )
         }
   }
}
