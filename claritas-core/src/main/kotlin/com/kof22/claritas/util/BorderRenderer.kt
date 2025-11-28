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

import com.kof22.claritas.model.FlowerboxStyle

/**
 * Utility for rendering flowerbox comment borders and structure.
 */
object BorderRenderer
{
   /**
    * Render a complete flowerbox comment.
    *
    * @param contentLines The wrapped content lines (without markers)
    * @param style The flowerbox style configuration
    * @return Complete flowerbox comment text
    */
   fun render(
      contentLines: List<String>,
      style: FlowerboxStyle
   ): String
   {
      if (contentLines.isEmpty())
      {
         return ""
      }

      // Calculate actual width based on content and style
      val actualWidth = calculateWidth(contentLines, style)

      val result = StringBuilder()
      val indent = " ".repeat(style.indent)

      // Top border
      result.append(indent)
      result.append(renderTopBorder(actualWidth, style))
      result.append("\n")

      // Top blank lines
      repeat(style.addTopBlanks)
      {
         result.append(indent)
         result.append(renderBlankLine(actualWidth, style))
         result.append("\n")
      }

      // Content lines
      for (line in contentLines)
      {
         result.append(indent)
         result.append(renderContentLine(line, actualWidth, style))
         result.append("\n")
      }

      // Bottom blank lines
      repeat(style.addBottomBlanks)
      {
         result.append(indent)
         result.append(renderBlankLine(actualWidth, style))
         result.append("\n")
      }

      // Bottom border
      result.append(indent)
      result.append(renderBottomBorder(actualWidth, style))

      return result.toString()
   }

   /**
    * Calculate the actual width to use for the flowerbox.
    *
    * For fixed-width: uses the configured width
    * For dynamic-width: uses the longest line + decoration
    */
   private fun calculateWidth(
      contentLines: List<String>,
      style: FlowerboxStyle
   ): Int
   {
      // Fixed width takes precedence
      style.fixedWidth?.let {
         return it
      }

      // Dynamic width: find longest content line + decoration width
      val maxContentLength = contentLines.maxOfOrNull { it.length } ?: 0
      val decorationWidth = style.linePrefix.length + " */".length
      val calculatedWidth = maxContentLength + decorationWidth

      // Clamp to min/max
      return calculatedWidth.coerceIn(style.minWidth, style.maxWidth)
   }

   /**
    * Render the top border line.
    *
    * Examples:
    * - Javadoc style: slash-star-star followed by asterisks then star-slash
    * - Block style: slash-star followed by asterisks then star-slash
    */
   private fun renderTopBorder(
      width: Int,
      style: FlowerboxStyle
   ): String
   {
      val openMarker = if (style.useJavadocStyle) "/**" else "/*"
      val closeMarker = "*/"
      val availableWidth = width - openMarker.length - closeMarker.length

      return if (availableWidth > 0)
      {
         openMarker + style.borderChar.toString().repeat(availableWidth) + closeMarker
      }
      else
      {
         openMarker + closeMarker
      }
   }

   /**
    * Render the bottom border line.
    *
    * Example: slash-star followed by asterisks then star-slash
    */
   private fun renderBottomBorder(
      width: Int,
      style: FlowerboxStyle
   ): String
   {
      val openMarker = "/*"
      val closeMarker = "*/"
      val availableWidth = width - openMarker.length - closeMarker.length

      return if (availableWidth > 0)
      {
         openMarker + style.borderChar.toString().repeat(availableWidth) + closeMarker
      }
      else
      {
         openMarker + closeMarker
      }
   }

   /**
    * Render a blank content line.
    *
    * Example: prefix + spaces + star-slash
    */
   private fun renderBlankLine(
      width: Int,
      style: FlowerboxStyle
   ): String
   {
      val closeMarker = "*/"
      val availableWidth = width - style.linePrefix.length - closeMarker.length

      val padding = if (availableWidth > 0) " ".repeat(availableWidth) else ""

      return style.linePrefix + padding + closeMarker
   }

   /**
    * Render a content line with proper padding.
    *
    * Example: prefix + content text + padding + star-slash
    */
   private fun renderContentLine(
      content: String,
      width: Int,
      style: FlowerboxStyle
   ): String
   {
      val closeMarker = "*/"
      val availableWidth = width - style.linePrefix.length - closeMarker.length

      // Pad content to fill available width
      val paddedContent = if (content.length < availableWidth)
      {
         content + " ".repeat(availableWidth - content.length)
      }
      else
      {
         content.take(availableWidth)
      }

      return style.linePrefix + paddedContent + closeMarker
   }

   /**
    * Calculate the content width available for text wrapping.
    *
    * This is the width minus decoration (prefix + suffix).
    */
   fun calculateContentWidth(
      totalWidth: Int,
      style: FlowerboxStyle
   ): Int
   {
      val decorationWidth = style.linePrefix.length + " */".length
      return (totalWidth - decorationWidth).coerceAtLeast(10) // Minimum 10 chars for content
   }
}

