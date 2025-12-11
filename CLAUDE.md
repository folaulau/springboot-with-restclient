# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.0 application demonstrating REST client patterns using the new `spring-boot-starter-webmvc`. The project uses Java 21 and Maven.

## Build Commands

```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SpringbootWithRestclientApplicationTests

# Run a single test method
./mvnw test -Dtest=SpringbootWithRestclientApplicationTests#contextLoads
```

## Architecture

- **Main Application**: `SpringbootWithRestclientApplication` - Entry point with a `CommandLineRunner` that logs JVM and environment info at startup
- **Package Structure**: `com.folautech.restclient` - Base package for all application code
- **Configuration**: `application.properties` in `src/main/resources`

## Key Dependencies

- **Lombok**: Used for boilerplate reduction (getters, setters, constructors)
- **Spring Boot DevTools**: Enabled for hot reloading during development
- **spring-boot-starter-webmvc-test**: For testing web layer components
