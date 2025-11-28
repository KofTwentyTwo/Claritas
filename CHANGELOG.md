<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Claritas Changelog

## [Unreleased]

## [0.0.2] - 2025-11-28 - Phase 0 POC
### Added
- FormatCurrentLineAction: POC action that replaces current line with formatted version
- ClaritasSettings: Simple settings persistence with application-level storage
- ClaritasSettingsConfigurable: Basic settings UI with single checkbox
- Documentation: INITIAL_DESIGN_REQUIREMENTS.md, TECHNICAL_DESIGN.md, TODO.md, PHASE0_TESTING.md
- Ktlint configuration with 3-space indentation standard
- Proper undo/redo support via WriteCommandAction

### Changed
- Cleaned up template code (removed demo tool window and placeholder functions)
- Updated ClaritasService to remove random number generator
- Refactored package structure from com.github.koftwentytwo to com.kof22

### Removed
- Demo tool window (will be reimplemented in later phases)
- Template warning messages

## [0.0.1] - 2025-11-27
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Basic project structure with Kotlin support
- Test infrastructure with JUnit
