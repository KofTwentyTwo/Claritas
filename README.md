# Claritas

**Precision. Structure. Clarity. Your code—refined and illuminated.**

<!-- Plugin description -->
Claritas is an IntelliJ plugin that transforms messy comments and incomplete Javadocs into beautiful, consistent documentation. Using PSI-based analysis, it enforces your team's documentation standards while preserving content and ensuring safe, reversible transformations. Turn legacy code into well-documented, maintainable systems with a single action.
<!-- Plugin description end -->

## What It Does

Claritas brings discipline and elegance to Java documentation through three core features:

**Comment Flowerboxing** – Transform any comment into structured flowerbox style with configurable borders, width, and formatting. Inline comments get dynamic width; Javadocs use fixed-width flowerbox structure.

```java
// Before
/* This method processes orders and applies validation rules 
before saving to database */

// After
/*******************************************************************************
 ** This method processes orders and applies validation rules before saving to
 ** database.
 *******************************************************************************/
```

**Javadoc Generation** – Create complete, properly structured Javadocs from method signatures. Extracts parameters, return types, and exceptions while preserving any existing documentation you've written.

```java
// Before
public Order processOrder(Integer customerId, List<Item> items) throws ValidationException {

// After
/*******************************************************************************
 ** Process an order for the given customer.
 **
 ** @param customerId the customer identifier
 ** @param items the list of items to process
 ** @return the processed order
 ** @throws ValidationException if validation fails
 *******************************************************************************/
public Order processOrder(Integer customerId, List<Item> items) throws ValidationException {
```

**File Processing** – Apply formatting rules across entire files with a single command. All changes in one undo action.

## Configuration

Settings available at application or project level (stored in `.idea/claritas.xml`):
- Flowerbox width, borders, and prefix characters
- Line wrapping behavior and reflow thresholds
- Javadoc tag ordering and content preservation rules
- File processing scope and options

Live preview shows formatting results as you adjust settings.

## Current Status

**Version:** 0.0.2-POC  
**Status:** Phase 0 POC Complete ✅

Phase 0 implements a simple proof-of-concept to validate plugin mechanics:
- ✅ Action registration and menu integration
- ✅ Editor context access and document modification
- ✅ Settings persistence (application-level)
- ✅ Undo/redo support via WriteCommandAction

**Try the POC:**
1. Run `./gradlew runIde` to launch sandbox IDE
2. Open any file, right-click in editor
3. Select `Claritas` → `Format Current Line (POC)`
4. Current line will be replaced with `// CLARITAS: [original text]`

See `docs/PHASE0_TESTING.md` for complete manual testing guide.

**Next:** Phase 1 will replace POC logic with real comment processing (flowerbox formatting, PSI-based parsing, text normalization).

## Development

```bash
./gradlew runIde        # Run plugin in sandbox IDE
./gradlew test          # Execute test suite (3/3 passing)
./gradlew buildPlugin   # Create distribution package
./gradlew ktlintFormat  # Auto-format code (3-space indent)
```

Built on IntelliJ PSI APIs for safe, language-aware transformations. Three core services handle comment processing, Javadoc generation, and batch operations. See `docs/TECHNICAL_DESIGN.md` for architecture details and `docs/TODO.md` for implementation phases.
