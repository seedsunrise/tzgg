/**
 * Created by Administrator on 2016/10/9 0009.
 * this is a Interface file.
 * Need to indicate the function of each interface implementation with the corresponding module;
 */
;"use strict";
var flag_interface = (function (arr) {
	var faceArr = [
	    {"isManager":"/notify/isManager"},/*判断是否是超级管理员*/
		{"upload":"/fileUpload/upload"},/*上传附件接口*/
		{"notice":{/*通知公告下辖所有接口*/
			"management":"/notify/queryNotify",/*按活动类型+活动名称分页搜索*/
			"save":"/notify/saveNotify",/*新建保存*/
			"detail":"/notify/detailNotify",/*详情*/
			"edit":"/notify/editNotify",/*编辑接口*/
			"savePub":"/notify/pubNewNotify",/*新建发布/编辑发布*/
			"close":"/notify/closeNotify",/*列表关闭*/
			"publish":"/notify/pubListNotify",/*列表发布*/
			"del":"/notify/delNotify"/*列表删除*/,
			"orgTree":"/orgTree/org",/*获取当前管理员组织结构*/
			"member":"/member/list"/*获取当前管理员所在组织的下的人*/
		}},
		{"test":"http://10.0.51.29:8090/testData"}
	];
	var getAttrName = function (obj) {
		var s = "";
		for(var k in obj){
			s = k;
		}
		return s;
	};
	var o = {},m = {},r = {};
	for(var i = 0,n = faceArr.length;i<n;i++){
		var k = getAttrName(faceArr[i]);
			m[k] = new Object({
				value: faceArr[i][k],
				writable: false,
				enumerable: false,
				configurable: false
			});
			r[k] = m[k].value;
	}
	Object.defineProperties(o,m);
	return r;
})();


