1. Purpose of this document

This document defines the high-level design for the IntelliJ plugin Claritas.

You will give this to an LLM so that:
	•	It understands the overall goals and architecture of Claritas.
	•	It knows what the first three features must do.
	•	It can ask clarifying questions before generating concrete designs, classes, and code.

This is not an implementation spec; it is a conceptual design and requirements summary.

⸻

2. Product overview: Claritas

Claritas is an IntelliJ IDEA plugin that helps programmers maintain consistent, high-quality comments and API documentation according to a configurable house style.

Initial focus:
	•	Language: Java (PSI-based, using IntelliJ’s Java support).
	•	Scope: Comments and Javadocs only.
	•	Operation: user-invoked actions plus “process file” batch operation.
	•	Configurability: all behaviors tunable via plugin settings UI.

Core idea:

“Given any comment or Javadoc in a file, Claritas can parse it, normalize the content, and rebuild it according to our standard flowerbox and Javadoc style, optionally reflowing and rebalancing lines.”

⸻

3. Goals and non-goals

3.1 Goals
	1.	Provide reliable transformations of comments and Javadocs without breaking code.
	2.	Be language-aware via IntelliJ PSI; avoid dumb text replacements.
	3.	Keep behavior fully configurable:
	•	Flowerbox parameters (width, borders, prefixes, etc.).
	•	Line-length rules.
	•	Javadoc structure and which tags to include.
	4.	Support:
	•	Single comment / Javadoc actions.
	•	“Process entire file” action.
	5.	Provide safe, previewable changes (e.g., via normal IntelliJ refactoring / undo mechanisms).

3.2 Non-goals (initial versions)
	•	No automatic AI text generation of comment content. The plugin structures/normalizes but does not invent semantics.
	•	No multi-file batch processing (that can come later).
	•	No non-Java language support initially (but architecture should allow it later).

⸻

4. Core concepts

4.1 Flowerbox comment

A flowerbox is a block comment style with:
	•	A top border line (e.g., /**************/ or /* ======== */).
	•	Middle lines with a consistent prefix (e.g.,  * ... or  ** ...).
	•	Optional blank spacer lines.
	•	A bottom border line mirroring the top.

Claritas must:
	•	Take any existing comment (one-line or multi-line).
	•	Strip all leading comment markers and internal formatting.
	•	Reflow the plain text into lines obeying:
	•	Configured max width.
	•	Configured indentation.
	•	Rebuild the comment using the flowerbox pattern.

4.2 Javadoc standard

Claritas will enforce a particular Javadoc layout, for classes and methods. For example:
	•	First summary sentence (one line).
	•	Optional description block.
	•	Parameter tags @param in declaration order.
	•	Type parameter tags @param <T>.
	•	Return tag @return when non-void.
	•	Throws tags @throws.
	•	Optional custom sections (e.g., @implSpec, @since).

Claritas must be able to:
	•	Generate a new Javadoc when missing.
	•	Normalize an existing Javadoc:
	•	Extract and preserve meaningful free-text parts.
	•	Rebuild structure and tags according to the standard.
	•	Re-emit using the flowerbox Javadoc style.

⸻

5. High-level architecture

5.1 Major components
	1.	ClaritasPluginComponent / StartupActivity
	•	Registers actions, services, and settings.
	2.	ClaritasSettings (PersistentStateComponent)
	•	Stores all user configuration:
	•	Flowerbox style (chars, width, indent, padding lines).
	•	Line-wrapping behavior.
	•	Javadoc rules (which tags, order, required/optional).
	•	File-processing defaults.
	3.	ClaritasSettings UI (Configurable)
	•	IntelliJ settings page(s):
	•	“Flowerbox Comments”
	•	“Javadoc Style”
	•	“File Processing”
	•	Live preview of a sample comment/Javadoc under current settings.
	4.	CommentProcessorService
	•	Pure logic to:
	•	Detect and extract comments from PSI (line, block).
	•	Normalize comment text (strip markers, unify whitespace).
	•	Reflow text to lines.
	•	Rebuild as flowerbox comment string.
	•	Language-agnostic core with Java-specific adapters initially.
	5.	JavadocProcessorService
	•	Logic to:
	•	Given a Java PSI class/method:
	•	Inspect existing Javadoc (if any).
	•	Parse out sections: summary, description, tags, unknown content.
	•	Extract parameter names, return types, thrown exceptions from PSI.
	•	Merge existing text with rules (e.g., preserve summary if present).
	•	Generate canonical Javadoc block as string (flowerbox style).
	•	Optional: ability to collect warnings (e.g., missing param descriptions).
	6.	FileProcessorService
	•	Walks PSI tree for a file:
	•	For each comment node → CommentProcessorService.
	•	For each class/method → JavadocProcessorService.
	•	Applies transformations using WriteCommandAction / code style manager.
	7.	User Actions (AnAction implementations)
	•	ClaritasReformatCommentAction
	•	Context: caret inside a comment.
	•	Behavior: detect enclosing comment; reformat as flowerbox.
	•	ClaritasGenerateOrNormalizeJavadocAction
	•	Context: caret on class/method name or inside its header.
	•	Behavior: generate or normalize Javadoc.
	•	ClaritasProcessFileAction
	•	Context: current editor file or from project view.
	•	Behavior: run FileProcessorService across file.
Each action should support:
	•	Undo/redo.
	•	Optional “preview changes” via standard diff (future enhancement).

⸻

6. Feature 1 – Comment flowerboxing

6.1 User scenarios
	•	User selects a comment (or just puts caret inside it) and invokes “Claritas: Reformat Comment”.
	•	User runs “Claritas: Process File” and all comments are normalized.

6.2 Functional behavior
	1.	Comment detection
	•	Given caret PSI context, find the nearest:
	•	PsiComment node.
	•	For line comments // ... possibly consecutive lines forming a single logical block.
	•	For block comments /* ... */, treat as one block.
	2.	Content normalization
	•	Strip all comment markers:
	•	Remove leading //, /*, /**, *, */, and trailing spaces.
	•	Preserve paragraph breaks (blank lines) if possible.
	•	Optionally normalize multiple spaces to single space except in code blocks (future enhancement).
	3.	Text layout / wrapping
	•	Split text into logical paragraphs.
	•	Wrap lines to maxWidth minus indent and decoration characters.
	•	Option for:
	•	“Hard wrap” at width.
	•	“No wrap, just frame existing lines”.
	4.	Flowerbox emission
	•	Use settings:
	•	Top border pattern (e.g., /* + repeated char + */).
	•	Inner line prefix (e.g., * or **).
	•	Blank lines at top/bottom inside box.
	•	Reconstruct with appropriate indentation based on original comment location.
	5.	Code integration
	•	Replace original PSI comment with new text via PsiElement.replace inside write action.
	•	Respect IntelliJ’s code style where necessary.

⸻

7. Feature 2 – Javadoc creation and normalization

7.1 User scenarios
	•	Caret on method name → user invokes “Claritas: Javadoc for Method”.
	•	A method with messy or missing Javadoc is cleaned to Claritas standard.
	•	Similar behavior for classes/interfaces.

7.2 Functional behavior
	1.	Target detection
	•	From caret, resolve to:
	•	PsiMethod
	•	PsiClass / PsiInterface / PsiEnum
	2.	Existing Javadoc analysis
	•	If Javadoc present (PsiDocComment):
	•	Extract:
	•	Summary sentence.
	•	Description paragraphs.
	•	Known tags (@param, @return, @throws, @deprecated, etc.).
	•	Unknown/custom tags (to be preserved or dropped based on settings).
	•	If absent:
	•	Start with empty structure.
	3.	PSI-based signature inspection
	•	From the PSI element, obtain:
	•	Type parameters.
	•	Parameter list and names.
	•	Return type and whether it’s void.
	•	Thrown exceptions.
	•	Use this to determine:
	•	Which tags must exist.
	•	Their order and contents.
	4.	Merging rules
	•	Settings define:
	•	Whether to keep existing summary/description if present.
	•	Whether to overwrite tag descriptions or only add missing ones.
	•	Whether to preserve unknown tags.
	•	Build an intermediate Javadoc model:
	•	summary: String
	•	description: List<String>
	•	params: Map<String, String>
	•	typeParams: Map<String, String>
	•	returnDescription: String?
	•	throws: Map<String, String>
	•	customTags: List<...>
	5.	Emission using standard
	•	Render the model into a Javadoc block following Claritas rules:
	•	Specific order of sections.
	•	Blank line spacing.
	•	Alignment (e.g., vertically aligned @param names).
	•	Optionally using flowerbox style comments (top/bottom borders).
	6.	Replacement
	•	Insert new Javadoc:
	•	If none existed, create new PsiDocComment above the element.
	•	If one existed, replace its text.
	•	Ensure valid PSI and formatting.

⸻

8. Feature 3 – Process entire file

8.1 User scenarios
	•	User opens a legacy Java file and chooses Claritas: Process File.
	•	All comments and API headers are normalized in one shot.

8.2 Functional behavior
	1.	Traversal
	•	Use PSI visitor on the file:
	•	For every PsiComment → CommentProcessorService.
	•	For every PsiMethod / PsiClass → JavadocProcessorService.
	2.	Ordering and safety
	•	Prefer processing from top to bottom to keep edits stable.
	•	All changes must be done inside one write command to support a single undo.
	3.	Configurability
	•	Settings toggle:
	•	Whether to process comments only, Javadocs only, or both.
	•	Whether to skip files with certain annotations or markers.
	•	Whether to skip already “clean” blocks detected as compliant.
	4.	Feedback
	•	Optionally show a summary:
	•	Number of comments reformatted.
	•	Number of Javadocs created/normalized.
	•	Any warnings (e.g., parameters missing documentation).

⸻

9. Configuration and settings

9.1 General
	•	Implemented via PersistentStateComponent and Configurable.
	•	Grouped into logical sections.

9.2 Flowerbox settings
	•	Comment max width (integer).
	•	Border style:
	•	Top/bottom characters (e.g., *, =).
	•	Whether to use /* ... */ or /** ... */.
	•	Inner line prefix.
	•	Indent strategy:
	•	Respect existing indent.
	•	Fixed indent.
	•	Paragraph handling:
	•	Preserve blank lines vs compact paragraphs.

9.3 Javadoc settings
	•	Required tags:
	•	Always generate @param, @return, @throws, etc.
	•	Behavior for existing docs:
	•	Preserve summary/description.
	•	Merge tags without deleting unknown ones.
	•	Overwrite all tags from signature.
	•	Alignment and spacing rules.

9.4 File processing settings
	•	What to process:
	•	Comments only / Javadocs only / both.
	•	Skip rules:
	•	Annotations to skip, patterns in file name/path.
	•	Preview behavior (future): dry-run mode.

⸻

10. Extensibility and future directions (informational)

Not required for v0, but architecture should allow:
	•	Support for other languages (Kotlin, Scala, etc.) by plugging in language-specific PSI adapters.
	•	Rulesets per project (stored in .idea or external file).
	•	AI-assisted text improvement while keeping structural formatting.

⸻

11. Topics the LLM should clarify before implementation

When you hand this document to an LLM, it should ask clarifying questions in areas like:
	1.	Flowerbox Style Details
	•	Exact default top/bottom border format?
	•	Should Javadocs and non-Javadocs share the same style?
	•	How to handle comments that contain ASCII art or code samples?
	2.	Line-Wrapping Rules
	•	Desired default width?
	•	Should words be preserved at line boundaries (no breaking inside tokens)?
	•	How to treat very long tokens (URLs, fully qualified names)?
	3.	Javadoc Semantics
	•	Should it attempt to infer summaries from method names (e.g., “setX” → “Sets X”) or leave text blank?
	•	How to treat undocumented parameters—leave placeholders or blank descriptions?
	•	Any project-specific custom tags that must always be present (e.g., @implNote, @apiNote)?
	4.	Behavior on Existing Javadocs
	•	Is it acceptable for Claritas to reorder tags?
	•	Should it ever delete user-written free text, or must it be preserved verbatim?
	•	How aggressively should it normalize whitespace and formatting?
	5.	Scope and Safety
	•	Should “Process File” be enabled only for Java files?
	•	Should there be a confirmation dialog for large changes?
	•	Are there coding standards the plugin must never override (e.g., company-wide templates)?
	6.	Settings & UX
	•	Preference for one unified settings page vs multiple tabs?
	•	Desired defaults for all settings so the plugin is useful out of the box.
	7.	Testing Expectations
	•	Required unit test coverage for processors?
	•	Need for golden-file tests comparing before/after transformations?
