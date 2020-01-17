<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri ="/struts-tags" prefix ="s" %>
<html>
<head>
    <title>这是一个struts请求响应的jsp</title>
</head>
<body>
<s:property value="username" />
${username},<br/>
${password},<br/>
${list},<br/>
${array},<br/>

${userModel},<br/>
${userModel.userId},<br/>
${userModel.username},<br/>
${userModel.date},<br/>
<s:date name="userModel.date" format="yyyy-MM-dd HH:ss:mm" nice="false"/>
<br/>
${userModel.amt}

</body>
</html>