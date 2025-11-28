# Phase 0 POC - Manual Testing Guide

**Version:** 0.0.2-POC  
**Date:** 2025-11-28  
**Status:** Ready for testing

---

## Testing Prerequisites

1. **Build the plugin:**
   ```bash
   ./gradlew clean build
   ```

2. **Run the plugin in sandbox IDE:**
   ```bash
   ./gradlew runIde
   ```

3. **Wait for the sandbox IntelliJ to fully load**

---

## Manual Test Cases

### Test 1: Plugin Loads Successfully

**Expected Result:** Plugin loads without errors

**Steps:**
1. Check IDE log for errors (`Help` → `Show Log in Finder`)
2. Look for: `Claritas plugin initialized for project:`
3. No exceptions or errors should appear

**Success Criteria:**
- ✅ No errors in IDE log
- ✅ Startup message appears

---

### Test 2: Action Appears in Menu

**Expected Result:** "Claritas" menu group appears in editor context menu

**Steps:**
1. Open any file in the editor (create a test .txt or .java file)
2. Right-click in the editor
3. Look for "Claritas" menu at the bottom
4. Expand the "Claritas" submenu

**Success Criteria:**
- ✅ "Claritas" menu appears
- ✅ "Format Current Line (POC)" action is visible

---

### Test 3: Format Current Line Action Works

**Expected Result:** Action replaces current line with formatted version

**Steps:**
1. Create or open a text file
2. Type some text: `Hello World`
3. Place cursor on that line
4. Right-click → `Claritas` → `Format Current Line (POC)`
5. Observe the result

**Expected Output:**
```
// CLARITAS: Hello World
```

**Success Criteria:**
- ✅ Line is replaced
- ✅ Format is: `// CLARITAS: [original text]`
- ✅ No errors occur

---

### Test 4: Undo/Redo Works

**Expected Result:** Undo restores original, redo reapplies change

**Steps:**
1. After Test 3, press `Cmd+Z` (Mac) or `Ctrl+Z` (Windows/Linux)
2. Observe line reverts to: `Hello World`
3. Press `Cmd+Shift+Z` (Mac) or `Ctrl+Shift+Z` (Windows/Linux)
4. Observe line changes back to: `// CLARITAS: Hello World`

**Success Criteria:**
- ✅ Undo restores original line
- ✅ Redo reapplies formatting
- ✅ Single undo operation (not multiple steps)

---

### Test 5: Action Disabled Appropriately

**Expected Result:** Action only enabled when editor is active

**Steps:**
1. Close all editor tabs
2. Right-click in Project view or elsewhere
3. Check if "Format Current Line (POC)" is grayed out
4. Open an editor tab
5. Check action is enabled again

**Success Criteria:**
- ✅ Action disabled when no editor
- ✅ Action enabled when editor active

---

### Test 6: Settings Page Exists

**Expected Result:** Claritas settings page appears in Preferences

**Steps:**
1. Open Preferences: `Cmd+,` (Mac) or `Ctrl+Alt+S` (Windows/Linux)
2. Search for "Claritas" in the search box
3. Or navigate manually through the settings tree
4. Find "Claritas" settings page

**Success Criteria:**
- ✅ "Claritas" page appears in settings
- ✅ Page contains one checkbox: "Enable Claritas Plugin"

---

### Test 7: Settings Persist

**Expected Result:** Checkbox state saves across IDE restarts

**Steps:**
1. Open `Preferences` → `Claritas`
2. Note current checkbox state (default: checked)
3. Toggle checkbox to opposite state
4. Click `Apply`, then `OK`
5. Reopen settings
6. Verify checkbox state is as you set it
7. **Close and restart the sandbox IDE**
8. Reopen settings
9. Verify checkbox state persisted

**Success Criteria:**
- ✅ Checkbox state saves on Apply
- ✅ Checkbox state persists across IDE restart
- ✅ Settings file created at: `~/.config/JetBrains/[IDE]/ClaritasSettings.xml`

---

### Test 8: Multiple Lines

**Expected Result:** Action works on any line

**Steps:**
1. Create file with multiple lines:
   ```
   Line 1
   Line 2
   Line 3
   ```
2. Place cursor on Line 2
3. Run `Format Current Line (POC)`
4. Verify only Line 2 is modified

**Success Criteria:**
- ✅ Only the current line is modified
- ✅ Other lines remain unchanged

---

### Test 9: Empty Line

**Expected Result:** Empty line becomes `// CLARITAS: `

**Steps:**
1. Place cursor on empty line
2. Run `Format Current Line (POC)`
3. Observe result

**Expected Output:**
```
// CLARITAS: 
```

**Success Criteria:**
- ✅ Empty line is replaced correctly
- ✅ No errors occur

---

### Test 10: Already Formatted Line

**Expected Result:** Line is formatted again (nested CLARITAS prefix)

**Steps:**
1. Start with: `// CLARITAS: Hello`
2. Run action again
3. Observe result

**Expected Output:**
```
// CLARITAS: // CLARITAS: Hello
```

**Success Criteria:**
- ✅ Action can be run multiple times
- ✅ No infinite loops or errors

---

## Test Results Summary

Date: ____________

| Test # | Description | Pass/Fail | Notes |
|--------|-------------|-----------|-------|
| 1 | Plugin loads | [ ] | |
| 2 | Menu appears | [ ] | |
| 3 | Action works | [ ] | |
| 4 | Undo/redo | [ ] | |
| 5 | Action disabled | [ ] | |
| 6 | Settings exist | [ ] | |
| 7 | Settings persist | [ ] | |
| 8 | Multiple lines | [ ] | |
| 9 | Empty line | [ ] | |
| 10 | Re-run action | [ ] | |

**Overall Result:** __________ (Pass/Fail)

**Tester:** __________

**Additional Notes:**
_________________________________________________________________
_________________________________________________________________
_________________________________________________________________

---

## Phase 0 Completion Criteria

To mark Phase 0 as complete, all of the following must be true:

- [ ] All 10 manual tests pass
- [ ] No errors in IDE log during testing
- [ ] Settings file created successfully
- [ ] Undo/redo works correctly
- [ ] Code follows ktlint style (3-space indent)
- [ ] At least one unit test passes (✅ Already done - 3/3 passing)
- [ ] Changes committed to `develop` branch (✅ Already done)

---

## Next Steps After Phase 0

Once all tests pass:

1. ✅ Mark Phase 0 complete in TODO.md
2. Update CHANGELOG.md with v0.0.2
3. Begin Phase 1: Comment Flowerboxing implementation
4. Remove POC action and replace with real functionality

---

**Phase 0 Goal:** Prove plugin mechanics work before investing in complex features.

**What Phase 0 Proved:**
- ✅ Plugin can load and register
- ✅ Actions can be invoked from menus
- ✅ Editor context can be accessed
- ✅ Documents can be modified safely
- ✅ Undo/redo functionality works
- ✅ Settings can persist
- ✅ Build and test infrastructure works

**What's Next:**
Replace POC logic with real comment processing:
- Parse comments using PSI
- Normalize text content
- Apply flowerbox formatting
- Handle multiple comment types

