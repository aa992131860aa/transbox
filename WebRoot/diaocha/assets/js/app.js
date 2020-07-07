function submitBtn() {
  var keshi = $("#doc-ipt-3").val()
  var Name = $("#doc-ipt-pwd-2").val()
  var app_sel1 = document.getElementsByName('doc-radio-1')
  var app_sel2 = document.getElementsByName('doc-radio-2')
  var app_sel3 = document.getElementsByName('doc-radio-3')
  var app_sel4 = document.getElementsByName('doc-radio-4')
  var app_sel5 = document.getElementsByName('doc-radio-5')
  var app_sel6 = document.getElementsByName('doc-radio-6')
  var app_suggest = $("#doc-ipt-4").val()
  var device_sel1 =document.getElementsByName('doc_radio-1')
  var device_sel2 =document.getElementsByName('doc_radio-2')
  var device_sel3 =document.getElementsByName('doc_radio-3')
  var device_sel4 =document.getElementsByName('doc_radio-4')
  var device_sel5 =document.getElementsByName('doc_radio-5')
  var device_sel6 =document.getElementsByName('doc_radio-6')
  var device_suggest = $("#doc-ipt-5").val()
  var obj = [app_sel1,app_sel2,app_sel3,app_sel4,app_sel5,app_sel6,device_sel1,device_sel2,device_sel3,device_sel4,device_sel5,device_sel6]
  function fn(){
    var num = [];
    for(var i=0; i<obj.length; i++){
      // console.log(obj[i])
      for(var j=0; j<obj[i].length; j++){ 
        // console.log(obj[i].length)
        if(obj[i][j].checked){
          num[i] = (obj[i][j].value);
        }
      }
    }
    return num
  }
  
  var num = fn(obj)

  function fn1(){
    var num1 = [];
    for(var k=0; k<num.length; k++){
      if(num[k] != null){
        num1.push(num[k])
      }
    }
    return num1
  }

  var num1 = fn1(num)
  var url = "https://www.lifeperfusor.com/transbox/box.do?action=insertQuestion&department=" + keshi + "&question=&name=" + Name + "&app_sel1=" + num[0] + "&app_sel2=" + num[1] + "&app_sel3=" + num[2] + "&app_sel4=" + num[3] + "&app_sel5=" + num[4] + "&app_sel6=" + num[5] + "&app_suggest=" + app_suggest + "&device_sel1=" + num[6] + "&device_sel2=" + num[7] + "&device_sel3=" + num[8] + "&device_sel4=" + num[9] + "&device_sel5=" + num[10] + "&device_sel6=" + num[11] + "&device_suggest=" + device_suggest
  var Btn = $('#btnn')
  if(keshi.length>=2 && Name.length>=2 && num1.length == 12){
        $.ajax({
        url:url,
        dataType:"jsonp",
        jsonp:"callback",
        processData:false,
        type:"get",
        success: function (data) {
    // alert(data);
    location.href='./success.html'
         },error:function(err){
        window.open('./success.html')
      }
   
    });
  }else {
    $('#my-alert').modal({
      relatedTarget: this
    })
  }

}
// data-am-modal="{target: '#my-alert'}"