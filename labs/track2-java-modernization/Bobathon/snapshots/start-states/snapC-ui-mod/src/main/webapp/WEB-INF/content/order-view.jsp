<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Order Details - Pharmacy Dashboard</title>
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
        .detail-section {
            margin-bottom: 30px;
        }
        .detail-row {
            display: grid;
            grid-template-columns: 200px 1fr;
            padding: 12px 0;
            border-bottom: 1px solid #ecf0f1;
        }
        .detail-label {
            font-weight: bold;
            color: #34495e;
        }
        .detail-value {
            color: #2c3e50;
        }
        .status {
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 14px;
            font-weight: bold;
            display: inline-block;
        }
        .status-pending {
            background-color: #f39c12;
            color: white;
        }
        .status-paid {
            background-color: #3498db;
            color: white;
        }
        .status-collected {
            background-color: #27ae60;
            color: white;
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
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
        .payment-form {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #34495e;
            font-weight: bold;
        }
        select, textarea {
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
        .message {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        .message-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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
        
        <h1>Order Details</h1>
        
        <s:if test="hasActionMessages()">
            <div class="message message-success">
                <s:actionmessage/>
            </div>
        </s:if>
        
        <s:if test="hasActionErrors()">
            <div class="message message-error">
                <s:actionerror/>
            </div>
        </s:if>
        
        <div class="detail-section">
            <div class="detail-row">
                <div class="detail-label">Order ID:</div>
                <div class="detail-value"><s:property value="order.id"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Prescription ID:</div>
                <div class="detail-value"><s:property value="order.prescriptionId"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Patient Name:</div>
                <div class="detail-value"><s:property value="order.patientName"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Patient ID:</div>
                <div class="detail-value"><s:property value="order.patientId"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Medicine:</div>
                <div class="detail-value"><s:property value="order.medicineName"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Quantity:</div>
                <div class="detail-value"><s:property value="order.quantity"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Total Amount:</div>
                <div class="detail-value">$<s:property value="order.totalAmount"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Order Date:</div>
                <div class="detail-value"><s:date name="order.orderDate" format="MM/dd/yyyy HH:mm"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Payment Method:</div>
                <div class="detail-value"><s:property value="order.paymentMethod"/></div>
            </div>
            <div class="detail-row">
                <div class="detail-label">Status:</div>
                <div class="detail-value">
                    <span class="status status-<s:property value='order.status.toLowerCase()'/>">
                        <s:property value="order.status"/>
                    </span>
                </div>
            </div>
            <s:if test="order.pharmacistNotes != null && order.pharmacistNotes != ''">
                <div class="detail-row">
                    <div class="detail-label">Pharmacist Notes:</div>
                    <div class="detail-value"><s:property value="order.pharmacistNotes"/></div>
                </div>
            </s:if>
        </div>
        
        <s:if test="order.status == 'PENDING'">
            <div class="payment-form">
                <h2>Process Payment</h2>
                <s:form action="order-processPayment" method="post">
                    <s:hidden name="orderId" value="%{order.id}"/>
                    
                    <div class="form-group">
                        <label for="paymentMethod">Payment Method *</label>
                        <s:select name="paymentMethod" id="paymentMethod" list="{'CASH','CREDIT_CARD','INSURANCE'}" required="true"/>
                    </div>
                    
                    <div class="form-group">
                        <label for="pharmacistNotes">Pharmacist Notes</label>
                        <s:textarea name="pharmacistNotes" id="pharmacistNotes" placeholder="Optional notes"/>
                    </div>
                    
                    <div class="form-group">
                        <s:submit value="Process Payment" cssClass="btn"/>
                        <a href="<s:url action='order-list'/>" class="btn btn-secondary">Back to Orders</a>
                    </div>
                </s:form>
            </div>
        </s:if>
        <s:else>
            <div>
                <a href="<s:url action='order-list'/>" class="btn btn-secondary">Back to Orders</a>
            </div>
        </s:else>
    </div>
</body>
</html>