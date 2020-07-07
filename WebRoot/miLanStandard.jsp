<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
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

    <title>米兰标准</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">

    <meta name="viewport"
          content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <!--
<link rel="stylesheet" type="text/css" href="styles.css">
-->
    <link href="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet">

    <style type="text/css">
        html, body {
            margin: 0;
            width: 100%;
            background-color: #ffffff;
        }
    </style>
</head>

<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>

<script src="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>


<body>
<div class="main">


    <%--①单个肿瘤直径不超过5ｃｍ,或肿瘤数目不超过3个,最大直径不超过3ｃｍ--%>
    <%--②不伴有血管及淋巴结的侵犯。--%>
    <div class="row" style="margin-top: 20px;margin-left: 20px;">
        <div style="float: left;margin-left: 20px;margin-right: 20px;">
            <br>
            <input onclick="selectStandard()" id="check1" style="width: 20px;height: 20px;"   type="checkbox">
            <br>
        </div>
        <label style="float: left"><span>单个肿瘤直径不超过5ｃｍ,<br>
            或肿瘤数目不超过3个,<br>
            最大直径不超过3ｃｍ。</span></label>


    </div>

    <div class="row" style="margin-top: 20px;margin-left: 20px;">
        <div style="float: left;margin-left: 20px;margin-right: 20px;">
            <input onclick="selectStandard()" id="check2" style="width: 20px;height: 20px;"
                   type="checkbox">
        </div>


        <div style="float: left"><span>不伴有血管及淋巴结的侵犯。</span></div>


    </div>

        <div style="border-bottom:1px solid #e5e5e5;margin:20px 20px 20px 20px;"></div>

    <label id="standard" style="font-size: 20px;margin-left: 60px;color: #49ADEB;">当前不符合米兰标准</label>
</div>

</body>

<script type="text/javascript">

    function selectStandard() {
        var c1=  $("#check1").is(':checked');
        var c2=  $("#check2").is(':checked');
        if(c1&&c2){
            $("#standard").text("当前符合米兰标准")
        } else{
            $("#standard").text("当前不符合米兰标准")
        }
    }
</script>
</html>
