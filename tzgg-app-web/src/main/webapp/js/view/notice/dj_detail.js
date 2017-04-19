/**
 * 通知公告js
 */
$(function() {
	var notice = new DJ();

	notice.initPage = function() {
		mui.init();
		// 初始化单页的区域滚动
		mui('.mui-scroll-wrapper').scroll({
			deceleration : 0.0010,
			indicators : false,
			bounce : true
		});
		// 初始化下载进度条

		// 初始化头部
		var actionBtn0 = {text: '返回首页', icon: 'http://'+window.location.host+global.getContextPath()+'/img/icon03.png', action: 'workbench'};
	    var actionBtn1 = {text: '退出登录', icon: 'http://'+window.location.host+global.getContextPath()+'/img/icon04.png', action: 'logout'};
		var params = {style: {header: {optBtn: {text: '• • •', action: '', icon: '', actionBtns: [actionBtn0, actionBtn1]}}}}
		UmsApp.action.viewSettings(params);
	}
	notice.initEvent = function() {
		// 点击公告附件
		$(document).on('tap','#notice_detail .attach-item',function(e) {
			e.preventDefault();
			//判断该文件是否存在于本地，或者是否已下载，如果是，则直接打开，反之，则跳转到下载页
			var url = $(this).data('url');
			if(global.isEmpty(url)){
				var id = $(this).data('id');
				window.location.href='download.html?id='+id;
			}else{
				UmsApp.action.fileOpen({url:url},function(err,data){
					if(err){
						 mui.alert('打开失败');
					}
				})
			}
		});
		var body = document.querySelector('body')
	    body.on('logout', function (data) {
	    	UmsApp.action.logout();
	    });
		body.on('workbench', function (data) {
	    	UmsApp.view.workbench();
	    });
	}
	notice.initFun = function(){
		notice.getDetail();
	}
	/**
	 * 获取通知公告详情
	 */
	notice.getDetail = function() {
		var id = global.getParam('id'),
			type = global.getParam('type');
		global.ajax({
			url:$.CONSTANT.MODULE.NOTICE.REQUEST_URL.GET_NOTICE_DETAIL,
			data:{notifyId:id,type:type},
			dataType:'json',
			type:'post',
			cache : false,
			success:function(data){
				if (data.head.resultCode == 200) {
//					var result = notice.proccessData(data.body);
					var result = data.body;
					var attachs = result.attachmentList;
					if(attachs.length > 0){
						var fileSys = null;
						//获取本地文件系统中已下载的文件
						var data = UmsApp.action.fileList({rootPath: '',model:2}, function (err, data) {
							var list = data['file-list'];
							result.attachmentList = $.map(attachs,function(v){
								for(var i in list){
									var value = list[i];
									var fileName = v.path.substr(v.path.lastIndexOf('/')+1);
									if(value.indexOf(fileName) == -1){
										v.url = '';
									}else{
										v.url = value;
										break;
									}
								}
								return v;
							});
							var str = '<div>'+result.content.replace(/\n/g,'</div><div>').replace(/\s/g,'')+'</div>';
							result.content = str;
							$('#notice_detail').setTemplateElement('noticeDetailTemplate');
							$('#notice_detail').processTemplate(result);
							$('#content').html(result.content);
							if(result.isRead == 'unread'){
								notice.setRead(id,type);
							}
						});
					}else{
						$('#notice_detail').setTemplateElement('noticeDetailTemplate');
						$('#notice_detail').processTemplate(result);
						$('#content').html(result.content);
						if(result.isRead == 'unread'){
							notice.setRead(id,type);
						}
					}
				} else {
					mui.toast(data.head.message);
				}
			}
		});
	}
	notice.setRead = function(id,type){
		global.ajax({
			url:$.CONSTANT.MODULE.NOTICE.REQUEST_URL.SET_READ,
			data:{notifyId:id,type:type},
			dataType:'json',
			type:'post',
			cache : false,
			success:function(data){
				if (data.head.resultCode == 200) {
				} else {
					mui.toast(data.head.message);
				}
			}
		});
	}
	//处理返回的公告详情数据，判断详情所携带数据在本地是否存在，或者是否已下载
	notice.proccessData = function(attachData){
		/**
		 * 取到data里的attachs，遍历每个attach，调用平台接口判断是否已经下载，若已经下载，则添加属性true，反之则添加属性false
		 */
		var attachs = attachData.attachmentList;
		if(attachs.length > 0){
			var fileSys = null;
			//获取本地文件系统中已下载的文件
			var data = UmsApp.action.fileList({rootPath: '',model:2}, function (err, data) {
				var list = data['file-list'];
				attachData.attachmentList = $.map(attachs,function(v){
					for(var i in list){
						var value = list[i];
						var fileName = v.path.substr(v.path.lastIndexOf('/')+1);
						if(value.indexOf(fileName) == -1){
							v.url = '';
						}else{
							v.url = value;
							break;
						}
					}
					return v;
				});
				var str = '<div>'+attachData.content.replace(/\n/g,'</div><div>').replace(/\s/g,'')+'</div>';
				attachData.content = str;
				return attachData;
			});
		}else{
			return attachData;
		}
	}
	/**
	 * 分享附件
	 */
	notice.share = function() {
	}

	notice.initPage();
	notice.initEvent();
	notice.initFun();
});