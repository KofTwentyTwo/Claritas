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

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.kof22.claritas.model.CommentType

/**
 * Abstract base class for TextBlockExtractor tests.
 *
 * Defines the complete test suite that must be implemented for each language.
 * Subclasses provide the file extension and language-specific comment syntax.
 *
 * This ensures consistent testing across all supported languages (Java, JavaScript, Kotlin, etc.).
 */
abstract class TextBlockExtractorTestBase : BasePlatformTestCase()
{
   /**
    * The file extension for this language (e.g., "java", "js", "kt").
    */
   protected abstract val fileExtension: String

   /**
    * Helper to create a test file with the given content.
    */
   protected fun configureByText(content: String) =
      myFixture.configureByText("Test.$fileExtension", content)

   // ==============================================
   // Selection Tests
   // ==============================================

   fun testExtractSelectedBlock_SingleLine()
   {
      val psiFile = configureByText(
         """
         <selection>This is a line</selection>
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals("This is a line", block.rawText)
      assertEquals(CommentType.STANDARD, block.type)
      assertEquals(0, block.indentLevel)
      assertTrue(block.preserveParagraphs)
   }

   fun testExtractSelectedBlock_MultiLine_BecomesBlock()
   {
      val psiFile = configureByText(
         """
         <selection>Line one
         Line two
         Line three</selection>
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("Line one"))
      assertTrue(block.rawText.contains("Line two"))
      assertTrue(block.rawText.contains("Line three"))
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractSelectedBlock_WithSpaceIndent()
   {
      val psiFile = configureByText("    <selection>Indented text</selection>")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(4, block.indentLevel)
      assertTrue(block.rawText.contains("Indented text"))
   }

   fun testExtractSelectedBlock_WithTabIndent()
   {
      val psiFile = configureByText("\t\t<selection>Tab indented text</selection>")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(2, block.indentLevel)
   }

   fun testExtractSelectedBlock_EmptySelection_FallsBack()
   {
      val psiFile = configureByText(
         """
         First line
         Second<caret> line
         Third line
         """.trimIndent()
      )

      // Simulate empty selection by ensuring no selection exists
      myFixture.editor.selectionModel.removeSelection()

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      // Should extract the block around current line
      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Second line"))
      assertTrue(block.rawText.contains("Third line"))
   }

   // ==============================================
   // Line Comment Tests
   // ==============================================

   fun testExtractCommentBlock_LineComment()
   {
      val psiFile = configureByText(
         """
         // This is <caret>a comment
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals("// This is a comment", block.rawText)
      assertEquals(CommentType.STANDARD, block.type)
      assertEquals(0, block.indentLevel)
   }

   fun testExtractCommentBlock_LineComment_CaretAtStart()
   {
      val psiFile = configureByText(
         """
         <caret>// Comment at start
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals("// Comment at start", block.rawText)
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractCommentBlock_LineComment_CaretAtEnd()
   {
      val psiFile = configureByText(
         """
         // Comment at end<caret>
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals("// Comment at end", block.rawText)
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractCommentBlock_LineComment_Indented()
   {
      val psiFile = configureByText("    // Indented <caret>comment")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(4, block.indentLevel)
      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("// Indented comment"))
   }

   // ==============================================
   // Block Comment Tests
   // ==============================================

   fun testExtractCommentBlock_BlockComment()
   {
      val psiFile = configureByText(
         """
         /* This is
         a <caret>multi-line
         comment */
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.startsWith("/*"))
      assertTrue(block.rawText.endsWith("*/"))
      assertEquals(CommentType.STANDARD, block.type)
      assertTrue(block.rawText.contains("multi-line"))
   }

   fun testExtractCommentBlock_BlockComment_SingleLine()
   {
      val psiFile = configureByText(
         """
         /* Single line <caret>block comment */
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.startsWith("/*"))
      assertTrue(block.rawText.endsWith("*/"))
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractCommentBlock_BlockComment_Indented()
   {
      val psiFile = configureByText(
         "    /* Indented\n" +
         "    block <caret>comment\n" +
         "    */"
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(4, block.indentLevel)
      assertEquals(CommentType.STANDARD, block.type)
   }

   // ==============================================
   // JavaDoc/JSDoc/KDoc Comment Tests
   // ==============================================

   fun testExtractCommentBlock_DocComment()
   {
      val psiFile = configureByText(
         """
         /** Doc <caret>comment
          * with multiple lines
          */
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.startsWith("/**"))
      assertEquals(CommentType.DOCUMENTATION, block.type)
   }

   fun testExtractCommentBlock_DocComment_WithAnnotations()
   {
      val psiFile = configureByText(
         """
         /**
          * Doc comment <caret>with annotations
          * @param foo description
          * @return value
          */
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.startsWith("/**"))
      assertEquals(CommentType.DOCUMENTATION, block.type)
      assertTrue(block.rawText.contains("@param"))
   }

   // ==============================================
   // Raw Text Block Tests
   // ==============================================

   fun testExtractBlockFromCurrentLine_SingleLine()
   {
      val psiFile = configureByText(
         """
         
         Single<caret> line of text
         
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals("Single line of text", block.rawText)
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractBlockFromCurrentLine_ExpandsMultiLine()
   {
      val psiFile = configureByText(
         """
         First line
         Second<caret> line
         Third line
         
         Different block
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Second line"))
      assertTrue(block.rawText.contains("Third line"))
      assertFalse(block.rawText.contains("Different block"))
      assertEquals(CommentType.STANDARD, block.type)
   }

   fun testExtractBlockFromCurrentLine_CaretAtFirstLine()
   {
      val psiFile = configureByText(
         """
         First<caret> line
         Second line
         Third line
         
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Second line"))
      assertTrue(block.rawText.contains("Third line"))
   }

   fun testExtractBlockFromCurrentLine_CaretAtLastLine()
   {
      val psiFile = configureByText(
         """
         
         First line
         Second line
         Third<caret> line
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Second line"))
      assertTrue(block.rawText.contains("Third line"))
   }

   fun testExtractBlockFromCurrentLine_AtStartOfDocument()
   {
      val psiFile = configureByText(
         """
         <caret>First line
         Second line
         
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Second line"))
      assertEquals(0, range.startOffset)
   }

   fun testExtractBlockFromCurrentLine_AtEndOfDocument()
   {
      val psiFile = configureByText(
         """
         
         First line
         Last line<caret>
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("First line"))
      assertTrue(block.rawText.contains("Last line"))
   }

   fun testExtractBlockFromCurrentLine_StopsAtBlankLines()
   {
      val psiFile = configureByText(
         """
         Block 1 line 1
         Block 1 line 2
         
         Block 2 <caret>line 1
         Block 2 line 2
         
         Block 3 line 1
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("Block 2 line 1"))
      assertTrue(block.rawText.contains("Block 2 line 2"))
      assertFalse(block.rawText.contains("Block 1"))
      assertFalse(block.rawText.contains("Block 3"))
   }

   // ==============================================
   // Indentation Tests
   // ==============================================

   fun testIndentation_SpacesOnly()
   {
      val psiFile = configureByText("    <caret>Indented with 4 spaces")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(4, block.indentLevel)
   }

   fun testIndentation_TabsOnly()
   {
      val psiFile = configureByText("\t\t\t<caret>Indented with 3 tabs")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(3, block.indentLevel)
   }

   fun testIndentation_MixedTabsAndSpaces()
   {
      val psiFile = configureByText("\t  <caret>Mixed indent (1 tab + 2 spaces)")

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(3, block.indentLevel) // 1 tab + 2 spaces = 3 characters
   }

   fun testIndentation_NoIndent()
   {
      val psiFile = configureByText(
         """
         <caret>No indent
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertEquals(0, block.indentLevel)
   }

   // ==============================================
   // Edge Cases
   // ==============================================

   fun testEdgeCase_EmptyDocument()
   {
      val psiFile = configureByText("")

      // Should not throw, but will extract empty or minimal content
      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      // Just verify it doesn't crash
      assertNotNull(block)
      assertNotNull(range)
   }

   fun testEdgeCase_OnlyWhitespace()
   {
      val psiFile = configureByText(
         """
         
         
         <caret>
         
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      // Should extract empty line or minimal content
      assertNotNull(block)
   }

   fun testEdgeCase_MultipleConsecutiveBlankLines()
   {
      val psiFile = configureByText(
         """
         Block 1
         
         
         
         Block<caret> 2
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      assertTrue(block.rawText.contains("Block 2"))
      assertFalse(block.rawText.contains("Block 1"))
   }

   // ==============================================
   // TextRange Validation Tests
   // ==============================================

   fun testTextRange_MatchesActualContent()
   {
      val psiFile = configureByText(
         """
         // Comment <caret>line
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      // Get actual text from document using the returned range
      val actualText = myFixture.editor.document.getText(range)

      assertEquals(block.rawText, actualText)
   }

   fun testTextRange_MultiLineComment()
   {
      val psiFile = configureByText(
         """
         /* Multi
         line <caret>comment
         here */
         """.trimIndent()
      )

      val (block, range) = TextBlockExtractor.extractBlockAtCaret(myFixture.editor, psiFile)

      val actualText = myFixture.editor.document.getText(range)

      assertEquals(block.rawText, actualText)
      assertTrue(actualText.startsWith("/*"))
      assertTrue(actualText.endsWith("*/"))
   }
}

