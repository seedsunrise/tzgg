/**
 * 封装公用工具方法
 * @param global
 * @param $
 * @param factory
 */
(function(global,$,factory){
	window.global = factory;
})(window,jQuery,{
	flagClass : 'ajax-doing',
	errorTip : '网络异常',
	/**
	 * 获取浏览器地址栏中的参数值
	 * @param name 参数名字
	 */
	getParam : function(name){
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	    var r = window.location.search.substr(1).match(reg);
	    if(r!=null)
	    	return  r[2]; 
	    return null;
	},
	/**
	 * 获取项目根目录
	 * @returns
	 */
	getContextPath : function(){
	    var curPath=window.document.location.href;
	    var pathName=window.document.location.pathname;
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    if(projectName === '/app'){
	    	return '';
	    }
	    return projectName;
	},
	/**
	 * 判断对象是否为空，如果是字符串，则判断是否为空字符串
	 * @param obj
	 */
	isEmpty : function(obj){
		if(typeof(obj) == 'object'){
			if(obj == null || obj == undefined || obj.length == 0){
				return true;
			}else{
				return false;
			}
		}else if(typeof(obj) == 'string'){
			if(obj == '' || obj.length == 0){
				return true;
			}else{
				return false;
			}
		}else if(typeof(obj) == 'number'){
			if(obj == null || obj == undefined || obj == NaN){
				return true;
			}else{
				return false;
			}
		}else if(typeof(obj) == 'undefined'){
			return true;
		}else{
			return true;
		}
	},
	/**
	 * 计算文本字数
	 * @param str
	 */
	textNumber : function(str){
		var zh = val.match(/[\u4E00-\u9FA5\uF900-\uFA2D]/g),
		en = val.match(/[^\u4E00-\u9FA5\uF900-\uFA2D]/g);
		return (zh==null?0:zh.length) + (en==null?0:en.length);
	},
	/**
	 * dom点击ajax操作，防止重复点击提交，业务可在点击之后业务添加自定义逻辑再调用此方法
	 * @arguments[dom,option]
	 * dom:表示点击的DOM节点选择器
	 * option:表示传入的ajax对象
	 */
	clickAjax : function(){
		var dom = arguments[0],
			option = arguments[1];
		var _this = this;
		if(tool.isEmpty(dom) || tool.isEmpty(option)){
			console.error('click dom and ajax option is required');
			return;
		}
		if(!$(dom).hasClass(_this.flagClass)){
			var _default = {
				complete:function(data){
					$(dom).removeClass(_this.flagClass);
				},
				error:function(data){
					mui.toast(_this.errorTip);
				}
			}
			$(dom).addClass(_this.flagClass);
			$.extend(_default,option);
			$.ajax(_default);
		}
	},
	/**
	 * ajax
	 */
	ajax : function(){
		var _this = this;
		var _default = {
			error:function(data){
				mui.toast(_this.errorTip);
			}
		}
		$.extend(_default,arguments[0]);
		$.ajax(_default);
	},
	uploadImg : function(){
		$('#addImgBtn').on('touchend',function(e) {
			e.preventDefault();
			imgList = $('#uploadImg .img_item');
			if(imgList.length < 9){
				UmsApp.photo();
			}
		}).on('umsapp://data/photo', function(e, img) {
			var _div = '<div class="mui-col-sm-4 mui-col-xs-4 border_padding">'+
				'<img class="del_icon" src="/djplatform-app-web/app/img/icon/img02.png"/>'+
				'<img class="img-item" src="'+img+'" data-preview-src="" data-preview-group="dj-previewImage"/></div>';
			$('#uploadImg').prepend(_div);
			$('#imgNum').html(parseInt($('#imgNum').html())+1);
			mui.previewImage();
		});
	},
	/**
	 * 删除上传的照片
	 */
	delImg : function(){
		$(document).on('touchend','#uploadImg .del_icon',function(e){
			e.preventDefault();
			$(this).parent().remove();
			$('#imgNum').html(parseInt($('#imgNum').html())-1);
		});
	},
	removeHead : function(){
		setTimeout(function(){
//			UmsApp.send('umsapp://action/view-settings?style='+JSON.stringify({header:{hide:true}}));
			UmsApp.titleGone();
		}, 50);
	},
	returnIndex : function(){
//		UmsApp.view.workbench();
		UmsApp.send('umsapp://view/workbench');
	},
	logOut : function(){
		UmsApp.action.logout();
	},
	getFileImgType:function(fileName){
		$.each(bgList,function(index,item){
			var fileName = $(item).data('type');
			var fileType = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length);
			var typeImg = '';
			var reg = new RegExp(fileType,"ig");
			if(FILE_TYPE.DOC_TYPE.match(reg)){
				typeImg = 'bg_doc';
			}else if(FILE_TYPE.PPT_TYPE.match(reg)){
				typeImg = 'bg_ppt';
			}else if(FILE_TYPE.EXCEL_TYPE.match(reg)){
				typeImg = 'bg_excel';
			}else if(FILE_TYPE.IMG_TYPE.match(reg)){
				typeImg = 'bg_img';
			}else if(FILE_TYPE.PDF_TYPE.match(reg)){
				typeImg = 'bg_pdf';
			}
			$(item).addClass(typeImg);
		});
	}
});
