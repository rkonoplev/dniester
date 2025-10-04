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
- **Purpose**: Code quality checks (not formatting!)
- **Usage**: `./gradlew checkstyleMain checkstyleTest`

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
     - ✅ Rearrange code
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
# Check main code
./gradlew checkstyleMain

# Check tests  
./gradlew checkstyleTest

# Check everything
./gradlew check
```

## Responsibility Separation

| Tool | Purpose | When it works |
|------|---------|---------------|
| **IntelliJ IDEA** | Code formatting (line length, indentation, wrapping) | On save / Ctrl+Alt+L |
| **Checkstyle** | Code quality checks (unused imports, naming style) | During project build |

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

### If Checkstyle shows many errors:
- This is normal! Checkstyle checks quality, not formatting
- Fix errors gradually or configure more lenient rules