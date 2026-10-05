<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Pharmacy Dashboard</title>
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
        h2 {
            color: #34495e;
            margin-top: 30px;
            margin-bottom: 15px;
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
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        .stat-card h3 {
            font-size: 36px;
            margin-bottom: 10px;
        }
        .stat-card p {
            font-size: 14px;
            opacity: 0.9;
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
        .status-validated {
            background-color: #3498db;
            color: white;
        }
        .status-fulfilled {
            background-color: #27ae60;
            color: white;
        }
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
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
        
        <h1>Pharmacy Dashboard</h1>
        
        <div class="stats">
            <div class="stat-card">
                <h3><s:property value="totalPrescriptions"/></h3>
                <p>Total Prescriptions</p>
            </div>
            <div class="stat-card">
                <h3><s:property value="totalOrders"/></h3>
                <p>Total Orders</p>
            </div>
        </div>
        
        <h2>Pending Prescriptions</h2>
        <s:if test="pendingPrescriptions != null && pendingPrescriptions.size() > 0">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Patient</th>
                        <th>Medicine</th>
                        <th>Doctor</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="pendingPrescriptions">
                        <tr>
                            <td><s:property value="id"/></td>
                            <td><s:property value="patientName"/></td>
                            <td><s:property value="medicineName"/></td>
                            <td><s:property value="doctorName"/></td>
                            <td><span class="status status-pending"><s:property value="status"/></span></td>
                            <td>
                                <a href="<s:url action='prescription-view'><s:param name='prescriptionId' value='id'/></s:url>" class="btn">View</a>
                                <a href="<s:url action='prescription-validate'><s:param name='prescriptionId' value='id'/></s:url>" class="btn btn-success">Validate</a>
                            </td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </s:if>
        <s:else>
            <div class="empty-state">No pending prescriptions</div>
        </s:else>
        
        <h2>Pending Orders</h2>
        <s:if test="pendingOrders != null && pendingOrders.size() > 0">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Patient</th>
                        <th>Medicine</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="pendingOrders">
                        <tr>
                            <td><s:property value="id"/></td>
                            <td><s:property value="patientName"/></td>
                            <td><s:property value="medicineName"/></td>
                            <td>$<s:property value="totalAmount"/></td>
                            <td><span class="status status-pending"><s:property value="status"/></span></td>
                            <td>
                                <a href="<s:url action='order-view'><s:param name='orderId' value='id'/></s:url>" class="btn">View</a>
                            </td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </s:if>
        <s:else>
            <div class="empty-state">No pending orders</div>
        </s:else>
    </div>
</body>
</html>