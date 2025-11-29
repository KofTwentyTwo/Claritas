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

package com.kof22.claritas.intellij.psi

import com.kof22.claritas.model.CommentType

/**
 * TextBlockExtractor tests for Java language.
 *
 * Inherits all test cases from TextBlockExtractorTestBase and validates
 * comment extraction for Java files (.java).
 *
 * Note: Tests focus on top-level comments and documentation. Comments embedded
 * within complex code structures may not be recognized by PSI and are covered
 * by the base test class in simpler contexts.
 */
class TextBlockExtractorJavaTest : TextBlockExtractorTestBase()
{
   override val fileExtension = "java"

   // ==============================================
   // Java-Specific Tests
   // ==============================================

   fun testJavaSpecific_JavaDocOnClass()
   {
      val psiFile = configureByText(
         """
         /**
          * Represents a configuration <caret>object.
          * @author John Doe
          * @version 1.0
          */
         public class Config {
             private String value;
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.startsWith("/**"))
      assertTrue(block.rawText.contains("@author"))
      assertTrue(block.rawText.contains("@version"))
   }

   fun testJavaSpecific_JavaDocWithMultipleParams()
   {
      val psiFile = configureByText(
         """
         /**
          * Complex method with multiple <caret>parameters.
          * @param first the first parameter
          * @param second the second parameter
          * @param third the third parameter
          * @return the result
          * @throws IllegalArgumentException if params are invalid
          */
         public String process(String first, int second, boolean third) {
             return first;
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.contains("@param first"))
      assertTrue(block.rawText.contains("@param second"))
      assertTrue(block.rawText.contains("@param third"))
      assertTrue(block.rawText.contains("@throws"))
   }
}
