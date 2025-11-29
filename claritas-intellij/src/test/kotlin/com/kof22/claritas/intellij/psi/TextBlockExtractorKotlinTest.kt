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
 * TextBlockExtractor tests for Kotlin language.
 *
 * Inherits all test cases from TextBlockExtractorTestBase and validates
 * comment extraction for Kotlin files (.kt).
 */
class TextBlockExtractorKotlinTest : TextBlockExtractorTestBase()
{
   override val fileExtension = "kt"

   // ==============================================
   // Kotlin-Specific Tests
   // ==============================================

   fun testKotlinSpecific_KDocOnFunction()
   {
      val psiFile = configureByText(
         """
         /**
          * Process a string value and return it.
          * <caret>
          * @param value the input string
          * @return the processed string
          */
         fun process(value: String): String {
             return value
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.startsWith("/**"))
      assertTrue(block.rawText.contains("@param"))
      assertTrue(block.rawText.contains("@return"))
   }

   fun testKotlinSpecific_LineCommentInClass()
   {
      val psiFile = configureByText(
         """
         package com.example
         
         class Person {
             // This is the <caret>name property
             var name: String = ""
             
             fun greet() {
                 println("Hello")
             }
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// This is the name property"))
   }

   fun testKotlinSpecific_CommentInDataClass()
   {
      val psiFile = configureByText(
         """
         data class User(
             // Unique <caret>identifier
             val id: Int,
             val name: String
         )
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// Unique identifier"))
   }

   fun testKotlinSpecific_KDocOnClass()
   {
      val psiFile = configureByText(
         """
         /**
          * Represents a person in the <caret>system.
          * @property name The person's full name
          */
         data class Person(val name: String)
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.startsWith("/**"))
      assertTrue(block.rawText.contains("@property"))
   }

   fun testKotlinSpecific_CommentInCompanionObject()
   {
      val psiFile = configureByText(
         """
         class Config {
             companion object {
                 // Default <caret>timeout value
                 const val TIMEOUT = 5000
             }
         }
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// Default timeout value"))
   }
}
