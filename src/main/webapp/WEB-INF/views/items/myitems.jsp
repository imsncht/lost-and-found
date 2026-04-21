<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>My Items</title>
<link rel="stylesheet" href="../../css/style.css">
</head>
<body>

<h2>My Posted Items</h2>

<a href="../../items">All Items</a> |
<a href="../../logout">Logout</a>

<hr>

<table border="1" cellpadding="8">

<tr>
<th>Title</th>
<th>Type</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach items="${items}" var="i">

<tr>
<td>${i.title}</td>
<td>${i.type}</td>
<td>${i.status}</td>

<td>

<a href="../close?id=${i.id}">Close</a>
|
<a href="../delete?id=${i.id}">Delete</a>

</td>

</tr>

</c:forEach>

</table>

</body>
</html>