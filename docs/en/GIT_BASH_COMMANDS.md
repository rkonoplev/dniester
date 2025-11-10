# Git and Bash Commands Reference

This document contains practical Git and Bash commands used during development and troubleshooting of the Phoebe CMS project.

## Table of Contents
- [Git Commands](#git-commands)
- [Bash Commands](#bash-commands)
- [Gradle Commands](#gradle-commands)
- [Docker Commands](#docker-commands)
- [File Operations](#file-operations)
- [Troubleshooting](#troubleshooting)

---

## Git Commands

### Basic Git Operations
```bash
# Check repository status
git status

# Add files to staging
git add .
git add specific-file.txt

# Commit changes
git commit -m "Your commit message"

# Push to remote repository
git push origin main
git push -u origin main  # Set upstream and push

# Pull latest changes
git pull origin main
```

### Handling Merge Conflicts
```bash
# Pull with merge strategy
git pull origin main --no-rebase

# Resolve conflicts by keeping local changes
git checkout --ours .
git add .
git commit -m "Resolve merge conflicts by keeping local changes"

# Resolve conflicts by keeping remote changes
git checkout --theirs .
git add .
git commit -m "Resolve merge conflicts by keeping remote changes"
```

### Force Push (Use with Caution)
```bash
# Force push (overwrites remote)
git push --force origin main

# Safer force push (checks if someone else pushed)
git push --force-with-lease origin main
```

### Git Configuration
```bash
# Increase Git buffers for large repositories
git config http.postBuffer 1048576000  # 1GB
git config http.maxRequestBuffer 1048576000
git config pack.windowMemory 256m
git config core.compression 1

# Set pull behavior
git config pull.rebase false  # Use merge
git config pull.rebase true   # Use rebase

# Auto-setup remote tracking
git config --global push.autoSetupRemote true
```

### Repository Information
```bash
# View commit history
git log --oneline -10

# Check remote repositories
git remote -v

# Check branch tracking
git branch -vv

# Count objects and repository size
git count-objects -vH
du -sh .git
```

---

## Bash Commands

### File and Directory Operations
```bash
# List directory contents with details
ls -la

# Check file modification times
stat -f "%Sm %N" -t "%Y-%m-%d %H:%M:%S" filename

# Find files by size
find . -size +50M -not -path "./.git/*" -not -path "./node_modules/*"

# Check disk usage
du -sh folder_name
du -sh node_modules

# Count files in directory
ls directory_name | wc -l
```

### Text Processing
```bash
# View specific lines from file
sed -n '10,15p' filename.txt
sed -n '12p' filename.txt  # View line 12

# Search for patterns
grep -r "pattern" /path/to/search/
grep -l "pattern" *.java  # List files containing pattern

# Search with exclusions
find /path -name "*.java" -exec grep -l "pattern" {} \;
```

### Process and System Information
```bash
# Check running processes
ps aux | grep java

# Check system resources
top
htop

# Check network connections
netstat -an | grep :8080
lsof -i :8080
```

---

## Gradle Commands

### Basic Gradle Operations
```bash
# Make gradlew executable
chmod +x gradlew

# Clean and build
./gradlew clean build

# Run tests
./gradlew test
./gradlew integrationTest

# Run with specific profile
SPRING_PROFILES_ACTIVE=ci ./gradlew test

# Run with debugging
./gradlew test --debug --stacktrace
```

### Code Quality Checks
```bash
# Run Checkstyle
./gradlew checkstyleMain checkstyleTest checkstyleIntegrationTest

# Run PMD
./gradlew pmdMain pmdTest

# Generate test coverage report
./gradlew jacocoTestReport
```

---

## Docker Commands

### Container Management
```bash
# Start services
docker compose up -d
docker compose up --build

# Stop services
docker compose down

# Check running containers
docker compose ps
docker ps

# View container logs
docker logs container_name
docker compose logs service_name

# Execute commands in container
docker exec -it container_name bash
docker exec -it phoebe-mysql mysql -uroot -proot
```

### Database Operations
```bash
# Connect to MySQL in Docker
docker exec -it phoebe-mysql mysql -uroot -proot

# Wait for MySQL to be ready
timeout 60s bash -c 'until docker exec phoebe-mysql mysqladmin ping -h localhost --silent; do sleep 2; done'

# Check MySQL status
docker exec phoebe-mysql mysqladmin ping -h localhost --silent
```

---

## File Operations

### Search and Replace
```bash
# Search for files containing text
grep -r "search_text" /path/to/directory/

# Find files by name pattern
find . -name "*.yml" -type f

# Search in specific file types
find . -name "*.java" -exec grep -l "pattern" {} \;
```

### File Permissions
```bash
# Make file executable
chmod +x filename

# Change file permissions
chmod 755 filename
chmod 644 filename
```

### Archive Operations
```bash
# Create tar archive
tar -czf archive.tar.gz directory/

# Extract tar archive
tar -xzf archive.tar.gz

# View archive contents
tar -tzf archive.tar.gz
```

---

## Troubleshooting

### Common Issues and Solutions

#### Git Push Failures
```bash
# Issue: HTTP 400 error during push
# Solution: Increase Git buffers
git config http.postBuffer 1048576000

# Issue: Repository rule violations (no merge commits)
# Solution: Use rebase or disable rule in GitHub settings
git rebase -i HEAD~3  # Interactive rebase to squash commits
```

#### Large Repository Issues
```bash
# Check repository size
git count-objects -vH

# Find large files in history
git rev-list --objects --all | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' | awk '/^blob/ {print substr($0,6)}' | sort --numeric-sort --key=2 | tail -10

# Clean up repository (use with caution)
git gc --aggressive --prune=now
```

#### Node.js/npm Issues
```bash
# Check node_modules size
du -sh node_modules

# Clean npm cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

#### Test Failures
```bash
# Run tests with verbose output
./gradlew test --info

# Run specific test class
./gradlew test --tests "ClassName"

# Run tests with specific profile
SPRING_PROFILES_ACTIVE=test ./gradlew test
```

### Environment Debugging
```bash
# Check environment variables
env | grep SPRING
echo $SPRING_PROFILES_ACTIVE

# Check Java version
java -version
./gradlew -version

# Check Docker status
docker --version
docker compose version
```

---

## Best Practices

### Git Workflow
1. Always check status before committing: `git status`
2. Use descriptive commit messages
3. Pull before pushing: `git pull origin main`
4. Use `--force-with-lease` instead of `--force` when needed
5. Keep commits atomic and focused

### File Management
1. Use `.gitignore` for generated files (`node_modules`, `build/`, `.env`)
2. Check file sizes before committing large files
3. Use relative paths in documentation
4. Keep configuration files synchronized between languages

### Development Workflow
1. Test locally before pushing: `./gradlew test`
2. Check code quality: `./gradlew checkstyleMain pmdMain`
3. Use appropriate Spring profiles for different environments
4. Monitor resource usage during development

This reference should help developers quickly find and use the right commands for common development tasks.