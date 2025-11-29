# TextBlockExtractor Test Suite

## Overview

The `TextBlockExtractor` test suite uses an **abstract base class pattern** to ensure consistent testing across all supported languages. This design ensures that every language implementation passes the same comprehensive test suite.

## Test Structure

```
TextBlockExtractorTestBase (abstract)
├── TextBlockExtractorJavaTest
├── TextBlockExtractorJavaScriptTest
└── TextBlockExtractorKotlinTest
```

### Base Class: `TextBlockExtractorTestBase`

The abstract base class defines **all required test cases** that every language must pass:

#### Test Categories

1. **Selection Tests** (5 tests)
   - Single line selection
   - Multi-line selection
   - Indented selections (spaces/tabs)
   - Empty selection fallback

2. **Line Comment Tests** (4 tests)
   - Basic line comments
   - Caret positions (start/middle/end)
   - Indented line comments

3. **Block Comment Tests** (3 tests)
   - Multi-line block comments
   - Single-line block comments
   - Indented block comments

4. **Doc Comment Tests** (2 tests)
   - Basic doc comments (JavaDoc/JSDoc/KDoc)
   - Doc comments with annotations (@param, @return, etc.)

5. **Raw Text Block Tests** (7 tests)
   - Single line extraction
   - Multi-line expansion
   - Caret positions in blocks
   - Document boundaries
   - Blank line separation

6. **Indentation Tests** (4 tests)
   - Space indentation
   - Tab indentation
   - Mixed indentation
   - No indentation

7. **Edge Cases** (3 tests)
   - Empty documents
   - Whitespace-only documents
   - Multiple consecutive blank lines

8. **TextRange Validation** (2 tests)
   - Range matches content
   - Multi-line range validation

**Total: 30 base tests per language**

### Language-Specific Test Classes

Each language extends the base class and adds language-specific test cases:

#### `TextBlockExtractorJavaTest`
- Class context comments
- JavaDoc on methods
- Method-level comments
- End-of-line comments

#### `TextBlockExtractorJavaScriptTest`
- Function context comments
- JSDoc on functions
- Arrow function comments
- Object literal comments
- ES6 class method comments

#### `TextBlockExtractorKotlinTest`
- Class context comments
- KDoc on functions
- Data class comments
- Companion object comments
- Lambda comments
- When expression comments

## Adding a New Language

To add support for a new language:

1. **Create a new test class** extending `TextBlockExtractorTestBase`
2. **Override `fileExtension`** property (e.g., "py", "rb", "ts")
3. **Add language-specific tests** for unique syntax/patterns

Example:

```kotlin
class TextBlockExtractorPythonTest : TextBlockExtractorTestBase() {
    override val fileExtension = "py"
    
    // Add Python-specific tests here
    fun testPythonSpecific_DocstringExtraction() {
        // Test triple-quoted docstrings
    }
}
```

All 30 base tests will automatically run for the new language!

## Running Tests

### Run all TextBlockExtractor tests
```bash
./gradlew :claritas-intellij:test --tests "*TextBlockExtractor*"
```

### Run tests for a specific language
```bash
./gradlew :claritas-intellij:test --tests "TextBlockExtractorJavaTest"
./gradlew :claritas-intellij:test --tests "TextBlockExtractorJavaScriptTest"
./gradlew :claritas-intellij:test --tests "TextBlockExtractorKotlinTest"
```

### Run a specific test method
```bash
./gradlew :claritas-intellij:test --tests "TextBlockExtractorJavaTest.testExtractCommentBlock_LineComment"
```

## Test File Markers

The tests use IntelliJ's test fixture markers:

- `<caret>` - Indicates caret position in the editor
- `<selection>...</selection>` - Indicates selected text

Example:
```kotlin
val psiFile = configureByText(
    """
    // This is a <caret>comment
    """.trimIndent()
)
```

## Known Limitations

### Comments Inside Complex Code Structures

When a comment appears inside a multi-line code structure (class body, function body, etc.), the PSI element lookup may not correctly identify it as a comment element. Instead, `extractBlockFromCurrentLine()` expands the surrounding code block and treats it as raw text.

This affects tests like:
- Comments inside class bodies
- Comments inside function/method bodies  
- Comments in data structures (objects, interfaces, etc.)

**Workaround**: The base tests cover comment extraction in simpler contexts, which works correctly. Language-specific tests should focus on documentation comments (JavaDoc/JSDoc/KDoc) which are typically outside code blocks.

## Benefits of This Design

1. **Consistency** - All languages pass the same comprehensive test suite
2. **Maintainability** - Add new test requirements once in the base class
3. **Language Coverage** - Easy to see which languages are tested
4. **Extensibility** - New languages only need to implement language-specific tests
5. **Clarity** - Clear separation between common and language-specific behavior

## Coverage

The test suite provides comprehensive coverage for:

- ✅ All comment types (line, block, doc)
- ✅ All extraction modes (selection, comment, raw text)
- ✅ Indentation handling (spaces, tabs, mixed)
- ✅ Edge cases (boundaries, whitespace, empty files)
- ✅ TextRange accuracy
- ✅ Language-specific syntax patterns

