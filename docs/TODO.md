# Claritas Implementation TODO

**Project:** Claritas - IntelliJ Plugin for Java Comment & Javadoc Formatting  
**Current Phase:** Phase 0 - POC  
**Started:** 2025-11-28  
**Target Completion:** Phase 0 by 2025-11-29

---

## Phase 0 - Simple POC (1-2 days)

**Goal:** Prove plugin mechanics work with minimal functionality

### Setup & Infrastructure

- [ ] Clean up existing template code
  - [ ] Remove demo `ClaritasService.getRandomNumber()`
  - [ ] Remove template warnings from existing files
  - [ ] Remove demo tool window or repurpose for future use
  - [ ] Keep `ClaritasBundle` and `ClaritasActivity` (useful for future)

### Core POC Implementation

- [ ] **Create FormatCurrentLineAction**
  - [ ] Create `actions/` package
  - [ ] Implement `FormatCurrentLineAction.kt`
    - [ ] Get editor and document from AnActionEvent
    - [ ] Get current line number from caret position
    - [ ] Read current line text
    - [ ] Replace with `// CLARITAS: [original text]`
    - [ ] Use `WriteCommandAction` for undo support
  - [ ] Register action in `plugin.xml`
    - [ ] Add to Editor popup menu
    - [ ] Set appropriate icon (optional)
    - [ ] Add keyboard shortcut (optional: Ctrl+Alt+C)

- [ ] **Create Simple Settings**
  - [ ] Create `settings/` package
  - [ ] Implement `ClaritasSettings.kt`
    - [ ] Use `@State` annotation with proper storage
    - [ ] Create `State` data class with one field: `enableClaritas: Boolean`
    - [ ] Implement `PersistentStateComponent` interface
    - [ ] Add companion object `getInstance()` method
    - [ ] Set `Service.Level.APP` for now (will expand in Phase 2)
  - [ ] Implement `ClaritasSettingsConfigurable.kt`
    - [ ] Implement `Configurable` interface
    - [ ] Create simple UI with one checkbox
    - [ ] Wire checkbox to settings state
    - [ ] Implement `isModified()`, `apply()`, `reset()`
  - [ ] Register configurable in `plugin.xml`
    - [ ] Add `<applicationConfigurable>` extension
    - [ ] Set display name: "Claritas"
    - [ ] Set ID: "claritas.settings"

### Testing & Validation

- [ ] **Manual Testing Checklist**
  - [ ] Plugin loads without errors
  - [ ] Action appears in Editor context menu under "Claritas"
  - [ ] Action replaces current line correctly
  - [ ] Undo (Cmd+Z) restores original line
  - [ ] Redo (Cmd+Shift+Z) reapplies change
  - [ ] Settings page appears under Preferences
  - [ ] Checkbox state saves when Apply/OK clicked
  - [ ] Checkbox state persists after IDE restart
  - [ ] Settings file created at correct location

- [ ] **Write Basic Unit Test**
  - [ ] Create test for `FormatCurrentLineAction`
  - [ ] Verify line replacement works
  - [ ] Verify undo/redo functionality
  - [ ] Test with empty line
  - [ ] Test with whitespace-only line

### Documentation

- [ ] Update `README.md`
  - [ ] Note that Phase 0 POC is complete
  - [ ] Document how to test the POC
  - [ ] Add screenshot of menu action (optional)

- [ ] Update `CHANGELOG.md`
  - [ ] Add [0.0.2] section
  - [ ] Note POC implementation
  - [ ] List basic features

### Phase 0 Completion Criteria

- [ ] All manual tests pass
- [ ] No errors in IDE log
- [ ] Settings persist correctly
- [ ] Undo/redo works properly
- [ ] Code follows ktlint style (3-space indent)
- [ ] At least one unit test passes
- [ ] Changes committed to `develop` branch

---

## Phase 1 - Comment Flowerboxing (1 week)

### Core Services

- [ ] Implement `CommentProcessorService`
  - [ ] Text normalization (strip markers, whitespace)
  - [ ] Paragraph detection and preservation
  - [ ] Line wrapping algorithm
  - [ ] Flowerbox rendering (fixed width for Javadoc)
  - [ ] Inline comment rendering (dynamic width)
  - [ ] Rebalancing logic (threshold-based)

- [ ] Implement PSI utilities
  - [ ] `CommentExtractor` - find comments in PSI tree
  - [ ] Detect consecutive line comments
  - [ ] Calculate indentation level
  - [ ] Handle comment types (line, block, Javadoc)

- [ ] Implement text utilities
  - [ ] `TextWrapper` - word-boundary line wrapping
  - [ ] `BorderRenderer` - generate flowerbox borders
  - [ ] `IndentCalculator` - preserve/calculate indents

### Actions

- [ ] Replace POC action with real `ReformatCommentAction`
  - [ ] Detect comment at caret position
  - [ ] Process comment through `CommentProcessorService`
  - [ ] Replace PSI element with formatted version
  - [ ] Handle inline vs block comments
  - [ ] Show error if no comment found

### Data Models

- [ ] Create `model/` package
- [ ] Implement `CommentBlock` data class
- [ ] Implement `FlowerboxStyle` data class
- [ ] Implement `InlineCommentStyle` data class
- [ ] Implement `CommentType` enum

### Testing

- [ ] Unit tests for `CommentProcessorService`
- [ ] Unit tests for `TextWrapper`
- [ ] Unit tests for `BorderRenderer`
- [ ] Golden-file tests (before/after pairs)
  - [ ] Simple single-line comment
  - [ ] Multi-line comment
  - [ ] Long comment requiring reflow
  - [ ] Inline comments (dynamic width)
  - [ ] Inline comments (with rebalancing)
- [ ] Integration test for `ReformatCommentAction`

### Documentation

- [ ] Update `README.md` with Feature 1 completion
- [ ] Add usage examples
- [ ] Update `CHANGELOG.md`

---

## Phase 2 - Settings Infrastructure (3-4 days)

### Settings Architecture

- [ ] Implement hybrid settings (App + Project)
  - [ ] Refactor to `ClaritasApplicationSettings`
  - [ ] Create `ClaritasProjectSettings`
  - [ ] Create unified `ClaritasSettings` facade
  - [ ] Implement fallback chain (Project → App → Defaults)

### Settings UI

- [ ] Create tabbed settings panel
  - [ ] Flowerbox Style tab
  - [ ] Inline Comments tab
  - [ ] Javadoc Behavior tab (prepare for Phase 3)
  - [ ] File Processing tab (prepare for Phase 4)
- [ ] Add project-level controls
  - [ ] "Use project-specific settings" checkbox
  - [ ] "Copy from Application Settings" button
  - [ ] "Reset to Defaults" button
- [ ] Implement live preview panel
  - [ ] Show sample comment/Javadoc
  - [ ] Update in real-time as settings change

### Settings Fields

- [ ] Add all flowerbox configuration options
- [ ] Add all inline comment options
- [ ] Wire settings to processor services
- [ ] Add validation for numeric fields

### Testing

- [ ] Test settings persistence (application-level)
- [ ] Test settings persistence (project-level)
- [ ] Test fallback chain logic
- [ ] Test Copy/Reset functionality
- [ ] Verify `.idea/claritas.xml` created correctly

### Documentation

- [ ] Document all settings options
- [ ] Add screenshots of settings UI
- [ ] Update `CHANGELOG.md`

---

## Phase 3 - Javadoc Creation & Normalization (1-2 weeks)

### Core Services

- [ ] Implement `JavadocProcessorService`
  - [ ] Extract existing Javadoc from PSI
  - [ ] Build Javadoc model from method/class signature
  - [ ] Merge existing content with signature
  - [ ] Render Javadoc with flowerbox style
  - [ ] Handle field Javadocs (single-line format)

### Data Models

- [ ] Implement `JavadocModel` data class
- [ ] Implement tag classes (`ParamTag`, `ReturnTag`, etc.)
- [ ] Implement `ProcessingWarning` for validation

### PSI Utilities

- [ ] `JavadocExtractor` - parse existing Javadoc
- [ ] Extract summary and description
- [ ] Extract all tag types (@param, @return, @throws, etc.)
- [ ] Preserve custom/unknown tags
- [ ] Handle signature inspection (parameters, return type, throws)

### Actions

- [ ] Implement `GenerateJavadocAction`
  - [ ] Detect class/method at caret
  - [ ] Check for existing Javadoc
  - [ ] Generate or normalize Javadoc
  - [ ] Insert or replace PSI element
  - [ ] Handle fields separately

### Testing

- [ ] Unit tests for `JavadocProcessorService`
- [ ] Unit tests for Javadoc extraction
- [ ] Unit tests for signature inspection
- [ ] Golden-file tests for Javadoc scenarios
  - [ ] Method with parameters
  - [ ] Method with return value
  - [ ] Method with throws
  - [ ] Method with generic types
  - [ ] Class Javadoc
  - [ ] Field Javadoc (single-line)
  - [ ] Existing Javadoc normalization
- [ ] Integration test for `GenerateJavadocAction`

### Documentation

- [ ] Document Javadoc formatting rules
- [ ] Add examples of before/after
- [ ] Update `CHANGELOG.md`

---

## Phase 4 - Process Entire File (3-4 days)

### Core Services

- [ ] Implement `FileProcessorService`
  - [ ] PSI tree traversal with visitor pattern
  - [ ] Collect all comments in file
  - [ ] Collect all Javadoc targets (classes/methods)
  - [ ] Process in top-to-bottom order
  - [ ] Single `WriteCommandAction` for all changes
  - [ ] Collect warnings and statistics

### Data Models

- [ ] Implement `ProcessingResult` data class
- [ ] Implement `FileProcessingOptions` data class
- [ ] Implement `ProcessingElements` data class

### Actions

- [ ] Implement `ProcessFileAction`
  - [ ] Trigger from editor or project view
  - [ ] Show progress indicator for large files
  - [ ] Process all comments and Javadocs
  - [ ] Show summary dialog with statistics
  - [ ] Support cancellation

### UI Components

- [ ] Create processing summary dialog
  - [ ] Show counts (comments, Javadocs)
  - [ ] Show warnings list
  - [ ] Allow navigation to warnings

### Testing

- [ ] Unit tests for `FileProcessorService`
- [ ] Test visitor pattern traversal
- [ ] Test undo behavior (single undo)
- [ ] Golden-file test for complete file processing
- [ ] Test with files of various sizes
- [ ] Test skip logic (clean blocks, annotations)

### Documentation

- [ ] Document "Process File" feature
- [ ] Add batch processing guidelines
- [ ] Update `CHANGELOG.md`

---

## Phase 5 - Polish & Documentation (2-3 days)

### Error Handling

- [ ] Improve error messages
- [ ] Add proper exception handling
- [ ] Add logging for debugging
- [ ] Handle edge cases gracefully
  - [ ] Malformed comments
  - [ ] Invalid Javadoc
  - [ ] Concurrent modifications

### Performance Optimization

- [ ] Profile large file processing
- [ ] Optimize PSI traversal
- [ ] Add background thread support
- [ ] Implement caching where appropriate

### UI/UX Polish

- [ ] Add icons for actions
- [ ] Improve action availability (update() methods)
- [ ] Add keyboard shortcuts
- [ ] Improve settings UI layout
- [ ] Add tooltips and help text

### Documentation

- [ ] Write comprehensive user documentation
  - [ ] Getting started guide
  - [ ] Feature documentation
  - [ ] Settings reference
  - [ ] FAQ
- [ ] Write developer documentation
  - [ ] Architecture overview
  - [ ] Contribution guidelines
  - [ ] API documentation
- [ ] Update `README.md` with complete feature list
- [ ] Finalize `CHANGELOG.md` for v0.1.0 release

### Testing

- [ ] Final integration testing
- [ ] Test on various project types
- [ ] Test with different IntelliJ versions
- [ ] Performance testing with large files
- [ ] Verify test coverage meets goals (80%+ instruction)

### Release Preparation

- [ ] Update version to 0.1.0
- [ ] Update plugin description in `plugin.xml`
- [ ] Create release notes
- [ ] Tag release in git
- [ ] Build plugin distribution
- [ ] Test plugin installation from zip

---

## Future Enhancements (Post-v0.1.0)

### Language Support

- [ ] Kotlin support
- [ ] Scala support
- [ ] Groovy support

### Advanced Features

- [ ] AI-assisted text generation (summary inference)
- [ ] Multi-file batch processing
- [ ] Custom rule templates
- [ ] Import/export settings
- [ ] Code inspection integration
- [ ] Quick fixes for violations

### Integration

- [ ] Git pre-commit hook support
- [ ] CI/CD integration
- [ ] Team settings synchronization
- [ ] JetBrains Marketplace publication

---

## Notes & Decisions

### Technical Decisions

- **Language:** Kotlin for plugin, Java for target files
- **Minimum IntelliJ:** 2025.2 (build 252)
- **JDK:** Java 21
- **Code Style:** 3-space indentation (ktlint enforced)
- **Testing Framework:** JUnit with IntelliJ test fixtures

### Key Design Principles

1. **Never generate content** - only structure existing text (until AI feature added)
2. **PSI-based** - no dumb text replacement
3. **Undo-friendly** - proper WriteCommandAction usage
4. **Configurable** - all behaviors tunable via settings
5. **Safe** - preserve user content, never lose data

### Phase 0 Success Metrics

- ✅ Plugin loads without errors
- ✅ Menu action works
- ✅ Line replacement works correctly
- ✅ Undo/redo functional
- ✅ Settings persist
- ✅ One unit test passes

### Project Milestones

- **v0.0.1** - Initial scaffold (✅ COMPLETE)
- **v0.0.2** - Phase 0 POC (🔄 IN PROGRESS)
- **v0.1.0** - Phase 1-5 complete (Full feature set)
- **v0.2.0** - Additional language support
- **v1.0.0** - Public release with AI features

---

**Last Updated:** 2025-11-28  
**Status:** Phase 0 in progress  
**Next Actions:** Begin Phase 0 implementation

