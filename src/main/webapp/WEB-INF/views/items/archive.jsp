<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>Archive</title>
<link rel="stylesheet"
href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h2>All Items Archive</h2>

<a href="${pageContext.request.contextPath}/items">Public Board</a> |
<a href="${pageContext.request.contextPath}/logout">Logout</a>

<hr>

<table border="1" cellpadding="8">

<tr>
<th>Image</th>
<th>Title</th>
<th>Type</th>
<th>Posted By</th>
<th>Status</th>
<th>Date</th>
</tr>

<c:forEach items="${items}" var="i">

<tr>

<td>
<img src="${pageContext.request.contextPath}/images?name=${i.imagePath}"
width="90">
</td>

<td>${i.title}</td>
<td>${i.type}</td>
<td>${i.user.name}</td>
<td>${i.status}</td>
<td>${i.createdAt}</td>

</tr>

</c:forEach>

</table>

</body>
</html>