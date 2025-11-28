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

## Development

```bash
./gradlew runIde        # Run plugin in sandbox IDE
./gradlew test          # Execute test suite
./gradlew buildPlugin   # Create distribution package
```

Built on IntelliJ PSI APIs for safe, language-aware transformations. Three core services handle comment processing, Javadoc generation, and batch operations. See `docs/TECHNICAL_DESIGN.md` for architecture details and implementation phases.
