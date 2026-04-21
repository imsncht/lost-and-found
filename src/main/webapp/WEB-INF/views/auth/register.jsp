<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<title>Register</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<h2>Register</h2>

<form method="post" action="register">

    Name:<br>
    <input type="text" name="name"><br><br>

    Email:<br>
    <input type="email" name="email"><br><br>

    Password:<br>
    <input type="password" name="password"><br><br>

    <button type="submit">Register</button>

</form>

<p style="color:red">${error}</p>

<p>Already have account? <a href="login">Login</a></p>

</body>
</html>