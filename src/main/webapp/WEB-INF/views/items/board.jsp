<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>Items</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<h2>Lost & Found Board</h2>

<a href="items/post">Post Item</a> |
<a href="logout">Logout</a>

<hr>

<table border="1" cellpadding="8">

<tr>
<th>Title</th>
<th>Type</th>
<th>Location</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach items="${items}" var="i">

<tr>
<td>${i.title}</td>
<td>${i.type}</td>
<td>${i.location}</td>
<td>${i.status}</td>

<td>

<form method="post" action="claims/submit">
<input type="hidden" name="itemId" value="${i.id}">
<input type="text" name="message" placeholder="Claim message">
<button>Claim</button>
</form>

</td>

</tr>

</c:forEach>

</table>

</body>
</html>