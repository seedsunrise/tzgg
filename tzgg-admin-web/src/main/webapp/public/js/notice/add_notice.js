/**
 * 通知公告新建
 */
;"use strict";
var dataObj = {
	title:"",
	content:"",
	range:"",
	notifyType:"",
	publishType:{},
	notifyRelationOrgs:[],
	notifyPersonRelations:[],
	attachmentses:[]/*附件列表无论是*/
};
var uploaded = [];/*存放已经上传的附件*/
var Fun = function(){};
    Fun.prototype.count = function(id,l){
        var v = "",
            i = $("#"+id),
            s = i.siblings("i").eq(0);
        var a = 0;
        i.on("input",function(){
            v = $(this).val();
            a = (v.split("")).length;
            
           
            if($(this).attr("data-length") === "4000"){
            	if(dataObj[id].length<l+1){
            		 dataObj[id] = $.trim(v);
            		 if(dataObj[id].length>l){
            			s.text(l+"/"+l);
 	           			dataObj[id] = dataObj[id].substring(0,l);
 	           			$(this).val(dataObj[id]);
            		 }else{
            			 s.text(dataObj[id].length+"/"+l);
            		 }
            	}else{
            		$(this).val(v.substring(0,l));
            		dataObj[id] = dataObj[id].substring(0,l);
            		s.text(dataObj[id].length+"/"+l);
            	}
            	
            }else{
            	if(dataObj[id].length<l+1){
	           		dataObj[id] = v;
	           		if(dataObj[id].length>l){
	           			s.text(l+"/"+l);
	           			$(this).val(dataObj[id].substring(0,l));
	           			dataObj[id] = dataObj[id].substring(0,l);
	           		}else{
	           			s.text(dataObj[id].length+"/"+l);
	           		}
            	}else{
	           		$(this).val(dataObj[id].substring(0,l));
	           		dataObj[id] = dataObj[id].substring(0,l);
	           		s.text(dataObj[id].length+"/"+l);
            	}
            	
            }
            
            if($(this).val() !== ""){
            	$(this).parent(".err").removeClass("has-error");
            	$(this).parent(".err").siblings("span").eq(0).fadeOut();
            }
        });
    };
$(function(){
	$("#content").on('keydown',function(e) {
        if (e.keyCode == 13) {
            e.preventDefault();
            var indent = '\n'+'        ';
            var start = this.selectionStart;
            var end = this.selectionEnd;
            var selected = window.getSelection().toString();
            selected = indent + selected.replace(/\n/g, '\n' + indent);
            this.value = this.value.substring(0, start) + selected
                    + this.value.substring(end);
            this.setSelectionRange(start + indent.length, start
                    + selected.length);
            var v = $(this).val();
            if(dataObj["content"].length<4001){
       		 	dataObj["content"] = v;
	       		if(dataObj["content"].length>4000){
	       			$(this).siblings("i").text(4000+"/"+4000);
	           		dataObj["content"] = dataObj["content"].substring(0,4000);
	           		$(this).val(dataObj["content"]);
	       		}else{
	       			$(this).siblings("i").text(dataObj["content"].length+"/"+4000);
	       		}
            }else{
	       		$(this).val(v.substring(0,4000));
	       		dataObj["content"] = dataObj["content"].substring(0,4000);
	       		$(this).siblings("i").text(dataObj["content"].length+"/"+4000);
            }
        }
    })
	
	
	dataObj["range"] = $("#allClicks").find(".range input:radio:checked").eq(0).val();
	/*输入统计输入字数效果*/
	 var font = new Fun();
	 	font.count("title",100);
	 	font.count("content",4000);
    
    
/*-------------------------------附件上传----------------------------------------*/
    var abortVar,tag = 0;

    /*
     * statusFun
     * 上传是否完成的状态标记
     * 判断是否有文件正在上传
     */
    var statusFun = {
        fileName:"",/*存放上传文件名字的变量 请求发送后置为空*/
        s:true,
        get:function(){
            return this.s;
        },
        set:function(status){
            this.s = status;
        }
    };
    var animateFun = function(id,time){
        var l = 0,i = 0;
        var m = "progressIng_"+id,
                e = document.getElementById(m);
        var set = setInterval(function(id){
            if(l < 80){
                l += 1;
                e.style.width = l+"%";
                $("#"+m).siblings("span").eq(0).text((++i)+"%");
            }else{
                if(statusFun.get() && l < 100){
                    l += 5;
                    e.style.width = l+"%";
                    $("#"+m).siblings("span").eq(0).text((i += 5)+"%");
                }
                if(parseInt(l) == 100){
                	$("#"+m).parent(".progressIng").fadeOut().remove();
                    clearInterval(set);
                }
            }
        },time);
    };
    /*上传附件请求*/
    var size = 0;
    $("#enclosure").fileupload({
        url:flag_interface.upload,
        dataType : 'json',
        add : function(e, data){
            statusFun.fileName = data.files[0].name;
            if(statusFun.get()){
            	var l = $("#list_enclosure").children();
    			if(l.length === 5){
    				Messager.alert({Msg :"您上传的文件数量超过上传限制，请重新选择。",iconImg : "warning",isModal : false});
    				return false;
    			}
    			var f = /(jpg|jpeg|png|doc|docx|ppt|pptx|xls|xlsx|txt|pdf)$/.test(data.files[0].name);
            	size = Math.ceil(data.files[0].size/1024);
            	if(size===0){
            		Messager.alert({Msg :"您上传的文件内容为空，请重新选择。",iconImg : "warning",isModal : false});
            		return false;
            	}
            	if(size>5120){
    				Messager.alert({Msg : "您上传的文件大小超过了上传限制，请重新选择。",iconImg : "warning",isModal : false});
    				return false;
    			}
            	if (f) {
            		abortVar = data.submit();
    			} else {
    				Messager.alert({Msg :"您上传的文件格式不正确，请重新选择。",iconImg : "warning",isModal : false});
    				return false;
    			}
                $("#list_enclosure").append('<li class="overH p_t_15">'
                        +'<span class="pull-left text-center p_r_5">'+statusFun.fileName+'</span>'
                        +'<div class="posR col-sm-5 p_a_0 text-center progressIng" style="top: 2px;height:12px;background-color:#ccc;border-radius: 5px;">'
                        +'<span class="posR" style="top: -2px;right: -190px;font-size:12px;z-index:1;">0%</span>'
                        +'<div class="progressIng posA" id="progressIng_'+tag+'"'
                        +'style="top:0;left:0;width:0%;height:100%;background-color:#f00;border-radius: 5px;"></div>'
                        +'</div><span class="del">删除</span></li>');
                statusFun.fileName = "";
                statusFun.set(false);
                animateFun(tag,((size/1024).toFixed(2))*100);
            }else{
            	Messager.alert({Msg : "已经有一个文件正在上传中...",iconImg : "warning",isModal : false});
            }
        },
        done:function(e,d){
        	try{
        		var data = d.result;
            	var o = {
            		path    :data.body.result.path,
            		size    :size/1024,
            		name    :data.body.fileName,
            		fileType:data.body.result.type
            	};
            	uploaded.push(o);
        	}catch(e){
        		Messager.alert({Msg : "上传失败!",iconImg : "warning",isModal : false});
        		$(".progressIng").eq(0).parents("li").remove();
        	}finally{
        		statusFun.set(true);
                size = 0;
                tag += 1;
        	}
        	
        }
    });
  /*上传资源删除  此处涉及 正在上传中的删除 必须清除请求*/
    $("#list_enclosure").on("click",".del",function(){
       $(this).parent("li").remove();
       var s = $(this).prev(".progressIng"),
       	   f =  $(this).siblings("span").eq(0).text();
       var arr = [];
       /*将要删除的值从数据中剔除*/
       $.each(uploaded,function(){
    	   if(this.name !== f){
    		   arr.push(this);
    	   }
       });
       uploaded = arr;
       if(s.length !== 0){
			size = 0;
			abortVar.abort();/*取消请求*/
			statusFun.set(true);/*告知状态的改变*/
       }
    });
/*-------------------------------新建保存 发布  返回----------------------------------------*/   
    /**
     * 此处保存与发布使用一个方法
     */
    var saveData = function(v){
    	dataObj["notifyRelationOrgs"] = [],
    	dataObj["notifyPersonRelations"] = [],
        dataObj["attachmentses"] = [];/*附件列表无论是*/
    	var isSub = true;
    	/*判断必填项是否为空*/
    	if($("#title").val().trim() === ""){
    		$("#title").parent(".err").addClass("has-error");
    		$("#title").parent(".err").siblings("span").eq(0).fadeIn();
    		isSub = false;
    	}else{
    		$("#title").parent(".err").removeClass("has-error");
    		dataObj.title = ($("#title").val()).replace(/\n|\r\n/gi,"<br>");
    	}
    	if($("#content").val().trim() === ""){
    		$("#content").parent(".err").addClass("has-error");
    		$("#content").parent(".err").siblings("span").eq(0).fadeIn();
    		isSub = false;
    	}
    	if(dataObj["range"] === "common"){
    		dataObj["notifyType"] = "";
    		dataObj["publishType"] = "";
    		dataObj["notifyRelationOrgs"] = [];
    		dataObj["notifyPersonRelations"] = [];
    	}else{
    		dataObj["publishType"] = $("#tab").find(".tabed").eq(0).attr("data-name");/*获取是发送部门还是个人*/
    		var data;
    		/*member manager*/
    		if(dataObj["publishType"] === "party"){/*指定发布选择部门*/
    			dataObj["notifyType"] = $("#countList").find("input:radio:checked").eq(0).val();
    			data = $.filterData(1,"isChecked",party);/*筛选出所有被选中的数据*/
    			if(dataObj["notifyType"] !== "manager"){
    				$.each(data,function(){/*部门选择通知部门所有成员*/
        				var obj = {};
        					obj["orgId"]   = this.id;
        					obj["orgName"] = this.name;
        				dataObj["notifyRelationOrgs"].push(obj);	
        			});
    				dataObj["notifyPersonRelations"] = [];
    				if(dataObj["notifyRelationOrgs"].length === 0){
        				Messager.alert({Msg : "请勾选指定的部门或者人员",iconImg : "warning",isModal : false});
            			$("#loading").fadeOut();
            			return false;
        			}
    			}else{
    				dataObj["notifyRelationOrgs"] = [];
    				$.each(data,function(){
        				var obj = {};
        					obj["orgId"]   = this.id;
        					obj["orgName"] = this.name;
        					obj["userId"]  = "";
        					obj["userName"]= "";
        				dataObj["notifyPersonRelations"].push(obj);	
        			});
    				if(dataObj["notifyPersonRelations"].length === 0){
        				Messager.alert({Msg : "请勾选指定的部门或者人员",iconImg : "warning",isModal : false});
            			$("#loading").fadeOut();
            			return false;
        			}
    			}
    			
    		}else{/*指定发布选择个人*/
    			dataObj["notifyRelationOrgs"] = [];
    			dataObj["notifyType"] = "";
    			data = $.filterData(1,"isChecked",member);
    			$.each(data,function(){
    				var obj = {};
    					obj["userId"]   = this.id;
    					obj["userName"] = this.name;
    					obj["phone"] = this.phone;
    				dataObj["notifyPersonRelations"].push(obj);	
    			});
    			if(dataObj["notifyPersonRelations"].length === 0){
    				Messager.alert({Msg : "请勾选指定的部门或者人员",iconImg : "warning",isModal : false});
        			$("#loading").fadeOut();
        			return false;
    			}
    		}
    	}
    	dataObj["attachmentses"] = uploaded.concat(dataObj["attachmentses"]);
    	
    	var loc = $.dataParse();
    	if(loc["id"]){/*解析地址栏 判断是否有id*/
    		dataObj["id"] = loc["id"];
    	}
    	
    	if(isSub){
    		var url = (v === "s")?flag_interface.notice.save:flag_interface.notice.savePub; /*判断是调用发布还是保存*/
    		$.saveAjaxApp($.dataBox(url,"post",$.stringifyJson(dataObj)),function(res){
    			if(res.head.resultCode === "200" || res.head.resultCode === 200){
    				$.openUrl("notice_management.html");
    			}else{
    				$("#loading").fadeOut();
    				Messager.alert({Msg : (v === "s")?"保存失败":"发布失败",iconImg : "warning",isModal : false});
    			}
    		});
    	}else{
    		$("#loading").fadeOut();
    	}
    };
    $("#save").on("click",function(){
    	var l = $("#list_enclosure").find("div");
    	if(l.length >0){
    		/*此时存在还有文件正在上传*/
    		Messager.alert({Msg : "有附件正在上传，请等待上传完毕",iconImg : "warning",isModal : false});
    	}else{
    		$("#loading").fadeIn();
        	saveData("s")
    	}
    });

    $("#publish").on("click",function(){
    	var l = $("#list_enclosure").find("div");
    	if(l.length >0){
    		/*此时存在还有文件正在上传*/
    		Messager.alert({Msg : "有附件正在上传，请等待上传完毕",iconImg : "warning",isModal : false});
    	}else{
    		$("#loading").fadeIn();
        	saveData("p")
    	}
    });
    /*返回*/
    $("#back").on("click",function(){
    	/*此时如果值不为空 给予提示是否放弃此次编辑*/
    	if(($("#backModal")).length === 0){
    		$.openUrl("/page/notice/notice_management.html");
    	}else{
    		if(dataObj["title"] !=="" || dataObj["content"] !== ""){
    			$("#backModal").modal("show");
    		}else{
    			$.openUrl("/page/notice/notice_management.html");
    		}
    	}
    });
    $("#backSure").on("click",function(){
    	$.openUrl("/page/notice/notice_management.html");
    });
    $("#title_back").on("click",function(){
    	if(($("#backModal")).length === 0){
    		$.openUrl("/page/notice/notice_management.html");
    	}else{
    		if(dataObj["title"] !=="" || dataObj["content"] !== ""){
    			$("#backModal").modal("show");
    		}else{
    			$.openUrl("/page/notice/notice_management.html");
    		}
    	}
    });
});

   

   
    

   


    
    


