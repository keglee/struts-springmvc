<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>用户列表</title>
</head>
<body>
<c:forEach items="${users}" var="item">
    ${item.username}, ${item.password}, ${item.sex}<br/>
</c:forEach>
</body>
</html>