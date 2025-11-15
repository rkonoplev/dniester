# Code Style Setup Guide

## What's Configured

### 1. IntelliJ IDEA Code Style
- **Files**: `.idea/codeStyles/Project.xml`, `.idea/codeStyles/codeStyleConfig.xml`
- **Settings**: 120 character line length, automatic wrapping on typing
- **Application**: Automatically applied when opening the project

### 2. Actions on Save
- **File**: `.idea/actionsOnSave.xml`
- **Actions**: Automatic formatting, import optimization, code rearrangement
- **Trigger**: On file save (Ctrl+S / Cmd+S)

### 3. Checkstyle
- **File**: `backend/config/checkstyle/checkstyle.xml`
- **Purpose**: Code quality checks for adherence to coding standards.
- **Description**: This file contains a set of rules that the project's Java code should comply with.
- **Usage**: `./gradlew checkstyleMain checkstyleTest`

### 4. PMD
- **File**: `backend/config/pmd/pmd.xml`
- **Purpose**: Static code analysis to identify potential bugs, performance issues, and duplicate code.
- **Usage**: `./gradlew pmdMain pmdTest`

## How to Enable Automatic Formatting

### In IntelliJ IDEA:

1. **Verify project settings**:
   - `File → Settings → Editor → Code Style → Scheme = "Project"`
   - Should be set automatically

2. **Enable Actions on Save** (if not working automatically):
   - `File → Settings → Tools → Actions on Save`
   - Enable:
     - ✅ Reformat code
     - ✅ Optimize imports  
     - ✅ Rearrange code (reorders class members, such as fields, constructors, methods, in a specific order).
   - File path patterns: `**/*.java`

3. **Check automatic line wrapping**:
   - `File → Settings → Editor → Code Style → Java → Wrapping and Braces`
   - "Wrap on typing" should be enabled

## Usage

### Automatic formatting:
- **On save**: Code automatically formats on Ctrl+S (Cmd+S)
- **Manual**: Ctrl+Alt+L (Cmd+Alt+L) to format code

### Code quality checks:
```bash
# Check main code with Checkstyle
./gradlew checkstyleMain

# Check tests with Checkstyle
./gradlew checkstyleTest

# Check main code with PMD
./gradlew pmdMain

# Check tests with PMD
./gradlew pmdTest

# Run all code quality checks (including Checkstyle, PMD, and others)
./gradlew check
```
*   **Note**: The `check` task in Gradle aggregates all code quality check tasks, such as `checkstyle` and `pmd`, running them sequentially.

## Responsibility Separation

| Tool | Purpose | When it works |
|------|---------|---------------|
| **IntelliJ IDEA** | Code formatting (line length, indentation, wrapping) | On save / Ctrl+Alt+L |
| **Checkstyle** | Code quality checks for adherence to coding standards | During project build / `./gradlew check` |
| **PMD** | Static code analysis to identify potential bugs and issues | During project build / `./gradlew check` |

## What's Fixed

1. ✅ **Removed duplication**: Checkstyle no longer checks line length (IntelliJ handles this)
2. ✅ **Automatic formatting**: Configured via `actionsOnSave.xml`
3. ✅ **Wrap on typing**: Enabled `WRAP_ON_TYPING` in project settings
4. ✅ **Git tracking**: All configuration files added to repository

## Troubleshooting

### If automatic formatting doesn't work:
1. Restart IntelliJ IDEA
2. Check `File → Settings → Tools → Actions on Save`
3. Ensure code scheme = "Project"

### If Checkstyle or PMD shows many errors:
- This is normal! These tools check code quality and potential issues, not just formatting.
- Fix errors gradually or configure more lenient rules in their configuration files (`checkstyle.xml`, `pmd.xml`).
