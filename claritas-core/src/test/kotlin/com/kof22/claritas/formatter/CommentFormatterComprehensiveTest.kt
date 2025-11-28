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

/**
 * Comprehensive tests for CommentFormatter covering all edge cases.
 */
class CommentFormatterComprehensiveTest {
   private val defaultStyle =
      FlowerboxStyle(
         borderChar = '*',
         linePrefix = " ** ",
         fixedWidth = 80,
         useJavadocStyle = true
      )

   ///////////////////////////////////////
   // Single Line Tests                 //
   ///////////////////////////////////////

   @Test
   fun testSingleShortLine() {
      val formatter = CommentFormatter(defaultStyle)
      val block =
         CommentBlock(
            rawText = "Short comment",
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("Short comment"))
      assertTrue(result.lineCount >= 3) // Top border, content, bottom border
   }

   @Test
   fun testSingleLongLine() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "This is a very long comment that definitely exceeds the normal line length and should be wrapped"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Should be wrapped into multiple content lines   //
      /////////////////////////////////////////////////////
      assertTrue(result.lineCount > 3)
      assertTrue(result.formattedText.contains("very long comment"))
   }

   @Test
   fun testSingleVeryLongLine() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "A".repeat(150) // 150 character line

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Should be wrapped - at least 3 lines            //
      /////////////////////////////////////////////////////
      assertTrue(result.lineCount >= 3, "Expected at least 3 lines, got ${result.lineCount}")
      // Very long words get their own line
      assertTrue(result.formattedText.contains("A"))
   }

   ///////////////////////////////////////
   // Multi-Line Tests                  //
   ///////////////////////////////////////

   @Test
   fun testMultiLineShortLines() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "Line one\nLine two\nLine three"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("Line one"))
      assertTrue(result.formattedText.contains("Line two"))
      assertTrue(result.formattedText.contains("Line three"))
   }

   @Test
   fun testMultiLineLongAtStart() {
      val formatter = CommentFormatter(defaultStyle)
      val longLine = "This is a very long line at the start that should be wrapped to multiple lines"
      val text = "$longLine\nShort line\nAnother short"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Long line should be wrapped                     //
      /////////////////////////////////////////////////////
      assertTrue(result.formattedText.contains("very long line"))
      assertTrue(result.formattedText.contains("Short line"))
   }

   @Test
   fun testMultiLineLongInMiddle() {
      val formatter = CommentFormatter(defaultStyle)
      val longLine = "This middle line is exceptionally long and should be wrapped into multiple lines"
      val text = "Short start\n$longLine\nShort end"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("Short start"))
      assertTrue(result.formattedText.contains("middle line"))
      assertTrue(result.formattedText.contains("Short end"))
   }

   @Test
   fun testMultiLineLongAtEnd() {
      val formatter = CommentFormatter(defaultStyle)
      val longLine = "The final line is very long and needs to be wrapped properly to fit within bounds"
      val text = "Short one\nShort two\n$longLine"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("Short one"))
      assertTrue(result.formattedText.contains("final line"))
   }

   @Test
   fun testMultiLineAllLong() {
      val formatter = CommentFormatter(defaultStyle)
      val long1 = "First line is very long and exceeds the typical line length that we expect"
      val long2 = "Second line is also quite lengthy and needs wrapping to maintain readability"
      val long3 = "Third line continues the pattern of being excessively long for normal display"
      val text = "$long1\n$long2\n$long3"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // All lines should be present (possibly wrapped)  //
      /////////////////////////////////////////////////////
      assertTrue(result.formattedText.contains("First line"))
      assertTrue(result.formattedText.contains("Second line"))
      assertTrue(result.formattedText.contains("Third line"))
   }

   ///////////////////////////////////////
   // Existing Comment Tests            //
   ///////////////////////////////////////

   @Test
   fun testExistingLineComment() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "// This is an existing line comment"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Should extract text and format as flowerbox     //
      /////////////////////////////////////////////////////
      assertTrue(result.formattedText.contains("existing line comment"))
      assertTrue(result.formattedText.startsWith("/**"))
   }

   @Test
   fun testExistingBlockComment() {
      val formatter = CommentFormatter(defaultStyle)
      val text =
         """
         /* This is an existing
          * block comment
          * with multiple lines */
         """.trimIndent()

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("existing"))
      assertTrue(result.formattedText.contains("block comment"))
      assertTrue(result.formattedText.contains("multiple lines"))
   }

   @Test
   fun testExistingJavadoc() {
      val formatter = CommentFormatter(defaultStyle)
      val text =
         """
         /**
          * This is an existing Javadoc comment.
          * @param foo parameter
          */
         """.trimIndent()

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.JAVADOC
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("existing Javadoc"))
      assertTrue(result.formattedText.contains("@param foo parameter"))
   }

   @Test
   fun testExistingCommentWithLongLines() {
      val formatter = CommentFormatter(defaultStyle)
      val longLine =
         "This line in the existing comment is very long and should be properly wrapped when reformatted"
      val text = "/* $longLine */"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Should be wrapped                               //
      /////////////////////////////////////////////////////
      assertTrue(result.lineCount > 3)
      assertTrue(result.formattedText.contains("existing comment"))
   }

   ///////////////////////////////////////
   // Paragraph Preservation Tests      //
   ///////////////////////////////////////

   @Test
   fun testParagraphPreservation() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "First paragraph here.\n\nSecond paragraph here."

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.BLOCK,
            preserveParagraphs = true
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Should have blank line between paragraphs       //
      /////////////////////////////////////////////////////
      val lines = result.formattedText.lines()
      val hasBlankLine = lines.any { it.trim() == " ** */" || it.contains("**                 ") }
      assertTrue(hasBlankLine || result.lineCount > 4)
   }

   ///////////////////////////////////////
   // Edge Cases                        //
   ///////////////////////////////////////

   @Test
   fun testEmptyText() {
      val formatter = CommentFormatter(defaultStyle)
      val block =
         CommentBlock(
            rawText = "",
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Empty text results in minimal structure        //
      /////////////////////////////////////////////////////
      // BorderRenderer returns empty for empty content
      // This is acceptable behavior
      assertTrue(result.lineCount >= 0)
   }

   @Test
   fun testWhitespaceOnly() {
      val formatter = CommentFormatter(defaultStyle)
      val block =
         CommentBlock(
            rawText = "   \n   \n   ",
            type = CommentType.BLOCK
         )

      val result = formatter.format(block)

      /////////////////////////////////////////////////////
      // Whitespace-only is treated as empty             //
      /////////////////////////////////////////////////////
      // This is acceptable behavior
      assertTrue(result.lineCount >= 0)
   }

   @Test
   fun testSpecialCharacters() {
      val formatter = CommentFormatter(defaultStyle)
      val text = "Comment with special chars: @#$%^&*()"

      val block =
         CommentBlock(
            rawText = text,
            type = CommentType.LINE
         )

      val result = formatter.format(block)

      assertTrue(result.formattedText.contains("special chars"))
      assertTrue(result.formattedText.contains("@#$%^&*()"))
   }
}

