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

    <title>医学小工具</title>
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


    <div class="row">
        <div style="font-size: 18px;margin-left:10px;margin-top:20px;margin-bottom:20px;color: #49ADEB"
             class="col-sm-12 col-md-12 col-12"><strong>终末期肝病模型(12岁及12岁以上)</strong></div>


    </div>
    <div class="row">

        <label style="" class="col-sm-7 col-md-7 col-7"><span style="margin-left: 10px;">类型</span></label>
        <div style=" margin-left: 0;padding-left: 0;" class="form-group col-sm-4 col-md-4 col-4">

            <select id="sel" onchange="sel()" class="form-control">
                <option value="0">SI</option>
                <option value="1">US</option>
            </select>

        </div>

    </div>
    <div class="row" style="margin-top: 20px;">
        <label style="" class="col-sm-7 col-md-7 col-7"><span style="margin-left: 10px;font-size: 14px;">血清胆红素</span></label>

        <input onchange="standard()" id="bil" type="number" class="form-control col-sm-2 col-md-2 col-2">
        <label id="m1" class="col-sm-2 col-md-2 col-2">μmol/L</label></div>
</div>
<div style="margin-top: 20px;" class="row">
    <label style="" class="col-sm-7 col-md-7 col-7"><span style="margin-left: 10px;font-size: 14px;">国际标准化比值(INR)</span></label>


    <input onchange="standard()" id="inr" type="number" class="form-control col-sm-2 col-md-2 col-2">

</div>
<div style="margin-top: 20px;" class="row">
    <label style="" class="col-sm-7 col-md-7 col-7"><span style="margin-left: 10px;font-size: 14px;">血清肌酐</span></label>


    <input onchange="standard()" id="cr" type="number" class="form-control col-sm-2 col-md-2 col-2">
    <label id="m3" class="col-sm-2 col-md-2 col-2">μmol/L</label></div>
</div>

<div style="margin-top: 20px" class="row">
    <label style="" class="col-sm-7 col-md-7 col-7"><span style="margin-left: 10px;font-size: 14px;">有无酒精肝或胆汁性肝硬化</span></label>
    <input id="check2" onclick="standard()" class="col-sm-1 col-md-1 col-1" style="width: 20px;height: 20px;"
           type="checkbox">
</div>

<div style="margin-top: 50px;" class="row">
    <label class="col-sm-6 col-md-6 col-6"><span style="margin-left: 10px;">MELD评分</span></label>

    <button id="standard" onclick="standard()" type="button" class="btn btn-primary"
            style="background-color: #49ADEB;border: 1px solid #49ADEB">确定评分
    </button>
    <label id="num" onclick="showNum()"
           style="display:none;color: red;font-size: 24px;padding-left: 20px;padding-right: 20px;"
           class="">0分</label>


</div>

</body>
<script type="text/javascript">
    function sel() {

        var sel = $("#sel").val();
        var m1 = $("#m1");
        var m3 = $("#m3");
        if (sel == 0) {
            m1.text("μmol/L")
            m3.text("μmol/L")
        } else {
            m1.text("mg/dl")
            m3.text("mg/dl")
        }
        $("#bil").val('')
        $("#inr").val('')
        $("#cr").val('')
        standard()
        $("#num").hide();
        $('#check2').prop('checked',false)
        $("#standard").show();
    }

    // US
    // MELD=3.78×ln [T-BiL(mg/dl)]+11.2×ln[INR]+9.57×ln[Cr (mg/dl)] + 6.43
    //
    // SI
    // MELD=3.78×ln[T-BiL(μmol/L)÷17.1]+11.2×ln[INR]+9.57×ln[Cr (μmol/L)÷88.4] + 6.43
    //
    // ln log 胆红素(T-BiL) 国际标准化比值(INR) 血清肌酐(Cr)
    function standard() {
        var sel = $("#sel").val();

        var bil = $("#bil").val();
        var inr = $("#inr").val();
        var cr = $("#cr").val();
        var total = 0;
        var dyh = 1;
        if (parseFloat(cr) < 88.4) {
            cr = 88.4;
        }
        var c2 = $("#check2").is(':checked');

        if (c2) {
            dyh = 0
        }

        if (sel == 0) {

            total = 3.8 * Math.log(bil / 17) + 11.2 * Math.log(inr) + 9.6 * Math.log(cr / 88.4) + 6.4*dyh
        } else {
            total = 3.8 * Math.log(bil) + 11.2 * Math.log(inr) + 9.6 * Math.log(cr) + 6.4*dyh
        }


        if (total > -10000) {
            if (total < 6) {
                total = 6;
            }
            $("#num").show();
            $("#num").text(total.toFixed(2) + "分")
            $("#standard").hide();
        } else {

        }
    }

    function showNum() {
        $("#num").hide();

        $("#standard").show();
    }
</script>
</html>
