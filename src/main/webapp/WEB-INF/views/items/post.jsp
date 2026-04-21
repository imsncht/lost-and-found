<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
<title>Post Item</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h2>Post Lost / Found Item</h2>

<form method="post"
      action="post"
      enctype="multipart/form-data">

Title:<br>
<input type="text" name="title"><br><br>

Description:<br>
<textarea name="description"></textarea><br><br>

Category:<br>
<input type="text" name="category"><br><br>

Location:<br>
<input type="text" name="location"><br><br>

Date:<br>
<input type="date" name="itemDate"><br><br>

Type:<br>
<select name="type">
    <option value="LOST">LOST</option>
    <option value="FOUND">FOUND</option>
</select><br><br>

Image:<br>
<input type="file" name="image"><br><br>

<button type="submit">Submit</button>

</form>

<br>
<a href="../items">Back</a>

</body>
</html>