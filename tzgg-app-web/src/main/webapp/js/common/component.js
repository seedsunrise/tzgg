/**
 * 通用UI组件
 * @param global
 * @param $
 * @param factory
 */
(function(global,$,factory){
	global.UI = factory;
})(window,jQuery,{
	/**
	 * comment component，mui
	 * @param
	 * options{
	 * 	target:comment button
	 * 	container：comment dom popover
	 * 	number : comment total number
	 * 	isFixed: is a fixed comment nav
	 * 	submit : submit comment function
	 * }
	 */
	commentInit : function(options){
		//click comment button event
		$(document).on('tap',options.target,function(e){
			e.preventDefault();
			if(options.isFixed){
				document.getElementById('commentInput').style.display='none';
				document.getElementById('commentInner').style.display='block';
			}else{
				document.getElementById('commentInput').style.display='block';
				document.getElementById('commentInner').style.display='none';
			}
			mui(options.container).popover('toggle');
			/*$('#commentText').trigger('click');
			$('#commentText').trigger('focus');*/
			$('#commentBtn').removeClass('mui-btn-danger');
		});
		//click comment text event
		$('#commentInput').on('tap',function(e){
			e.preventDefault();
			document.getElementById('commentInput').style.display='none';
			document.getElementById('commentInner').style.display='block';
		});
		
		//comment input event
		var bind_name = "input";
		if(navigator.userAgent.indexOf('MISE') != -1){
			bind_name = 'propertychange';
		}
		$('#commentText').on(bind_name, function(e) {
			var val = $('#commentText').val().trim();
			var zh = val.match(/[\u4E00-\u9FA5\uF900-\uFA2D]/g),
				en = val.match(/[^\u4E00-\u9FA5\uF900-\uFA2D]/g);
			var num = (zh==null?0:zh.length) + (en==null?0:en.length);
			document.getElementById('comment_num').innerHTML = num+'/'+options.number;
			
			if (tool.isEmpty($('#commentText').val().trim())) {
				/*以下代码中 attr新增的 16-11-7*/
				$('#commentBtn').removeClass('mui-btn-danger').attr("disabled",true).addClass("comment_btn");
				
			} else {
				/*以下代码中 removeAttr新增的 16-11-7*/
				$('#commentBtn').removeClass("comment_btn").addClass('mui-btn-danger').removeAttr("disabled");
			}
		});
		//popover hidden event
		mui('body').on('hidden', options.container, function(e) {
			document.getElementById('commentText').value='';
			$('#commentText').trigger('blur');
			/*以下代码是新增的 16-11-7*/
			document.getElementById('commentBtn').setAttribute("disabled",true);
			
			$("#commentBtn").removeClass('mui-btn-danger').addClass("comment_btn");
		});
		
		//submit comment
		$('#commentBtn').on('tap',function(e){
			e.preventDefault();
			if($(this).hasClass('mui-btn-danger')){
				if($.isFunction(options.submit)){
					options.submit();
					mui(options.container).popover('toggle');
					document.getElementById('comment_num').innerHTML='0/'+options.number;
					$('#commentText').trigger('blur');
				};
			}
		});
	},
});
