/**
 * 静态资源，存放静态常量，请求url等等
 * @param global
 * @param $
 * @param factory
 */
(function(global,$,factory){
	$.CONSTANT = factory;
})(window,jQuery,{
	GET_DIC : global.getContextPath() + '/dataDict/getByType',// 获取字典表
	// 字典表
	DIC_LIST : {},
	FILE_TYPE : {
		DOC_TYPE:'doc_docx',
		PPT_TYPE:'ppt_pptx',
		EXCEL_TYPE:'xls_xlsx',
		IMG_TYPE:'jpg_png_gif_bmp_jpeg',
		PDF_TYPE:'pdf'
	},
	// 模块
	MODULE : {
		//公共模块
		COMMEN : {
			// 请求url
			REQUEST_URL : {}
		},
		//通知公告
		NOTICE : {
			// 请求url
			REQUEST_URL : {
				GET_LIST:global.getContextPath()+'/notify/list',//获取列表
				GET_NOTICE_DETAIL:global.getContextPath()+'/notify/get',//获取公告详情
				GET_FILE_DETAIL:global.getContextPath()+'/notify/file',//获取附件详情
				SET_READ:global.getContextPath()+'/notify/read'//设置已读状态
			}
		}
	}
});