/*	+ "<option value=\"10\">10</option>"
				+ "<option value=\"15\">15</option>"
				+ "<option value=\"20\">20</option>" + "</select>"*/
;
var totalRecord;
var pageNo;// 当前页数
var pageSize;// 每页条数
var outstr = "";
// 分页对象
var page = {
	// 数据节点
	domModal : {
		// 分页对象html
		divDom : "<div>"
				+ "<div class=\"pull-left\" id=\"_pageOtherInfo\"></div>"
				+ "<div class=\"pull-left\">"
				+ "<div class=\"show-rows\" id=\"show-size\">"
				+ "每页显示 <select id=\"recordsPerPage\" style=\"width: auto;\" name=\"page.pageSize\""
				+ "class=\"form-control select-sm inline\">"
				+ "<option value=\"20\">20</option>"
				+ "<option value=\"50\">50</option>"
				+ "<option value=\"100\">100</option>" + "</select>"
				+ " 条 </div>" + "<div class=\"num\" id=\"totalpage\"></div>"
				+ "</div>" + "<div class=\"pull-right\">"
				+ "<ul class=\"pagination\" id=\"pagination\"></ul>" + "</div>"
				+ "<div class='cb'></div>" + "</div>"
	},
	// 初始化树形
	initPage : function(pageDiv) {
		// 在节点上加载dom数据
		pageDiv.empty();
		pageDiv.append(page.domModal.divDom);
		pageNo = (pageNo == "" || pageNo == undefined || pageNo == null) ? 1
				: pageNo;
		pageSize = (pageSize == "" || pageSize == undefined || pageSize == null) ? 20
				: pageSize;
		if (pageSize != "") {
			pageDiv.find("#recordsPerPage").val(pageSize);
		}
		pageDiv.find('#jumpPage').blur(function() {
			var jpage = parseInt(pageDiv.find("#jumpPage").val());
			var max = parseInt(pageDiv.find("#jumpPage").attr("max"));
			var reg = new RegExp("^[1-9]\d*|0$");
			if (jpage == "") {
				pageDiv.find("#jumpPage").val(1);
				setPage(totalRecord, pageDiv);
				loadPage(1);
				return false;
			} else {
				if (!reg.test(jpage)) {
					pageDiv.find("#jumpPage").val(1);
					page.setPage(totalRecord, pageDiv);
					loadPage(1);
					return false;
				} else {
					if (1 <= jpage && jpage <= max) {
						page.setPage(totalRecord, pageDiv);
						loadPage(jpage);
					} else {
						pageDiv.find("#jumpPage").val(1);
						page.setPage(totalRecord, pageDiv);
						loadPage(1);
						return false;
					}
				}
			}
		});
		page.eventBind(pageDiv);
	},
	// 设置分页对象
	// total总记录数
	// pageDiv分页对象的DIV
	setPage : function(total, pageDiv) {
		totalRecord = total;
		pageDiv.find("#totalpage").html("共" + totalRecord + "条");
		var ps = new Array();
		var totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize
				: parseInt(totalRecord / pageSize) + 1;
		outstr = '<li id="first"class="disabled"><a aria-label="Previous"> <span aria-hidden="true"></i>共'
				+ totalPage + '页</span></a></li>';
		if (pageNo == 1) {
			outstr += '<li id="first"class="disabled"><a href="javascript:;" aria-label="Previous"> <span aria-hidden="true"><i class="ep ep-arrow-left"></i>首页</span></a></li>'
					+ '<li id="prev"class="disabled"><a href="javascript:;" aria-label="Previous"> <span aria-hidden="true"><i class="ep ep-arrow-left"></i>上一页</span></a></li>';
		} else
			outstr += '<li id="first">'
					+ '<a href="javascript:page.gotopage(1);" aria-label="Previous"><span aria-hidden="true"><i class="ep ep-arrow-left"></i>首页</span></a></li>'
					+ '<li id="prev"><a href="javascript:page.gotopage('
					+ (Number(pageNo) - 1)
					+ ',\''
					+ pageDiv.selector
					+ '\');" aria-label="Previous"> <span aria-hidden="true"><i class="ep ep-arrow-left"></i>上一页</span></a></li>';
		for (var i = pageNo - 3; i <= totalPage; i++) {
			if ((totalPage - i > 4 && i < pageNo - 2) || ps.length > 5 || i < 1)
				continue;
			if (ps.length == 5 && totalPage > 5) {
				break;
			} else
				ps.push(i + "");
		}
		for ( var item in ps) {
			outstr += '';
			if (ps[item] == pageNo) {
				outstr += '<li class="disabled" ><a href="javascript:page.gotopage('
						+ ps[item]
						+ ',\''
						+ pageDiv.selector
						+ '\');" >'
						+ ps[item] + '</a></li>';
			} else {
				outstr += '<li><a href="javascript:page.gotopage(' + ps[item]
						+ ',\'' + pageDiv.selector + '\');" >' + ps[item]
						+ '</a></li>';
			}
		}
		if (pageNo == totalPage) {
			outstr += '<li id="next" class="disabled"><a href="javascript:;" aria-label="Next" > <span aria-hidden="true">下一页<i class="ep ep-arrow-right"></i></span>'
					+ '<li id="next" class="disabled"><a href="javascript:;" aria-label="Next" > <span aria-hidden="true">末页<i class="ep ep-arrow-right"></i></span>';
		} else
			if(total == 0){
				outstr += '<li id="next" class="disabled"><a aria-label="Next" > <span aria-hidden="true">下一页<i class="ep ep-arrow-right"></i></span>'
					+ '<li id="next" class="disabled"><a aria-label="Next" > <span aria-hidden="true">末页<i class="ep ep-arrow-right"></i></span>';
			}else{
				outstr += '<li id="next"><a href="javascript:page.gotopage('
					+ (Number(pageNo) + 1)
					+ ',\''
					+ pageDiv.selector
					+ '\');" aria-label="Next" > <span aria-hidden="true">下一页<i class="ep ep-arrow-right"></i></span>'
					+ '<li id="next"><a href="javascript:page.gotopage(\''
					+ totalPage
					+ '\');" aria-label="Next" > <span aria-hidden="true">末页<i class="ep ep-arrow-right"></i></span>';
			}
			
		// 设置最大值属性
		pageDiv.find("#jumpPage").attr("max", totalPage);
		pageDiv.find("#pagination").html(outstr);
	},
	// 设置每页条数
	// pageSize：每页条数
	setPageSize : function(size, pageDiv) {
		pageSize = size;
		// 重置分页
		pageNo = 1;
		page.setPage(totalRecord, pageDiv);
		// 再次加载页面
		loadPage(1);
		$("#recordsPerPage").val(pageSize);
	},
	// 点击下一页或上一页
	gotopage : function(target, pageDiv) {
		pageNo = target
		page.setPage(totalRecord, $(pageDiv));
		loadPage(target);
	},
	eventBind : function(pageDiv) {
		pageDiv.find('#recordsPerPage').on('change', function() {
			page.setPageSize($(this).val(), pageDiv);
		});
	}
}