/**
 * 通知公告编辑 加载数据
 */
;$(function(){
	"use strict";
	var o = $.dataParse();
	$.asyncF($.dataBox(flag_interface.notice.edit,"post",o),"loading",function(res){
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
/*	返回
	$(document).on("click","#back",function(){
		$("#backModal").modal("show");
	});
	$("#backSure").on("click",function(){
		$.openUrl("/page/notice/notice_management.html");
	});*/
});



