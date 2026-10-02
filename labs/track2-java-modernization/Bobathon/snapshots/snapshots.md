# Healthcare Pharmacy Application - Snapshots & Lab Mapping
## Table of Contents

- [Overview](#overview)
- [Snapshots](#snapshots)
  - [Starting Point Snapshots](#starting-point-snapshots)
    - [Snapshot A: java-liberty-replatforming](#snapshot-a-java-liberty-replatforming)
    - [Snapshot B: java-upgrade](#snapshot-b-java-upgrade)
    - [Snapshot C: ui-mod](#snapshot-c-ui-mod)
    - [Snapshot D: unit-test-gen](#snapshot-d-unit-test-gen)
    - [Snapshot TDD: test-driven-development](#snapshot-tdd-test-driven-development)
    - [Snapshot E: security-vulnerabilities](#snapshot-e-security-vulnerabilities)
  - [End Result Snapshots (Reference Implementations)](#end-result-snapshots-reference-implementations)
    - [Snapshot F: unit-test-gen - With Unit Tests](#snapshot-f-unit-test-gen---with-unit-tests)
    - [Snapshot G: security-vulnerabilities-fix](#snapshot-g-security-vulnerabilities-fix)
    - [Snapshot TDD-Generated-API: test-driven-development - Generated Implementation](#snapshot-tdd-generated-api-test-driven-development---generated-implementation)
- [Lab Mapping](#lab-mapping)
  - [Lab 1: Java Liberty Replatforming (TWas → Liberty)](#lab-1-java-liberty-replatforming-twas--liberty)
  - [Lab 2: Java Upgrade (J8 → J21)](#lab-2-java-upgrade-j8--j21)
  - [Lab 3: UI Modernization (Struts → Angular)](#lab-3-ui-modernization-struts--angular)
  - [Lab 4: Unit Test Generation](#lab-4-unit-test-generation)
  - [Lab Alt-4: Test Driven Development (TDD)](#lab-alt-4-test-driven-development-tdd)
  - [Lab 5: Security Vulnerability Remediation](#lab-5-security-vulnerability-remediation)
- [Modernization Journey Flow](#modernization-journey-flow)
- [Quick Reference](#quick-reference)
- [Notes](#notes)

---
This document provides an overview of the application snapshots and their relationship to the modernization labs.

## Overview

The Healthcare Pharmacy Application demonstrates a complete modernization journey from a legacy Java 8 WebSphere/Struts application to a modern Java 21 Liberty/Angular application with security hardening and comprehensive testing.

---
## Snapshots

### Starting Point Snapshots

#### Snapshot A: java-liberty-replatforming
**Description:** Simple pharmacy app running on WebSphere Application Server with Java 8 and Struts framework  
**Technology Stack:**
- WebSphere Application Server (TWas)
- Java 8
- Struts Framework
- Traditional JSP/Servlet architecture

**Use Case:** Starting point for Java Liberty Replatforming lab

---

#### Snapshot B: java-upgrade
**Description:** Simple pharmacy app migrated to Liberty with Java 8 and Struts framework  
**Technology Stack:**
- Liberty Application Server
- Java 8

- Struts Framework
- Traditional JSP/Servlet architecture

**Use Case:** Starting point for Java Upgrade lab

---

#### Snapshot C: ui-mod
**Description:** Simple pharmacy app running on Liberty with Java 21 and Struts framework  
**Technology Stack:**
- Liberty Application Server
- Java 21
- Struts Framework
- Traditional JSP/Servlet architecture

**Use Case:** Starting point for UI Modernization lab

---

#### Snapshot D: unit-test-gen
**Description:** Simple pharmacy app with modernized UI using Angular frontend  
**Technology Stack:**

- Liberty Application Server
- Java 21
- Angular Frontend
- RESTful API architecture

**Use Case:** Starting point for Unit Test Generation lab

---

#### Snapshot TDD: test-driven-development
**Description:** OpenAPI specification for prescription management - starting point for TDD workflow
**Technology Stack:**
- OpenAPI 3.0 Specification
- Prescription API definition
- Test-first development approach

**Use Case:** Starting point for Test Driven Development lab

---

#### Snapshot E: security-vulnerabilities
**Description:** Simple pharmacy app with Angular frontend containing intentional security vulnerabilities  

**Technology Stack:**
- Liberty Application Server
- Java 21
- Angular Frontend
- RESTful API architecture
- **Contains baked-in security vulnerabilities for learning purposes**

**Use Case:** Starting point for Security Vulnerability Remediation lab

---

### End Result Snapshots (Reference Implementations)

#### Snapshot F: unit-test-gen - With Unit Tests
**Description:** Fully modernized pharmacy app with comprehensive unit test coverage  
**Technology Stack:**
- Liberty Application Server
- Java 21
- Angular Frontend
- RESTful API architecture
- Comprehensive unit tests (JUnit, Mockito, Jasmine/Karma)

**Use Case:** Reference implementation showing completed Unit Test Generation

---
#### Snapshot G: security-vulnerabilities-fix
**Description:** Fully modernized pharmacy app with all security vulnerabilities remediated  
**Technology Stack:**
- Liberty Application Server
- Java 21
- Angular Frontend
- RESTful API architecture
- Security best practices implemented

**Use Case:** Reference implementation showing completed Security Vulnerability Remediation

---

#### Snapshot TDD-Generated-API: test-driven-development - Generated Implementation
**Description:** Complete PrescriptionResource implementation generated through TDD methodology
**Technology Stack:**
- Java 21
- JAX-RS REST API
- JUnit 5 test suite
- Generated from OpenAPI specification
- Test-driven implementation

**Use Case:** Reference implementation showing completed Test Driven Development lab


[↑ Back to Top](#)

---

## Lab Mapping
### Lab 1: Java Liberty Replatforming (TWas → Liberty)
**Starting Snapshot:** Snapshot A - java-liberty-replatforming  
**Ending Snapshot:** Snapshot B - java-upgrade  
**Objective:** Migrate application from WebSphere Application Server to Liberty while maintaining Java 8 and Struts

**Key Activities:**
- Convert WebSphere-specific configurations to Liberty
- Update deployment descriptors
- Migrate server configurations
- Validate application functionality on Liberty

---

### Lab 2: Java Upgrade (J8 → J21)
**Starting Snapshot:** Snapshot B - java-upgrade  
**Ending Snapshot:** Snapshot C - ui-mod  
**Objective:** Upgrade Java runtime from version 8 to version 21

**Key Activities:**
- Update Java version in build configurations
- Resolve deprecated API usage
- Update dependencies for Java 21 compatibility
- Test application with new Java runtime

---

### Lab 3: UI Modernization (Struts → Angular)
**Starting Snapshot:** Snapshot C - ui-mod  
**Ending Snapshot:** Snapshot D - unit-test-gen  
**Objective:** Replace legacy Struts UI with modern Angular frontend
**Key Activities:**
- Design and implement RESTful APIs
- Build Angular frontend components
- Migrate business logic to backend services
- Implement modern UI/UX patterns

---
### Lab 4: Unit Test Generation
**Starting Snapshot:** Snapshot D - unit-test-gen
**Ending Snapshot:** Snapshot F - unit-tests (With Unit Tests)
**Objective:** Generate comprehensive unit test coverage for the modernized application

**Key Activities:**
- Generate backend unit tests using Bob (JUnit, Mockito)
- Create repository and API resource tests
- Achieve high code coverage (>80%)
- Implement test automation and best practices

---

### Lab Alt-4: Test Driven Development (TDD)
**Starting Snapshot:** Snapshot TDD - test-driven-development (snapTDD)
**Ending Snapshot:** Snapshot TDD-Generated-API (snapTDD-generated-api)
**Objective:** Implement API-first development using Test Driven Development methodology
**Key Activities:**
- Generate unit tests from OpenAPI specification
- Implement PrescriptionResource driven by tests
- Follow Red-Green-Refactor TDD cycle
- Compare TDD vs traditional development approaches

---

### Lab 5: Security Vulnerability Remediation
**Starting Snapshot:** Snapshot E - security-vulnerabilities  
**Ending Snapshot:** Snapshot G - security-vulnerabilities-fix  
**Objective:** Identify and remediate security vulnerabilities in the application
**Key Activities:**
- Scan for security vulnerabilities
- Implement input validation and sanitization
- Apply security best practices
- Verify vulnerability remediation


[↑ Back to Top](#)

---

## Modernization Journey Flow

```
┌──────────────────────────────────────────────────────────────────────┐
│                     MODERNIZATION JOURNEY                            │
└──────────────────────────────────────────────────────────────────────┘

Snapshot A                    Snapshot B                    Snapshot C
┌──────────┐    Lab 1        ┌──────────┐    Lab 2        ┌───────────┐
│  TWas J8 │ ─────────────>  │Liberty J8│ ─────────────>  │Liberty J21│
│  Struts  │  Replatform     │  Struts  │   Upgrade       │  Struts   │
└──────────┘                 └──────────┘                 └───────────┘
                                                                │
                                                                │ Lab 3
                                                                │ UI Mod
                                                                ▼
                                                         ┌──────┴──────┐
                                                         │             │
                                                    ─────┘             └─────
                                                    │                       │
                                                    ▼                       ▼
 Snapshot TDD                                     Snapshot E            Snapshot D
┌───────────┐                                    ┌───────────┐         ┌───────────┐
│  OpenAPI  │                                    │Liberty J21│         │Liberty J21│
│   Spec    │                                    │  Angular  │         │  Angular  │
│           │                                    │  + Bugs   │         │           │
└───────────┘                                    └───────────┘         └───────────┘
     │                                                │                      │
     │ Lab Alt-4                               Lab 5  │                      │  Lab 4
     │ TDD                                   Security │                      │  Unit Tests
     ▼                                                ▼                      ▼
┌───────────┐                                    ┌───────────┐         ┌───────────┐
│Generated  │                                    │Liberty J21│         │Liberty J21│
│Prescription                                    │  Angular  │         │  Angular  │
│Resource   │                                    │ Hardened  │         │ + Tests   │
└───────────┘                                    └───────────┘         └───────────┘

  Snapshot                                         Snapshot G           Snapshot F
TDD-Generated-
    API
```

---

## Quick Reference

| Snapshot | Description | Lab Starting Point | Lab Ending Point |
|----------|-------------|-------------------|------------------|
| A | TWas J8 Struts | Lab 1 | - |
| B | Liberty J8 Struts | Lab 2 | Lab 1 |
| C | Liberty J21 Struts | Lab 3 | Lab 2 |
| D | Liberty J21 Angular | Lab 4 | Lab 3 |
| TDD | OpenAPI Spec | Lab Alt-4 | - |
| E | Liberty J21 Angular + Bugs | Lab 5 | - |
| F | Liberty J21 Angular + Tests | - | Lab 4 |
| G | Liberty J21 Angular (Hardened) | - | Lab 5 |
| TDD-Generated-API | Generated PrescriptionResource | - | Lab Alt-4 |

---

## Notes

- **Snapshots A-G and TDD** serve as starting points for various labs
- **Snapshots F, G, and TDD-Generated-API** are reference implementations showing completed modernization outcomes
- Labs 1-3 follow a sequential modernization path
- Lab 4 (Unit Test Generation) and Lab 5 (Security) branch from Snapshot D
- Lab Alt-4 (TDD) is a standalone lab using the OpenAPI specification
- All snapshots are self-contained and can be used independently for their respective labs


[↑ Back to Top](#)
