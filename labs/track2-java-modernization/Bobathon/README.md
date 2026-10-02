# Bobathon - Java Modernization Labs

## 🚀 New to This Repository?

**Start here:** [GETTING_STARTED.md](GETTING_STARTED.md) - Complete guide to get up and running in 10 minutes!

This repository contains **8 hands-on labs** for learning AI-assisted application modernization:
- **6 Java Modernization Labs** - Transform legacy Java applications step-by-step
- **2 Figma Integration Labs** - Learn to integrate IBM Bob with Figma for UI design

**Links to Demos:**
- Java Modernization Demos: [https://ibm.ent.box.com/folder/365179537734](https://ibm.ent.box.com/folder/365179537734)
- Figma Editor Access Lab: [https://ibm.ent.box.com/folder/373365438389](https://ibm.ent.box.com/folder/373365438389)
- Figma NonEditor Access Lab: [https://ibm.ent.box.com/folder/373364692035](https://ibm.ent.box.com/folder/373364692035)
---

## 📑 Table of Contents

- [New to This Repository?](#-new-to-this-repository)
- [What is IBM Bob?](#what-is-ibm-bob)
- [About This Workshop](#about-this-workshop)
  - [The Modernization Journey](#the-modernization-journey)
- [Workshop Structure](#workshop-structure)
  - [Lab Overview](#lab-overview)
- [Lab Details](#lab-details)
  - [Java Modernization Labs](#java-modernization-labs)
    - [Lab 1: Java Liberty Replatforming](#lab-1-java-liberty-replatforming)
    - [Lab 2: Java Upgrade (Java 8 to Java 21)](#lab-2-java-upgrade-java-8-to-java-21)
    - [Lab 3: UI Modernization (Struts to Angular)](#lab-3-ui-modernization-struts-to-angular)
    - [Lab 4: Unit Test Generation](#lab-4-unit-test-generation)
    - [Lab Alt-4: Test Driven Development (TDD)](#lab-alt-4-test-driven-development-tdd)
    - [Lab 5: Security Vulnerability Remediation](#lab-5-security-vulnerability-remediation)
  - [Figma Integration Labs](#figma-integration-labs)
    - [Figma-Editor: Integration with Editor Access](#figma-editor-integration-with-editor-access)
    - [Figma-NonEditor: Integration without Editor Access](#figma-noneditor-integration-without-editor-access)
- [Templates & Resources](#templates--resources)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [How to Use This Repository](#how-to-use-this-repository)
  - [Understanding Snapshots](#understanding-snapshots)
  - [Working Through Labs](#working-through-labs)
  - [Starting a Lab](#starting-a-lab)
- [Lab Progression Summary](#lab-progression-summary)
- [Learning Outcomes](#learning-outcomes)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Getting Help](#getting-help)
- [Additional Resources](#additional-resources)
  - [Documentation](#documentation)
  - [Related Materials](#related-materials)
- [Repository Structure](#repository-structure)
- [Quick Start Guide](#quick-start-guide)
- [Tips for Success](#tips-for-success)
- [Contributing](#contributing)

---
Welcome to the Bobathon Java Modernization workshop! This repository contains hands-on labs that demonstrate how to use **IBM Bob AI Copilot** to modernize legacy Java applications through a progressive modernization journey.

## What is IBM Bob?
IBM Bob is an AI-powered coding assistant that integrates seamlessly with VS Code. Bob helps developers:
- Write code faster with intelligent suggestions
- Identify and fix security vulnerabilities
- Improve code quality and maintainability
- Implement security best practices
- Refactor and optimize existing codebases

## About This Workshop

This workshop guides you through modernizing a **Simple Pharmacy Management System** - a web-based application for managing pharmacy operations including prescriptions, orders, medicine inventory, and dashboards. You'll use IBM Bob's AI-powered capabilities to transform this legacy application step-by-step.

### The Modernization Journey

```
┌─────────────────────────┐
│   Starting Point        │
│ Traditional WebSphere   │
│   Java 8 + Struts       │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│        Lab 1            │
│   Migrate to Liberty    │
│   ─────────────────     │
│   Result: Liberty       │
│   Java 8 + Struts       │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│        Lab 2            │
│   Upgrade to Java 21    │
│   ─────────────────     │
│   Result: Liberty       │
│   Java 21 + Struts      │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│        Lab 3            │
│   Modernize Frontend    │
│   ─────────────────     │
│   Result: Liberty       │
│   Java 21 + Angular     │
└───────────┬─────────────┘
            │
            ├──────────────────────────────────┐
            │                                  │
            ▼                                  ▼
┌─────────────────────────┐      ┌─────────────────────────┐
│        Lab 4            │      │      Lab Alt-4          │
│   Unit Test Generation  │      │  Test Driven Dev (TDD)  │
│   ─────────────────     │      │   ─────────────────     │
│   Result: Tested App    │      │   Result: Generated     │
│   with Coverage         │      │   PrescriptionResource  │
└───────────┬─────────────┘      └─────────────────────────┘
            │                         (Standalone Lab)
            ▼
┌─────────────────────────┐
│        Lab 5            │
│   Security Hardening    │
│   ─────────────────     │
│   Result: Secure App    │
│   with Remediation      │
└─────────────────────────┘
```

**Progressive Modernization Path:**
- **Starting Point** → Traditional WebSphere, Java 8, Struts
- **Lab 1 Result** → Liberty Runtime, Java 8, Struts ✅
- **Lab 2 Result** → Liberty Runtime, Java 21, Struts ✅
- **Lab 3 Result** → Liberty Runtime, Java 21, Angular ✅
- **Lab 4 Result** → Fully tested application ✅
- **Lab Alt-4 Result** → TDD-generated PrescriptionResource ✅ (Standalone)
- **Lab 5 Result** → Security-hardened application ✅

## Workshop Structure

This workshop contains **8 hands-on labs** covering Java modernization and Figma integration. Each lab is self-contained with its own starting point. The Java labs (1-5) can follow a sequential modernization path or be completed independently.

### Lab Overview

#### Java Modernization Labs

| Lab | Focus Area | Starting Snapshot | Tech Stack | Duration | Status |
|-----|-----------|-------------------|------------|----------|--------|
| **Lab 1** | Application Server Migration | `snapA-java-liberty-replatforming` | TWas → Liberty, Java 8, Struts | 45-60 min | ✅ Available |
| **Lab 2** | Java Version Upgrade | `snapB-java-upgrade` | Liberty, Java 8 → Java 21, Struts | 45-60 min | ✅ Available |
| **Lab 3** | Frontend Modernization | `snapC-ui-mod` | Liberty, Java 21, Struts → Angular | 60-90 min | ✅ Available |
| **Lab 4** | Unit Test Generation | `snapD-unit-test-gen` | Liberty, Java 21, Angular | 60-75 min | ✅ Available |
| **Lab Alt-4** | Test Driven Development (TDD) | `snapTDD` | OpenAPI → Java 21 REST API | 45-60 min | ✅ Available |
| **Lab 5** | Security Vulnerability Remediation | `snapE-security-vulnerabilities` | Liberty, Java 21, Angular | 60-75 min | ✅ Available |

#### Figma Integration Labs

| Lab | Focus Area | Requirements | Duration | Status |
|-----|-----------|--------------|----------|--------|
| **Figma-Editor** | Figma Integration with Editor Access | Figma account + editor permissions | 30-45 min | ✅ Available |
| **Figma-NonEditor** | Figma Integration without Editor Access | Figma account + read access | 30-45 min | ✅ Available |


[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Lab Details

### Lab 1: Java Liberty Replatforming
**Migrate from Traditional WebSphere to Liberty Runtime**

**Objective:** Learn how to use IBM Bob's Java Modernization mode to migrate a legacy application from Traditional WebSphere Application Server (TWas) to the modern, lightweight Liberty runtime.

**What You'll Learn:**
- Analyzing legacy WebSphere applications
- Using IBM Bob's Java Modernization workflow
- Executing automated Liberty replatforming transformations
- Validating migrated applications

**Starting Point:** `labs/lab1-java-liberty-replatforming/snapA-java-liberty-replatforming/`

**Tech Stack:**
- **Before:** Traditional WebSphere 9, Java 8, Struts
- **After:** Liberty Runtime, Java 8, Struts

**📘 [View Lab 1 Guide](labs/lab1-java-liberty-replatforming/LAB1-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Lab 2: Java Upgrade (Java 8 to Java 21)
**Upgrade Your Application to Modern Java**

**Objective:** Use IBM Bob to upgrade a Java 8 application to Java 21, taking advantage of modern language features, performance improvements, and long-term support.

**What You'll Learn:**
- Analyzing Java 8 codebases for upgrade readiness
- Using Bob's Java Modernization workflow for version upgrades
- Handling namespace changes (Java EE → Jakarta EE)
- Updating dependencies and configurations for Java 21
- Validating Java 21 compatibility

**Starting Point:** `labs/lab2-java-upgrade/snapB-java-upgrade/`

**Tech Stack:**
- **Before:** Liberty Runtime, Java 8, Struts
- **After:** Liberty Runtime, Java 21, Struts

**📘 [View Lab 2 Guide](labs/lab2-java-upgrade/LAB2-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Lab 3: UI Modernization (Struts to Angular)
**Transform Your Frontend with Modern Frameworks**

**Objective:** Migrate the application's frontend from legacy Struts to modern Angular, creating a responsive single-page application (SPA) with improved user experience.

**What You'll Learn:**
- Analyzing Struts applications for frontend migration
- Using Bob's Advanced mode for complex framework migrations
- Creating Angular standalone components
- Implementing RESTful API integration
- Building modern, responsive user interfaces

**Starting Point:** `labs/lab3-ui-modernization/snapC-ui-mod/`

**Tech Stack:**
- **Before:** Liberty Runtime, Java 21, Struts (JSP views)
- **After:** Liberty Runtime, Java 21, Angular 19 (REST API + SPA)

**📘 [View Lab 3 Guide](labs/lab3-ui-modernization/LAB3-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Lab 4: Unit Test Generation
**Generate Comprehensive Unit Tests with IBM Bob**

**Objective:** Use IBM Bob to automatically generate comprehensive unit tests for your modernized application, improving code quality and maintainability.

**What You'll Learn:**
- Analyzing code coverage gaps
- Using Bob to generate unit tests for Java backend
- Creating repository and API resource tests
- Implementing test best practices (JUnit, Mockito)
- Achieving high code coverage (>80%)
- Validating test quality and effectiveness

**Starting Point:** `snapshots/start-states/snapD-unit-test-gen/`

**Tech Stack:**
- **Before:** Liberty Runtime, Java 21, Angular (no tests)
- **After:** Liberty Runtime, Java 21, Angular + comprehensive unit tests

**📘 [View Lab 4 Guide](labs/lab4-unit-test-generation/LAB4-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Lab Alt-4: Test Driven Development (TDD)
**API-First Development Using TDD Methodology**

**Objective:** Implement API-first development using Test Driven Development methodology, generating implementation from tests derived from an OpenAPI specification.

**What You'll Learn:**
- Generating unit tests from OpenAPI specifications
- Following the Red-Green-Refactor TDD cycle
- Implementing PrescriptionResource driven by tests
- Comparing TDD vs traditional development approaches
- Understanding benefits of test-first development

**Starting Point:** `labs/alt-lab4-test-driven-development/snapTDD/`

**Tech Stack:**
- **Input:** OpenAPI 3.0 specification for prescription management
- **Output:** Java 21 JAX-RS REST API with JUnit 5 tests

**📘 [View Lab Alt-4 (TDD) Guide](labs/alt-lab4-test-driven-development/LAB-TDD-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Lab 5: Security Vulnerability Remediation
**Identify and Fix Security Vulnerabilities**

**Objective:** Learn how to use IBM Bob to identify and remediate security vulnerabilities in your modernized application.

**What You'll Learn:**
- Scanning applications for security vulnerabilities
- Understanding common security issues (SQL injection, XSS, etc.)
- Using Bob to implement security best practices
- Remediating identified vulnerabilities
- Implementing input validation and sanitization
- Validating security improvements

**Starting Point:** `snapshots/start-states/snapE-security-vulnerabilities/`

**Tech Stack:**
- **Before:** Liberty Runtime, Java 21, Angular (with security vulnerabilities)
- **After:** Liberty Runtime, Java 21, Angular (security-hardened)

**📘 [View Lab 5 Guide](labs/lab5-security-vulnerability-remediation/LAB5-GUIDE.md)**

[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Figma Integration Labs

### Figma-Editor: Integration with Editor Access
**Learn Figma Integration with Full Editor Permissions**

**Objective:** Learn how to integrate IBM Bob with Figma when you have editor access to design files. Use IBM Bob to read, analyze, and modify Figma designs programmatically.

**What You'll Learn:**
- Connecting IBM Bob to Figma with editor permissions
- Using IBM Bob to interact with Figma designs
- Understanding the Model Context Protocol (MCP) for Figma
- Automating design tasks with AI assistance
- Making programmatic changes to Figma files

**Prerequisites:**
- IBM Bob installed
- Figma account (free or paid)
- Editor access to a Figma design file

**Duration:** 30-45 minutes

**📘 [View Figma-Editor Guide](../Figma-Editor/README.md)**

**Lab Files:** `Figma-Editor/` folder contains PDF guide and resources

[↑ Back to Top](#bobathon---java-modernization-labs)

---

### Figma-NonEditor: Integration without Editor Access
**Learn Figma Integration with Read-Only Access**

**Objective:** Learn how to integrate IBM Bob with Figma when you only have read access to design files. Use IBM Bob to analyze designs, extract specifications, and build applications based on Figma designs.

**What You'll Learn:**
- Connecting IBM Bob to Figma with read-only access
- Analyzing Figma designs programmatically
- Extracting design specifications and assets
- Building applications from Figma designs
- Working with design data in your development workflow

**What You'll Build:**
- A Pill Tracker application based on Figma designs
- Complete with HTML, CSS, JavaScript, and database integration

**Prerequisites:**
- IBM Bob installed
- Figma account (free or paid)
- Read access to a Figma design file
- Basic web development knowledge

**Duration:** 30-45 minutes

**📘 [View Figma-NonEditor Guide](../Figma-NonEditor/README.md)**

**Lab Files:** `Figma-NonEditor/` folder contains PDF guide, code, and Docker setup

[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Templates & Resources

This repository includes helpful templates and configuration files:

### IBM Bob Model Import Template
**File:** `IBM_BOB_Model_Import_Template.pdf`

A reference guide for importing AI models into IBM Bob, including:
- Model configuration instructions
- Parameter setup
- Best practices
- Troubleshooting tips

### Java Modernization Export Configuration
**File:** `java-modernization-export.yaml`

A YAML configuration template for customizing Java modernization exports:
- Export format options
- Output path configuration
- Analysis depth settings
- Metric collection options

[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Getting Started

### Prerequisites

Before starting any lab, ensure you have:

1. **IBM Bob IDE Extension** (v1.0.0 or later)
   - Installed in VS Code
   - Active and connected

2. **SDKMAN!** (for Java and Maven management)
   ```bash
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

3. **Maven** (via SDKMAN!)
   ```bash
   sdk install maven
   ```

4. **Node.js and npm** (for Lab 3 only)
   - Node.js v18+ and npm v9+
   - Download from: https://nodejs.org/

5. **Angular CLI** (for Lab 3 only)
   ```bash
   npm install -g @angular/cli
   ```

### How to Use This Repository

#### Understanding Snapshots

Each lab has a **snapshot** - a self-contained starting point with all necessary code and configuration. Snapshots are organized in the `snapshots/` directory and can be used to reset lab progress at any time. To reset a lab, navigate to the `snapshots/start-states/` folder, find the snapshot with the same name as the one in your lab directory, and copy it to replace your current lab snapshot. For example, if working on Lab 4, copy `snapshots/start-states/snapD-unit-test-gen/` to replace the snapshot in your lab directory.

**Start State Snapshots:**
- **snapA-java-liberty-replatforming**: Traditional WebSphere with Java 8 and Struts (Lab 1)
- **snapB-java-upgrade**: Liberty with Java 8 and Struts (Lab 2)
- **snapC-ui-mod**: Liberty with Java 21 and Struts (Lab 3)
- **snapD-unit-test-gen**: Liberty with Java 21 and Angular (Lab 4)
- **snapTDD**: OpenAPI specification for prescription management (Lab Alt-4)
- **snapE-security-vulnerabilities**: Liberty with Java 21 and Angular with security issues (Lab 5)

**End State Snapshots (Reference Implementations):**
- **snapF-unit-tests**: Completed Lab 4 with comprehensive unit tests
- **snapG-security-vulnerabilities-fix**: Completed Lab 5 with security fixes
- **snapTDD-generated-api**: Completed Lab Alt-4 with TDD-generated implementation

#### Working Through Labs

**Option 1: Complete Labs Sequentially** (Recommended for full learning experience)
1. Start with Lab 1 and work through Labs 2-3 for the core modernization journey
2. Then complete Labs 4 and 5 (can be done in any order)
3. Lab Alt-4 (TDD) is standalone and can be completed anytime

**Option 2: Complete Labs Independently**
1. Choose any lab based on your learning goals
2. Each snapshot provides everything needed for that specific lab
3. No need to complete previous labs

#### Starting a Lab

1. **Open the lab snapshot folder in your IDE:**
   - Lab 1: Open `labs/lab1-java-liberty-replatforming/snapA-java-liberty-replatforming/`
   - Lab 2: Open `labs/lab2-java-upgrade/snapB-java-upgrade/`
   - Lab 3: Open `labs/lab3-ui-modernization/snapC-ui-mod/`
   - Lab 4: Open `labs/lab4-unit-test-generation/snapD-unit-test-gen/`
   - Lab Alt-4: Open `labs/alt-lab4-test-driven-development/snapTDD/`
   - Lab 5: Open `labs/lab5-security-vulnerability-remediation/snapE-security-vulnerabilities/`

2. **Navigate to the snapshot directory in your terminal (if not using terminal in the IDE):**
   ```bash
   cd Bobathon/labs/lab[X]-[lab-name]/snap[X]-[snapshot-name]/
   ```

3. **Open the lab guide:**
   - Navigate to `labs/lab[X]-[lab-name]/LAB[X]-GUIDE.md`
   - Follow the step-by-step instructions

4. **Activate IBM Bob:**
   - Open Bob's chat interface in your IDE
   - Switch to the appropriate mode (Java Modernization, Code, or Advanced)
   - Follow the lab guide instructions

[↑ Back to Top](#bobathon---java-modernization-labs)

---
## Lab Progression Summary

### Lab 1: Liberty Replatforming ✅
- **Input:** Traditional WebSphere application
- **Process:** Use Bob's Java Modernization workflow with Liberty Replatforming
- **Output:** Liberty-compatible application
- **Key Artifact:** Migration plan ZIP file (`simple-pharmacy.war_migrationPlan.zip`)

### Lab 2: Java Upgrade ✅
- **Input:** Java 8 application on Liberty
- **Process:** Use Bob's Java Modernization workflow with Java Upgrade recipes
- **Output:** Java 21 application with Jakarta EE
- **Key Changes:** Namespace updates, dependency upgrades, modern Java features

### Lab 3: UI Modernization ✅
- **Input:** Struts-based web application
- **Process:** Use Bob's Advanced mode for framework migration
- **Output:** Angular SPA with REST API backend
- **Key Changes:** Component migration, service architecture, modern UI/UX

### Lab 4: Unit Test Generation ✅
- **Input:** Modernized application without tests
- **Process:** Use Bob's Code mode to generate comprehensive unit tests
- **Output:** Application with >80% test coverage
- **Key Artifacts:** JUnit tests, Mockito mocks, repository tests, API tests

### Lab Alt-4: Test Driven Development (TDD) ✅
- **Input:** OpenAPI specification for prescription management
- **Process:** Generate tests from spec, implement from tests using Bob
- **Output:** TDD-generated PrescriptionResource implementation
- **Key Artifacts:** JUnit test suite, JAX-RS resource implementation

### Lab 5: Security Vulnerability Remediation ✅
- **Input:** Application with intentional security vulnerabilities
- **Process:** Use Bob to identify and fix security issues
- **Output:** Security-hardened application
- **Key Changes:** Input validation, SQL injection fixes, XSS prevention

[↑ Back to Top](#bobathon---java-modernization-labs)

---
## Learning Outcomes

By completing these labs, you will:

✅ Understand IBM Bob's AI-powered modernization capabilities
✅ Master the Java Modernization workflow
✅ Learn to migrate between application servers
✅ Upgrade Java applications to modern versions
✅ Transform legacy frontends to modern frameworks
✅ Validate and test modernized applications
✅ Apply best practices for Java modernization projects

[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Troubleshooting

### Common Issues

**Bob Mode Not Available**
- Ensure IBM Bob is up to date (v1.0.0+)
- Try typing `/mode` in Bob's chat to see available modes

**Maven Not Detected**
- Restart your IDE after installing Maven via SDKMAN!
- Verify installation: `mvn --version`

**Port Already in Use**
- Check if another application is using the port (typically 9081)
- Stop any running Liberty servers: `./stop-liberty.sh`

**Build Failures**
- Ensure all prerequisites are installed
- Check Java version matches lab requirements
- Review the lab guide's troubleshooting section

### Getting Help

1. **Check the Lab Guide** - Each lab has detailed troubleshooting sections
2. **Ask Bob** - Bob can help explain errors and suggest fixes
3. **Review Error Messages** - Provide full error messages to Bob for assistance

---

## Additional Resources

### Documentation
- [IBM Bob Documentation](https://www.ibm.com/products/watsonx-code-assistant)
- [Liberty Documentation](https://openliberty.io/docs/)
- [Java 21 Release Notes](https://openjdk.org/projects/jdk/21/)
- [Angular Documentation](https://angular.io/docs)

### Related Materials
- Demo Video: [Simple Pharmacy Demo](https://ibm.ent.box.com/folder/365179537734)
- Webisode: [Java Modernization](https://ec.yourlearning.ibm.com/w3/series/10476203?layout=grid)

---

## Repository Structure

```
bobathon/
├── README.md (this file)
└── Bobathon/
    ├── labs/
    │   ├── lab1-java-liberty-replatforming/
    │   │   └── LAB1-GUIDE.md
    │   ├── lab2-java-upgrade/
    │   │   └── LAB2-GUIDE.md
    │   ├── lab3-ui-modernization/
    │   │   └── LAB3-GUIDE.md
    │   ├── lab4-unit-test-generation/
    │   │   └── LAB4-GUIDE.md
    │   ├── alt-lab4-test-driven-development/
    │   │   ├── LAB-TDD-GUIDE.md
    │   │   └── snapTDD/ (starting point)
    │   │       └── prescription-api-spec.yaml
    │   └── lab5-security-vulnerability-remediation/
    │       └── LAB5-GUIDE.md
    └── snapshots/
        ├── snapshots.md (snapshot documentation)
        ├── start-states/
        │   ├── snapA-java-liberty-replatforming/
        │   ├── snapB-java-upgrade/
        │   ├── snapC-ui-mod/
        │   ├── snapD-unit-test-gen/
        │   ├── snapTDD/
        │   └── snapE-security-vulnerabilities/
        └── end-states/
            ├── snapF-unit-tests/
            ├── snapG-security-vulnerabilities-fix/
            └── snapTDD-generated-api/
```

---

## Quick Start Guide

**Ready to begin? Follow these steps:**

1. **Choose Your Lab**
   - New to modernization? Start with Lab 1
   - Want to focus on Java upgrade? Jump to Lab 2
   - Interested in frontend? Go to Lab 3
   - Want to add tests? Try Lab 4 or Lab Alt-4 (TDD)
   - Focus on security? Go to Lab 5

2. **Set Up Prerequisites**
   - Install IBM Bob
   - Install SDKMAN! and Maven
   - (Lab 3 only) Install Node.js and Angular CLI

3. **Open the Lab**
   - Navigate to the lab directory
   - Open the snapshot folder in your IDE
   - Open the corresponding lab guide

4. **Start Modernizing!**
   - Activate IBM Bob
   - Follow the lab guide
   - Let Bob help you modernize

[↑ Back to Top](#bobathon---java-modernization-labs)

---

## Tips for Success

- **Start with "Read" auto-approval only** until comfortable with Bob's capabilities
- **Read the lab guide thoroughly** before starting each exercise
- **Ask Bob for explanations** when you don't understand something
- **Take your time** - modernization is a learning process
- **Experiment** - each snapshot is independent, so feel free to try different approaches

---

## Contributing

This is a workshop repository. If you find issues or have suggestions for improvements, please reach out to the workshop organizers. We welcome your contributions!

---

## 📄 License

This workshop material is provided for educational purposes of IBM Bob.

---

**Happy Modernizing! 🚀**
[↑ Back to Top](#bobathon---java-modernization-labs)

---
