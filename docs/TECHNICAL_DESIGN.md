# Claritas Technical Design Document

## 1. Overview

This document translates the high-level requirements from `INITIAL_DESIGN_REQUIREMENTS.md` into concrete technical specifications, class structures, and implementation guidance.

**Target:** IntelliJ IDEA Plugin for Java comment and Javadoc formatting
**Language:** Kotlin (plugin implementation), Java (target files)
**Approach:** PSI-based parsing and transformation

---

## 2. Design Decisions Summary

### 2.1 Flowerbox Formatting Rules

**Class/Method Javadocs (Fixed Width):**
```java
/*******************************************************************************
 ** Summary sentence here.
 **
 ** Additional description paragraph that wraps at configured width (default 80).
 ** Multiple paragraphs are preserved with blank lines between them.
 **
 ** @param customerId the customer identifier
 ** @return the customer record
 *******************************************************************************/
```

**Inline Comments (Dynamic Width):**
```java
/////////////////////////////////////////////////////////////////////////
// Width determined by longest line in the original comment.         //
// If any line exceeds threshold (default 70), reflow all lines.     //
// Otherwise just add borders to existing line lengths.              //
/////////////////////////////////////////////////////////////////////////
```

**Key Rules:**
- **Javadoc width:** Fixed at configured value (default 80 chars)
- **Inline comment width:** Dynamic - longest line OR max configured width if rebalancing triggered
- **Rebalance trigger:** Any line > configured threshold (default 70) causes full reflow
- **Whitespace:** Normalize to single spaces between words, preserve paragraph breaks
- **Content preservation:** Never generate or infer text - only restructure existing content

### 2.2 Configuration Defaults

| Setting | Default | Rationale |
|---------|---------|-----------|
| Javadoc max width | 80 | QQQ standard |
| Inline comment rebalance threshold | 70 | Reasonable line length |
| Top border char | `*` | QQQ flower box style |
| Inner line prefix (Javadoc) | ` **` | QQQ standard |
| Inner line prefix (inline) | `//` | Standard Java comment |
| Preserve existing summaries | `true` | Never lose user content |
| Generate missing @param descriptions | `false` | Leave blank, require user input |

---

## 3. Phase 0 - Simple POC

**Goal:** Prove the plugin mechanics work with minimal functionality.

### 3.1 POC Scope

**What it does:**
- Single menu action: "Claritas → Format Current Line"
- When invoked, takes the current line and replaces it with: `// CLARITAS: [original text]`
- Basic settings panel with one checkbox: "Enable Claritas"

**What it proves:**
- Plugin loads and registers correctly
- Menu actions work
- Can read editor context and cursor position
- Can modify document via write action
- Settings persistence works
- Undo/redo functions

### 3.2 POC Components

```
src/main/kotlin/com/kof22/claritas/
├── actions/
│   └── FormatCurrentLineAction.kt       # POC action
├── settings/
│   ├── ClaritasSettings.kt              # State component (1 boolean field)
│   └── ClaritasSettingsConfigurable.kt  # Settings UI (1 checkbox)
└── ClaritasBundle.kt                    # Already exists
```

### 3.3 POC Implementation Details

**FormatCurrentLineAction.kt:**
```kotlin
class FormatCurrentLineAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      val editor = e.getData(CommonDataKeys.EDITOR) ?: return
      val project = e.project ?: return
      val document = editor.document
      val caretModel = editor.caretModel
      val lineNumber = document.getLineNumber(caretModel.offset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)
      val lineEndOffset = document.getLineEndOffset(lineNumber)
      val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
      
      WriteCommandAction.runWriteCommandAction(project) {
         document.replaceString(
            lineStartOffset,
            lineEndOffset,
            "// CLARITAS: $lineText"
         )
      }
   }
}
```

**ClaritasSettings.kt:**
```kotlin
@State(
   name = "ClaritasSettings",
   storages = [Storage("ClaritasSettings.xml")]
)
@Service(Service.Level.APP)
class ClaritasSettings : PersistentStateComponent<ClaritasSettings.State> {
   data class State(
      var enableClaritas: Boolean = true
   )
   
   private var myState = State()
   
   override fun getState() = myState
   override fun loadState(state: State) {
      myState = state
   }
   
   companion object {
      fun getInstance(): ClaritasSettings = service()
   }
}
```

**plugin.xml additions:**
```xml
<actions>
   <group id="Claritas.Menu" text="Claritas" popup="true">
      <add-to-group group-id="EditorPopupMenu" anchor="last"/>
      <action id="Claritas.FormatCurrentLine"
              class="com.kof22.claritas.actions.FormatCurrentLineAction"
              text="Format Current Line"
              description="POC action to test plugin mechanics"/>
   </group>
</actions>

<extensions defaultExtensionNs="com.intellij">
   <applicationConfigurable 
      instance="com.kof22.claritas.settings.ClaritasSettingsConfigurable"
      id="claritas.settings"
      displayName="Claritas"/>
</extensions>
```

**Success Criteria for POC:**
- [ ] Menu action appears in Editor context menu
- [ ] Action modifies current line as expected
- [ ] Settings page appears under Preferences
- [ ] Checkbox state persists across IDE restarts
- [ ] Undo/redo works correctly

---

## 4. Core Architecture (Post-POC)

### 4.1 Package Structure

```
com.kof22.claritas/
├── actions/                    # User-invoked actions
│   ├── ReformatCommentAction.kt
│   ├── GenerateJavadocAction.kt
│   └── ProcessFileAction.kt
├── services/                   # Business logic services
│   ├── CommentProcessorService.kt
│   ├── JavadocProcessorService.kt
│   ├── FileProcessorService.kt
│   └── FormattingService.kt
├── model/                      # Data models
│   ├── CommentBlock.kt
│   ├── JavadocModel.kt
│   ├── FlowerboxStyle.kt
│   └── ProcessingResult.kt
├── psi/                        # PSI utilities
│   ├── CommentExtractor.kt
│   ├── JavadocExtractor.kt
│   └── PsiModifier.kt
├── settings/                   # Configuration
│   ├── ClaritasSettings.kt
│   ├── ClaritasSettingsConfigurable.kt
│   └── ui/
│       └── ClaritasSettingsPanel.kt
├── util/                       # Utilities
│   ├── TextWrapper.kt
│   ├── IndentCalculator.kt
│   └── BorderRenderer.kt
└── ClaritasBundle.kt           # Existing i18n
```

### 4.2 Core Data Models

**CommentBlock.kt:**
```kotlin
data class CommentBlock(
   val originalText: String,
   val normalizedText: String,
   val paragraphs: List<String>,
   val indentLevel: Int,
   val type: CommentType,
   val psiElement: PsiElement
)

enum class CommentType {
   LINE_COMMENT,          // Single // line
   BLOCK_COMMENT,         // /* ... */
   JAVADOC,               // /** ... */
   INLINE_BLOCK           // Multiple // lines treated as block
}
```

**JavadocModel.kt:**
```kotlin
data class JavadocModel(
   val summary: String?,
   val description: List<String>,
   val params: List<ParamTag>,
   val typeParams: List<TypeParamTag>,
   val returnTag: ReturnTag?,
   val throwsTags: List<ThrowsTag>,
   val customTags: List<CustomTag>,
   val deprecatedTag: DeprecatedTag?
)

data class ParamTag(
   val name: String,
   val description: String?,
   val fromSignature: Boolean = true
)

data class ReturnTag(
   val description: String?,
   val returnType: PsiType
)

data class ThrowsTag(
   val exceptionType: String,
   val description: String?
)

data class CustomTag(
   val name: String,
   val content: String
)
```

**FlowerboxStyle.kt:**
```kotlin
data class FlowerboxStyle(
   val topBorderChar: Char = '*',
   val bottomBorderChar: Char = '*',
   val innerLinePrefix: String = " **",
   val innerLineSuffix: String = "",
   val maxWidth: Int = 80,
   val useJavadocStyle: Boolean = true,  // /** vs /*
   val blankLinesTop: Int = 0,
   val blankLinesBottom: Int = 0,
   val alignInnerContent: Boolean = false
)

data class InlineCommentStyle(
   val borderChar: Char = '/',
   val innerLinePrefix: String = "//",
   val innerLineSuffix: String = "//",
   val dynamicWidth: Boolean = true,
   val rebalanceThreshold: Int = 70,
   val paddingChars: Int = 1
)
```

**ProcessingResult.kt:**
```kotlin
data class ProcessingResult(
   val success: Boolean,
   val commentsProcessed: Int = 0,
   val javadocsProcessed: Int = 0,
   val warnings: List<ProcessingWarning> = emptyList(),
   val errors: List<ProcessingError> = emptyList()
)

data class ProcessingWarning(
   val element: PsiElement,
   val message: String,
   val severity: WarningSeverity
)

enum class WarningSeverity {
   INFO,
   WARNING,
   ERROR
}
```

---

## 5. Service Implementations

### 5.1 CommentProcessorService

**Responsibility:** Transform any comment into flowerbox style.

**Interface:**
```kotlin
@Service(Service.Level.PROJECT)
class CommentProcessorService(private val project: Project) {
   
   fun processComment(
      comment: PsiComment,
      style: FlowerboxStyle
   ): String {
      // Implementation
   }
   
   fun processInlineComment(
      comments: List<PsiComment>,
      style: InlineCommentStyle
   ): String {
      // Implementation
   }
   
   private fun normalizeText(rawText: String): String
   private fun extractParagraphs(text: String): List<String>
   private fun wrapText(text: String, maxWidth: Int): List<String>
   private fun buildFlowerbox(lines: List<String>, style: FlowerboxStyle): String
}
```

**Key Algorithms:**

**Text Normalization:**
1. Strip comment markers (`//`, `/*`, `/**`, `*/`, `*`)
2. Trim leading/trailing whitespace per line
3. Collapse multiple consecutive spaces to single space
4. Identify paragraph breaks (blank lines)
5. Return plain text paragraphs

**Line Wrapping:**
```kotlin
fun wrapText(text: String, maxWidth: Int): List<String> {
   val words = text.split(Regex("\\s+"))
   val lines = mutableListOf<String>()
   var currentLine = StringBuilder()
   
   for (word in words) {
      if (currentLine.length + word.length + 1 > maxWidth) {
         if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
            currentLine = StringBuilder()
         }
         // Handle words longer than maxWidth
         if (word.length > maxWidth) {
            lines.add(word) // Allow overflow for unbreakable tokens
         } else {
            currentLine.append(word)
         }
      } else {
         if (currentLine.isNotEmpty()) currentLine.append(" ")
         currentLine.append(word)
      }
   }
   if (currentLine.isNotEmpty()) {
      lines.add(currentLine.toString())
   }
   return lines
}
```

**Flowerbox Construction:**
```kotlin
fun buildFlowerbox(
   lines: List<String>,
   style: FlowerboxStyle,
   indentLevel: Int
): String {
   val indent = " ".repeat(indentLevel)
   val opening = if (style.useJavadocStyle) "/**" else "/*"
   val topBorder = opening + style.topBorderChar.toString().repeat(style.maxWidth - 2 - indent.length)
   val bottomBorder = " " + style.bottomBorderChar.toString().repeat(style.maxWidth - 3 - indent.length) + "*/"
   
   val result = StringBuilder()
   result.appendLine(indent + topBorder)
   
   // Blank lines at top
   repeat(style.blankLinesTop) {
      result.appendLine("$indent${style.innerLinePrefix}")
   }
   
   // Content lines
   for (line in lines) {
      result.appendLine("$indent${style.innerLinePrefix} $line")
   }
   
   // Blank lines at bottom
   repeat(style.blankLinesBottom) {
      result.appendLine("$indent${style.innerLinePrefix}")
   }
   
   result.append(indent + bottomBorder)
   return result.toString()
}
```

**Inline Comment Logic:**
```kotlin
fun processInlineComment(
   comments: List<PsiComment>,
   style: InlineCommentStyle
): String {
   val lines = comments.map { normalizeText(it.text) }
   val maxLineLength = lines.maxOfOrNull { it.length } ?: 0
   
   val needsRebalance = lines.any { it.length > style.rebalanceThreshold }
   
   val processedLines = if (needsRebalance) {
      // Reflow all text to fit threshold
      val allText = lines.joinToString(" ")
      wrapText(allText, style.rebalanceThreshold - style.innerLinePrefix.length - 4)
   } else {
      lines
   }
   
   val boxWidth = if (style.dynamicWidth) {
      processedLines.maxOfOrNull { it.length }!! + style.innerLinePrefix.length + style.innerLineSuffix.length + 4
   } else {
      style.rebalanceThreshold
   }
   
   return buildInlineBox(processedLines, style, boxWidth, indentLevel)
}
```

### 5.2 JavadocProcessorService

**Responsibility:** Generate or normalize Javadoc for classes/methods.

**Interface:**
```kotlin
@Service(Service.Level.PROJECT)
class JavadocProcessorService(private val project: Project) {
   
   fun generateJavadoc(
      element: PsiElement,  // PsiMethod or PsiClass
      existingDoc: PsiDocComment?,
      style: FlowerboxStyle
   ): String {
      // Implementation
   }
   
   private fun extractExistingJavadoc(doc: PsiDocComment): JavadocModel
   private fun buildJavadocFromSignature(element: PsiElement): JavadocModel
   private fun mergeJavadocModels(existing: JavadocModel, signature: JavadocModel): JavadocModel
   private fun renderJavadoc(model: JavadocModel, style: FlowerboxStyle): String
}
```

**Key Algorithms:**

**Extract Existing Javadoc:**
```kotlin
private fun extractExistingJavadoc(doc: PsiDocComment): JavadocModel {
   val summary = doc.descriptionElements
      .firstOrNull()
      ?.text
      ?.trim()
      ?.takeWhile { it != '\n' }
   
   val params = doc.findTagsByName("param").map { tag ->
      ParamTag(
         name = tag.valueElement?.text ?: "",
         description = tag.dataElements.joinToString(" ") { it.text.trim() },
         fromSignature = false
      )
   }
   
   val returnTag = doc.findTagByName("return")?.let {
      ReturnTag(
         description = it.dataElements.joinToString(" ") { elem -> elem.text.trim() },
         returnType = null  // Will be filled from signature
      )
   }
   
   // ... extract throws, custom tags, etc.
   
   return JavadocModel(summary, description, params, typeParams, returnTag, throwsTags, customTags, deprecatedTag)
}
```

**Build from Signature:**
```kotlin
private fun buildJavadocFromSignature(method: PsiMethod): JavadocModel {
   val params = method.parameterList.parameters.map { param ->
      ParamTag(
         name = param.name ?: "",
         description = null,  // Will be filled from existing or left blank
         fromSignature = true
      )
   }
   
   val returnTag = if (!method.returnType?.equals(PsiType.VOID) == true) {
      ReturnTag(null, method.returnType!!)
   } else null
   
   val throwsTags = method.throwsList.referencedTypes.map { type ->
      ThrowsTag(
         exceptionType = type.presentableText,
         description = null
      )
   }
   
   return JavadocModel(null, emptyList(), params, emptyList(), returnTag, throwsTags, emptyList(), null)
}
```

**Render Javadoc:**
```kotlin
private fun renderJavadoc(
   model: JavadocModel,
   style: FlowerboxStyle,
   indentLevel: Int
): String {
   val lines = mutableListOf<String>()
   
   // Summary (always first, single line preferred)
   model.summary?.let { lines.add(it) }
   
   // Description paragraphs
   if (model.description.isNotEmpty()) {
      if (lines.isNotEmpty()) lines.add("")  // Blank line after summary
      lines.addAll(model.description)
   }
   
   // Parameters
   if (model.params.isNotEmpty()) {
      if (lines.isNotEmpty()) lines.add("")
      val maxParamLength = model.params.maxOf { it.name.length }
      for (param in model.params) {
         val desc = param.description ?: ""
         lines.add("@param ${param.name.padEnd(maxParamLength)} $desc".trim())
      }
   }
   
   // Return
   model.returnTag?.let {
      if (lines.isNotEmpty()) lines.add("")
      lines.add("@return ${it.description ?: ""}".trim())
   }
   
   // Throws
   if (model.throwsTags.isNotEmpty()) {
      if (lines.isNotEmpty()) lines.add("")
      for (throwsTag in model.throwsTags) {
         lines.add("@throws ${throwsTag.exceptionType} ${throwsTag.description ?: ""}".trim())
      }
   }
   
   // Build flowerbox around these lines
   return buildFlowerbox(lines, style, indentLevel)
}
```

### 5.3 FileProcessorService

**Responsibility:** Process entire file (all comments and Javadocs).

**Interface:**
```kotlin
@Service(Service.Level.PROJECT)
class FileProcessorService(
   private val project: Project,
   private val commentProcessor: CommentProcessorService,
   private val javadocProcessor: JavadocProcessorService
) {
   
   fun processFile(
      psiFile: PsiFile,
      options: FileProcessingOptions
   ): ProcessingResult {
      // Implementation
   }
   
   private fun collectElements(file: PsiFile, options: FileProcessingOptions): ProcessingElements
   private fun processElements(elements: ProcessingElements): ProcessingResult
}

data class FileProcessingOptions(
   val processComments: Boolean = true,
   val processJavadocs: Boolean = true,
   val skipCleanBlocks: Boolean = false,
   val showSummary: Boolean = true
)

data class ProcessingElements(
   val comments: List<PsiComment>,
   val javadocTargets: List<Pair<PsiElement, PsiDocComment?>>
)
```

**Implementation:**
```kotlin
fun processFile(psiFile: PsiFile, options: FileProcessingOptions): ProcessingResult {
   val elements = collectElements(psiFile, options)
   
   var commentsProcessed = 0
   var javadocsProcessed = 0
   val warnings = mutableListOf<ProcessingWarning>()
   
   WriteCommandAction.runWriteCommandAction(project) {
      // Process comments
      if (options.processComments) {
         for (comment in elements.comments) {
            try {
               val formatted = commentProcessor.processComment(comment, getCommentStyle())
               val newComment = PsiElementFactory.getInstance(project)
                  .createCommentFromText(formatted, null)
               comment.replace(newComment)
               commentsProcessed++
            } catch (e: Exception) {
               warnings.add(ProcessingWarning(comment, e.message ?: "Unknown error", WarningSeverity.ERROR))
            }
         }
      }
      
      // Process Javadocs
      if (options.processJavadocs) {
         for ((element, existingDoc) in elements.javadocTargets) {
            try {
               val formatted = javadocProcessor.generateJavadoc(element, existingDoc, getJavadocStyle())
               // Replace or insert Javadoc
               javadocsProcessed++
            } catch (e: Exception) {
               warnings.add(ProcessingWarning(element, e.message ?: "Unknown error", WarningSeverity.ERROR))
            }
         }
      }
   }
   
   return ProcessingResult(
      success = warnings.none { it.severity == WarningSeverity.ERROR },
      commentsProcessed = commentsProcessed,
      javadocsProcessed = javadocsProcessed,
      warnings = warnings
   )
}
```

---

## 6. Settings Implementation

### 6.1 ClaritasSettings (Post-POC)

```kotlin
@State(
   name = "ClaritasSettings",
   storages = [Storage("ClaritasSettings.xml")]
)
@Service(Service.Level.APP)
class ClaritasSettings : PersistentStateComponent<ClaritasSettings.State> {
   
   data class State(
      // Flowerbox settings
      var javadocMaxWidth: Int = 80,
      var javadocTopBorderChar: Char = '*',
      var javadocInnerPrefix: String = " **",
      var javadocUseJavadocStyle: Boolean = true,
      
      // Inline comment settings
      var inlineCommentRebalanceThreshold: Int = 70,
      var inlineCommentDynamicWidth: Boolean = true,
      var inlineCommentBorderChar: Char = '/',
      
      // Javadoc behavior
      var preserveExistingSummary: Boolean = true,
      var preserveExistingDescriptions: Boolean = true,
      var generateMissingParamDescriptions: Boolean = false,
      var preserveUnknownTags: Boolean = true,
      var alignParamNames: Boolean = true,
      
      // File processing
      var processCommentsInFile: Boolean = true,
      var processJavadocsInFile: Boolean = true,
      var skipCleanBlocks: Boolean = false,
      var showProcessingSummary: Boolean = true
   )
   
   private var myState = State()
   
   override fun getState() = myState
   override fun loadState(state: State) {
      myState = state
   }
   
   companion object {
      fun getInstance(): ClaritasSettings = service()
   }
   
   // Convenience methods
   fun getFlowerboxStyle() = FlowerboxStyle(
      topBorderChar = myState.javadocTopBorderChar,
      bottomBorderChar = myState.javadocTopBorderChar,
      innerLinePrefix = myState.javadocInnerPrefix,
      maxWidth = myState.javadocMaxWidth,
      useJavadocStyle = myState.javadocUseJavadocStyle
   )
   
   fun getInlineCommentStyle() = InlineCommentStyle(
      borderChar = myState.inlineCommentBorderChar,
      rebalanceThreshold = myState.inlineCommentRebalanceThreshold,
      dynamicWidth = myState.inlineCommentDynamicWidth
   )
}
```

### 6.2 Settings UI

**ClaritasSettingsPanel.kt:**
```kotlin
class ClaritasSettingsPanel : JPanel() {
   private val tabbedPane = JBTabbedPane()
   
   // Flowerbox tab
   private val javadocWidthField = JBIntSpinner(80, 60, 120)
   private val javadocBorderCharField = JBTextField("*", 1)
   private val javadocPrefixField = JBTextField(" **")
   private val previewPanel = JBTextArea()
   
   // Inline comments tab
   private val rebalanceThresholdField = JBIntSpinner(70, 40, 120)
   private val dynamicWidthCheckbox = JBCheckBox("Use dynamic width")
   
   // Javadoc behavior tab
   private val preserveSummaryCheckbox = JBCheckBox("Preserve existing summary")
   private val generateParamDescCheckbox = JBCheckBox("Generate parameter descriptions")
   
   init {
      layout = BorderLayout()
      
      tabbedPane.addTab("Flowerbox Style", createFlowerboxPanel())
      tabbedPane.addTab("Inline Comments", createInlineCommentsPanel())
      tabbedPane.addTab("Javadoc Behavior", createJavadocBehaviorPanel())
      tabbedPane.addTab("File Processing", createFileProcessingPanel())
      
      add(tabbedPane, BorderLayout.CENTER)
      add(createPreviewPanel(), BorderLayout.SOUTH)
   }
   
   private fun createPreviewPanel(): JPanel {
      val panel = JPanel(BorderLayout())
      panel.border = IdeBorderFactory.createTitledBorder("Preview")
      
      previewPanel.isEditable = false
      previewPanel.font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
      
      panel.add(JBScrollPane(previewPanel), BorderLayout.CENTER)
      
      // Update preview when settings change
      javadocWidthField.addChangeListener { updatePreview() }
      javadocBorderCharField.document.addDocumentListener(object : DocumentAdapter() {
         override fun textChanged(e: DocumentEvent) = updatePreview()
      })
      
      return panel
   }
   
   private fun updatePreview() {
      val sampleComment = """
         This is a sample method that demonstrates the flowerbox formatting.
         It has multiple lines and will be formatted according to your settings.
      """.trimIndent()
      
      val style = FlowerboxStyle(
         maxWidth = javadocWidthField.number,
         topBorderChar = javadocBorderCharField.text.firstOrNull() ?: '*',
         innerLinePrefix = javadocPrefixField.text
      )
      
      // Generate preview using actual formatting logic
      val formatted = generatePreviewFlowerbox(sampleComment, style)
      previewPanel.text = formatted
   }
}
```

---

## 7. Action Implementations

### 7.1 ReformatCommentAction

```kotlin
class ReformatCommentAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      val project = e.project ?: return
      val editor = e.getData(CommonDataKeys.EDITOR) ?: return
      val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
      
      val offset = editor.caretModel.offset
      val comment = findCommentAtCaret(psiFile, offset) ?: run {
         showError("No comment found at cursor position")
         return
      }
      
      val settings = ClaritasSettings.getInstance()
      val processor = project.service<CommentProcessorService>()
      
      val formatted = when (comment.tokenType) {
         JavaTokenType.END_OF_LINE_COMMENT -> {
            val consecutiveComments = collectConsecutiveLineComments(comment)
            processor.processInlineComment(consecutiveComments, settings.getInlineCommentStyle())
         }
         else -> {
            processor.processComment(comment, settings.getFlowerboxStyle())
         }
      }
      
      WriteCommandAction.runWriteCommandAction(project) {
         val factory = PsiElementFactory.getInstance(project)
         val newComment = factory.createCommentFromText(formatted, null)
         comment.replace(newComment)
      }
   }
   
   override fun update(e: AnActionEvent) {
      val psiFile = e.getData(CommonDataKeys.PSI_FILE)
      val editor = e.getData(CommonDataKeys.EDITOR)
      
      e.presentation.isEnabled = psiFile != null && 
         editor != null && 
         psiFile is PsiJavaFile &&
         findCommentAtCaret(psiFile, editor.caretModel.offset) != null
   }
}
```

### 7.2 GenerateJavadocAction

```kotlin
class GenerateJavadocAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      val project = e.project ?: return
      val editor = e.getData(CommonDataKeys.EDITOR) ?: return
      val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile ?: return
      
      val offset = editor.caretModel.offset
      val element = findJavadocTarget(psiFile, offset) ?: run {
         showError("No class or method found at cursor position")
         return
      }
      
      val existingDoc = (element as? PsiDocCommentOwner)?.docComment
      val settings = ClaritasSettings.getInstance()
      val processor = project.service<JavadocProcessorService>()
      
      val javadoc = processor.generateJavadoc(element, existingDoc, settings.getFlowerboxStyle())
      
      WriteCommandAction.runWriteCommandAction(project) {
         if (existingDoc != null) {
            val factory = PsiElementFactory.getInstance(project)
            val newDoc = factory.createDocCommentFromText(javadoc)
            existingDoc.replace(newDoc)
         } else {
            // Insert new Javadoc above element
            val factory = PsiElementFactory.getInstance(project)
            val newDoc = factory.createDocCommentFromText(javadoc)
            element.parent.addBefore(newDoc, element)
         }
      }
   }
}
```

### 7.3 ProcessFileAction

```kotlin
class ProcessFileAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      val project = e.project ?: return
      val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile ?: return
      
      val settings = ClaritasSettings.getInstance()
      val options = FileProcessingOptions(
         processComments = settings.state.processCommentsInFile,
         processJavadocs = settings.state.processJavadocsInFile,
         skipCleanBlocks = settings.state.skipCleanBlocks,
         showSummary = settings.state.showProcessingSummary
      )
      
      val processor = project.service<FileProcessorService>()
      val result = processor.processFile(psiFile, options)
      
      if (settings.state.showProcessingSummary) {
         showProcessingSummary(result)
      }
   }
   
   private fun showProcessingSummary(result: ProcessingResult) {
      val message = buildString {
         appendLine("Claritas Processing Complete")
         appendLine()
         appendLine("Comments processed: ${result.commentsProcessed}")
         appendLine("Javadocs processed: ${result.javadocsProcessed}")
         if (result.warnings.isNotEmpty()) {
            appendLine()
            appendLine("Warnings: ${result.warnings.size}")
            result.warnings.take(5).forEach {
               appendLine("  - ${it.message}")
            }
         }
      }
      
      Messages.showInfoMessage(message, "Claritas")
   }
}
```

---

## 8. Testing Strategy

### 8.1 Test Structure

```
src/test/kotlin/com/kof22/claritas/
├── services/
│   ├── CommentProcessorServiceTest.kt
│   ├── JavadocProcessorServiceTest.kt
│   └── FileProcessorServiceTest.kt
├── util/
│   ├── TextWrapperTest.kt
│   └── BorderRendererTest.kt
├── integration/
│   ├── ReformatCommentActionTest.kt
│   └── ProcessFileActionTest.kt
└── testdata/
    ├── comments/
    │   ├── simple_comment_before.java
    │   ├── simple_comment_after.java
    │   ├── long_comment_before.java
    │   ├── long_comment_after.java
    │   └── ...
    └── javadoc/
        ├── method_with_params_before.java
        ├── method_with_params_after.java
        └── ...
```

### 8.2 Golden File Test Example

```kotlin
class CommentProcessorServiceTest : BasePlatformTestCase() {
   
   fun testSimpleCommentFlowerboxing() {
      val before = loadTestData("comments/simple_comment_before.java")
      val expected = loadTestData("comments/simple_comment_after.java")
      
      val style = FlowerboxStyle()
      val processor = project.service<CommentProcessorService>()
      
      val psiFile = myFixture.configureByText("Test.java", before)
      val comment = PsiTreeUtil.findChildOfType(psiFile, PsiComment::class.java)!!
      
      val result = processor.processComment(comment, style)
      
      assertEquals(expected.trim(), result.trim())
   }
   
   fun testLongCommentWrapping() {
      // Test that lines exceeding maxWidth are properly wrapped
   }
   
   fun testPreserveParagraphBreaks() {
      // Test that blank lines between paragraphs are preserved
   }
   
   fun testDynamicWidthInlineComment() {
      // Test inline comment width calculation
   }
}
```

### 8.3 Coverage Goals

- **Target:** 80% instruction coverage, 90% class coverage
- **Focus Areas:**
  - Text normalization logic (edge cases)
  - Line wrapping algorithm (boundary conditions)
  - PSI element detection and traversal
  - Settings persistence

---

## 9. Implementation Phases

### Phase 0: POC (1-2 days)
- [ ] Create basic action that modifies current line
- [ ] Create minimal settings panel with one checkbox
- [ ] Verify plugin mechanics work
- [ ] Test undo/redo

### Phase 1: Comment Flowerboxing (1 week)
- [ ] Implement `CommentProcessorService`
- [ ] Text normalization utilities
- [ ] Line wrapping algorithm
- [ ] Flowerbox rendering (fixed width)
- [ ] Inline comment rendering (dynamic width)
- [ ] `ReformatCommentAction` implementation
- [ ] Unit tests for all comment processing

### Phase 2: Settings Infrastructure (3-4 days)
- [ ] Complete `ClaritasSettings` with all fields
- [ ] Build tabbed settings UI
- [ ] Live preview panel
- [ ] Settings persistence tests

### Phase 3: Javadoc Processing (1-2 weeks)
- [ ] Implement `JavadocProcessorService`
- [ ] Javadoc extraction from PSI
- [ ] Javadoc model building from signature
- [ ] Model merging logic
- [ ] Javadoc rendering
- [ ] `GenerateJavadocAction` implementation
- [ ] Comprehensive Javadoc tests

### Phase 4: File Processing (3-4 days)
- [ ] Implement `FileProcessorService`
- [ ] PSI tree traversal
- [ ] Batch processing logic
- [ ] Progress indication
- [ ] Summary dialog
- [ ] `ProcessFileAction` implementation
- [ ] Integration tests

### Phase 5: Polish & Documentation (2-3 days)
- [ ] Error handling improvements
- [ ] Performance optimization
- [ ] User documentation
- [ ] Code cleanup
- [ ] Final testing

**Total Estimated Time:** 4-5 weeks for full implementation

---

## 10. Technical Considerations

### 10.1 PSI Manipulation Safety

**Always use `WriteCommandAction`:**
```kotlin
WriteCommandAction.runWriteCommandAction(project) {
   // All PSI modifications here
}
```

**Ensure PSI validity:**
```kotlin
if (!element.isValid) return
```

**Handle concurrent modifications:**
```kotlin
ReadAction.run<Throwable> {
   // Read PSI
}

WriteCommandAction.runWriteCommandAction(project) {
   // Modify PSI
}
```

### 10.2 Performance Optimization

**Large File Handling:**
- Process in background thread for files >1000 lines
- Show progress indicator
- Allow cancellation

**PSI Tree Traversal:**
- Use `PsiRecursiveElementWalkingVisitor` for efficient traversal
- Filter early to avoid unnecessary processing
- Cache results when possible

### 10.3 Error Handling

**User-facing errors:**
```kotlin
try {
   // Process
} catch (e: Exception) {
   Messages.showErrorDialog(
      project,
      "Failed to process comment: ${e.message}",
      "Claritas Error"
   )
   LOG.error("Comment processing failed", e)
}
```

**Validation:**
- Validate settings before applying
- Check PSI element types before casting
- Handle missing or malformed Javadoc gracefully

---

## 11. Open Questions for Implementation

1. **Alignment of @param names:** Should we pad all param names to align descriptions vertically?
   ```java
   @param id          the identifier
   @param customerName the customer name
   ```
   vs
   ```java
   @param id the identifier
   @param customerName the customer name
   ```

2. **Multiple consecutive line comments:** Should they always be treated as a single block, or only if no blank lines between them?

3. **Javadoc for fields:** Should field Javadocs also use flowerbox style, or simpler single-line format?

4. **Undo granularity:** Should "Process File" be one undo action, or one undo per comment/Javadoc?

5. **Settings scope:** Application-level (all projects) or project-level (per project)?

---

## 12. Next Steps

1. Review and approve this technical design
2. Answer open questions
3. Implement Phase 0 (POC)
4. Iterate based on POC learnings
5. Proceed with Phase 1-5 implementation

---

**Document Version:** 1.0  
**Last Updated:** 2025-11-28  
**Status:** Ready for Review

