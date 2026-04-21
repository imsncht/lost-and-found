<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>Claims</title>
</head>
<body>

<h2>Pending Claims</h2>

<a href="../logout">Logout</a>

<table border="1" cellpadding="8">

<tr>
<th>ID</th>
<th>Message</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach items="${claims}" var="c">

<tr>
<td>${c.id}</td>
<td>${c.message}</td>
<td>${c.status}</td>

<td>
<a href="approve?id=${c.id}">Approve</a>
|
<a href="reject?id=${c.id}">Reject</a>
</td>

</tr>

</c:forEach>

</table>

</body>
</html>