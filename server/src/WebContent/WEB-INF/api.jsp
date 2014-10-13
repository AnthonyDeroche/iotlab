

<h3 style="text-align:center;">API documentation</h3>
<div style="font-weight:bold;">
	Version : <span id="version"></span>
</div>

<hr />
<div id="doc" style="width:100%;">

</div>


<script type="text/javascript">

	$(function(){
		
		//parsing
		$.get("api?f=doc",function(root){
			if($.isXMLDoc(root)){
				
				var version;
				var dataTypes = [];
				var paramTypes = [];
				var modelTypes = [];
				var categories = [];
				var models = [];
				
				var c=0;
				
				//console.log(root);
				for(var key in root.children[0].children){
					var node = root.children[0].children[key];
					var content = root.children[0].children[key].textContent;
					var name = root.children[0].children[key].tagName;
					switch(name){
						case "version" :
							version=content;
							break;
						case "dataTypes" : 
							$(node).find('type').each(function(){
								dataTypes.push($(this).attr('value'));
							});
							break;
						case "paramTypes" :
							$(node).find('type').each(function(){
								paramTypes.push($(this).attr('value'));
							});
							break;
						case "modelTypes" :
							$(node).find('type').each(function(){
								modelTypes.push($(this).attr('value'));
							});
							break;
						case "models" :
							$(node).find('model').each(function(){
								models[$(this).attr('id')]={"requests":[],"responses":[]};
								var model = models[$(this).attr('id')];
								
								$(this).find('response').each(function(){
									model.responses.push({
										"success":$(this).attr('success'),
										"type":$(this).attr('type'),
										"code":$(this).attr('code'),
										"content":$(this).text(),
										});
								});
								$(this).find('request').each(function(){
									model.requests.push({
										"content":$(this).text()
										});
								});
							});
						break;
						case "categories" :
								$(node).find('category').each(function(){
									//CATEGORY
									categories[c]={
											"name":$(this).attr('name'),
											"desc":$(this).attr('desc'),
											"endpoints":[]
									};
		
									var e = 0;
									$(this).find('endpoint').each(function(){
										//ENDPOINT
										categories[c].endpoints[e]={
												"url":$(this).attr('url'),
												"method":$(this).attr('method'),
												"model":models[$(this).attr('model')],
												"desc":$(this).find('desc').text(),
												"parameters":[],
												"security":$(this).find('security').attr('allowed')
											};

										var p = 0;
										$(this).find('parameters>param').each(function(){
											categories[c].endpoints[e].parameters[p]={
													"name":$(this).attr('name'),
													"type":$(this).attr('type'),
													"dataType":$(this).attr('dataType'),
													"desc":$(this).attr('desc')
											};
											p++;
										});
										
										e++;
									});
									c++;
								});
							break;
						
						default : break;
					
					}
				}
			}
			
			//display
			
			
			$doc = $('#doc');
			$('#version').text(version);
			for(var c in categories){
				var category = categories[c];
				var $table = $('<table class="api-table"></table>').appendTo($doc);
				$table.append('<thead class="api-depth1"><tr class="api-cat"><th colspan="4">'+category.name+' &nbsp;&nbsp;<span class="api-cat-desc">'+category.desc+'</span></th></tr></thead>');
				var $body = $('<tbody class="api-depth1"></tbody>').appendTo($table);
				for(var e in category.endpoints){
					var endpoint = category.endpoints[e];
					$body.append('<tr class="api-depth2-head"><td class="api-'+endpoint.method.toLowerCase()+'">'+endpoint.method+'</td><td>'+endpoint.url+'</td><td>'+endpoint.desc+'</td><td class="api-security-'+endpoint.security.toLowerCase()+'">'+endpoint.security.toUpperCase()+'</td></tr>');
					
					$params = $('<table class="api-table"></table');
					$params.append('<thead><tr><th>Parameters</th><th>Description</th><th>Parameter type</th><th>Data type</th><th></th></tr></thead>');
					for(var p in endpoint.parameters){
						var param = endpoint.parameters[p];
						$params.append('<tr><td>'+param.name+'</td><td>'+param.desc+'</td><td>'+param.type+'</td><td>'+param.dataType+'</td><td></tr>');	
					}
					
					
					var $cell = $('<tr class="api-depth2"><td colspan="4"></td></tr>').appendTo($body).children('td:first').append($params);
					if(typeof endpoint.model != 'undefined'){
						$resps = $('<table class="api-table"></table');
						if(endpoint.model.responses.length>0){
							$resps.append('<thead><tr><th>Responses</th><th>Content type</th><th>Content</th></tr></thead>');
							for(var r in endpoint.model.responses){
								var resp=endpoint.model.responses[r];
								$resps.append('<tr><td class="api-success-'+resp.success+'">'+resp.code+'</td>'+
										'<td>'+resp.type+'</td><td>'+resp.content+'</td>');
										 
							}
							$cell.append($resps);
						}
						
						$reqs = $('<table class="api-table"></table');
						if(endpoint.model.requests.length>0){
							$reqs.append('<thead><tr><th>Request model</th><th>Content</th></tr></thead>');
							for(var r in endpoint.model.requests){
								var req=endpoint.model.requests[r];
								$reqs.append('<tr><td colspan="2">'+req.content+'</td></tr>');
										 
							}
							$cell.append($reqs);
						}
					}
					
				}
			}
			
			$('.api-table > thead.api-depth1').css('cursor','pointer').on('click',function(){
				$('.api-table tbody.api-depth1').hide();
				$(this).next().show();
			});
			
			$('tr.api-depth2-head').css('cursor','pointer').on('click',function(){
				$('tr.api-depth2').hide();
				$('tr.api-depth2-head').css('border','none');
				$('tr.api-depth2').css('border','none');
				$(this).css('border-top','1px solid #880088');
				$(this).next().css('border-bottom','1px solid #880088');
				$(this).next().show();
			});
		});

	});
	
</script>