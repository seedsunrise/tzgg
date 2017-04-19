/**
 * 通知公告详情
 * 此脚本包括详情中一些点击效果
 */
;
/*下载的方法 不带参数*/
function post(id) {
	var params = {id:id};
    var temp = document.createElement("form");
    temp.action = '/fileUpload/downLoad';
    temp.method = "post";
    temp.style.display = "none";
    for (var x in params) {
        var opt = document.createElement("textarea");
        opt.name = x;
        opt.value = params[x];
        temp.appendChild(opt);
    }
    document.body.appendChild(temp);
    temp.submit();
    return temp;
}
$(function(){
	"use strict";
	var o = $.dataParse();
	var getManager = function(){
		$.asyncT($.dataBox(flag_interface.isManager,"post",""),"",function(res){
			if(res.head.resultCode === 200 || res.head.resultCode === "200"){
				var s = parseInt(res.body.roleType);/*0 是超级管理员*/
				if(s){
					$("#edit").length && $("#edit").fadeIn();
					$("#publish").length && $("#publish").fadeIn();
					$("#closed").length && $("#closed").fadeIn();
				}
			}
		});
	};
	
	$.asyncT($.dataBox(flag_interface.notice.detail,"post",o),"loading",function(res){
		if(res.head.resultCode === 200 || res.head.resultCode === "200"){
			$.loadData("notice","hideArea",res,function(){
				$("#loading").fadeOut();
			});
			$.loadData("img_tag","img_tag_hide",res);
		}else{
			
		}
		getManager();
	});
	/*返回*/
	$(document).on("click","#back",function(){
		$.openUrl("/page/notice/notice_management.html");
	});
	$("#title_back").on("click",function(){
    	$.openUrl("/page/notice/notice_management.html");
    });
	/*发布 关闭*/
	$(document).on("click","#publish,#closed",function(){
		if($(this).attr("id") === "publish"){
			$("#sendId").val($(this).attr("data-name"));
			$("#isSend").modal("show");
		}else{
			$("#closeId").val($(this).attr("data-name"));
			$("#isClose").modal("show");
		}
	});
	$(document).on("click","#send,#close",function(){
		var o;
		if($(this).attr("id") === "send"){
			$("#isSend").modal("hide");
			o = $.dataParse($("#sendId").val());
		}else{
			$("#isClose").modal("hide");
			o = $.dataParse($("#closeId").val());
			
		}
		var url = ($(this).attr("id") === "send")?flag_interface.notice.publish:flag_interface.notice.close;
		$.asyncT($.dataBox(url,"post",o),"loading",function(res){
    		$("#loading").fadeOut();
    		$.openUrl("/page/notice/notice_management.html");
    	});
	});
	/*编辑*/
	$(document).on("click","#edit",function(){
		var v = $(this).attr("data-name");
		$.openUrl("/page/notice/edit_notice.html?"+v);
	});
	
});


