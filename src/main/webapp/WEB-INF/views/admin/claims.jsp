<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>Claims</title>

<link rel="stylesheet"
href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>

<h2>Pending Claims</h2>

<a href="${pageContext.request.contextPath}/admin/dashboard">
Dashboard
</a>
|

<a href="${pageContext.request.contextPath}/logout">
Logout
</a>

<hr>

<c:forEach items="${claims}" var="c">

<div style="border:1px solid #ccc; padding:15px; margin:15px;">

<h3>${c.item.title}</h3>

<c:if test="${not empty c.item.imagePath}">
    <img src="${pageContext.request.contextPath}/images?name=${c.item.imagePath}"
         width="180"><br><br>
</c:if>

<b>Description:</b> ${c.item.description}<br>
<b>Type:</b> ${c.item.type}<br>
<b>Location:</b> ${c.item.location}<br>
<b>Status:</b> ${c.item.status}<br>

<hr>

<b>Posted By:</b> ${c.item.user.name}<br>
<b>Posted At:</b> ${c.item.createdAt}<br>

<hr>

<b>Claimed By:</b> ${c.claimant.name}<br>
<b>Email:</b> ${c.claimant.email}<br>
<b>Claimed At:</b> ${c.createdAt}<br>

<hr>

<b>Color Answer:</b> ${c.colorAnswer}<br>
<b>Marks:</b> ${c.identifyingMarks}<br>
<b>Contents:</b> ${c.contentsAnswer}<br>
<b>Message:</b> ${c.message}<br><br>

<a href="approve?id=${c.id}">Approve</a>
|
<a href="reject?id=${c.id}">Reject</a>

</div>

</c:forEach>

</body>
</html>