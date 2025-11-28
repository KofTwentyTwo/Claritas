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

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * Claritas application-level settings.
 *
 * This is a simple POC version with minimal settings.
 * Will be expanded in Phase 2 to include all configuration options.
 */
@State(
   name = "ClaritasSettings",
   storages = [Storage("ClaritasSettings.xml")]
)
@Service(Service.Level.APP)
class ClaritasSettings : PersistentStateComponent<ClaritasSettings.State> {
   data class State(
      var enableClaritas: Boolean = true
   )

   private var myState = State()

   override fun getState(): State = myState

   override fun loadState(state: State) {
      myState = state
   }

   companion object {
      fun getInstance(): ClaritasSettings = service()
   }
}
