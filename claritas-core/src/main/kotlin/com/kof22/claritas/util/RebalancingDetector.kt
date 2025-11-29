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
 * Detects when text blocks need rebalancing.
 *
 * Rebalancing is needed when:
 * - Any line exceeds the maximum allowed length
 * - Content is unevenly distributed (very long and very short lines)
 */
object RebalancingDetector {
   /**
    * Check if text needs rebalancing based on line lengths.
    *
    * @param text The text to check (plain text, no comment markers)
    * @param maxLineLength Maximum allowed line length
    * @param rebalanceThreshold Lines exceeding this need rebalancing
    * @return true if rebalancing is needed
    */
   fun needsRebalancing(
      text: String,
      maxLineLength: Int,
      rebalanceThreshold: Int = maxLineLength
   ): Boolean {
      if (text.isEmpty()) return false

      val lines = text.lines().filter { it.isNotBlank() }
      if (lines.isEmpty()) return false

      ///////////////////////////////////////////////////////
      // Check if any line exceeds the rebalance threshold //
      ///////////////////////////////////////////////////////
      return lines.any { it.length > rebalanceThreshold }
   }

   /**
    * Analyze text and suggest if it needs reformatting.
    *
    * @return Analysis result with recommendation
    */
   fun analyzeText(
      text: String,
      maxLineLength: Int
   ): RebalanceAnalysis {
      val lines = text.lines().filter { it.isNotBlank() }

      if (lines.isEmpty()) {
         return RebalanceAnalysis(
            needsRebalancing = false,
            longestLine = 0,
            averageLineLength = 0.0,
            linesOverThreshold = 0
         )
      }

      val lengths = lines.map { it.length }
      val longestLine = lengths.maxOrNull() ?: 0
      val averageLength = lengths.average()
      val linesOverThreshold = lengths.count { it > maxLineLength }

      return RebalanceAnalysis(
         needsRebalancing = linesOverThreshold > 0,
         longestLine = longestLine,
         averageLineLength = averageLength,
         linesOverThreshold = linesOverThreshold
      )
   }

   /**
    * Result of text analysis.
    */
   data class RebalanceAnalysis(
      val needsRebalancing: Boolean,
      val longestLine: Int,
      val averageLineLength: Double,
      val linesOverThreshold: Int
   )
}


