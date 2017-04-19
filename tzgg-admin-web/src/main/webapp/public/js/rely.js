/**
 * 公共方法
 * 提供基础调用
 */
;'use strict';
(function($){
	$.extend({
		isManager:0,
		notifyPersonRelations:[],
		seledHover:function(target){
	    	$(document).find(target).hover(function(){
	        	$(this).removeClass("border").addClass("btn-success").children(".del_sel").fadeIn();
	        },function(){
	        	$(this).removeClass("btn-success").addClass("border").children(".del_sel").fadeOut();
	        });
	    },
		/*filterData 根据传入的字段、字符、数组返回数据*/
		filterData:function(str,key,data){
			var arr = [];
			$.each(data,function(){
				if(this[key] === str){
					arr.push(this);
				}
			})
			return arr;
		},
		/**
		 * inIndexCon 点击检索
		 */
		inIndexCon:function(id,v,callback){
			var m = v;
			$("#"+id).on("click",function(){
				var v = $("#"+m).val();
				callback(v);
			});
		},
		/**
		 * 打开新的地址
		 * @param url
		 */
		openUrl:function(url){
			setTimeout(function(){
				window.location.href = url;
			},500);
		},
		stringifyJson:function(v){
			return JSON.stringify(v);
		},
		/**
		 * 组装请求数据
		 * @param u 请求地址
		 * @param t 请求类型
		 * @param d 请求数据
		 * @returns {Object}
		 */
		dataBox:function(u,t,d,m){
			var obj = {};
				obj["url"] = u;
				obj["type"]= t;
				obj["data"]= d;
			return obj;
		},
		/**
		 * 解析请求地址当中的参数 以对象形式返回
		 * @returns {{}}
		 */
		dataParse:function(v){
	        var s;
	            s = v?v:location.search.replace(/[?]/g,"");
	        var obj = {}, m = null;
	            s = s.split("&");
	        for(var i =0,n = s.length;i<n;i++){
	            m = s[i].split("=");
	            if(decodeURI(m[1]) !== m[1]){
	            	obj[m[0]] = decodeURI(m[1]);
	            }else{
	            	obj[m[0]] = m[1];
	            }
	        }
	        return obj;
	    },
		/**
		 * status 标记一个状态
		 * 仅供ajax 需要请求等待时使用
		 */
		status:{
			s:true,
			get:function(){return this.s;},
			set:function(v){this.s = v;}
		},
		/**
		 * 异步请求
		 * @param o 请求的数据
		 * @param callback 回调函数
		 * @param load 等待加载
		 */
		asyncT:function(o,load,callback){
			if(load !== "" || load !== undefined){
				$("#"+load).fadeIn();
			}
			var obj = o;
			$.ajax({
				url:obj["url"],
				type:obj["type"],
				dataType:"json",
				data:obj["data"],
				success:function(d){
					if(obj["modal"]){
						callback(d,obj["modal"]);
					}else{
						callback(d);
					}
				},error:function(){
					callback({
						head:{resultCode:1},
						body:{
							pageNo:1,
							pageSize:10,
							total:0,
							results:[]
						}
					});
					$.error("ajax请求异常,请求地址为--------"+obj["url"]);
				}
			});
		},
		asyncF:function(o,load,callback){
			if(load !== "" || load !== undefined){
				$("#"+load).fadeIn();
			}
			var obj = o;
			$.ajax({
				url:obj["url"],
				type:obj["type"],
				dataType:"json",
				data:obj["data"],
				ansync:false,
				success:function(d){
					if(obj["modal"]){
						callback(d,obj["modal"]);
					}else{
						callback(d);
					}
				},error:function(){
					callback({
						head:{resultCode:1},
						body:{
							pageNo:1,
							pageSize:10,
							total:0,
							results:[]
						}
					});
					$.error("ajax请求异常,请求地址为--------"+obj["url"]);
				}
			});
		},
		asyncTApp:function(o,load,callback){
			$("#"+load).fadeIn();
			var obj = o;
			$.ajax({
				url:obj["url"],
				type:obj["type"],
				dataType:"json",
				contentType:"application/json",
				data:obj["data"],
				success:function(d){
					if(obj["modal"]){
						callback(d,obj["modal"]);
					}else{
						callback(d);
					}
				},error:function(){
					callback({
						head:{resultCode:1},
						body:{
							pageNo:1,
							pageSize:10,
							total:0,
							results:[]
						}
					});
					$.error("ajax请求异常,请求地址为--------"+obj["url"]);
				}
			});
		},
		saveAjax:function(o,callback){
			if($.status.get()){
				var obj = o;
				$.ajax({
					url:obj["url"],
					type:obj["type"],
					dataType:"json",
					data:obj["data"],
					beforeSend:function(){
						$.status.set(false);
					},
					success:function(d){
						callback(d);
						$.status.set(true);
					},error:function(){
						callback({
							head:{resultCode:1}
						});
						$.status.set(true);
						$.error("ajax请求异常,请求地址为--------"+obj["url"]);
					}
				});
			}else{
				console.log("您的请求正在处理中请稍候尝试");	
			}
		},
		saveAjaxApp:function(o,callback){
			if($.status.get()){
				var obj = o;
				$.ajax({
					url:obj["url"],
					contentType:"application/json",
					type:obj["type"],
					dataType:"json",
					data:obj["data"],
					beforeSend:function(){
						$.status.set(false);
					},
					success:function(d){
						callback(d);
						$.status.set(true);
					},error:function(){
						callback({
							head:{resultCode:1}
						});
						$.status.set(true);
						$.error("ajax请求异常,请求地址为--------"+obj["url"]);
					}
				});
			}else{
				console.log("您的请求正在处理中请稍候尝试");	
			}
		},
		/**
		 * 根据输入计算可剩余字数
		 * @param e 绑定事件的id
		 * @param d 已经输入的字数
		 * @param ing 剩下可输入的字数
		 * @param c 一共可输入的字数
		 */
		fontCount:function(e,d,ing,c){
			$("#"+e).on("input",function(){
				var v = $(this).val();
				v = v.split("");
				$("#"+d).text(v.length);
				$("#"+ing).text(c-v.length);
			});
		},
		/**
		 * @param obj 传递的数组对象必须传一个key 如果只是纯粹的数组则不需要传key
		 * @param key 依据那一个字段来去重
		 * @returns {Array}
		 */
		noRepeat:function(obj,key){
			var arr    = obj,
				k      = key;
			var back   = [],
				status = {};
			if(this.isObj(arr[0]) === "[object Object]"){
				for(var i = 0,n = arr.length;i<n;i++){
					if(!status[arr[i][k]]){
						back.push(arr[i]);
						status[arr[i][k]]=true;
					}
				}
			}else{
				for(var j = 0,m = arr.length;j<m;j++){
					if(!status[arr[j]]){
						back.push(arr[j]);
						status[arr[j]]=true;
					}
				}
			}
			return back;
		},
		/**
		 * This method depends to jquery-jtemplates and jquery;
		 * show:表示显示容器的id
		 * hide:表示隐藏模板的id
		 * data:表示需要渲染的数据
		 */
		loadData:function(show,hide,data,callback){
			var showList = $("#"+show);
			showList.setTemplateElement(hide);
			showList.processTemplate(data);
			if(typeof callback == "function"){
				callback();
			}
		},
		/**
		 * This method depends to laydate;
		 */
		layOptionM:function (e,s,d,min,max){
			var arr = {
				elem: e,
				format: d?'YYYY-MM-DD':'YYYY-MM-DD hh:mm:ss',
				istime: d?false:true,
				istoday: true,
				min:min,
				max:max,
				start:s,
				choose: function(datas){
					this.start.max = datas; //结束日选好后，重置开始日的最大日期
					if($("#ui-form").length !== 0){
						var notNullStr = function(){
							if(isNullStr()){
								$("#clear_condition").css("color","#333");
							}else{
								$("#clear_condition").css("color","#ccc");
							}
						};
						var isNullStr = function(){
							var arr = $("#ui-form").find("input,select");
							var s = false;
							$.each(arr,function(i){
								if(this.value !== ""){
									s = true;
									return false;
								}
							});
							return s;
						}
						notNullStr();
					}
						
					
				}
			};
			return arr;
		},
		/**
		 * @returns {Number} 返回当前年月日
		 */
		tDate:function(myDate){
			var myD;
			myD = (myDate)?new Date(myDate):new Date();
			var d = parseInt(myD.getMonth()+1,10),
				s = parseInt(myD.getDate(),10);
			d = d<10?("0"+d):(d);/*月份*/
			s = s<10?("0"+s):(s);/*日期*/
			return parseInt((myD.getFullYear()+""+d+""+s),10);
		},
		forDate: function (x,symbol) {
			var s = String((x == "")?$.tDate():$.tDate(x));
			return (s.substr(0,4)+symbol+s.substr(4,2)+symbol+s.substr(6,2));
		},
	});
})(jQuery);



