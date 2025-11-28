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

package com.kof22.claritas.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.kof22.claritas.ClaritasBundle

@Service(Service.Level.PROJECT)
class ClaritasService(
   project: Project
)
{
   init
   {
      thisLogger().info(ClaritasBundle.message("projectService", project.name))
      thisLogger().warn(
         "Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`."
      )
   }

   fun getRandomNumber() = (1..100).random()
}
