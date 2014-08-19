$(function() {

	/********************************************** LABELS **************************************************/
	
	var LabelsView = function(loadingManager) {
		var lm=loadingManager;
		var lv = this;
		this.label_id=null;
		
		this.refreshLabel=function(data) {
			var columnsNb = 2;
			$('#labelsTables').empty();
			$('#labelFilterAdd').empty();
			
			for(var i=0;i<columnsNb;i++){
				
				$('<div class="columns medium-'+12/columnsNb+'" style="width:'+100/columnsNb+'%;"></div>')
				.append('<table style="width:100%;"><thead><tr><th>label_id</th><th>label</th><th>Action</th></tr></thead><tbody id="labelsTable'+i+'"></tbody></table>')
				.appendTo('#labelsTables');
				
				for(var k=Math.ceil(i*data.labelsNb/columnsNb);k<Math.ceil((i+1)*data.labelsNb/columnsNb);k++){
					var removeButton="";
					//if(k>5)
						removeButton = '<a href="#'+data.labels[k].label_id+'-label"'+
						' class="deleteLabel fi-x" style="color:red;text-align:center;"'+
						' data-reveal-id="deleteLabelModal" title="delete"></a>';
					
					$('<tr><td>'+data.labels[k].label_id+'</td><td>'+data.labels[k].label+'</td><td>'+removeButton+'</td></tr>').appendTo('#labelsTable'+i+'');
				}
				
				$('.deleteLabel').on('click',function(e) {
					lv.label_id = parseInt($(this).attr('href').substr(1,$(this).attr('href').length));
				});
				
			}
		};
		
		$('#addLabelForm').on('submit valid invalid',function(e) {
			e.stopPropagation();
			e.preventDefault();
			if (e.type == "valid") {

				var label = $('#labelFieldAdd').val();

				$.ajax({
					type : "POST",
					url : "rest/info/labels/add",
					data : "label="+label,
					beforeSend : function() {
						$('#addLabelForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addLabelForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.notify("labels");
							$('#addLabelModal').foundation('reveal','close');
							lm.success(content.message,$('#content'));	
							
						} else {
							lm.error(content.message,$('#addLabelModal'));
						}
					}

				});
			}
		});

		$('#deleteLabelForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/info/labels/delete",
				data : "label_id="+ lv.label_id,
				beforeSend : function() {
					$('#deleteLabelForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#deleteLabelForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {

					if (content.success == 1) {
						lm.notify("labels");
						$('#deleteLabelModal')
						.foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#deleteLabelModal'));
					}
				}

			});
			return false;
		});
			
		this.refresh = [this.refreshLabel];
		this.urls = ["rest/info/labels"];
	};
		
	/********************************************** FILTERS **************************************************/

	var FiltersView = function(loadingManager) {
		var lm=loadingManager;
		var fv = this;
		this.filter_id=null;
		this.refreshFilter = function(data) {
			var columnsNb = 2;
			$('#filtersTables').empty();
			
			for(var i=0;i<columnsNb;i++){
				
				$('<div class="columns medium-'+12/columnsNb+'" style="width:'+100/columnsNb+'%;"></div>')
				.append('<table style="width:100%;"><thead><tr><th>Type</th><th>Offset</th><th>Label</th><th>Strategy</th><th>Action</th></tr></thead><tbody id="filtersTable'+i+'"></tbody></table>')
				.appendTo('#filtersTables');
				
				for(var k=Math.ceil(i*data.filtersNb/columnsNb);k<Math.ceil((i+1)*data.filtersNb/columnsNb);k++){
					var removeButton = '<a href="#'+data.filters[k].id+'-filter"'+
						' class="deleteFilter fi-x" style="color:red;text-align:center;"'+
						' data-reveal-id="deleteFilterModal" title="delete"></a>';
					
					$('<tr><td>'+data.filters[k].type.id+'</td><td>'+data.filters[k].offset+'</td><td>'+data.filters[k].label.label+'</td><td>'+data.filters[k].strategy.className+'</td><td>'+removeButton+'</td></tr>').appendTo('#filtersTable'+i+'');
				}
				
				$('.deleteFilter').on('click',function(e) {
					fv.filter_id = parseInt($(this).attr('href').substr(1,$(this).attr('href').length));
				});
				
			}
		};
		
		this.refreshLabelsList = function(data){
			//filling in the filter form list
			for(var k in data.labels)
				$('<option value="'+data.labels[k].label_id+'">'+data.labels[k].label+'</option>').appendTo('#labelFilterAdd');
		};
		
		this.refreshTypesList = function(data){
			//filling in the filter form list
			for(var i in data.types)
				$('<option value="'+data.types[i].id+'">'+data.types[i].id+' ('+data.types[i].description+')</option>').appendTo('#typeFilterAdd');
		};
		
		$('#addFilterForm').on('submit valid invalid',function(e) {
			e.stopPropagation();
			e.preventDefault();
			if (e.type == "valid") {
				var type = $('#typeFilterAdd > option:selected').val();
				var offset = $('#offsetFilterAdd').val();
				var label_id = $('#labelFilterAdd > option:selected').val();
				var strategy_id = $('#strategyFilterAdd > option:selected').val();
				$.ajax({
					type : "POST",
					url : "rest/info/filters/add",
					data : "offset="+offset+"&label="+label_id+"&strategy="+strategy_id+"&type="+type,
					beforeSend : function() {
						$('#addFilterForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addFilterForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.notify("filters");
							$('#addFilterModal').foundation('reveal','close');
							lm.success(content.message,$('#content'));	
							
						} else {
							lm.error(content.message,$('#addFilterModal'));
						}
					}
	
				});
			}
		});
		
		
		$('#deleteFilterForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/info/filters/delete",
				data : "filter_id="+ fv.filter_id,
				beforeSend : function() {
					$('#deleteFilterForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#deleteFilterForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {
	
					if (content.success == 1) {
						lm.notify("filters");
						$('#deleteFilterModal')
						.foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#deleteFilterModal'));
					}
				}
	
			});
			return false;
		});
		
		this.refresh = [this.refreshFilter,this.refreshLabelsList,this.refreshTypesList];
		this.urls = ["rest/info/filters","rest/info/labels","rest/info/types"];
	};
	
	/********************************************** STRATEGIES **************************************************/
	
	var StrategiesView = function(loadingManager) {
		var lm=loadingManager;
		var sv = this;
		this.strategy_id=null;
		this.refreshStrategy = function(data) {
			var columnsNb = 2;
			$('#strategiesTables').empty();
			
			$('#strategyFilterAdd').empty();
			
			for(var i=0;i<columnsNb;i++){
				
				$('<div class="columns medium-'+12/columnsNb+'" style="width:'+100/columnsNb+'%;"></div>')
				.append('<table style="width:100%;"><thead><tr><th>id</th><th>className</th><th>Action</th></tr></thead><tbody id="strategiesTable'+i+'"></tbody></table>')
				.appendTo('#strategiesTables');
				
				for(var k=Math.ceil(i*data.strategiesNb/columnsNb);k<Math.ceil((i+1)*data.strategiesNb/columnsNb);k++){
					var removeButton = '<a href="#'+data.strategies[k].id+'-strategy"'+
						' class="deleteStrategy fi-x" style="color:red;text-align:center;"'+
						' data-reveal-id="deleteStrategyModal" title="delete"></a>';
					
					$('<tr><td>'+data.strategies[k].id+'</td><td>'+data.strategies[k].className+'</td><td>'+removeButton+'</td></tr>').appendTo('#strategiesTable'+i+'');
				
					//filling in the filter form list
					$('<option value="'+data.strategies[k].id+'">'+data.strategies[k].className+'</option>').appendTo('#strategyFilterAdd');
				}
				
				$('.deleteStrategy').on('click',function(e) {
					sv.strategy_id = parseInt($(this).attr('href').substr(1,$(this).attr('href').length));
				});
				
			}
		};

		$('#addStrategyForm').on('submit valid invalid',function(e) {
			e.stopPropagation();
			e.preventDefault();
			if (e.type == "valid") {
				var className = encodeURIComponent($('#classNameStrategyAdd').val());
				$.ajax({
					type : "POST",
					url : "rest/info/strategies/add",
					data : "className="+className,
					beforeSend : function() {
						$('#addFStrategyForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addStrategyForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.notify("strategies");
							$('#addStrategyModal').foundation('reveal','close');
							lm.success(content.message,$('#content'));	
							
						} else {
							lm.error(content.message,$('#addStrategyModal'));
						}
					}
	
				});
			}
		});
		
		
		$('#deleteStrategyForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/info/strategies/delete",
				data : "id="+ sv.strategy_id,
				beforeSend : function() {
					$('#deleteStrategyForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#deleteStrategyForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {
	
					if (content.success == 1) {
						lm.notify("strategies");
						$('#deleteStrategyModal')
						.foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#deleteStrategyModal'));
					}
				}
	
			});
			return false;
		});
		this.refresh = [this.refreshStrategy];
		this.urls = ["rest/info/strategies"];
	};
	
	/********************************************** TYPES **************************************************/

	var TypesView = function(loadingManager) {
		var lm=loadingManager;

		this.refreshType = function(data) {
			var types = {};
			types = data.types;
			$('#typesTable > tbody').empty();
			
			for(var i=0;i<data.typesNb; i++){
				
				$('#typesTable > tbody').append('<tr><td>'+types[i].id+'</td><td>'+types[i].description
						+'</td><td>'+types[i].streamName+'</td>'
						+'<td>'+types[i].minDataNumber+'</td>'
					    +'<td><a href="#'+types[i].id+'" class="deleteType fi-x" data-reveal-id="deleteTypeModal" style="color:red;"></a></td></tr>');
			}
			
			$('.deleteType').on('click',function(e) {
				type_id = parseInt($(this).attr('href').substr(1,$(this).attr('href').length));
			});
			
		};
		
		$('#addTypeForm').on('submit valid invalid',function(e) {
			e.stopPropagation();
			e.preventDefault();
			if (e.type == "valid") {
				var type_id = $('#idTypeAdd').val();
				var description = $('#descriptionTypeAdd').val();
				var streamName = $('#streamNameTypeAdd').val();
				var minDataNumber = $('#minimunDataNumberTypeAdd').val();
				$.ajax({
					type : "POST",
					url : "rest/info/types/add",
					data : "type_id="+type_id+"&description="+description+"&streamName="+streamName+"&minDataNumber="+minDataNumber,
					beforeSend : function() {
						$('#addTypeForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addTypeForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.notify("types");
							$('#addTypeModal').foundation('reveal','close');
							lm.success(content.message,$('#content'));	
							
						} else {
							lm.error(content.message,$('#addTypeModal'));
						}
					}

				});
			}
		});
		
		$('#deleteTypeForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/info/types/delete",
				data : "type_id="+ type_id,
				beforeSend : function() {
					$('#deleteTypeForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#deleteTypeForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {

					if (content.success == 1) {
						lm.notify("types");
						$('#deleteTypeModal')
						.foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#deleteTypeModal'));
					}
				}

			});
			return false;
		});
		
		this.refresh = [this.refreshType];
		this.urls = ["rest/info/types"];
		
	};
	/********************************************** DBSTATS **************************************************/

	var DbStatsView = function(loadingManager) {
		var lm=loadingManager;

		this.refreshStats = function(param){
			$('<table id="statsTable"><th colspan="4">Database Statistics</th></table').appendTo('#dashboardStats');
			$('<tr></tr>').appendTo('#statsTable').append(
					'<td colspan="2">'+param[0].parameter+'</td><td colspan="2">'+param[0].value+' '+param[0].unit+'</td>');
			for(var i=1;i<param.length-1;i+=2){
				$('<tr></tr>').appendTo('#statsTable').append(
						'<td>'+param[i].parameter+'</td><td>'+param[i].value+' '+param[i].unit+'</td>').append(
						'<td>'+param[i+1].parameter+'</td><td>'+param[i+1].value+' '+param[i+1].unit+'</td>');
			}
		};
		
		this.refresh = [this.refreshStats];
		this.urls = ["rest/info/dbstats"];
	};
	
	/********************************************** CONFIG **************************************************/

	var ConfigView = function(loadingManager) {
		var lm=loadingManager;
		this.refreshConfig = function(param){
			$('<table id="configTable"><th colspan="2">Config</th></table').appendTo('#dashboardConfig');
			for(var i=0;i<param.length;i++){
				$('<tr></tr>').appendTo('#configTable').append(
						'<td>'+param[i].key+'</td><td>'+param[i].value+'</td>');
			}
		};
		
		this.refresh = [this.refreshConfig];
		this.urls = ["rest/info/conf"];
	};
	
	/********************************************** MONITORING RULES **************************************************/
	
	var MonitoringRulesView = function(loadingManager) {
		var lm=loadingManager;
		
		this.refreshMonitoringRules = function(data){
			$("#rules").html(data);
		};

		this.refresh = [this.refreshMonitoringRules];
		this.urls = ["admin?load=rules"];
	};

	/********************************************** GEOLOCATION **************************************************/
	
	var GeolocationView = function(loadingManager) {
		var lm=loadingManager;
		var gv = this;
		this.senders={};
		this.anchors={};
		this.r = 0;
		
		this.refreshServices = function(data) {
			var services = data.services;
			$('#g_config > tbody').empty();
			for(var i in services){
				var checked = "";
				if(services[i].enabled)
					checked = "checked";
				$('#g_config > tbody').append('<tr><td>'+services[i].name+'</td><td>'+
						'<div class="switch">'+
						  	'<input id="t'+services[i].name+'" class="g_toggle" '+checked+' type="checkbox">'+
							'<label for="t'+services[i].name+'"></label>'+
						'</div>'+
						'</td></tr>');
			}
			
			$('#g_config').foundation();
			
			$('.g_toggle').on('click',function(e){
				
				var serviceName = $(this).attr('id').substring(1,$(this).attr('id').length);
				//var $this = $(this);
				if($(this).is(':checked')){
					page = 'enable';
				}else{
					page = 'disable';
				}
				
				$.ajax({
					type : "POST",
					url : "rest/geo/services/"+page,
					data : "service="+serviceName,
					beforeSend : function() {$('#main-loader').css('visibility','visible');},
					complete : function() {$('#main-loader').css('visibility','hidden');},
					success : function(content) {
						if (content.success) {
							lm.success(content.message,$('#content'));
						}else{
							lm.error(content.message,$('#content'));
						}
					}

				});
				
			});
		};
		
		this.refreshMeasures = function(content){
			var measures = content.measures;
			$('#calibrationMeasures > tbody').empty();
			for(var i in measures){
				$('#calibrationMeasures > tbody').append('<tr><td>'+measures[i].first.mac+'</td><td>'+measures[i].second.mac+'</td><td>'+measures[i].wallNumber+'</td></tr>');
			}
		};
		
		this.refreshSenders = function(data) {
			gv.senders = data.sender;
			(gv.r)++;
			gv.fillTables();
		};
		
		this.refreshAnchors = function(data) {
			gv.anchors = data.anchor;
			(gv.r)++;
			gv.fillTables();
		};
		
		this.fillTables = function(){
			if(gv.r==2){
				var anchorsIds = new Array();
				$('#anchorsTable > tbody').empty();
				for(var i in gv.anchors){
					$('#anchorsTable > tbody').append('<tr id="gmote'+gv.anchors[i].id+'"><td>'+gv.anchors[i].mac+'</td><td><a href="#'+gv.anchors[i].id+'" class="removeAnchor fi-minus" style="color:red;font-size:16px;"></a></td></tr>');
					anchorsIds[gv.anchors[i].id]=true;
				}
				
				$('#motesTable > tbody').empty();
				for(var i in gv.senders){
					if(!anchorsIds[gv.senders[i].id])
						$('#motesTable > tbody').append('<tr id="gmote'+gv.senders[i].id+'"><td>'+gv.senders[i].mac+'</td><td><a href="#'+gv.senders[i].id+'" class="addAnchor fi-plus" style="color:lightgreen;font-size:16px;"></a></td></tr>');
				}
				
				$('.addAnchor').on('click',function(e){gv.addAnchor(e,$(this));});
				$('.removeAnchor').on('click',function(e){gv.removeAnchor(e,$(this));});
				gv.r=0;
			}
		};
		
		this.addAnchor = function(e,$elt){
			e.preventDefault();
			var href = $elt.attr('href');
			var id = $elt.attr('href').substring(1,href.length);
			$.ajax({
				type : "POST",
				url : "rest/info/motes/anchor/add",
				data : "mote_id="+id,
				beforeSend : function() {$('#main-loader').css('visibility','visible');},
				complete : function() {$('#main-loader').css('visibility','hidden');},
				success : function(content) {
					if (content.success) {
						$('#gmote'+content.mote_id).appendTo('#anchorsTable > tbody');
						$('#gmote'+content.mote_id).find('.addAnchor').
							removeClass('addAnchor').removeClass('fi-plus').addClass('removeAnchor').
							addClass('fi-minus').css('color','red').off('click').on('click',function(e){gv.removeAnchor(e,$(this));});
					} else {
						lm.error(content.message,$('#content'));
					}
				}
			});
		};
		
		this.removeAnchor = function(e,$elt){
			e.preventDefault();
			var href = $elt.attr('href');
			var id = $elt.attr('href').substring(1,href.length);
			$.ajax({
				type : "POST",
				url : "rest/info/motes/anchor/remove",
				data : "mote_id="+id,
				beforeSend : function() {$('#main-loader').css('visibility','visible');},
				complete : function() {$('#main-loader').css('visibility','hidden');},
				success : function(content) {
					if (content.success) {
						$('#gmote'+content.mote_id).appendTo('#motesTable > tbody');
						$('#gmote'+content.mote_id).find('.removeAnchor').
						removeClass('removeAnchor').removeClass('fi-minus').addClass('addAnchor').
						addClass('fi-plus').css('color','lightgreen').off('click').on('click',function(e){addAnchor(e,$(this));});
					} else {
						lm.error(content.message,$('#content'));
					}
				}
			});
		};
		
		$('#refreshGeo').on('click',function(){
			lm.loadView("geolocation");
		});

		this.refresh = [this.refreshSenders,this.refreshAnchors,this.refreshMeasures,this.refreshServices];
		this.urls = ["rest/info/motes","rest/info/motes/anchor","rest/geo/calibration/measures","rest/geo/services"];
	};
	
	/********************************************** NETWORK MONITORING **************************************************/
	
	var MonitoringView = function(loadingManager) {
		var lm=loadingManager;
		this.mote_id=null;
		var mv = this;
		this.sinks = {};
		this.senders = {};
	
		this.refreshMotes = function(data) {
				mv.sinks = data.sink;
				mv.senders = data.sender;
				mv.fillMotesTable();
		};
			
		this.fillMotesTable = function(){
				$('#sinksTable > tbody').empty();
				for(var i in mv.sinks){
						$('#sinksTable > tbody').append('<tr><td>'+mv.sinks[i].id+'</td><td>'
								+mv.sinks[i].mac+'</td><td>'+mv.sinks[i].dodagVersionNumber+'</td><td><a href="#'
								+mv.sinks[i].id+'" class="removeMote fi-x" data-reveal-id="deleteMoteModal" style="color:red;"></a></td></tr>');
				}
				$('#sendersTable > tbody').empty();
				for(var i in mv.senders){
						$('#sendersTable > tbody').append('<tr><td>'+mv.senders[i].id+'</td><td>'
								+mv.senders[i].mac+'</td><td><a href="#'
								+mv.senders[i].id+'" class="removeMote fi-x" data-reveal-id="deleteMoteModal" style="color:red;"></a></td></tr>');
				}
				
				$('.removeMote').on('click',function(e) {
					mv.mote_id = parseInt($(this).attr('href').substr(1,$(this).attr('href').length));
				});
		};
		
		$('.command').on('click',function(e){
			e.preventDefault();
			var data = $(this).attr('href').substring(1,$(this).attr('href').length).toUpperCase();
			var $eltToEmpty=null;
			
			if(data=="START"){
				data+=" "+$('#start_select > option:selected').val();
			}
			else if(data =="STOP"){
				data+=" "+$('#stop_select > option:selected').val();
			}
			else if(data=="DATA_PERIOD"){
				$eltToEmpty = $('#periodValue');
				var val = parseInt($('#periodValue').val());
				data+=" "+val;
				if(isNaN(val) || val>65535){
					$('#periodValue').css('background-color','#dd6666');
					return;
				}
				$('#periodValue').css('background-color','white');
			}
			else if(data=="TXPOWER"){
				$eltToEmpty = $('#txpowerValue');
				var val = parseInt($('#txpowerValue').val());
				data+=" "+val;
				if(isNaN(val) || val<1 || val>31 ){
					$('#txpowerValue').css('background-color','#dd6666');
					return;
				}
				$('#txpowerValue').css('background-color','white');
			}
			else if(data=="SINK_ID_DVN"){
				window.setTimeout(function(){lm.notify("monitoring");},1000);
			}
			
			
			$.ajax({
				type : "POST",
				url : "rest/sendCommand",
				data : "data="+data,
				beforeSend : function() {
					$('.loader').css('visibility','visible');
				},
				complete : function() {
					$('.loader').css('visibility','hidden');
				},
				success : function(content) {
					if (content.success) {
						lm.success(content.message,$('#content'));
						if($eltToEmpty!=null)
							$eltToEmpty.val("");
					} else {
						lm.error(content.message,$('#content'));	
					}
				}
	
			});
		
		});
		
		$('#deleteMoteForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/info/motes/remove",
				data : "mote_id="+ mv.mote_id,
				beforeSend : function() {
					$('#deleteMoteForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#deleteMoteForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {
	
					if (content.success == 1) {
						lm.notify("monitoring");
						//$('.tableCenter').find('a[href="#'+mote_id+'"]').parent().parent().remove();
						$('#deleteMoteModal')
						.foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#deleteMoteModal'));
					}
				}
	
			});
			return false;
		});
		
		$('#refreshNetworkMonitoring').on('click',function(){
			lm.loadView("monitoring");
		});

		this.refresh = [this.refreshMotes];
		this.urls = ["rest/info/motes"];
	};
	
	/********************************************** LOGS **************************************************/
		var LogsView = function(loadingManager) {
			var lm=loadingManager;
			
			this.refreshLogs = function(data) {
				$('#logsTable > tbody').empty();
				for(var i in data){
					$('#logsTable > tbody').append('<tr><td>'+data[i].datetime+'</td><td>'+data[i].member+'</td><td>'+data[i].module+'</td>'+
							'<td>'+data[i].message+'</td><td>'+data[i].ip+'</td><td>'+data[i].level+'</td></tr>');
				}
			};
			
			$('#refreshLogs').on('click',function(){
				lm.loadView("logs");
			});

			this.refresh = [this.refreshLogs];
			this.urls = ["rest/info/logs/200"];
		};

	/********************************************** ACCOUNTS **************************************************/

	var AccountsView = function(loadingManager) {
		var lm=loadingManager;
		var av = this;
		this.account_username=null;
		var refreshAccounts = function(data) {
			var accounts = data.accounts;
			$('#accountsTable > tbody').empty();
			for(var i in accounts){
				var checked = accounts[i].admin ? "checked" : "";
				var removeButton = accounts[i].admin ? "" : '<a class="button small radius alert removeAccount" data-reveal-id="removeAccountModal" href="#'+accounts[i].username+'">Remove</a>';
				$('#accountsTable > tbody').append('<tr><td><input type="text" class="editAccountUsername" value="'+accounts[i].username+'" readonly /></td><td><input class="editAccountPassword" type="password"/></td><td><input type="email" class="editAccountEmail" value="'+accounts[i].email+'"/></td><td>'+
						'<div class="switch small">'+
						  '<input id="'+accounts[i].username+'-admin" class="editAccountAdmin" type="checkbox" '+checked+' >'+
						  '<label for="'+accounts[i].username+'-admin"></label>'+
						'</div>'+
					'</td><td><a class="button small radius updateAccount" href="#'+accounts[i].username+'">Update</a></td>'+
						'<td>'+removeButton+'</td></tr>');
			}
			
			$('.removeAccount').on('click',function(e) {
				e.preventDefault();
				av.account_username = $(this).attr('href').substr(1,$(this).attr('href').length);
			});
			
			$('.updateAccount').on('click',function(e) {
				e.preventDefault();
				var username = $(this).parent().parent().find('.editAccountUsername').val();
				
				var $passwordField = $(this).parent().parent().find('.editAccountPassword');
				var $emailField = $(this).parent().parent().find('.editAccountEmail');
				var password = $passwordField.val();
				var email = $emailField.val();
				
				var admin = $(this).parent().parent().find('.editAccountAdmin').is(':checked');

				if(password.length>0 && password.length<6){
					lm.error("Password must contain at least 6 characters",$('#content'));
					return;
				}

				if(!$emailField[0].validity.valid){
					lm.error("Email is not valid",$('#content'));
					return;
				}
				
				$.ajax({
					type : "POST",
					url : "rest/account/update",
					data : "username="+username+"&password="+password+"&email="+email+"&admin="+admin,
					beforeSend : function() {
						$('#addAccountForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addAccountForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.success(content.message,$('#content'));	
							$passwordField.val("");
						} else {
							lm.error(content.message,$('#content'));
						}
					}

				});
			});
			
			$('#accountsTable').foundation();
			
		};
		
		
		$('#addAccountForm').on('submit valid invalid',function(e) {
			e.stopPropagation();
			e.preventDefault();
			if (e.type == "valid") {
				var username = $('#usernameAccountAdd').val();
				var password = $('#passwordAccountAdd').val();
				var email = $('#emailAccountAdd').val();
				$.ajax({
					type : "POST",
					url : "rest/account/register",
					data : "username="+username+"&password="+password+"&email="+email,
					beforeSend : function() {
						$('#addAccountForm .loader').css('visibility','visible');
					},
					complete : function() {
						$('#addAccountForm .loader').css('visibility','hidden');
					},
					success : function(content) {
						if (content.success) {
							lm.notify("accounts");
							$('#addAccountForm').foundation('reveal','close');
							lm.success(content.message,$('#content'));	
							 $('#addAccountForm')[0].reset();
							
						} else {
							lm.error(content.message,$('#addAccountModal'));
						}
					}

				});
			}
		});
		

		$('#removeAccountForm').on('submit',function(e) {
			$.ajax({
				type : "POST",
				url : "rest/account/remove",
				data : "username="+ av.account_username,
				beforeSend : function() {
					$('#removeAccountForm .loader').css('visibility','visible');
				},
				complete : function() {
					$('#removeAccountForm .loader').css('visibility','hidden');
				},
				success : function(
					content) {

					if (content.success == 1) {
						lm.notify("accounts");
						$('#removeAccountModal').foundation('reveal','close');
						lm.success(content.message,$('#content'));
					} else {
						lm.error(content.message,$('#removeAccountModal'));
					}
				}

			});
			return false;
		});
		
		this.refresh = [refreshAccounts];
		this.urls = ["rest/account/accounts"];
	};

	/********************************************** **************************************************/
	
	var LoadingManager = function(){
		var lm = this;
		this.views = {	"monitoring" : new MonitoringView(this),
						"dbStats" : new DbStatsView(this), 
						"config" : new ConfigView(this), 
						"labels" : new LabelsView(this), 
						"filters" : new FiltersView(this),
						"strategies" : new StrategiesView(this), 
						"types" : new TypesView(this), 
						"monitoringRules" : new MonitoringRulesView(this), 
						"geolocation" : new GeolocationView(this),
						"accounts" : new AccountsView(this), 
						"logs" : new LogsView(this)
		};
		
		this.dependencies={};
		
		this.construct = function(){
			for(var name in lm.views){
				for(var u=0;u<lm.views[name].urls.length;u++){
					var dependency = lm.dependencies[lm.views[name].urls[u]];
					if(!dependency)
						lm.dependencies[lm.views[name].urls[u]]=[];
					lm.dependencies[lm.views[name].urls[u]].push(lm.views[name]);
				}
					
			}
		};
		this.construct();
		
		this.loadView=function(name){
			if(typeof lm.views[name] != "undefined"){
				for(var u=0;u<lm.views[name].refresh.length;u++)
					$.get(lm.views[name].urls[u],function(data){
						for(var v in lm.dependencies[this.url]){
							if(lm.dependencies[this.url][v]===lm.views[name]){
								for(var r in lm.dependencies[this.url][v].refresh){
									if(!lm.dependencies[this.url][v].refresh[r])
										console.log(lm.dependencies[this.url][v]);
									else if(this.url==lm.dependencies[this.url][v].urls[r])
										(lm.dependencies[this.url][v].refresh[r])(data);
								}
								break;
							}
						}
					});
				
			}else
				console.log("View "+name+" unknown");
		};
		
		this.loadUrl = function(url){
			$.get(url,function(data){
				for(var i in lm.dependencies[this.url]){
					var view = lm.dependencies[this.url][i];
					for(var c=0;c<view.urls.length;c++){
						if(view.urls[c]==this.url){
							var callback = view.refresh[c];
							if(typeof callback != "function")
								console.log(callback);
							callback(data);
							break;
						}
					}
				}
			});
		};
		
		this.load = function(){
			for(var url in lm.dependencies){
				lm.loadUrl(url);
			}
		};
		
		this.notify = function(name){
			if(typeof lm.views[name] != "undefined"){
				for(var i in lm.views[name].urls)
					lm.loadUrl(lm.views[name].urls[i]);
			}else
				console.log("View "+name+" unknown");
		};

		
		this.success = function(message, $parent) {
			$('<div style="position:fixed;width:70%;left:15%;top:10%;" data-alert class="alert-box success radius">'
							+ message
							+ '<a href="#" class="close fi-x"></a></div>')
					.appendTo($parent);
			lm.scheduleAutoClose($parent);
		};


		this.error = function(message, $parent) {
			$('<div style="position:fixed;width:70%;left:15%;top:10%;" data-alert class="alert-box warning radius">'
							+ message
							+ '<a href="#" class="close fi-x"></a></div>').appendTo($parent);
			lm.scheduleAutoClose($parent);
		};

		this.scheduleAutoClose = function($parent) {
			var clearAlert = setTimeout(function() {
				$parent.find(".alert-box").fadeOut('slow');
			}, 4000);

			$(document).on("click", ".alert-box a.close", function(event) {
				clearTimeout(clearAlert);
			});

			$(document).on("click", ".alert-box a.close", function(event) {
				event.preventDefault();
				$(this).closest(".alert-box").fadeOut(function(event) {
					$(this).remove();
				});
			});
		};
	};
	
	var lm = new LoadingManager();
	lm.load();
});
