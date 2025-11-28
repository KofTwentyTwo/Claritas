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

package com.kof22.claritas.formatter

import com.kof22.claritas.model.CommentBlock
import com.kof22.claritas.model.CommentType
import com.kof22.claritas.model.FlowerboxStyle
import kotlin.test.Test
import kotlin.test.assertTrue

class CommentFormatterTest
{
   @Test
   fun testFormatSimpleComment()
   {
      val style = FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = 40,
         useJavadocStyle = true
      )

      val formatter = CommentFormatter(style)
      val comment = CommentBlock(
         rawText = "// This is a test comment",
         type = CommentType.LINE
      )

      val result = formatter.format(comment)

      //////////////////////
      // Verify structure //
      //////////////////////
      val lines = result.formattedText.lines()
      assertTrue(lines.size >= 3) // Top border, content, bottom border

      ////////////////////
      // Verify borders //
      ////////////////////
      assertTrue(lines.first().startsWith("/**"))
      assertTrue(lines.first().endsWith("*/"))
      assertTrue(lines.last().startsWith("/*"))
      assertTrue(lines.last().endsWith("*/"))

      ////////////////////////////////////
      // Verify content line has prefix //
      ////////////////////////////////////
      assertTrue(lines[1].contains(" ** "))
      assertTrue(lines[1].contains("This is a test comment"))
   }

   @Test
   fun testFormatMultiLineComment()
   {
      val style = FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = 50,
         useJavadocStyle = false
      )

      val formatter = CommentFormatter(style)
      val comment = CommentBlock(
         rawText = """
            /* This is a
             * longer comment
             * with multiple lines */
         """.trimIndent(),
         type = CommentType.BLOCK
      )

      val result = formatter.format(comment)

      //////////////////
      // Debug output //
      //////////////////
      println("Formatted text:")
      println(result.formattedText)
      println("Line count: ${result.lineCount}")

      ////////////////////////////
      // Verify we have content //
      ////////////////////////////
      assertTrue(result.lineCount >= 3, "Expected at least 3 lines, got ${result.lineCount}")

      ///////////////////////////////////////////////////////////////////////////
      // Content should be preserved (words may be rearranged due to wrapping) //
      ///////////////////////////////////////////////////////////////////////////
      assertTrue(result.formattedText.contains("longer") || result.formattedText.contains("comment"))
      assertTrue(result.formattedText.contains("multiple") || result.formattedText.contains("lines"))
   }

   @Test
   fun testFormatTextConvenience()
   {
      val style = FlowerboxStyle.JAVADOC_DEFAULT
      val formatter = CommentFormatter(style)

      val result = formatter.formatText("Simple comment text")

      assertTrue(result.isNotEmpty())
      assertTrue(result.contains("Simple comment text"))
      assertTrue(result.startsWith("/**"))
   }

   @Test
   fun testDynamicWidth()
   {
      val style = FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = null, // Dynamic width
         minWidth = 40,
         maxWidth = 100,
         useJavadocStyle = false
      )

      val formatter = CommentFormatter(style)
      val comment = CommentBlock(
         rawText = "Short text",
         type = CommentType.INLINE
      )

      val result = formatter.format(comment)

      ///////////////////////////////////////////
      // Dynamic width should adapt to content //
      ///////////////////////////////////////////
      assertTrue(result.actualWidth >= style.minWidth)
      assertTrue(result.actualWidth <= style.maxWidth)
   }
}
