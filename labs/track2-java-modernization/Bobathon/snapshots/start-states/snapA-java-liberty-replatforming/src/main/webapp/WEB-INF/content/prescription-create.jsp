<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Prescription - Pharmacy Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 20px;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
        }
        .nav {
            background-color: #3498db;
            padding: 15px;
            margin: -20px -20px 20px -20px;
            border-radius: 8px 8px 0 0;
        }
        .nav a {
            color: white;
            text-decoration: none;
            padding: 10px 15px;
            margin-right: 10px;
            border-radius: 4px;
            display: inline-block;
        }
        .nav a:hover {
            background-color: #2980b9;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #34495e;
            font-weight: bold;
        }
        input[type="text"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        .btn {
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            display: inline-block;
            margin-right: 10px;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .btn-success {
            background-color: #27ae60;
        }
        .btn-success:hover {
            background-color: #229954;
        }
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
        .message {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .message-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav">
            <a href="<s:url action='dashboard'/>">Dashboard</a>
            <a href="<s:url action='prescription-list'/>">Prescriptions</a>
            <a href="<s:url action='order-list'/>">Orders</a>
            <a href="<s:url action='medicine-list'/>">Medicines</a>
        </div>
        
        <h1>Create New Prescription</h1>
        
        <s:if test="hasActionErrors()">
            <div class="message message-error">
                <s:actionerror/>
            </div>
        </s:if>
        
        <s:form action="prescription-save" method="post">
            <div class="form-group">
                <label for="patientName">Patient Name *</label>
                <s:textfield name="patientName" id="patientName" required="true" placeholder="Enter patient name"/>
            </div>
            
            <div class="form-group">
                <label for="patientId">Patient ID *</label>
                <s:textfield name="patientId" id="patientId" required="true" placeholder="Enter patient ID"/>
            </div>
            
            <div class="form-group">
                <label for="doctorName">Doctor Name *</label>
                <s:textfield name="doctorName" id="doctorName" required="true" placeholder="Enter doctor name"/>
            </div>
            
            <div class="form-group">
                <label for="medicineId">Medicine *</label>
                <s:select name="medicineId" id="medicineId" list="medicines" listKey="id" 
                         listValue="name + ' - ' + description" headerKey="" headerValue="Select Medicine" required="true"/>
            </div>
            
            <div class="form-group">
                <label for="quantity">Quantity *</label>
                <s:textfield type="number" name="quantity" id="quantity" required="true" min="1" value="1"/>
            </div>
            
            <div class="form-group">
                <label for="dosage">Dosage Instructions *</label>
                <s:textfield name="dosage" id="dosage" required="true" placeholder="e.g., 1 tablet twice daily"/>
            </div>
            
            <div class="form-group">
                <label for="notes">Notes</label>
                <s:textarea name="notes" id="notes" placeholder="Additional notes or instructions"/>
            </div>
            
            <div class="form-group">
                <s:submit value="Create Prescription" cssClass="btn btn-success"/>
                <a href="<s:url action='prescription-list'/>" class="btn btn-secondary">Cancel</a>
            </div>
        </s:form>
    </div>
</body>
</html>