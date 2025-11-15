# Git and Bash Commands Reference

This document contains practical Git and Bash commands used during development and troubleshooting in the Phoebe CMS project.

## Table of Contents
- [Git Commands](#git-commands)
- [Bash Commands](#bash-commands)
- [Gradle Commands](#gradle-commands)
- [Docker Commands](#docker-commands)
- [File Operations](#file-operations)
- [Reverting Changes and Recovery](#reverting-changes-and-recovery)
- [Managing Stuck Testcontainers and Docker](#managing-stuck-testcontainers-and-docker)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

---

## Git Commands

### Basic Git Operations
```bash
# Check repository status
git status

# Add files to the index
git add .
git add specific-file.txt

# Commit changes
git commit -m "Your commit message"

# Push to remote repository
git push origin main
git push -u origin main  # Set upstream and push

# Get latest changes
git pull origin main
```

### Handling Merge Conflicts
```bash
# Get changes with merge strategy
git pull origin main --no-rebase

# Resolve conflicts, keeping local changes
git checkout --ours .
git add .
git commit -m "Resolve merge conflicts by keeping local changes"

# Resolve conflicts, keeping remote changes
git checkout --theirs .
git add .
git commit -m "Resolve merge conflicts by keeping remote changes"
```

### Force Push (use with caution)
```bash
# Force push (overwrites remote repository)
git push --force origin main

# Safer force push (checks if someone else has pushed)
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

# Automatic setup of remote tracking branches
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

# Check file modification time
stat -f "%Sm %N" -t "%Y-%m-%d %H:%M:%S" filename

# Find files by size
find . -size +50M -not -path "./.git/*" -not -path "./node_modules/*"

# Check disk usage
du -sh folder_name
du -sh node_modules

# Count files in a directory
ls directory_name | wc -l
```

### Text Processing
```bash
# View specific lines from a file
sed -n '10,15p' filename.txt
sed -n '12p' filename.txt  # View line 12

# Search by patterns
grep -r "pattern" /path/to/search/
grep -l "pattern" *.java  # List files containing the pattern

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

### Useful Command Chains
```bash
# Check file, make executable, and run tests
ls -la gradlew && chmod +x gradlew && ./gradlew integrationTest

# Command breakdown:
# - `ls -la gradlew` - checks for the existence of gradlew and shows its permissions
# - `&&` - logical "AND" operator (executes the next command only if the previous one is successful)
# - `chmod +x gradlew` - makes the gradlew file executable (adds execute permissions)
# - `./gradlew integrationTest` - runs integration tests via Gradle Wrapper

# Why this is needed:
# - Sometimes after cloning a repository, the gradlew file loses its execute permissions
# - The command automatically checks and fixes this
# - If any step fails, execution stops

# Other useful command chains
# Stop containers, clean up, and restart
docker compose down && docker system prune -f && docker compose up -d

# Check status and connect to the database
docker ps && docker exec -it phoebe-mysql mysql -uroot -proot

# Build the application and run all tests
./gradlew clean && ./gradlew build && ./gradlew test integrationTest
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

# Run with a specific profile
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

# Execute commands in a container
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
# Find files containing text
grep -r "search_text" /path/to/directory/

# Find files by name pattern
find . -name "*.yml" -type f

# Find in specific file types
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

## Reverting Changes and Recovery

### Revert all edits to the last push
```bash
# Revert all uncommitted changes
git checkout .
git clean -fd

# Revert to the state of the last commit (discards all local changes)
git reset --hard HEAD

# Revert to the state of the remote repository
git fetch origin
git reset --hard origin/main

# Revert a specific file
git checkout HEAD -- filename.txt

# Revert the last commit (keeping changes in the working directory)
git reset --soft HEAD~1

# Revert the last commit (discarding all changes)
git reset --hard HEAD~1
```

### Editing the last commit with vim
```bash
# Change the last commit message
git commit --amend

# Vim commands for editing:
# i          - enter insert mode
# Esc        - exit insert mode
# :w         - save file
# :q         - quit vim
# :wq        - save and quit
# :q!        - quit without saving
# dd         - delete line
# x          - delete character
# u          - undo last action
```

### Resolving Version Conflicts
```bash
# When the remote version is newer than local
# 1. Fetch changes from the remote repository
git fetch origin

# 2. View differences
git log HEAD..origin/main --oneline

# 3. Merge changes (may cause conflicts)
git merge origin/main

# 4. If there are conflicts, resolve them:
# - Edit files with conflicts
# - Remove conflict markers (<<<<<<, ======, >>>>>>)
# - Add resolved files
git add .
git commit -m "Resolve merge conflicts"

# Alternative: force take remote version
git reset --hard origin/main

# Alternative: rebase instead of merge
git rebase origin/main
```

---

## Managing Stuck Testcontainers and Docker

### Diagnosing Stuck Processes
```bash
# Check running Docker containers
docker ps
docker ps -a  # Including stopped ones

# Check stuck Java processes
ps aux | grep java | grep -v grep

# Check Gradle processes
ps aux | grep gradle | grep -v grep

# Check test processes
ps aux | grep "Test Executor" | grep -v grep
```

### Stopping Stuck Testcontainers
```bash
# Stop all running containers
docker stop $(docker ps -q)

# Stop specific containers
docker stop container_id_1 container_id_2

# Force kill containers
docker kill $(docker ps -q)

# Remove stopped containers
docker rm $(docker ps -aq)

# Stop and remove Testcontainers
docker stop $(docker ps -q --filter "label=org.testcontainers")
docker rm $(docker ps -aq --filter "label=org.testcontainers")

# Stop Ryuk container (Testcontainers cleanup)
docker stop $(docker ps -q --filter "name=testcontainers-ryuk")
```

### Stopping Stuck Java Processes
```bash
# Find process PID
ps aux | grep "Test Executor" | grep -v grep
ps aux | grep "gradlew" | grep -v grep

# Stop process by PID (replace XXXX with actual PID)
kill -9 XXXX

# Stop all Gradle processes
pkill -f gradle

# Stop all Java processes (CAUTION!)
# pkill -f java

# Stop Gradle daemon
./gradlew --stop
```

### Cleaning Docker Resources
```bash
# Remove unused images
docker image prune -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f

# Full Docker cleanup (CAUTION!)
docker system prune -af --volumes

# Check Docker disk usage
docker system df
```

### Diagnosing Test Problems
```bash
# Check Docker container logs
docker logs container_name

# Check real-time logs
docker logs -f container_name

# Check MySQL health status
docker exec phoebe-mysql mysqladmin ping -h localhost --silent

# Check database connection
docker exec -it phoebe-mysql mysql -uroot -proot -e "SHOW DATABASES;"

# Check ports
netstat -an | grep :3306
lsof -i :3306
```

### Sequence of Actions for Stuck Tests
```bash
# 1. Stop test execution (Ctrl+C in terminal)

# 2. Find and kill stuck processes
ps aux | grep java | grep -v grep
kill -9 PROCESS_PID

# 3. Stop Gradle daemon
./gradlew --stop

# 4. Stop all Docker containers
docker stop $(docker ps -q)

# 5. Remove stopped containers
docker rm $(docker ps -aq --filter "label=org.testcontainers")

# 6. Verify everything is clean
docker ps
ps aux | grep java | grep -v grep

# 7. Restart tests
./gradlew integrationTest --no-daemon
```

---

## Troubleshooting

### Common Problems and Solutions

#### Git Push Errors
```bash
# Problem: HTTP 400 error on push
# Solution: Increase Git buffers
git config http.postBuffer 1048576000

# Problem: Repository rule violations (e.g., disallowing merge commits)
# Solution: Use rebase or disable the rule in GitHub settings
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

# Clear npm cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

#### Failing Tests
```bash
# Run tests with detailed output
./gradlew test --info

# Run a specific test class
./gradlew test --tests "ClassName"

# Run tests with a specific profile
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
3. Pull changes before pushing: `git pull origin main`
4. Use `--force-with-lease` instead of `--force` when necessary
5. Make commits atomic and focused

### File Management
1. Use `.gitignore` for generated files (`node_modules`, `build/`, `.env`)
2. Check file sizes before committing large files
3. Use relative paths in documentation
4. Maintain synchronization of configuration files between languages

### Development Workflow
1. Test locally before pushing: `./gradlew test`
2. Check code quality: `./gradlew checkstyleMain pmdMain`
3. Use appropriate Spring profiles for different environments
4. Monitor resource usage during development

This reference will help developers quickly find and use the correct commands for common development tasks.