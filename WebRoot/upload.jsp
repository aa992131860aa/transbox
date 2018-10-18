<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>My JSP 'upload.jsp' starting page</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

	</head>

	<body>
		<form action="upload.do" enctype="multipart/form-data" method="post">
			version:
			<input type="text" name="version">
			<br />
			upload:
			<input type="file" name="uploadFile">
			<br />

			<input type="submit" value="提交">
		</form>
		<br/><br/>
		<form action="push.do" method="post">
		        推送的内容:<input type="text" name="push_content"/>
		        <br/>
		     <input type="submit" value="提交"/>  
		   
		</form>
	</body>
</html>

