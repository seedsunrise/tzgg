/**
 * 通知公告js
 */
$(function() {
	var attach = new DJ();

	//附件详情
	attach.detail = null;
	/**
	 * 获取文件详情
	 */
	attach.getFileDetail = function(){
		var fileId = global.getParam('id');
		global.ajax({
			url:$.CONSTANT.MODULE.NOTICE.REQUEST_URL.GET_FILE_DETAIL,
			data:{fileId:fileId},
			dataType:'json',
			type:'post',
			success:function(data){
				if (data.head.resultCode == 200) {
					$('#file_detail').setTemplateElement('fileDetailTemplate');
					$('#file_detail').processTemplate(data.body);
					attach.detail = data.body;
				} else {
					mui.toast(data.head.message);
				}
			}
		});
	}
	/**
	 * 下载通知公告附件
	 */
	attach.download = function() {
		mui('#download_progressbar').progressbar({
			progress : 0
		}).show();
		$('#begin_download').addClass('mui-hidden');
		$('#download_detail').removeClass('mui-hidden');
		var path = $('#begin_download').data('path');
		
		 var params = {url: path};
		 UmsApp.action.fileDown(params, function (err, data) {
			 if(err){
				 //下载失败
				 $('#speed').html('<span class="icon-Download-failed"></span>下载失败');
				 $('#begin_download').removeClass('mui-hidden');
				 $('#download_text').html('重新下载');
			 }else{
				 mui.toast('下载完成');
//				 alert(data);
//				 $('#progressValue').html(attach.detail.size);
				 UmsApp.action.fileOpen({url:data.url},function(err,data){
					 if(err){
						 mui.alert('打开失败');
					 }
				 })
			 }
		 });
		 attach.getProgross(params);
	}
	/**
	 * 获取附件下载进度
	 */
	attach.getProgross = function(params) {
		setTimeout(function () {
			 setInterval(function(){
				 UmsApp.action.fileProgress(params, function (data) {
					 mui('#download_progressbar').progressbar().setProgress(data.progress*100);
					 var progress = attach.detail.size*data.progress;
					 $('#progressValue').html(progress.toFixed(2));
				 });
			 }, 100)
		}, 0);
	}
	attach.initPage = function() {
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
	attach.initEvent = function() {
		// 点击开始下载
		$(document).on('tap','.attach_detail #begin_download', function(e) {
			e.preventDefault();
			$('#speed').html('');
			var downloadFlag = false;
			UmsApp.action.netstate(function (err, data) {
				if(data == '0'){
					//没有网络
					mui.alert('请检查网络设置');
					return;
				}else if(data == '1'){
					//移动wifi
					attach.download();
				}else if(data == '2'){
					//移动网络
					mui.confirm('您当前正在使用移动网络，继续下载将消耗流量','下载提示',['停止下载','继续下载'],function(e){
						if(e.index == 0){
							return;
						}else{
							attach.download();
						}
					});
				}else{
					mui.alert('无法获取网络信息');
					return;
				}
			});
		});
		var body = document.querySelector('body')
	    body.on('logout', function (data) {
	    	UmsApp.action.logout();
	    });
		body.on('workbench', function (data) {
	    	UmsApp.view.workbench();
	    });
	}
	attach.initFun = function(){
		attach.getFileDetail();
	}
	
	attach.initPage();
	attach.initEvent();
	attach.initFun();
});