/**
 * mui 扩展，依赖mui框架和扩展的mui.expand.css
 * @param $
 * @param window
 */
(function($, window) {
	var showWaiting = function(message) {
		$.closePopup();
		if (!$.os.plus) {
			try {
				/*$.createPopup('<div class="mui-loading-inner"><div id="loading" class="">'+
						'<div class="bar1"></div>'+
						'<div class="bar2"></div>'+
						'<div class="bar3"></div>'+
						'<div class="bar4"></div>'+
						'<div class="bar5"></div>'+
						'<div class="bar6"></div>'+
						'<div class="bar7"></div>'+
						'<div class="bar8"></div>'+
						'</div><div class="mui-loading-texty">'+(message || '加载中...')+'</div>'+
				'</div>');
				var backdrop = document.body.querySelector('.mui-popup-backdrop');
				backdrop.classList.add('loading-backdrop');
				var count = 0;
				function rotate() {
					var loading = document.getElementById('loading');
					if(loading){
						loading.style.MozTransform = 'scale(0.3) rotate('+count+'deg)';
						loading.style.WebkitTransform = 'scale(0.3) rotate('+count+'deg)';
						if (count==360) { count = 0 }
						count+=45;
						window.setTimeout(rotate, 100);
					}
				}
				window.setTimeout(rotate, 100);*/
				$.createPopup('<div class="mui-loading-inner">'+
						'<span class="mui-spinner"></span>'+
						'<div class="mui-loading-texty">'+(message || '加载中...')+'</div>'+
				'</div>');
				return;
			} catch (e) {
				// TODO: handle exception
				console.error('please import mui.js and open createPopup() before mui.expand.js')
			}
		}else{
			return plus.nativeUI.showWaiting(message);
		}
	};
	var hideWaiting = function(){
		$.closePopup();
	}
	$.hideWaiting = hideWaiting;
	$.showWaiting = showWaiting;
})(mui, window);