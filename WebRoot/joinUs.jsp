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

    <title>杭州标准</title>
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


    <%--肿瘤累计直径小于等于8 cm或肿瘤累计直径大于8 cm、术前AFP小于等于400 ng/mL且组织学分级为高/中分化。--%>
    <%--无门静脉癌栓。--%>

        <div class="row" style="margin-top: 20px;margin-left: 20px;">
            <div style="float: left;margin-left: 20px;margin-right: 20px;">
                <input onclick="selectStandard()" id="check2" style="width: 20px;height: 20px;"
                       type="checkbox">
            </div>


            <div style="float: left"><span>无门静脉癌栓。</span></div>


        </div>

    <div class="row" style="margin-top: 20px;margin-left: 20px;">
        <div style="float: left;margin-left: 20px;margin-right: 20px;">
            <br>
            <input onclick="selectStandard()" id="check1" style="width: 20px;height: 20px;"   type="checkbox">
            <br>
        </div>
        <label style="float: left"><span>肿瘤累计直径小于等于8 cm或肿瘤累计直径大于8 cm,<br>
            术前AFP小于等于400 ng/mL且组织学分级为高/中分化。</span></label>


    </div>



   <div style="border-bottom:1px solid #e5e5e5;margin:20px 20px 20px 20px;"></div>

    <label id="standard" style="font-size: 20px;margin-left: 60px;color: #49ADEB;">当前不符合杭州标准</label>
</div>

</body>

<script type="text/javascript">

   function selectStandard() {
       var c1=  $("#check1").is(':checked');
       var c2=  $("#check2").is(':checked');
         if(c1&&c2){
             $("#standard").text("当前符合杭州标准")
         } else{
             $("#standard").text("当前不符合杭州标准")
         }
   }
</script>
</html>
