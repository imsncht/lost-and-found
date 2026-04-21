<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<title>Login</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<h2>Login</h2>

<form method="post" action="login">
    Email:<br>
    <input type="email" name="email"><br><br>

    Password:<br>
    <input type="password" name="password"><br><br>

    <button type="submit">Login</button>
</form>

<p style="color:red">${error}</p>

<p>No account? <a href="register">Register</a></p>

</body>
</html>