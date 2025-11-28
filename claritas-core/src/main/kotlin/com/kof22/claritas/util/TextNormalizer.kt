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
 * Utility for normalizing comment text by stripping markers and cleaning whitespace.
 */
object TextNormalizer
{
   /**
    * Strip all comment markers from text.
    *
    * Removes:
    * - Line comment markers (double-slash)
    * - Block comment markers (slash-star and star-slash)
    * - Javadoc markers (slash-star-star and star-slash)
    * - Leading asterisks from intermediate lines
    * - Excessive whitespace
    *
    * @param rawText The raw comment text with markers
    * @param preserveParagraphs If true, preserve blank lines as paragraph breaks
    * @return Normalized plain text content
    */
   fun normalize(
      rawText: String,
      preserveParagraphs: Boolean = true
   ): String
   {
      val lines = rawText.lines()
      val normalized = mutableListOf<String>()

      for (line in lines)
      {
         val trimmed = line.trim()

         // Skip comment delimiters
         if (trimmed in setOf("/*", "/**", "*/", "*"))
         {
            continue
         }

         // Remove leading comment markers
         val cleaned = trimmed
            .removePrefix("//")
            .removePrefix("/**")
            .removePrefix("/*")
            .removeSuffix("*/")
            .trim()
            .let {
               // Remove leading asterisk if present
               if (it.startsWith("* "))
               {
                  it.substring(2)
               }
               else if (it.startsWith("*"))
               {
                  it.substring(1).trim()
               }
               else
               {
                  it
               }
            }

         // Handle blank lines based on preserveParagraphs setting
         if (cleaned.isEmpty())
         {
            if (preserveParagraphs && normalized.isNotEmpty() && normalized.last().isNotEmpty())
            {
               normalized.add("")
            }
         }
         else
         {
            normalized.add(cleaned)
         }
      }

      // Remove trailing blank lines
      while (normalized.isNotEmpty() && normalized.last().isEmpty())
      {
         normalized.removeLast()
      }

      return normalized.joinToString("\n")
   }

   /**
    * Split normalized text into paragraphs (groups separated by blank lines).
    *
    * @param normalizedText Text that has already been normalized
    * @return List of paragraphs, each containing one or more lines
    */
   fun splitIntoParagraphs(normalizedText: String): List<String>
   {
      if (normalizedText.isEmpty())
      {
         return emptyList()
      }

      val paragraphs = mutableListOf<String>()
      val currentParagraph = mutableListOf<String>()

      for (line in normalizedText.lines())
      {
         if (line.isEmpty())
         {
            if (currentParagraph.isNotEmpty())
            {
               paragraphs.add(currentParagraph.joinToString(" "))
               currentParagraph.clear()
            }
         }
         else
         {
            currentParagraph.add(line)
         }
      }

      // Add final paragraph if exists
      if (currentParagraph.isNotEmpty())
      {
         paragraphs.add(currentParagraph.joinToString(" "))
      }

      return paragraphs
   }
}

