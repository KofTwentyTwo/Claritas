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

package com.kof22.claritas.intellij.startup

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Claritas startup activity.
 * Executes when a project is opened. Can be used for initialization tasks.
 */
class ClaritasActivity : ProjectActivity {
   override suspend fun execute(project: Project) {
      thisLogger().info("Claritas plugin initialized for project: ${project.name}")
   }
}
