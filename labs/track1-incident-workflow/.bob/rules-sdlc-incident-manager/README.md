# SDLC Incident Manager Mode

## Overview

This custom Bob mode provides complete incident lifecycle management with automated diagnosis and remediation capabilities. It integrates ServiceNow for incident tracking, Ansible for automation, and Terraform for infrastructure management.

## Mode Details

- **Slug**: `sdlc-incident-manager`
- **Name**: 🎫 SDLC Incident Manager
- **Purpose**: Manage application incidents from creation through resolution
- **Scope**: Handles multiple incident types (performance, deployment, configuration, backup/restore)

## What This Mode Does

The SDLC Incident Manager mode helps you:

1. **Create and Track Incidents** - Log issues in ServiceNow with complete details
2. **Diagnose Problems** - Use automated tools (Ansible, Terraform) to identify root causes
3. **Apply Fixes** - Execute appropriate remediation playbooks
4. **Verify Resolution** - Confirm issues are resolved with objective metrics
5. **Document Everything** - Maintain complete audit trail in ServiceNow

## Supported Incident Types

### Performance Issues
- **Symptoms**: Slow response times, timeouts, degraded performance
- **Diagnostic Tools**: health-check.yml playbook
- **Remediation**: fix-performance.yml playbook
- **Typical Priority**: Medium

### Deployment Failures
- **Symptoms**: Broken functionality after deployment, deployment errors
- **Diagnostic Tools**: health-check.yml, logs analysis
- **Remediation**: rollback.yml or deploy.yml playbooks
- **Typical Priority**: High

### Configuration Problems
- **Symptoms**: Wrong settings, connection issues, misconfiguration
- **Diagnostic Tools**: health-check.yml, configuration file review
- **Remediation**: update-config.yml playbook
- **Typical Priority**: Medium

### Backup/Restore Operations
- **Symptoms**: Need for data backup or restore
- **Diagnostic Tools**: health-check.yml, file system checks
- **Remediation**: backup-database.yml playbook
- **Typical Priority**: Low (backup) / High (restore)

## Prerequisites

Before using this mode, ensure:

- ✅ Application infrastructure is deployed and accessible
- ✅ ServiceNow credentials configured in `.env`
- ✅ Ansible and Terraform MCP servers are active
- ✅ Appropriate playbooks available for your incident types

## How to Use This Mode

### Activating the Mode

1. Open Bob
2. Click the mode selector
3. Choose "🎫 SDLC Incident Manager"

### Starting Incident Management

Simply describe the issue you're experiencing:

```
"Users are reporting slow application performance"
```

or

```
"The recent deployment is causing errors"
```

or

```
"We need to backup the database before maintenance"
```

Bob will guide you through the complete incident lifecycle.

## Incident Workflow

### Phase 1: Incident Creation
- Gather complete incident details
- Determine urgency and impact levels
- Create ServiceNow incident
- Record incident number for tracking

### Phase 2: Initial Assessment
- Identify incident type
- Review available tools and playbooks
- Plan diagnostic approach

### Phase 3: Diagnosis
- Run appropriate health check playbooks
- Analyze diagnostic results
- Identify root cause
- Update ServiceNow with findings

### Phase 4: Remediation
- Select appropriate remediation playbook
- Explain fix to user
- Get confirmation if needed
- Execute remediation
- Document actions in ServiceNow

### Phase 5: Verification
- Re-run health checks
- Compare before/after metrics
- Confirm resolution
- Document verification results

### Phase 6: Closure
- Prepare resolution summary
- Update ServiceNow to resolved state
- Provide summary to user

## Mode Structure

### Instruction Files

1. **1_workflow.xml** (349 lines)
   - Complete 6-phase incident management workflow
   - Adaptive workflow for different incident types
   - Error handling scenarios
   - Communication guidelines

2. **2_best_practices.xml** (349 lines)
   - Incident management principles
   - Workflow best practices
   - Tool usage guidelines
   - Common pitfalls and solutions

3. **3_examples.xml** (489 lines)
   - Complete examples for each incident type
   - Adaptive behavior examples
   - Communication patterns
   - Real workflow demonstrations

4. **4_quick_reference.xml** (349 lines)
   - Phase-by-phase checklist
   - Incident type quick guide
   - ServiceNow and Ansible references
   - Troubleshooting guide
   - Communication templates

## Key Features

✅ **Adaptive Workflow** - Adjusts based on incident type  
✅ **Complete Lifecycle** - From creation to closure  
✅ **Automated Diagnosis** - Uses Ansible health checks  
✅ **Automated Remediation** - Executes appropriate playbooks  
✅ **ServiceNow Integration** - Full incident tracking  
✅ **Error Handling** - Graceful fallbacks when tools fail  
✅ **Clear Communication** - Explains actions and results  
✅ **Verification** - Confirms resolution with metrics

## ServiceNow Integration

### Urgency Levels
- **1 (High)**: Critical issue, immediate attention required
- **2 (Medium)**: Significant issue, needs timely resolution
- **3 (Low)**: Minor issue, can wait for scheduled maintenance

### Impact Levels
- **1 (High)**: Affects multiple users or critical systems
- **2 (Medium)**: Affects single user or non-critical system
- **3 (Low)**: Minimal impact, cosmetic issues

### State Transitions
- **1**: New
- **2**: In Progress
- **6**: Resolved
- **7**: Closed

## Ansible Playbooks

### Common Playbooks
- **health-check.yml**: Diagnose system health and performance
- **fix-performance.yml**: Resolve performance issues
- **deploy.yml**: Deploy application updates
- **rollback.yml**: Rollback to previous version
- **update-config.yml**: Update configuration settings
- **backup-database.yml**: Backup database

## Communication Style

Bob will:
- ✅ Explain what it's doing before each step
- ✅ Show actual command outputs and results
- ✅ Highlight key metrics and findings
- ✅ Ask for confirmation before disruptive actions
- ✅ Keep you informed of progress
- ✅ Document everything in ServiceNow

## Error Handling

The mode handles common issues gracefully:

- **ServiceNow Unavailable**: Proceeds with manual tracking
- **Ansible Playbook Fails**: Shows manual alternatives
- **Playbook Not Available**: Suggests alternatives or manual commands
- **Root Cause Unclear**: Runs additional diagnostics
- **Fix Doesn't Work**: Re-diagnoses and tries alternative approaches

## Example Usage

### Performance Issue Example

**User**: "The application is very slow"

**Bob**: 
1. Creates ServiceNow incident INC0010042
2. Runs health-check.yml playbook
3. Identifies backend slowness (3.2s response time)
4. Runs fix-performance.yml playbook
5. Verifies improvement (0.08s response time - 40x faster!)
6. Updates and resolves ServiceNow incident

### Deployment Failure Example

**User**: "The deployment is causing login errors"

**Bob**:
1. Creates high-priority incident INC0010043
2. Runs health-check.yml playbook
3. Identifies authentication module errors
4. Recommends rollback (gets user confirmation)
5. Executes rollback.yml playbook
6. Verifies authentication working
7. Resolves incident, notes need for deployment investigation

## Best Practices

1. **Always Diagnose First** - Don't skip to fixes
2. **Document Everything** - Update ServiceNow after each step
3. **Verify Resolution** - Confirm with metrics before closing
4. **Communicate Clearly** - Explain actions and results
5. **Get Confirmation** - Ask before disruptive actions
6. **Use Appropriate Tools** - Match playbook to incident type

## Troubleshooting

### Mode Not Working
- Verify MCP servers are connected
- Check ServiceNow credentials in `.env`
- Ensure playbooks exist in expected locations

### Playbooks Failing
- Check Ansible installation
- Verify inventory file is correct
- Check connectivity to target hosts
- Review playbook syntax

### ServiceNow Issues
- Verify credentials are correct
- Check ServiceNow instance accessibility
- Use manual tracking as fallback

## Related Files

- **Mode Configuration**: `.bob/custom_modes.yaml`
- **Workflow Instructions**: `.bob/rules-sdlc-incident-manager/1_workflow.xml`
- **Best Practices**: `.bob/rules-sdlc-incident-manager/2_best_practices.xml`
- **Examples**: `.bob/rules-sdlc-incident-manager/3_examples.xml`
- **Quick Reference**: `.bob/rules-sdlc-incident-manager/4_quick_reference.xml`

## Use Cases

This mode is ideal for:
- Managing production incidents
- Coordinating incident response
- Maintaining audit trails
- Standardizing incident workflows
- Training new team members
- Demonstrating SDLC capabilities

## Version

- **Created**: 2026-02-25
- **Last Updated**: 2026-02-25
- **Version**: 1.0.0
- **Mode Type**: General-purpose incident management