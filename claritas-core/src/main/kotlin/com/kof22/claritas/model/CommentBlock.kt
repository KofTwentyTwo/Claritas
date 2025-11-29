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
 * Represents a comment block to be formatted.
 *
 * @property rawText The original comment text (with or without markers)
 * @property type The type of comment
 * @property indentLevel Number of spaces to indent the formatted comment
 * @property preserveParagraphs Whether to preserve blank line paragraph breaks
 */
data class CommentBlock(
   val rawText: String,
   val type: CommentType,
   val indentLevel: Int = 0,
   val preserveParagraphs: Boolean = true
)


