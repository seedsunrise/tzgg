/**
 * 通用继承js总接口
 * @param global
 * @param $
 * @param factory
 */
(function(global,$,factory){
	DJ = factory();
})(window,jQuery,function(){
	'use strict';
	var DJ = function(){
		this.page = {
			pageNo : 1,
			pageSize : 10
		}
	};
	//初始化页面控件
	DJ.prototype.initPage = function(){}
	
	//初始化页面事件
	DJ.prototype.initEvent = function(){}
	
	//初始化方法
	DJ.prototype.initFun = function(){}
	
	//初始化字典
	DJ.prototype.initDic = function(type,callback) {
		mui.ajax($.CONSTANT.GET_DIC, {
			data : {type : type},
			dataType : 'json',
			type : 'post',
			async : false,
			success : function(data) {
				if (data.head.resultCode == 200) {
					callback(data.body[type]);
				} else {
					mui.toast(data.head.message);
				}
			},
			error : function(data) {
				mui.toast('网络异常');
			}
		});
	}
	
	return DJ;
});