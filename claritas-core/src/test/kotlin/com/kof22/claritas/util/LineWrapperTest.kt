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

package com.kof22.claritas.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LineWrapperTest
{
   @Test
   fun testWrapShortText()
   {
      val text = "Short text"
      val lines = LineWrapper.wrap(text, 50)

      assertEquals(1, lines.size)
      assertEquals("Short text", lines[0])
   }

   @Test
   fun testWrapLongText()
   {
      val text = "This is a longer piece of text that should be wrapped at word boundaries"
      val lines = LineWrapper.wrap(text, 30)

      // Verify it's wrapped into multiple lines
      assertTrue(lines.size >= 3)

      // Verify first line doesn't exceed max width
      assertTrue(lines[0].length <= 30)

      // Verify all content is preserved
      val rejoined = lines.joinToString(" ")
      assertEquals(text, rejoined)
   }

   @Test
   fun testWrapWithLongWord()
   {
      val text = "Short word verylongwordthatexceedsthemaxwidth short"
      val lines = LineWrapper.wrap(text, 20)

      // Long word gets its own line
      assertEquals(3, lines.size)
      assertEquals("Short word", lines[0])
      assertEquals("verylongwordthatexceedsthemaxwidth", lines[1])
      assertEquals("short", lines[2])
   }

   @Test
   fun testWrapParagraphs()
   {
      val paragraphs = listOf(
         "First paragraph text here",
         "Second paragraph text here"
      )

      val lines = LineWrapper.wrapParagraphs(paragraphs, 50)

      assertEquals(3, lines.size) // 1 + blank + 1
      assertEquals("First paragraph text here", lines[0])
      assertEquals("", lines[1])
      assertEquals("Second paragraph text here", lines[2])
   }
}

