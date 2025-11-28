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

package com.kof22.claritas.model

/**
 * The result of formatting a comment block.
 *
 * @property formattedText The complete formatted comment text
 * @property lineCount Number of lines in the formatted comment
 * @property actualWidth The actual width used (may differ from requested for dynamic width)
 */
data class FormattedComment(
   val formattedText: String,
   val lineCount: Int,
   val actualWidth: Int
)

