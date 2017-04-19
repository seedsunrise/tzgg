/**
 * 通知公告js
 */
$(function() {
	var notice = new DJ();

	//分页结果集,初始化暂不分配内存
	notice.list = null;
	//分页标识
	var pageFlag = false;
	
	notice.initPage = function() {
		mui.init({
			pullRefresh : {
			    container:'#pager',
			    down:{
			    	callback:function(){
						window.setTimeout(function(){
							mui('#pager').pullRefresh().endPulldownToRefresh();
							mui('#pager').pullRefresh().refresh(true);
							notice.page.pageNo = 1;
				    		notice.list.splice(0,notice.list.length);
							notice.getList();
						}, 1500);
			    	}
			    },
				up: {
					height:50,
					auto : true,
					contentrefresh: '正在加载...',
					ontentnomore:'没有更多数据了',
					callback: function(){
						setTimeout(function(){
							notice.getList();
						}, 1000);
					}
				}
			}
		});
		// 初始化单页的区域滚动
		mui('.mui-scroll-wrapper').scroll({
			deceleration : 0.0010,
			indicators : false,
			bounce : true
		});

		// 初始化头部
	    var actionBtn0 = {text: '返回首页', icon: 'http://'+window.location.host+global.getContextPath()+'/img/icon03.png', action: 'workbench'};
	    var actionBtn1 = {text: '退出登录', icon: 'http://'+window.location.host+global.getContextPath()+'/img/icon04.png', action: 'logout'};
	    var params = {style: {header: {optBtn: {text: '• • •', actioin: '', icon: '', actionBtns: [actionBtn0, actionBtn1]}}}}
	    UmsApp.action.viewSettings(params);
	    
	}
	notice.initEvent = function() {
		//点击公告项跳转
		$(document).on('tap','#notice_list .notice-item',function(e){
			e.preventDefault();
			var id = $(this).data('id'),
				type = $(this).data('type')
			window.location.href='detail.html?id='+id+'&type='+type;
		});
		//关掉搜索modal
		$('#closeSearch').on('tap',function(e){
			e.preventDefault();
			$('#noticeSearch').val('');
		});
		//点击搜索
		$('#searchBtn').on('tap',function(e){
			e.preventDefault();
			notice.page.pageNo = 1;
    		notice.list.splice(0,notice.list.length);
			notice.getList();
			$('#modal').removeClass('mui-active');
		});
		var body = document.querySelector('body')
	    body.on('logout', function (data) {
	    	UmsApp.action.logout();
	    });
		body.on('workbench', function (data) {
	    	UmsApp.view.workbench();
	    });
	}
	/**
	 * 获取通知公告列表
	 */
	notice.getList = function() {
		var keyword = $('#noticeSearch').val().trim();
//		mui.showWaiting();
		global.ajax({
			url:$.CONSTANT.MODULE.NOTICE.REQUEST_URL.GET_LIST,
			data:{pageNo:notice.page.pageNo,pageSize:notice.page.pageSize},
			dataType:'json',
			type:'post',
			cache : false,
			success:function(data){
				if (data.head.resultCode == 200) {
					var result = data.body.results;
					if(global.isEmpty(notice.list)){
						notice.list = new Array();
					}
					notice.list = notice.list.concat(result);
					pageFlag = (notice.list.length >= data.body.totalRecord) ? true : false;
					mui('#pager').pullRefresh().endPullupToRefresh(pageFlag);
					$('#notice_list').setTemplateElement('noticeListTemplate');
					$('#notice_list').processTemplate(notice.list);
					notice.page.pageNo++;
				} else {
					mui.toast(data.head.message);
				}
			}/*,
			complete:function(){
				mui.hideWaiting();
			}*/
		});
	}
	notice.initFun = function(){
//		notice.getList();
	}
	notice.initPage();
	notice.initEvent();
	notice.initFun();
});