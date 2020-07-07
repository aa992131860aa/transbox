<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="
		<%=basePath%>">

		<title>注册</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<style type="text/css">
pre {
	white-space: pre-wrap;
	word-wrap: break-word;
}

html,body {
	height: 100%;
	width: 100%;
	margin: 0;
	overflow: hidden;
	padding: 0;
}

body {
	background: url(./images/bg7.png) no-repeat;
	width: 100%;
	height: 100%;
	background-size: 100% 100%;
	position: absolute;
	filter: progid :                 
		   DXImageTransform.Microsoft.AlphaImageLoader (     
		 
		    
		        src =         
		          './images/bg.png', sizingMethod =                    
		'scale' );
}

button {
	-moz-border-radius: 15px; /* Firefox */
	-webkit-border-radius: 15px; /* Safari 和 Chrome */
	border-radius: 5px /* Opera 10.5+, 以及使用了IE-CSS3的IE浏览器 */;
	behavior: url(ie-css3.htc); /* 通知IE浏览器调用脚本作用于'box'类 */
}

.content {
	
}

#wrapper {
	display: table;
	width: 100%;
	height: 100%;
}

#cell {
	display: table-cell;
	vertical-align: middle;
}
</style>

		<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
		<script
			src="https://cdn.bootcss.com/twitter-bootstrap/4.1.3/js/bootstrap.min.js"></script>
		<link
			href="https://cdn.bootcss.com/twitter-bootstrap/4.1.3/css/bootstrap.min.css"
			rel="stylesheet">

	</head>

	<body>
		<div id="wrapper" class="content" style="">
			<div id="cell">


				<div>
					<center style="color: #ffffff; font-size: 16px">
						会员注册
					</center>
					<center style="color: #ffffff; font-size: 12px">
						Registered members
					</center>
				</div>


				<div class="form-group m-0" style="margin-top: 5px;">
					<label for="name" class="col-sm-offset-2"
						style="color: #ffffff; margin-left: 10%;">
						姓名 Name
					</label>
					<input id="name"
						style="border: 1px solid #9621C1; margin-left: 10%; width: 80%;"
						type="text" class="form-control " id="name">
				</div>
				<div class="form-group  m-0" style="display: none;">
					<label for="name" style="color: #ffffff; margin-left: 10%;">
						性别 Sex
					</label>



					<div style="color: #ffffff; margin-left: 10%;">
						<input id="man" style="width: 25px; height: 25px;" type="radio"
							checked="checked" name="1" />
						男 man
						<input style="width: 25px; height: 25px;" id="woman" type="radio"
							name="1" />
						女 woman

					</div>

				</div>
				<div class="form-group  m-0">
					<label for="name" style="color: #ffffff; margin-left: 10%;">
						手机 Telephone
					</label>
					<input id="phone"
						style="border: 1px solid #9621C1; margin-left: 10%; width: 80%"
						type="text" class="form-control " id="name">
				</div>

				<div class="form-group  m-0">
					<label for="name" style="color: #ffffff; margin-left: 10%;">
						单位 CO./Org
					</label>
					<input id="unit"
						style="border: 1px solid #9621C1; margin-left: 10%; width: 80%"
						type="text" class="form-control " id="name">
				</div>

				<div class="form-group  m-0">
					<label for="name" style="color: #ffffff; margin-left: 10%;">
						邮箱 Email
					</label>
					<input id="email"
						style="border: 1px solid #9621C1; margin-left: 10%; width: 80%"
						type="text" class="form-control " id="name">
				</div>
				<div class="form-group  m-0">
					<label for="name" style="color: #ffffff; margin-left: 10%;">
						职务 Job
					</label>
					<input id="job"
						style="border: 1px solid #9621C1; margin-left: 10%; width: 80%"
						type="text" class="form-control " id="name">
				</div>

				<button class="btn"    onclick="confirm()"
					style="margin-top: 5%; margin-left: 10%; width: 80%; background-color: #9621C1; padding-top: 10px; padding-bottom: 10px; border: 1px solid #9621C1; font-size: 14px; color: #ffffff;">
					提交
				</button>






			</div>
		</div>
	</body>
	<script type="text/javascript">
	$(function() {

		$('body').height($('body')[0].clientHeight);
	});

	function confirm() {
	 
	var name = $("#name").val();
	var phone = $("#phone").val();
	var unit = $("#unit").val();
	var email = $("#email").val();
	var job = $("#job").val();
	   $.get("/transbox/user.do?action=insertMUser&name="+name+"&phone="+phone+"&unit="+unit+"&email="+email+"&job="+job, function(result){
     
     window.location.href="/transbox/m_index.html";
  });
	
	}
</script>
</html>
