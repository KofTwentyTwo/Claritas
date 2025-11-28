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

package com.kof22.claritas.intellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.kof22.claritas.intellij.ClaritasBundle

/**
 * Claritas service for project-level operations.
 * This service will be used for future features like caching and batch processing.
 */
@Service(Service.Level.PROJECT)
class ClaritasService(
   private val project: Project
)
{
   init
   {
      thisLogger().info(ClaritasBundle.message("projectService", project.name))
   }

   companion object
   {
      fun getInstance(project: Project): ClaritasService = project.service()
   }
}
