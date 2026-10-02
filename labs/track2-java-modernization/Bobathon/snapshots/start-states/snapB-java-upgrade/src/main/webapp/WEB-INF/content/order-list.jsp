<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Orders - Pharmacy Dashboard</title>
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
            max-width: 1200px;
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
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #3498db;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            padding: 8px 16px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            display: inline-block;
            margin-right: 5px;
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
        .status {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
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
        
        <h1>Orders</h1>
        
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
        
        <table>
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Prescription ID</th>
                    <th>Patient Name</th>
                    <th>Medicine</th>
                    <th>Quantity</th>
                    <th>Total Amount</th>
                    <th>Order Date</th>
                    <th>Payment Method</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="orders">
                    <tr>
                        <td><s:property value="id"/></td>
                        <td><s:property value="prescriptionId"/></td>
                        <td><s:property value="patientName"/></td>
                        <td><s:property value="medicineName"/></td>
                        <td><s:property value="quantity"/></td>
                        <td>$<s:property value="totalAmount"/></td>
                        <td><s:date name="orderDate" format="MM/dd/yyyy"/></td>
                        <td><s:property value="paymentMethod"/></td>
                        <td>
                            <span class="status status-<s:property value='status.toLowerCase()'/>">
                                <s:property value="status"/>
                            </span>
                        </td>
                        <td>
                            <a href="<s:url action='order-view'><s:param name='orderId' value='id'/></s:url>" class="btn">View</a>
                            <s:if test="status == 'PENDING'">
                                <a href="<s:url action='order-view'><s:param name='orderId' value='id'/></s:url>" class="btn btn-success">Process Payment</a>
                            </s:if>
                            <s:if test="status == 'PAID'">
                                <a href="<s:url action='order-collect'><s:param name='orderId' value='id'/></s:url>" class="btn btn-success">Mark Collected</a>
                            </s:if>
                        </td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>
</body>
</html>