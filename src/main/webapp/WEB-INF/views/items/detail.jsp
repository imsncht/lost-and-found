<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
<title>Item Details</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h2>Item Details</h2>

<p><b>Title:</b> ${item.title}</p>
<p><b>Description:</b> ${item.description}</p>
<p><b>Category:</b> ${item.category}</p>
<p><b>Location:</b> ${item.location}</p>
<p><b>Date:</b> ${item.itemDate}</p>
<p><b>Type:</b> ${item.type}</p>
<p><b>Status:</b> ${item.status}</p>

<hr>

<form method="post"
action="${pageContext.request.contextPath}/claims/submit">

<input type="hidden" name="itemId" value="${item.id}">

What color is the item?<br>
<input type="text" name="colorAnswer"><br><br>

Any identifying marks?<br>
<textarea name="identifyingMarks"></textarea><br><br>

What was inside / unique detail?<br>
<textarea name="contentsAnswer"></textarea><br><br>

Additional message:<br>
<textarea name="message"></textarea><br><br>

<button type="submit">Submit Claim</button>

</form>

<br>

<a href="${pageContext.request.contextPath}/items">Back</a>

</body>
</html>