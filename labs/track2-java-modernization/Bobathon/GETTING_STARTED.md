# Getting Started with Bobathon Labs

Welcome! This guide will help you get started with the Bobathon Labs in 10 minutes.

---

## 📚 What's in This Repository?

This repository contains **hands-on labs** for learning AI-assisted application modernization using IBM Bob:

### Java Modernization Labs (6 labs)
Learn to modernize legacy Java applications step-by-step:
1. **Lab 1**: Migrate from WebSphere to Liberty (45-60 min)
2. **Lab 2**: Upgrade from Java 8 to Java 21 (45-60 min)
3. **Lab 3**: Modernize UI from Struts to Angular (60-90 min)
4. **Lab 4**: Generate comprehensive unit tests (60-75 min)
5. **Lab Alt-4**: Test-Driven Development (TDD) approach (45-60 min)
6. **Lab 5**: Fix security vulnerabilities (60-75 min)

### Figma Integration Labs (2 labs)
Learn to integrate IBM Bob with Figma for UI design:
- **Figma-Editor**: Integration with Figma editor access (30-45 min)
- **Figma-NonEditor**: Integration without editor access (30-45 min)

### Additional Resources
- **Templates**: IBM Bob model import template and configuration files
- **Archived Labs**: Previous versions of labs for reference

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Check Prerequisites

You need:
- **IBM Bob** installed in your IDE (VS Code, IntelliJ, or Visual Studio)
- **Java 8+** (Java 21 recommended)
- **Maven 3.6+**
- **Node.js 18+** and **npm** (for Lab 3 and Figma labs only)

**Quick Check:**
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check Node.js (optional, for Lab 3 and Figma labs)
node --version
npm --version
```

### Step 2: Clone the Repository

```bash
git clone <repository-url>
cd bobathon
```

### Step 3: Choose Your First Lab

**New to Java modernization?** → Start with **Lab 1** (Java Liberty Replatforming)

**Want to try Figma integration?** → Start with **Figma-NonEditor**

**Experienced developer?** → Pick any lab that interests you!

### Step 4: Open the Lab

Each lab is self-contained with everything you need:

```bash
# For Java Lab 1
cd Bobathon/labs/lab1-java-liberty-replatforming
code .  # or open in your IDE

# For Figma Lab
cd Figma-NonEditor
code .  # or open in your IDE
```

### Step 5: Follow the Lab Guide

Every lab has a detailed guide:
- Java Labs: Look for `LAB#-GUIDE.md` (e.g., `LAB1-GUIDE.md`)
- Figma Labs: Look for PDF guides in the folder

---

## 📂 Repository Structure

```
bobathon/
│
├── README.md                          ← Overview of all labs
├── GETTING_STARTED.md                 ← This file!
│
├── Bobathon/
│   ├── labs/                          ← All Java modernization labs
│   │   ├── lab1-java-liberty-replatforming/
│   │   │   ├── LAB1-GUIDE.md         ← Step-by-step instructions
│   │   │   └── snapA-.../            ← Starting code
│   │   ├── lab2-java-upgrade/
│   │   ├── lab3-ui-modernization/
│   │   ├── lab4-unit-test-generation/
│   │   ├── lab5-security-vulnerability-remediation/
│   │   └── alt-lab4-test-driven-development/
│   │
│   ├── snapshots/                     ← Reference implementations
│   │   ├── start-states/             ← Clean starting points
│   │   └── end-states/               ← Completed examples
│   │
│   └── Archived/                      ← Legacy lab versions
│
├── Figma-Editor/                      ← Figma lab with editor access
│   └── Pill Tracker...Lab Guide.pdf
│
├── Figma-NonEditor/                   ← Figma lab without editor
│   └── Pill Tracker Lab...Guide.pdf
│
├── IBM_BOB_Model_Import_Template.pdf  ← Template for model imports
└── java-modernization-export.yaml     ← Configuration template
```

---

## 🎯 Recommended Learning Paths

### Path 1: Complete Java Modernization Journey (Sequential)
Follow labs in order to see a complete modernization:
1. Lab 1 → Lab 2 → Lab 3 → Lab 4 → Lab 5

**Time:** 5-7 hours total  
**Best for:** Understanding the full modernization process

### Path 2: Pick What You Need (Independent)
Each lab is self-contained. Jump to what interests you:
- Need to migrate to Liberty? → Lab 1
- Upgrading Java version? → Lab 2
- Modernizing UI? → Lab 3
- Adding tests? → Lab 4 or Alt-4
- Security concerns? → Lab 5

**Time:** 45-90 minutes per lab  
**Best for:** Focused learning on specific topics

### Path 3: Figma Integration Focus
Start with Figma labs to learn UI design integration:
1. Figma-NonEditor (easier) → Figma-Editor (advanced)

**Time:** 1-2 hours total  
**Best for:** UI/UX designers and frontend developers

---

## 💡 How Labs Work

### Snapshots System
Each lab uses "snapshots" - complete, working codebases at specific stages:

- **Start State Snapshots**: Your starting point for each lab
  - Example: `snapA-java-liberty-replatforming/`
  - Contains the "before" code

- **End State Snapshots**: Reference implementations
  - Example: `snapF-unit-tests/`
  - Shows the "after" code if you get stuck

### Lab Structure
Every Java lab follows this pattern:

1. **LAB Guide** (`LAB#-GUIDE.md`): Step-by-step instructions
2. **Snapshot Folder** (`snap#-...`/): Starting code
3. **Prerequisites Section**: What you need installed
4. **Exercises**: Hands-on tasks with IBM Bob
5. **Validation**: How to verify success

---

## 🔧 Installation & Setup

### Install IBM Bob

1. Download IBM Bob from [IBM website](https://www.ibm.com/products/watsonx-code-assistant)
2. Install the IDE extension for your editor:
   - VS Code: Install from Extensions marketplace
   - IntelliJ: Install from Plugins marketplace
   - Visual Studio: Install from Extensions
3. Sign in to IBM Bob
4. Verify it's working: Look for IBM Bob icon in your IDE

### Install Java & Maven (via SDKMAN - Recommended)

```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Maven
sdk install maven

# Install Java 21 (optional, for Labs 2-5)
sdk install java 21.0.1-tem
sdk use java 21.0.1-tem
```

### Install Node.js & Angular (For Lab 3 and Figma Labs Only)

```bash
# Download Node.js from https://nodejs.org/ (v18 or higher)

# Install Angular CLI
npm install -g @angular/cli

# Verify
ng version
```

---

## 📖 Lab Guides Overview

### Lab 1: Java Liberty Replatforming
**What you'll learn:** Migrate from Traditional WebSphere to Liberty  
**Starting point:** WebSphere app with Java 8 and Struts  
**Ending point:** Liberty app with Java 8 and Struts
**Guide:** `Bobathon/labs/lab1-java-liberty-replatforming/LAB1-GUIDE.md`

### Lab 2: Java Upgrade
**What you'll learn:** Upgrade from Java 8 to Java 21  
**Starting point:** Liberty app with Java 8  
**Ending point:** Liberty app with Java 21
**Guide:** `Bobathon/labs/lab2-java-upgrade/LAB2-GUIDE.md`

### Lab 3: UI Modernization
**What you'll learn:** Migrate from Struts to Angular  
**Starting point:** Liberty app with Struts UI  
**Ending point:** Liberty app with Angular SPA
**Guide:** `Bobathon/labs/lab3-ui-modernization/LAB3-GUIDE.md`

### Lab 4: Unit Test Generation
**What you'll learn:** Generate comprehensive unit tests with IBM Bob  
**Starting point:** Modernized app without tests  
**Ending point:** App with >80% test coverage
**Guide:** `Bobathon/labs/lab4-unit-test-generation/LAB4-GUIDE.md`

### Lab Alt-4: Test Driven Development
**What you'll learn:** API-first development using TDD  
**Starting point:** OpenAPI specification  
**Ending point:** Complete REST API with tests
**Guide:** `Bobathon/labs/alt-lab4-test-driven-development/LAB-TDD-GUIDE.md`

### Lab 5: Security Vulnerability Remediation
**What you'll learn:** Identify and fix security issues  
**Starting point:** App with security vulnerabilities  
**Ending point:** Security-hardened app
**Guide:** `Bobathon/labs/lab5-security-vulnerability-remediation/LAB5-GUIDE.md`

### Figma Labs
**What you'll learn:** Integrate IBM Bob with Figma for UI design  
**Guides:** PDF files in `Figma-Editor/` and `Figma-NonEditor/` folders

---

## 🆘 Troubleshooting

### Common Issues

**Problem: Maven not found after installation**
```bash
# Solution: Restart your IDE/terminal
# Then verify:
mvn -version
```

**Problem: IBM Bob not detecting Maven**
```bash
# Solution: Restart IBM Bob/IDE after installing Maven
# Verify Maven is in PATH:
which mvn  # macOS/Linux
where mvn  # Windows
```

**Problem: Port already in use (9081)**
```bash
# Solution: Stop any running Liberty servers
./stop-liberty.sh  # In the lab directory
```

**Problem: Node modules errors (Lab 3)**
```bash
# Solution: Clean install
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### Getting Help

1. **Check the lab guide**: Each lab has a troubleshooting section
2. **Review error messages**: Share full error output when asking for help
3. **Check prerequisites**: Verify all required software is installed
4. **Try the end-state snapshot**: Compare with the reference implementation

---

## 📝 Important Notes

### Access Requirements

Some resources in this repository link to IBM internal systems:
- **IBM Box**: Demo videos (requires IBM employee access)
- **IBM YourLearning**: Training materials (requires IBM employee access)

**External users:** All labs work without these links. The comprehensive lab guides contain everything you need.

### Templates & Configuration Files

- **IBM_BOB_Model_Import_Template.pdf**: Reference guide for importing models
- **java-modernization-export.yaml**: Configuration template for exports
- These are optional reference materials

### Archived Content

The `Bobathon/Archived/` folder contains previous versions of labs. You can safely ignore this folder - use the labs in `Bobathon/labs/` instead.

---

## ✅ Success Checklist

Before starting labs, verify:
- [ ] IBM Bob installed and signed in
- [ ] Java installed (check with `java -version`)
- [ ] Maven installed (check with `mvn -version`)
- [ ] Node.js installed if doing Lab 3 or Figma labs (check with `node --version`)
- [ ] Repository cloned locally
- [ ] Can open lab folders in your IDE

---

## 🎓 Learning Tips

1. **Start Simple**: Begin with Lab 1 if you're new to modernization
2. **Read First**: Review the entire lab guide before starting
3. **Use IBM Bob**: Let Bob help you - that's what these labs teach!
4. **Take Your Time**: Labs are self-paced, no rush
5. **Experiment**: Each snapshot is independent, feel free to try different approaches
6. **Ask Questions**: Use IBM Bob's chat to ask questions as you work

---

## 🚀 Ready to Start?

1. **Pick a lab** from the list above
2. **Open the lab guide** (LAB#-GUIDE.md)
3. **Follow the instructions** step-by-step
4. **Have fun learning!** 🎉

---

## 📞 Questions?

- Review the lab guide's troubleshooting section
- Check the main [README.md](README.md) for additional information
- Refer to the [Distribution Plan](DISTRIBUTION_PLAN_UPDATED.md) for detailed repository information

**Happy Learning! 🚀**