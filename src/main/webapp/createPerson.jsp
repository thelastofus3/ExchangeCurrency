<%--
  Created by IntelliJ IDEA.
  User: Max
  Date: 10/02/2024
  Time: 22:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PersonTest</title>
</head>
<body>
<form name="saveForm" action="people" method="POST">
    <table>
        <tr>
            <td>Enter name:</td>
            <td><input type="text" name="name"/></td>
        </tr>
        <tr>
            <td>Enter age:</td>
            <td><input type="text" name="age"/></td>
        </tr>
        <tr>
            <td>Enter email:</td>
            <td><input type="text" name="email"/></td>
        </tr>
        <th><input type="submit" value="Submit" name="find"/></th>
    </table>
</form>
</body>
</html>
