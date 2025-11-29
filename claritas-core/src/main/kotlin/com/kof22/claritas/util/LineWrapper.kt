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

/**
 * Utility for wrapping text to a specified width while preserving word boundaries.
 */
object LineWrapper
{
   /**
    * Wrap text to fit within the specified width.
    *
    * Wraps at word boundaries when possible. Very long words that exceed
    * maxWidth will be placed on their own line.
    *
    * @param text Text to wrap (single paragraph, no internal line breaks)
    * @param maxWidth Maximum characters per line
    * @return List of wrapped lines
    */
   fun wrap(
      text: String,
      maxWidth: Int
   ): List<String>
   {
      if (text.isEmpty())
      {
         return emptyList()
      }

      if (maxWidth <= 0)
      {
         throw IllegalArgumentException("maxWidth must be positive")
      }

      val words = text.split(Regex("\\s+"))
      val lines = mutableListOf<String>()
      val currentLine = StringBuilder()

      for (word in words)
      {
         // If word alone exceeds maxWidth, put it on its own line
         if (word.length > maxWidth)
         {
            // Flush current line if not empty
            if (currentLine.isNotEmpty())
            {
               lines.add(currentLine.toString())
               currentLine.clear()
            }
            // Add long word as its own line
            lines.add(word)
            continue
         }

         // Check if adding this word would exceed maxWidth
         val testLength = if (currentLine.isEmpty())
         {
            word.length
         }
         else
         {
            currentLine.length + 1 + word.length // +1 for space
         }

         if (testLength <= maxWidth)
         {
            // Add word to current line
            if (currentLine.isNotEmpty())
            {
               currentLine.append(" ")
            }
            currentLine.append(word)
         }
         else
         {
            // Start new line with this word
            if (currentLine.isNotEmpty())
            {
               lines.add(currentLine.toString())
               currentLine.clear()
            }
            currentLine.append(word)
         }
      }

      // Add final line if exists
      if (currentLine.isNotEmpty())
      {
         lines.add(currentLine.toString())
      }

      return lines
   }

   /**
    * Wrap multiple paragraphs, preserving paragraph separation.
    *
    * @param paragraphs List of paragraph texts
    * @param maxWidth Maximum characters per line
    * @return List of wrapped lines with blank lines between paragraphs
    */
   fun wrapParagraphs(
      paragraphs: List<String>,
      maxWidth: Int
   ): List<String>
   {
      val allLines = mutableListOf<String>()

      for ((index, paragraph) in paragraphs.withIndex())
      {
         val wrappedLines = wrap(paragraph, maxWidth)
         allLines.addAll(wrappedLines)

         // Add blank line between paragraphs (but not after the last one)
         if (index < paragraphs.size - 1 && wrappedLines.isNotEmpty())
         {
            allLines.add("")
         }
      }

      return allLines
   }
}


