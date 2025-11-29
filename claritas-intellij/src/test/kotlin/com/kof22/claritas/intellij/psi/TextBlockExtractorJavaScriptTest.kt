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
 * TextBlockExtractor tests for JavaScript language.
 *
 * Inherits all test cases from TextBlockExtractorTestBase and validates
 * comment extraction for JavaScript files (.js).
 */
class TextBlockExtractorJavaScriptTest : TextBlockExtractorTestBase()
{
   override val fileExtension = "js"

   // ==============================================
   // JavaScript-Specific Tests
   // ==============================================

   fun testJavaScriptSpecific_JSDocOnFunction()
   {
      val psiFile = configureByText(
         """
         /**
          * Process a value and return <caret>the result.
          * @param {string} value - The input value
          * @returns {string} The processed value
          */
         function process(value) {
             return value;
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.startsWith("/**"))
      assertTrue(block.rawText.contains("@param"))
      assertTrue(block.rawText.contains("@returns"))
   }

   fun testJavaScriptSpecific_LineCommentInFunction()
   {
      val psiFile = configureByText(
         """
         function calculate(x, y) {
             // Calculate the <caret>sum
             const result = x + y;
             return result;
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// Calculate the sum"))
   }

   fun testJavaScriptSpecific_CommentInObjectLiteral()
   {
      val psiFile = configureByText(
         """
         const config = {
             // Server <caret>port number
             port: 3000,
             host: 'localhost'
         };
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// Server port number"))
   }

   fun testJavaScriptSpecific_JSDocOnClass()
   {
      val psiFile = configureByText(
         """
         /**
          * Represents a user in <caret>the system.
          * @class
          */
         class User {
             constructor(name) {
                 this.name = name;
             }
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.startsWith("/**"))
      assertTrue(block.rawText.contains("@class"))
   }
}
