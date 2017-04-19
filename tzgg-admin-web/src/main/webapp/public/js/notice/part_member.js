/**
 * 加载部门和人员  默认加载
 */
var party = [],
	member= [];

$(function(){
	var o = $.dataParse();
	var treeData = function(){
		var part_member_edit = function(editArr,arr){
			if(arr === "p"){
				for(var i =0 ,n = editArr.length;i<n;i++){
					$.each(party,function(k){
						if(editArr[i]["orgId"] === this.id){
							party[k]["isChecked"] = 1;
							return false;
						}
					});
				}
			}else{
				for(var i =0 ,n = editArr.length;i<n;i++){
					$.each(member,function(k){
						if(editArr[i]["id"] === this.id){
							member[k]["isChecked"] = 1;
							return false;
						}
					});
				}
			}
		};
		
		$.asyncT($.dataBox(flag_interface.notice.orgTree,"post",""),"",function(res){
			if(res.head.resultCode === 200 || res.head.resultCode === "200"){
				party = res.body;/*该数据暂时留存 默认只显示第一级的数据*/
				if($("#tab").children(".tabed").eq(0).attr("data-name") === "party"){
					part_member_edit($.notifyPersonRelations,"p");
				}
				var data = $.filterData("","pId",party);
				$("#sel_person_nav").children("span").text(data[0]["name"]);
				$.loadData("partList","partListHide",{body:data});/*此处需要筛选 只先展示第一级*/
			}
		});


		$.asyncT($.dataBox(flag_interface.notice.member,"post",""),"",function(res){
			if(res.head.resultCode === 200 || res.head.resultCode === "200"){
				member = res.body;/*该数据暂时留存*/
				if($("#tab").children(".tabed").eq(0).attr("data-name") === "person"){
					part_member_edit($.notifyPersonRelations,"m");
				}
				$.loadData("personList","personListHide",{body:member});
			}
		});
	};
	if(o["id"]){
		$.asyncF($.dataBox(flag_interface.notice.edit,"post",o),"loading",function(res){
			treeData();
			if(res.head.resultCode === 200 || res.head.resultCode === "200"){
				$("#title").val((res.body.title).replace(/"&nbsp;"/g,"\s")).next().text((res.body.title).length+"/"+(100 - res.body.title.length));
				var m = (res.body.content);
				$("#content").val(m).next().text(m.length+"/"+4000);
				
				$.loadData("allClicks","range",res.body);
				$.loadData("list_enclosure","enclosureArea",res.body);
				/*将已经存在的数据交付该变量存储*/
				dataObj["title"] = res.body.title;
				dataObj["content"] = res.body.content;
				dataObj["range"] = res.body.range;
				uploaded = res.body.attachmentses;/*该变量位于新增脚本的全局，用于存放附件*/
				
				dataObj["notifyType"] = res.body.notifyType === ""?[]:res.body.notifyType;
				dataObj["publishType"] = res.body.publishType === ""?[]:res.body.publishType;
				
				/*根据返回的数据标记接口回来的人员或者部门哪些是被选中的*/
				if(res.body.range !== "common"){
					var h = $("#sel_tree").children().eq(0).css("height");/*根据内容高度来设置动画执行的高度*/
		            $("#sel_tree").animate({height:h},300);
					if( res.body.publishType === "party"){/*部门*/
						
						$.loadData("seledParty","seledPartyHide",res.body);
						$.seledHover("#seledParty span");
						
						if(res.body.notifyType === "manager"){/*部门管理员*/
							/*notifyPersonRelations*/
							$.notifyPersonRelations = res.body.notifyPersonRelations;
							$("#seledPartCount").empty().text((res.body.notifyRelationOrgs).length+" 部门");
							
						}else{/*部门所有成员*/
							/*notifyRelationOrgs*/
							$.notifyPersonRelations = res.body.notifyRelationOrgs;
							$("#seledPartCount").empty().text((res.body.notifyRelationOrgs).length+" 部门");
							$("#countList .selSendParty").find("input:radio:checked").prop("checked",false).end().find(".label_checked").removeClass(":after");
							$("#countList").find(".selSendParty").eq(1).find("input:radio").prop("checked",true);
							$("#countList").find(".selSendParty").eq(1).find(".label_checked").addClass(":after");
						}
					}else{/*本部门成员*/
						$("#tab").children("span").removeClass("tabed").end().children("span").eq(1).addClass("tabed");
						$("#treeContent_party").hide().next().fadeIn();
						$.loadData("seledPerson","seledPersonHide",res.body);
						$.seledHover("#seledPerson span");
						/*notifyPersonRelations*/
						/*此处需要判断部门或者人员数据是否回来了*/
						$.notifyPersonRelations = res.body.notifyPersonRelations;
						$("#seledPersonCount").empty().text((res.body.notifyPersonRelations).length+" 人");
					}
				}
				
			}
			$("#loading").fadeOut();
		});
	}else{
		treeData();
	}
	
	
	
	
	
	/*修改 party member数据中的状态值*/
	var changeCheck = function(arr,v,t){
		if(arr === "p"){
			$.each(party,function(i){
				if(this.id === v){
					party[i]["isChecked"] = t;
					return false;
				}
			});
		}else{
			$.each(member,function(i){
				if(this.id === v){
					member[i]["isChecked"] = t;
					return false;
				}
			});
		}
	};
/*-------------------------------切换指定范围后tab选项卡的脚本-------------------------------*/   
    /*点击切换 公开发布与指定发布*/
    $(document).on("click","#allClicks .range",function(e){
        e.preventDefault();
        $(this).children("label").eq(0).addClass(":after");
        $(this).children("input").eq(0).prop("checked",true);
        var s = $(this).children("input").eq(0).attr("data-name");
            s = parseInt(s);
        dataObj["range"] = $("#allClicks").find("input:radio:checked").eq(0).val();   
        /*公开发布 0  指定发布 1*/
        if(s){
        	var h = $("#sel_tree").children().eq(0).css("height");/*根据内容高度来设置动画执行的高度*/
            $("#sel_tree").animate({height:h},300);
        }else{
            $("#sel_tree").animate({height:"0px"},300);
        }
    });
    $(document).on("click","#tab span",function(){
    	if(!($(this).hasClass("tabed"))){
    		$(this).addClass("tabed").siblings("span").removeClass("tabed");
    		/*此处切换选项卡时 treeContent 中的数据 显示的内容需要对应的切换*/
    		var tag = $(this).attr("data-name");/*管理员发布(manager),个人发布(person*/
    		if(tag === "party"){
    			$("#treeContent_"+tag).siblings("#treeContent_person").hide().end().fadeIn();
    		}else{
    			$("#treeContent_"+tag).siblings("#treeContent_party").hide().end().fadeIn();
    		}
    	}
    });
    /*-------------指定通知部门或者人员-----------*/
    var seledHover = function(target){
    	$(document).find(target).hover(function(){
        	$(this).removeClass("border").addClass("btn-success").children(".del_sel").fadeIn();
        },function(){
        	$(this).removeClass("btn-success").addClass("border").children(".del_sel").fadeOut();
        });
    };
    var seledDel = function(target,list,count,seled,all,data,unit){
    	 $(document).on("click",target,function(){
    	    	$(this).parent().remove();/*此处删除后需要更新选中的数据,以及展示的效果;*/
    	    	changeCheck(data,$(this).parent().attr("id"),"");
    	    	$("#"+list).find("#"+$(this).parent().attr("id")).prop("checked",false).end().find("label").removeClass(":after");
    	    	$("#"+count).empty().text(($("#"+seled).children()).length+unit);
    	    	
    	    	if($("#"+list).find("input:checkbox:checked").length === $("#"+list).children("li").length){
    				 $("#"+all).find("input").prop("checked",true).end().find("label").addClass(":after");
    			}else{
    			    $("#"+all).find("input").prop("checked",false).end().find("label").removeClass(":after");
    			}
    	    });
    };
    seledDel("#seledParty .del_sel","partList","seledPartCount","seledParty","allSelParty","p",' 部门');
    seledDel("#seledPerson .del_sel","personList","seledPersonCount","seledPerson","allSelPerson","m",' 人');
  
    /*--------------输入后检索-------------------*/
    var inIndexCom = function(nav,v,data,list,listHide,all){
    	$("#"+nav).children("span:first").attr("data-name","{}").addClass("prevClass").nextAll().remove();
		$("#"+nav).append('<span> > 搜索"'+v+'"</span>');
    	var arr = [];
		$.each(data,function(i){
			if(v === ""){
				arr = data;
				return false;
			}else{
				if((this.name).indexOf(v)>-1){
					arr.push(this);
				}
			}
		});
		$.loadData(list,listHide,{body:arr});
		if(arr.length!==0 & $("#"+list).find("input:checkbox:checked").length === arr.length){
			$("#"+all).find("input:checkbox").prop("checked",true).end().find("label").addClass(":after");
		}else{
			$("#"+all).find("input:checkbox").prop("checked",false).end().find("label").removeClass(":after");
		}
    };
    /*部门输入检索*/
	$.inIndexCon("party_search","searchPart",function(v){
		inIndexCom("sel_part_nav",v,party,"partList","partListHide","allSelParty");
	});
	/*个人输入检索*/
	$.inIndexCon("person_search","searchPerson",function(v){/*个人*/
		inIndexCom("sel_person_nav",v,member,"personList","personListHide","allSelPerson");
	});
	/*点击部门列表树：加载新的数据和更新面包屑*/
	$(document).on("click","#partList span",function(){
		var data = $(this).attr("data-name");
			data = $.dataParse(data);
		var v = $("#sel_part_nav").children("span:last").text();
		var m = $.filterData(data.id,"pId",party);
		if(m.length !== 0){
			if(v.indexOf("搜索")>-1){
				$("#sel_part_nav").children("span:last").remove();
				$("#sel_part_nav").children("span:first").addClass("prevClass").attr("data-name","{}");
				$("#sel_part_nav").append('<span> > '+data.name+'</span>');
			}else{
				$("#sel_part_nav").children("span:last").attr("data-name",$.stringifyJson(data)).addClass("prevClass");
				$("#sel_part_nav").append('<span> > '+data.name+'</span>');
			}
			$.loadData("partList","partListHide",{body:m});
			/*根据是否已经全部选中 来显示 全部的复选框是否被选中*/
			if($("#partList").find("input:checkbox:checked").length === m.length){
				$("#allSelParty").find("input:checkbox").prop("checked",true).end().find("label").addClass(":after");
			}else{
				$("#allSelParty").find("input:checkbox").prop("checked",false).end().find("label").removeClass(":after");
			}
		}
	});
	/*点击面包屑回到对应的数据层*/
	$(document).on("click","#sel_part_nav .prevClass",function(){
		var data = $.parseJSON($(this).attr("data-name")) || {};
		var m;
		if(data.groupLevel){
			m = $.filterData(parseInt(data.groupLevel,10),"groupLevel",party);/*筛选出一样的groupLevel*/
		}else{
			m = $.filterData("","pId",party);/*筛选出一样的groupLevel*/
		}
		$.loadData("partList","partListHide",{body:m});
		$(this).removeClass("prevClass").nextAll().remove();
		
		if($("#partList").find("input:checkbox:checked").length === m.length){
			$("#allSelParty").find("input:checkbox").prop("checked",true).end().find("label").addClass(":after");
		}else{
			$("#allSelParty").find("input:checkbox").prop("checked",false).end().find("label").removeClass(":after");
		}
	});
	$(document).on("click","#sel_person_nav .prevClass",function(){
		$.loadData("personList","personListHide",{body:member});
		$(this).removeClass("prevClass").nextAll().remove();
		
		if($("#personList").find("input:checkbox:checked").length === member.length){
			$("#allSelPerson").find("input:checkbox").prop("checked",true).end().find("label").addClass(":after");
		}else{
			$("#allSelPerson").find("input:checkbox").prop("checked",false).end().find("label").removeClass(":after");
		}
	});
	/*点击全选  allSelPerson*/ 
	var selAllFun = function(id,list,seled,count,seledUnit,data,hover){
		$(document).on("click","#"+id,function(e){
			e.preventDefault();
			var v = $(this).find("input:checkbox:checked");
			var iconS =  data=== "p"?'ep ep-depa':'ep ep-user';
			if(v.length === 0){
				$(this).find("input").prop("checked",true);
				$("#"+list).find("input").prop("checked",true).end().find(".label_checked").addClass(":after");
				$("#"+list).find(".ep").addClass("color_f67a17");
				var m = $("#"+list).find("span");
				/*右侧部分需要追加 */
				var str ='';
				$.each(m,function(){
					var v = $.dataParse($(this).attr("data-name"));
					changeCheck(data,v.id,1);
					if($("#"+seled).find("#"+v.id).length === 0){
						var m = v.name.length>5?v.name.substr(0,4)+'...':v.name;
						str +='<span id="'+v.id+'" title="'+v.name+'" class="seled_list btn border p_a_0">'+
						'<i class="color_f67a17 '+iconS+'"></i><b class="p_l_5">'+m+'</b>'+
						'<i class="c-primary ep-close posA del_sel"></i></span>';
					}
				});
				$("#"+seled).prepend(str);
				seledHover(hover);
				$("#"+count).empty().text(($("#"+seled).children()).length+seledUnit);
			}else{
				$(this).find("input").prop("checked",false);
				$("#"+list).find("input").prop("checked",false).end().find(".label_checked").removeClass(":after");
				$("#"+list).find(".ep").removeClass("color_f67a17");
				/*右边部分需要删除未被选中的*/
				var m = $("#"+list).find("span");
				$.each(m,function(){
					var v = $.dataParse($(this).attr("data-name"));
					changeCheck(data,v.id,"");
					$("#"+seled).find("#"+v.id).remove();
				});
				$("#"+count).empty().text(($("#"+seled).children()).length+seledUnit);
			}
		});
	};
	
	selAllFun("allSelParty","partList","seledParty","seledPartCount",' 部门',"p","#seledParty span");/*部门全选*/
	selAllFun("allSelPerson","personList","seledPerson","seledPersonCount",' 人',"m","#seledPerson span");/*人员全选*/
	/*部门点击单个复选框*/
	var selSingleFun = function(all,id,target,seled,count,seledUnit,data,hover){
		$(document).on("click",target,function(e){
			e.preventDefault();
			var v = $(this).find("input:checkbox:checked");
			var iconS =  data=== "p"?'ep ep-depa':'ep ep-user';
			if(v.length === 0){
				$(this).find("input").prop("checked",true).end().find(".label_checked").addClass(":after");
				$(this).siblings(".ep").addClass("color_f67a17");
				var str ='';
				var v = $.dataParse($(this).siblings("span").eq(0).attr("data-name"));
				changeCheck(data,v.id,1);
				if($("#"+seled).find("#"+v.id).length === 0){
					var m = v.name.length>5?v.name.substr(0,4)+'...':v.name;
					str +='<span id="'+v.id+'" title="'+v.name+'" class="seled_list btn border p_a_0">'+
					'<i class="color_f67a17 '+iconS+'"></i><b class="p_l_5">'+m+'</b>'+
					'<i class="c-primary ep-close posA del_sel"></i></span>';
				}
				$("#"+seled).prepend(str);
				$("#"+count).empty().text(($("#"+seled).children()).length+seledUnit);
				seledHover(hover);
			}else{
				$(this).find("input").prop("checked",false).end().find(".label_checked").removeClass(":after");
				$(this).siblings(".ep").removeClass("color_f67a17");
				var str ='';
				var v = $.dataParse($(this).siblings("span").eq(0).attr("data-name"));
				changeCheck(data,v.id,"");
				$("#"+seled).find("#"+v.id).remove();
				$("#"+count).empty().text(($("#"+seled).children()).length+seledUnit);
			}
			if($("#"+id).find("input:checkbox:checked").length === $("#"+id).children("li").length){
				$("#"+all).find("input").prop("checked",true).end().find("label").addClass(":after");
			}else{
				$("#"+all).find("input").prop("checked",false).end().find("label").removeClass(":after");
			}
		});
	};
	/*单个选中*/
	selSingleFun("allSelParty","partList","#partList li div","seledParty","seledPartCount",' 部门',"p","#seledParty span");
	selSingleFun("allSelPerson","personList","#personList li div","seledPerson","seledPersonCount",' 人',"m","#seledPerson span");
	/*通知部门与通知所有人员*/
	$(document).on("click","#countList .selSendParty",function(e){
		e.preventDefault();
		$(this).siblings(".selSendParty").find("input").prop("checked",false).end().find("label").removeClass(":after");
		$(this).find("input").prop("checked",true).end().find("label").addClass(":after");
	});
});

