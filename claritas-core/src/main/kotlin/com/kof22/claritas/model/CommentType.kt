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

package com.kof22.claritas.model

/**
 * Types of comments that Claritas can process.
 *
 * Claritas uses two fundamental comment types:
 * - DOCUMENTATION: Special documentation comments (JavaDoc, JSDoc, KDoc) that use slash-star-star syntax
 * - STANDARD: All other comments (line comments, block comments, or raw text)
 */
enum class CommentType
{
   /**
    * Documentation comment using JavaDoc/JSDoc/KDoc syntax.
    *
    * These comments start with slash-star-star and are formatted with special 
    * JavaDoc-style borders. They are typically used for API documentation.
    *
    * Recognized in Java, JavaScript, Kotlin, and other languages.
    */
   DOCUMENTATION,

   /**
    * Standard comment using line or block syntax, or raw text.
    *
    * This includes:
    * - Line comments (starting with double-slash)
    * - Block comments (starting with slash-star)
    * - Selected raw text without comment markers
    *
    * These are formatted with standard flowerbox borders according to user preferences.
    */
   STANDARD
}
