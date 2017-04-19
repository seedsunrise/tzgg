;"use strict";
/*操作模态时公共方法*/
var DoFun = function(){};
    DoFun.prototype.modalShow = function(id_hidden,v,modal_id){
        $("#"+id_hidden).val(v);
        $("#"+modal_id).modal("show");
    };
    DoFun.prototype.modalSure = function(id_hidden,modal_id,event,callback){
    	$("#"+event).on("click",function(){
    		var v = $("#"+id_hidden).val();
            callback(v,modal_id);
    	})
    };
var isPage = {s:true};
/*查询参数*/
var dataAjax = function(){
	return {
		"title"    :isPage.s?"":$("#title").val(),
		"status"   :isPage.s?"":$("#status").val(),
		"startTime":isPage.s?"":$("#start").val()==""?"":$("#start").val()+" 00:00:00",
		"endTime"  :isPage.s?"":$("#end").val()==""?"":$("#end").val()+" 23:59:59"
	};
};
/*通知管理列表加载公用方法*/
var loadList = function(dataAjax){
	var o = {
		body:{
			results:[]
		}
	};
	dataAjax["pageNo"] = pageNo;
	dataAjax["pageSize"] = parseInt(pageSize,10);

	$.asyncT($.dataBox(flag_interface.notice.management,"post",dataAjax),"loading",function(res){
		$("#loading").fadeOut();
		if(res.head.resultCode === 200 || res.head.resultCode === "200"){
			page.setPage(res.body.totalRecord,$("#pageCon"));
			$.loadData("showData","template",res);
		}else{
			page.setPage(0,$("#pageCon"));
			$.loadData("showData","template",o);
			Messager.alert({Msg : res.head.message,iconImg : "warning",isModal : false});
		}
		$("#showData").find(".publishOrganize").hover(function(){
			$(this).addClass("c-primary");
		},function(){
			$(this).removeClass("c-primary");
		})
	});
};
/*分页时调用的方法*/
var loadPage = function(){
	$("#ui-table").find("input:checkbox").prop("checked",false).siblings("label").removeClass(":after");
	loadList(dataAjax());
};  

$(function(){ 
	/*分页初始化*/
	page.initPage($("#pageCon"));
	/*$.asyncT($.dataBox(flag_interface.isManager,"post",""),"loading",function(res){
		if(res.head.resultCode === 200 || res.head.resultCode === "200"){
			var s = parseInt(res.body.roleType);0 是超级管理员
			s = true;
			if(s){
				$("#new_sub_notice").fadeIn();
				$("#sel_all").fadeIn();
				$("#theadAll").children().fadeIn();
			}
			加载列表
			$.isManager = s;
			loadList(dataAjax());
		}
	});*/
	loadList(dataAjax());
	$("#status").on("change",function(){
		if($.isManager){
			if($(this).val() !== "publish" && $(this).val() !== ""){
				$("#not_allowed").hide();
			}else{
				$("#not_allowed").show();
			}
		}
	});
	
	
	
	var doing = new DoFun();
	/*时间初始化*/
	$("#start").on("click",function(e){
		e.preventDefault();
		if($("#end").val() === ""){
			laydate($.layOptionM("#start","","d","1970-01-01",$.forDate("","-")));
		}else{
			laydate($.layOptionM("#start","","d","1970-01-01",$("#end").val()));
		}
	});
	$("#end").on("click",function(e){
		e.preventDefault();
		if($("#start").val() === ""){
			laydate($.layOptionM("#end","","d","1970-01-01",$.forDate("","-")));
		}else{
			laydate($.layOptionM("#end","","d",$("#start").val(),$.forDate("","-")));
		}
	});
	
	/*监听事件来控制清除条件的样式*/
	var clearFun = function(){
		$("#title").on("input",function(){
			notNullStr();
		});
		$("#status").on("change",function(){
			notNullStr();
		});
		$("#start,#end").on("input",function(){
			notNullStr();
		});
		
		var notNullStr = function(){
			if(isNullStr()){
				$("#clear_condition").css("color","#333");
			}else{
				$("#clear_condition").css("color","#ccc");
			}
		};
		var isNullStr = function(){
			var arr = $("#ui-form").find("input,select");
			var s = false;
			$.each(arr,function(i){
				if(this.value !== ""){
					s = true;
					return false;
				}
			});
			return s;
		}
	};
	clearFun();
	/*点击查询*/
    $("#search").on("click",function(){
        /*调用查询的方法*/
    	isPage.s = false;
    	loadList(dataAjax());
    });
    /*清除查询条件的样式 因样式是平台来的 所以用js实现这个效果*/
    $("#clear_condition").hover(function(){
        $(this).addClass("c-primary");
    },function(){
        $(this).removeClass("c-primary");
    });
    /*清除查询条件后 加载默认数据*/
    $("#clear_condition").on("click",function(){
    	$("#ui-form").find("input,select").val("");
    	/*调用加载数据的方法*/
    	isPage.s = true;
    	loadList(dataAjax);
    });
    /*全选*/
    $(".clickRadio").on("click",function(e){
        e.preventDefault();
        if($(this).children("input").eq(0).prop("checked")){
            $(".clickRadio").children("input").prop("checked",false);
            $(".clickRadio").children("label").removeClass(":after");

            $("#showData").find(".sel").find("input:checkbox").prop("checked",false);
        }else{
            $(".clickRadio").children("input").prop("checked",true);
            $(".clickRadio").children("label").addClass(":after");

            $("#showData").find(".sel").find("input:checkbox").prop("checked",true);
        }
    }); 
    /*表格操作时请求的参数*/
    var doData = function(id){
    	var s;
    	try{
    		var m = JSON.parse(id);
    	}catch(e){
    		var m = {};
    	}
    	
    	if(Object.prototype.toString.call(m) == "[object Array]"){
    		$.each(m,function(i){
    			s = m[i].replace(/&/gi," ").split(" ");
    			var obj = {
					id   :s[0],
        			range:s[1]
    			};
    			m[i] = obj;
    		});
    		return m; 
    	}else{
    		if(!$.isArray(id)){
        		s = id.replace(/&/gi," ").split(" ");
        		var o = {
        			id   :s[0],
        			range:s[1]
        		};
        		return o;
        	}else{
        		var arr = [];
        		$.each(id,function(){
        			s = this.replace(/&/gi," ").split(" ");
        			var o = {
    					id   :s[0],
    	    			range:s[1]
        			};
        			arr.push(o);
        		});
        		return arr;
        	}
    	}
    	
    };
    /*批量删除*/
    $("#batch").on("click",function(){
        var d = $("#showData").find("input:checkbox:checked"),
        	arr = [];
        if(d.length === 0){
            //如果没有勾选给予提示;
        	Messager.alert({Msg : "请您先勾选需要操作的行数据",iconImg : "warning",isModal : false});
        }else{
            //如果勾选了调用方法;
        	$.each(d,function(){
        		arr.push(this.id);
        	});
        	doing.modalShow("delId",$.stringifyJson(arr),"isDel");
        }
    });
    /*表格按钮操作*/
    $("#showData").on("click","button",function(){
    	var t = $(this).attr("data-name"),
    		i = $(this).children("span").eq(0).attr("data-name");
    	switch(t){
    		case "del":
    			doing.modalShow("delId",i,"isDel");
    			break;
    		case "close":
    			doing.modalShow("closeId",i,"isClose");
    			break;
    		default:
    			doing.modalShow("sendId",i,"isSend");
    			break;
    	}
    }); 
    
    /*表格操作 对于返回的结果做提示函数*/
    var resTips = function(res,suc){
    	if(res.head.resultCode === 200 || res.head.resultCode === "200"){
			loadList(dataAjax());
			$(".clickRadio").children("input").prop("checked",false);
            $(".clickRadio").children("label").removeClass(":after");
		}else{
			var mes = res.head.message===""?"操作失败":res.head.message,
				str = "";
			if(Object.prototype.toString.call(mes) !== "[object String]"){
				$.each(mes,function(i){
					str +=mes[i].substr(0,20)+"...<br/>";
				});
				str += "<br/>对于以上通知公告操作失败"
			}else{
				str = mes;
			}
			Messager.alert({Msg : str,iconImg : "warning",isModal : false});
		}
    };
    /*表格删除*/
    doing.modalSure("delId","isDel","del",function(id,modal){
    	$("#"+modal).modal("hide");
    	$("#loading").fadeIn();
    	if(id.indexOf("&")>-1){
    		var o = doData(id);
    		if(Object.prototype.toString.call(o) != "[object Array]"){
    			o = new Array(o);
    		}
    	}else{
    		var o = id;
    	}
    	$.asyncTApp($.dataBox(flag_interface.notice.del,"post",JSON.stringify({notifyExes:o})),"loading",function(res){
    		resTips(res,"删除成功");
    		$("#loading").fadeOut();
    	});
    });
  /*表格关闭*/
    doing.modalSure("closeId","isClose","close",function(id,modal){
    	$("#"+modal).modal("hide");
    	$("#loading").fadeIn();
    	var o = doData(id);
    	$.asyncT($.dataBox(flag_interface.notice.close,"post",o),"loading",function(res){
    		resTips(res,"关闭成功");
    		$("#loading").fadeOut();
    	});
    });
    
  /*表格发布*/
    doing.modalSure("sendId","isSend","send",function(id,modal){
    	$("#"+modal).modal("hide");
    	$("#loading").fadeIn();
    	var o = doData(id);
    	$.asyncT($.dataBox(flag_interface.notice.publish,"post",o),"loading",function(res){
    		resTips(res,"发布成功");
    		$("#loading").fadeOut();
    	});
    });
    
});    
    
    
    