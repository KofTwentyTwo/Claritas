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
 * Types of comments that Claritas can process.
 */
enum class CommentType
{
   /**
    * Single-line comment starting with //
    */
   LINE,

   /**
    * Multi-line block comment (slash-star ... star-slash)
    */
   BLOCK,

   /**
    * Javadoc comment (slash-star-star ... star-slash)
    */
   JAVADOC,

   /**
    * Inline comment within code (uses dynamic width)
    */
   INLINE
}

