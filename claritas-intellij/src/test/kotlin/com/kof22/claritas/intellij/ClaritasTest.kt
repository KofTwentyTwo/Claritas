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

package com.kof22.claritas.intellij

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.service
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.kof22.claritas.intellij.services.ClaritasService

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class ClaritasTest : BasePlatformTestCase() {
   fun testXMLFile() {
      val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
      val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

      assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

      assertNotNull(xmlFile.rootTag)

      xmlFile.rootTag?.let {
         assertEquals("foo", it.name)
         assertEquals("bar", it.value.text)
      }
   }

   fun testRename() {
      myFixture.testRename("foo.xml", "foo_after.xml", "a2")
   }

   fun testProjectService() {
      val projectService = project.service<ClaritasService>()

      // Simple test that the service can be instantiated
      assertNotNull(projectService)
   }

   override fun getTestDataPath() = "src/test/testData/rename"
}
