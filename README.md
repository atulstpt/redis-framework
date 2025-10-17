# Redis Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A robust Redis Framework for Java applications that simplifies CRUD (Create, Read, Update, Delete) operations with Redis
databases.

## Features

- Simple and intuitive API for Redis operations
- Support for basic CRUD operations
- Connection pooling
- Type-safe data handling
- Configurable serialization/deserialization
- Automatic connection management
- Support for Redis transactions
- Comprehensive error handling

## Installation

### Maven

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>redis-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

Basic usage example:

```java
import com.example.redis.RedisClient;

public class Main {
    public static void main(String[] args) {
        RedisClient redis = new RedisClient("localhost", 6379);
        redis.set("key", "value");
        String value = redis.get("key");
        System.out.println("Retrieved value: " + value);
        redis.close();
    }
}
```

## Continuous Integration (CI)

This repository includes a GitHub Actions workflow that runs build, tests, SonarQube analysis, and CodeQL scanning.

Files added:

- `.github/workflows/ci.yml` - runs on push, PR, schedule and supports manual `workflow_dispatch`. It builds with Maven, runs tests, uploads test reports, runs SonarQube analysis and runs CodeQL.
- `.github/dependabot.yml` - enables Dependabot updates for Maven (weekly) and GitHub Actions (daily).

Required repository secrets (set these in Settings -> Secrets -> Actions):

- `SONAR_TOKEN` - a SonarQube or SonarCloud token with analysis permissions.
- `SONAR_HOST_URL` - the SonarQube/Cloud host URL (e.g. https://sonarcloud.io or your SonarQube instance URL).
- `SONAR_ORGANIZATION` - (optional for SonarCloud) your organization slug.
- `SONAR_PROJECT_KEY` - a unique key for this project in Sonar.

How the workflow behaves:

- `Build & Test` job: checks out the repo, sets up the JDK, caches Maven repository, runs `mvn clean verify`, uploads surefire reports and then runs Sonar analysis using the provided Sonar secrets.
- `CodeQL` job: initializes CodeQL, autobuilds, and analyzes the repo for security issues.

Triggering the workflow

- Manually via GitHub UI: Go to the Actions tab, choose the `CI` workflow and click "Run workflow".

- From the command line using the GitHub CLI (gh):

```bash
# Run the workflow dispatch on the default branch
gh workflow run ci.yml --repo OWNER/REPO
```

Replace OWNER/REPO with your repository path. The `gh` CLI must be authenticated and installed.

Local Sonar scanning (optional)

If you want to run Sonar analysis locally (useful for quick checks), you can use the Maven Sonar goal — you must still provide the `SONAR_TOKEN` and `SONAR_HOST_URL` as environment variables:

```bash
# Linux / macOS example
export SONAR_TOKEN=your_token_here
export SONAR_HOST_URL=https://sonarcloud.io
mvn -B sonar:sonar -Dsonar.login="$SONAR_TOKEN" -Dsonar.host.url="$SONAR_HOST_URL"

# Windows (cmd.exe)
set SONAR_TOKEN=your_token_here
set SONAR_HOST_URL=https://sonarcloud.io
mvn -B sonar:sonar -Dsonar.login="%SONAR_TOKEN%" -Dsonar.host.url="%SONAR_HOST_URL%"
```

Notes and next steps

- If you use SonarCloud, set `SONAR_HOST_URL` to `https://sonarcloud.io` and provide `SONAR_ORGANIZATION` and `SONAR_PROJECT_KEY` as configured in SonarCloud.
- The workflow uses Java 21 by default; change the `java-version` matrix in `.github/workflows/ci.yml` if your project targets a different JDK.
