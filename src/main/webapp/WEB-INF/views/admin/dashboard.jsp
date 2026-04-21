<html>
<head>
<title>Admin Dashboard</title>
<link rel="stylesheet"
href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h2>Admin Dashboard</h2>

<a href="${pageContext.request.contextPath}/logout">Logout</a>

<hr>

<ul>
<li><a href="items?type=LOST">Lost Items</a></li>
<li><a href="items?type=FOUND">Found Items</a></li>
<li><a href="claims">Pending Claims</a></li>
<li><a href="resolved">Resolved / Archive</a></li>
</ul>

</body>
</html>