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

class TextNormalizerTest
{
   @Test
   fun testNormalizeLineComment()
   {
      val input = "// This is a comment"
      val result = TextNormalizer.normalize(input)
      assertEquals("This is a comment", result)
   }

   @Test
   fun testNormalizeBlockComment()
   {
      val input = """
         /* This is a
          * multi-line
          * comment */
      """.trimIndent()

      val result = TextNormalizer.normalize(input)
      assertEquals("This is a\nmulti-line\ncomment", result)
   }

   @Test
   fun testNormalizeJavadoc()
   {
      val input = """
         /**
          * This is a Javadoc comment.
          * @param foo the parameter
          */
      """.trimIndent()

      val result = TextNormalizer.normalize(input)
      assertEquals("This is a Javadoc comment.\n@param foo the parameter", result)
   }

   @Test
   fun testSplitIntoParagraphs()
   {
      val input = "First paragraph text.\n\nSecond paragraph text."
      val paragraphs = TextNormalizer.splitIntoParagraphs(input)

      assertEquals(2, paragraphs.size)
      assertEquals("First paragraph text.", paragraphs[0])
      assertEquals("Second paragraph text.", paragraphs[1])
   }

   @Test
   fun testSplitMultiLineParagraph()
   {
      val input = "Line one\nLine two\n\nLine three"
      val paragraphs = TextNormalizer.splitIntoParagraphs(input)

      assertEquals(2, paragraphs.size)
      assertEquals("Line one Line two", paragraphs[0])
      assertEquals("Line three", paragraphs[1])
   }
}


