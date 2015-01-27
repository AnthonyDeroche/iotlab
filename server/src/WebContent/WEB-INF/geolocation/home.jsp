<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	
		<div class="row" data-equalizer> 
			<div class="large-3 columns panel" data-equalizer-watch>
				<table>
					<tr><td colspan="2"><span data-tooltip class="has-tip" id="status"></span></td></tr>
					<tr><td><span data-tooltip class="has-tip" title="Path loss computed with automatic calibration ; RSSI = L0 + 10.a.log(d)">a </span>: </td><td><span id="a"></span></td></tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Localization : 
						</td>
						<td>
							<div class="switch round small">
							  <input id="pause" type="checkbox" checked >
							  <label for="pause"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Lock motes : 
						</td>
						<td>
							<div class="switch round small">
							  <input id="lockPos" type="checkbox" checked >
							  <label for="lockPos"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Scale (cm/px) : 
						</td>
						<td>
							<div class="switch round small">
							  <input id="scale" type="text" value="3.5">
							</div>
						</td>
					</tr>
				</table>
			</div> 
			<div class="large-3 columns panel" data-equalizer-watch>
				<table>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Barycenter
						</td>
						<td>
							<div class="switch small">
							  <input id="barycenter" type="radio" name="algos">
							  <label for="barycenter"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Multilateration
						</td>
						<td>
							<div class="switch small">
							  <input id="multilateration" type="radio" name="algos">
							  <label for="multilateration"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Trilateration
						</td>
						<td>
							<div class="switch small">
							  <input id="trilateration" type="radio" name="algos">
							  <label for="trilateration"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Combined
						</td>
						<td>
							<div class="switch small">
							  <input id="combined" type="radio" checked name="algos">
							  <label for="combined"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td style="vertical-align:top;padding-top:15px;">
							Custom
						</td>
						<td>
							<div class="switch small">
							  <input id="custom" type="radio" name="algos">
							  <label for="custom"></label>
							</div>
						</td>
					</tr>
				</table>
				
			</div>
			<div class="large-4 columns panel" data-equalizer-watch>
				<a href="#" data-dropdown="drop" class="small radius button dropdown">Algorithms' tester and optimizer</a><br> 
				<ul id="drop" data-dropdown-content class="f-dropdown">
					<li>
						<table style="border:none;width:100%;margin:0px;">
							<tr>
								<td style="vertical-align:top;padding-top:15px;">
									Status : 
								</td>
								<td id="optimizerSwitch">
		
								</td>
							</tr>
						</table>
						Retrieving live data : <span id="collectProgress">0%</span>
					</li>
					
					<li><a href="#results">Check out results</a></li>
					<li><a href="#" id="useWeights">Use these weights to customize combination</a></li> 
				</ul>
				<table id="coeffs">
					<tr><th></th><th>x</th><th>y</th></tr>
					<tr><th>Barycenter</th><td><input type="text" id="xbarycenter"/></td><td><input type="text" id="ybarycenter"/></td></tr>
					<tr><th>Multilateration</th><td><input type="text" id="xmultilateration"/></td><td><input type="text" id="ymultilateration"/></td></tr>
					<tr><th>Trilateration</th><td><input type="text" id="xtrilateration"/></td><td><input type="text" id="ytrilateration"/></td></tr>
				</table>
			</div>
			<div class="large-2 columns panel" data-equalizer-watch> 
				<div id="g_mote_info">
					<table>
					
					</table>
				</div>
			</div>
		</div>


		

	<div id="container" style="background-image:url('img/tn-et2.png');background-repeat:no-repeat;"></div>
	<div style="height:200px;width:100%;"></div>
	
	<div class="row">
		<div data-alert class="alert-box round">
		  Motes must be correctly placed in order to get relevant results !
		  <a href="#" class="close">&times;</a>
		</div>
		<div id="results" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
	</div>
	<script>
		$(function(){
			var protocol = "ws://";
			if(document.location.protocol=='https:')
				protocol = "wss://";
			var split = document.location.pathname.split('/');
			split.shift();
			split.pop();
			var host = protocol+window.location.host+"/"+split.join('/')+"/liveStream/10";
			console.log(host);
			
			var pause=false;
			var lockPos=true;
			
			if (!window.WebSocket) {
				console.log("Websocket unsupported");
			} else {
				
			$.get('rest/info/motes',function(content){
				
				var width = 1000;
				var height = 1700;

				var motes = content.sender;
				
				var stage = new Kinetic.Stage({
					container : 'container',
					width : width,
					height : height
				});
				
				var bg = new Kinetic.Rect({
			        x: 0,
			        y: 0,
			        width: stage.getWidth(),
			        height: stage.getHeight(),
			        id: 'bg'
			    });
				
				var layer = new Kinetic.Layer();
				layer.add(bg);
				
				layer.on('mousemove',function(e){
					mouseX = e.evt.pageX;
					mouseY = e.evt.pageY;
				});
				
				var circles = new Array();
				
				var gdata = new Array();
				
				for (var i = 0; i < motes.length; i++) {
					circles[motes[i].mac] = new Kinetic.Circle({
						x : motes[i].lon,
						y : motes[i].lat,
						radius : 20,
						fill : 'lightgray',
						draggable : true,
						mac : motes[i].mac,
					});
					
					
					
					circles[motes[i].mac].text = new Kinetic.Text({
						x : circles[motes[i].mac].x() - 30,
						y : circles[motes[i].mac].y() - 40,
						text : motes[i].mac,
						fill : 'red',
						fontSize : '17'
					});
					
					circles[motes[i].mac].ring = new Kinetic.Ring({
						x : circles[motes[i].mac].x(),
						y : circles[motes[i].mac].y(),
						innerRadius : circles[motes[i].mac].radius(),
						outerRadius : 20,
						opacity : 0.8,
						shadowColor : 'blue',
						shadowBlur : 30,
						shadowOpacity : 1,
						opacity : 0.05,
						fill : 'blue',
						stroke: 'black',
				        strokeWidth: 4,
				        visible : false,
					});
					
					circles[motes[i].mac].on('mouseover',function(){
						document.body.style.cursor = 'pointer';
					});
					
					circles[motes[i].mac].on('mouseout',function(){
						document.body.style.cursor = 'normal';
					});
					
					circles[motes[i].mac].on('click',function(){
						refresh(this.getAttrs()['mac']);
					});
					
					circles[motes[i].mac].on('dragend',function(e){
						
						this.text.x(this.x() -30);
						this.text.y(this.y() -40);
						this.ring.x(this.x());
						this.ring.y(this.y());
						layer.draw();
						
						if(lockPos)
							return;
						
						for(var i=0; i<motes.length; i++){
							motes[i].lat=Math.round(circles[motes[i].mac].y());
							motes[i].lon=Math.round(circles[motes[i].mac].x());
						}
						
						
						
						$.ajax({
							type: "POST",
							url: 'rest/info/motes',
							data: JSON.stringify(motes),
							contentType: "application/json",
							success: function(content){
								if(!content.success){
									error("Positions are unchanged : "+content.message,$('#content'));
								}
							}
						});
						
						
					});
					
					var group = new Kinetic.Group({
						draggable : true
					});
					
					group.add(circles[motes[i].mac]);
					group.add(circles[motes[i].mac].ring);
					group.add(circles[motes[i].mac].text);

					layer.add(circles[motes[i].mac]);
					layer.add(circles[motes[i].mac].text);
					layer.add(circles[motes[i].mac].ring);
					
					circles[motes[i].mac].setZIndex(100);
					circles[motes[i].mac].ring.setZIndex(1);
					
				}
				stage.add(layer);
				var myMac=null;
				var refresh = function(mac){
					myMac = mac;
					$('#g_mote_info > table').empty();
					$('#g_mote_info > table').append('<tr><th colspan="2">'+myMac+'</th></tr>');
					$('#g_mote_info > table').append('<tr><th>Anchors</th><th>RSSI</th></tr>');
					
					for(var mac in circles){
						circles[mac].ring.hide();
						if(circles[mac].fill()=='red')
							circles[mac].fill('lightgray');
					}
						
					for(var i in gdata[myMac]){
						if(gdata[myMac][i].algo=="trilateration"){
							for(var mac in gdata[myMac][i].circles){
								$('#g_mote_info > table').append('<tr><td>'+mac+'</td><td>'+gdata[myMac][i].circles[mac].rssi+'</td></tr>');
								if(circles[mac].fill()=='lightgray'){
									circles[mac].fill('red');
									circles[mac].ring.outerRadius(gdata[myMac][i].circles[mac].radius);
									circles[mac].ring.show();
								}else{
									circles[mac].fill('lightgray');
									circles[mac].ring.hide();
								}
							}	
						}	
					}
					myMacf = myMac;
					layer.draw();
				};
				
				/* ********************* CHART ******************************/
				
				$('#results').highcharts({
		            chart: {
		                type: 'column'
		            },
		            title: {
		                text: 'Algorithms comparison'
		            },
		            subtitle: {
		                text: ''
		            },
		            xAxis: {
		                categories: [
		                    'Barycenter',
		                    'Multilateration',
		                    'Trilateration',
		                    'Combined',
		                    'Customized'
		                ]
		            },
		            yAxis: {
		                min: 0,
		                title: {
		                    text: 'Error (cm)'
		                }
		            },
		            tooltip: {
		                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		                    '<td style="padding:0"><b>{point.y:.1f} px</b></td></tr>',
		                footerFormat: '</table>',
		                shared: true,
		                useHTML: true
		            },
		            plotOptions: {
		                column: {
		                    pointPadding: 0.2,
		                    borderWidth: 0
		                }
		            },
		            series: [{
		                name: 'Error',
		                data: []
		    
		            }],
		            exporting : {
						enabled : true,
						url:'http://localhost:8080/export'
					},
		        });
		        var chart = $('#results').highcharts();
				
				/************************* socket **************************/
				var socket = new WebSocket(host);
				
				socket.onopen = function() {
					var secureLogo = protocol=='wss://' ? '<span class="fi-lock"></span>' : '';
					$('#status').html(
							secureLogo+"[OK] Connected");
					$('#status').attr('title','Connected to '+host);
					$('#status').foundation();
					$('#status').css('color', 'green');
				};
				socket.onclose = function() {
					$('#status').text("disconnected from server");
					$('#status').css('color', 'red');
				};

				socket.onmessage = function(msg) {
					try {
						var content = JSON.parse(msg.data);
						
						if(typeof content.unauthorized != "undefined"){
							$('#status').text(content.unauthorized);
							$('#status').css('color', 'red');
							return;
						}
						
						var selectedAlgo = $('input[name="algos"]:checked').attr('id');
						var calibration = content.calibration;
						$('#a').text(calibration.a.toFixed(3));
						
						if(pause)
							return;
						
						var l = content.localization;
						console.log(content);
						
						var o = content.tester;
						
						
						if(typeof o == 'undefined')
							o=null;
						
						if(o!= null)
							if(o.collectProgress<1){
								$('#collectProgress').text(Math.round(o.collectProgress*100)+"%");
								o=null;
							}else
								$('#collectProgress').text("100%");
								
						
						for(var mac in l){
							
							if(!gdata[mac])
								gdata[mac] = new Array();

							gdata[mac] = l[mac];
							
							var xg=0,yg=0;
							if(o!=null){
								if(o.optimizerStarted){
									if(selectedAlgo=='custom'){
										xg = parseFloat(o.combined[mac].x);
										yg = parseFloat(o.combined[mac].y);
									}
									
									if(o.x && o.y && o.x.barycenter){
										$('#xbarycenter').val(o.x.barycenter.toFixed(4));
										$('#ybarycenter').val(o.y.barycenter.toFixed(4));
										
										
									}else{
										$('#xbarycenter').val(0);
										$('#ybarycenter').val(0);
									}
									if(o.x && o.y && o.x.multilateration){
										$('#xmultilateration').val(o.x.multilateration.toFixed(4));
										$('#ymultilateration').val(o.y.multilateration.toFixed(4));
										
										
									}
									else{
										$('#xmultilateration').val(0);
										$('#ymultilateration').val(0);
									}
									if(o.x && o.y && o.x.trilateration){
										$('#xtrilateration').val(o.x.trilateration.toFixed(4));
										$('#ytrilateration').val(o.y.trilateration.toFixed(4));
										
										
									}
									else{
										$('#xtrilateration').val(0);
										$('#ytrilateration').val(0);
									}
								}
								
								if(o.errors){
									var scale = parseFloat($('#scale').val());
									var cdata=[0,0,0,0,0];
									if(o.errors.barycenter){
										cdata[0]=Math.round(o.errors.barycenter*scale);
									}
									if(o.errors.multilateration){
										cdata[1]=Math.round(o.errors.multilateration*scale);
									}
									if(o.errors.trilateration){
										cdata[2]=Math.round(o.errors.trilateration*scale);
									}
									if(o.errors.combined){
										cdata[3]=Math.round(o.errors.combined*scale);
									}
									if(o.errors.combined_optimized){
										cdata[4]=Math.round(o.errors.combined_optimized*scale);
									}else if(o.errors.combined_customized){
										cdata[4]=Math.round(o.errors.combined_customized*scale);
									}
									chart.series[0].setData(cdata,true);
								}
								
							}
							
							if(!(o!=null && o.optimizerStarted && selectedAlgo=='custom'))
								for(var i in l[mac]){
									if(l[mac][i].algo==selectedAlgo){
										xg = parseFloat(l[mac][i].x);
										yg = parseFloat(l[mac][i].y);
										break;
									}	
								}
							
							circles[mac].x(xg);
							circles[mac].y(yg);
							circles[mac].text.x(xg-30);
							circles[mac].text.y(yg-40);
							circles[mac].ring.x(xg);
							circles[mac].ring.y(yg);
							circles[mac].fill("dodgerblue");
						}
						if(myMac!=null)
							refresh(myMac);
						else
							layer.draw();
						
					} catch (exception) {
						console.log(exception);
					}
				};
				
				$('#pause').on('click',function(){
					pause = !($(this).is(':checked'));
					if(pause){
						for(var i in motes){
							circles[motes[i].mac].x(motes[i].lon);
							circles[motes[i].mac].y(motes[i].lat);
							circles[motes[i].mac].text.x(motes[i].lon-30);
							circles[motes[i].mac].text.y(motes[i].lat-40);
							circles[motes[i].mac].ring.x(motes[i].lon);
							circles[motes[i].mac].ring.y(motes[i].lat);
						}
						layer.draw();
					}
				});
				
				$('#lockPos').on('click',function(){
					lockPos = $(this).is(':checked');
				});
				
				
				
				$('#useWeights').on('click',function(e){
					e.preventDefault();
					var weights = [
					    {name:"barycenter",xweight:$('#xbarycenter').val(),yweight:$('#ybarycenter').val()},
					    {name:"multilateration",xweight:$('#xmultilateration').val(),yweight:$('#ymultilateration').val()},
					    {name:"trilateration",xweight:$('#xtrilateration').val(),yweight:$('#ytrilateration').val()}
					];
					
					$.ajax({
						type: "POST",
						url: 'rest/geo/tester/weights',
						data: JSON.stringify(weights),
						contentType: "application/json",
						success: function(content){
							if(content.success){
								success(content.message,$('#content'));
							}else{
								error(content.message,$('#content'));
							}
						}
					});
				});
				
				$.get('rest/geo/tester/status',function(content){
					var checked="";
					if(content.started)
						checked="checked";
					$('#optimizerSwitch').append('<div class="switch round small">'+
			 		 '<input id="toggleOptimizer" type="checkbox" '+checked+'>'+
			 		 '<label for="toggleOptimizer"></label>'+
					'</div>');
					
					$('#optimizerSwitch').foundation();
					
					$('#toggleOptimizer').on('click',function(){
						if($(this).is(':checked'))
							$.ajax({
								type: "POST",
								url: 'rest/geo/tester/startOptimizer',
								data: "",
								contentType: "application/json",
								success: function(content){
									if(content.success){
										success(content.message,$('#content'));
									}else{
										error(content.message,$('#content'));
									}
								}
							});
						else
							$.ajax({
								type: "POST",
								url: 'rest/geo/tester/stopOptimizer',
								data: "",
								contentType: "application/json",
								success: function(content){
									if(content.success){
										success(content.message,$('#content'));
									}else{
										error(content.message,$('#content'));
									}
								}
							});
					});
				});
				
				

				$(window).on('beforeunload', function(){
				    socket.disconnect();
				});
				
			});
			}
			
			var success = function(message, $parent) {
				$('<div style="position:fixed;width:70%;left:15%;top:10%;" data-alert class="alert-box success radius">'
								+ message
								+ '<a href="#" class="close fi-x"></a></div>')
						.appendTo($parent);
				scheduleAutoClose($parent);
			};


			var error = function(message, $parent) {
				$('<div style="position:fixed;width:70%;left:15%;top:10%;" data-alert class="alert-box warning radius">'
								+ message
								+ '<a href="#" class="close fi-x"></a></div>').appendTo($parent);
				scheduleAutoClose($parent);
			};

			var scheduleAutoClose = function($parent) {
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
		});
	
		
	</script>
	<script src="js/map/kinetic-v5.1.0.min.js"></script>
	<script type="text/javascript" src="js/Highcharts-3.0.10/highcharts.js"></script>
	<script type="text/javascript" src="js/Highcharts-3.0.10/exporting.js"></script>
	<script type="text/javascript" src="js/Highcharts-3.0.10/export-csv.js"></script>
