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
 * Configuration for flowerbox comment formatting.
 *
 * @property borderChar Character used for top/bottom borders (e.g., '*', '=', '-')
 * @property linePrefix Prefix for each content line (e.g., " * ", " ** ")
 * @property fixedWidth Fixed width for the comment box (null = dynamic width)
 * @property minWidth Minimum width for dynamic-width comments
 * @property maxWidth Maximum width for any comment
 * @property indent Number of spaces to indent the entire comment
 * @property addTopBlanks Number of blank lines after top border
 * @property addBottomBlanks Number of blank lines before bottom border
 * @property useJavadocStyle Use slash-star-star style (true) or slash-star (false)
 */
data class FlowerboxStyle(
   val borderChar: Char = '*',
   val linePrefix: String = " ** ",
   val fixedWidth: Int? = 80,
   val minWidth: Int = 40,
   val maxWidth: Int = 120,
   val indent: Int = 0,
   val addTopBlanks: Int = 0,
   val addBottomBlanks: Int = 0,
   val useJavadocStyle: Boolean = true
)
{
   companion object
   {
      /**
       * Default style for Javadoc comments (fixed width 80).
       */
      val JAVADOC_DEFAULT = FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = 80,
         useJavadocStyle = true
      )

      /**
       * Default style for inline comments (dynamic width).
       */
      val INLINE_DEFAULT = FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = null,
         minWidth = 40,
         maxWidth = 100,
         useJavadocStyle = false
      )
   }
}

