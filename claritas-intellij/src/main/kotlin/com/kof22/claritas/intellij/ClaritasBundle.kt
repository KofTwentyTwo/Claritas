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

package com.kof22.claritas.intellij

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.Claritas"

object ClaritasBundle : DynamicBundle(BUNDLE) {
   @JvmStatic
   fun message(
      @PropertyKey(resourceBundle = BUNDLE) key: String,
      vararg params: Any
   ) = getMessage(key, *params)

   @Suppress("unused")
   @JvmStatic
   fun messagePointer(
      @PropertyKey(resourceBundle = BUNDLE) key: String,
      vararg params: Any
   ) = getLazyMessage(key, *params)
}
