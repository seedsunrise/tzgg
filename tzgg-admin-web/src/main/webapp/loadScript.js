/**
 * @description: 公共引用文件方式，其中包含以下初始化页面处理的方法包括，公共js动态脚本加入、表单元素的回车事件禁止、动态的菜单和权限按钮加入，存放再浏览器的缓冲中
 * @time: 2016-06-18
 * @version 1.1.0
 * @author: 罗春彬
 */
;
(function() {
	var headUrl = 'https://res.ums86.com/6/js/com/header.js';
	var s = location.href;
	if(s.indexOf("flaginfo.cn")>-1){
		headUrl = "http://res.flaginfo.cn/6/js/com/header.js";
	}
	if(s.indexOf(".demo.")>-1){
		headUrl = "http://res.demo.ums86.com/6/js/com/header.js";
	}
	if(s.indexOf('.test.')>-1){
		headUrl = 'http://res.test.ums86.com/6/js/com/header.js';
	}
	if(s.indexOf('.beta.')>-1){
		headUrl = 'http://res.beta.ums86.com/6/js/com/header.js';
	}
	
	document.write('<script src="'+headUrl+'?version='+(Math.random())*10+'"></script>');
	window.baseUrl = "";
	/**
	 * 添加公告引入的js文件
	 */
	var jsFiles = [];
	/*库文件*/
	jsFiles.push( baseUrl+"/commonResources/js/jquery-1.11.3.min.js");
	jsFiles.push( baseUrl+"/commonResources/js/jquery-jtemplates.js");
	/*公共的组件 */
	jsFiles.push( baseUrl+"/commonResources/js/bootstrap.min.js");
	/*自己定义的组件 */
	jsFiles.push( baseUrl+"/commonResources/tool/page.js");
	jsFiles.push( baseUrl+"/commonResources/tool/messager.js");
	/*日期插件*/
	jsFiles.push( baseUrl+"/thridPlugin/laydate/laydate.js");
	/** 存放将要写出的文件 */
	var jsFilesLength = jsFiles.length;
	for (var i = 0; i < jsFilesLength; i++) {
		document.write('<script src="' + jsFiles[i] + '" ></script>');
	}
})();




