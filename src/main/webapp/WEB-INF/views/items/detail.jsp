<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
<title>Item Details</title>
<link rel="stylesheet" href="../../css/style.css">
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

<form method="post" action="../../claims/submit">
    <input type="hidden" name="itemId" value="${item.id}">

    <textarea name="message"
              placeholder="Explain why this belongs to you"></textarea>
    <br><br>

    <button type="submit">Claim This Item</button>
</form>

<br>

<a href="../../items">Back</a>

</body>
</html>