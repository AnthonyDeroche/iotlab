<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="status" class="row" style="margin: 10px;"></div>

<dl class="tabs" data-tab id="chartTabs"></dl>
<div class="tabs-content" id="charts" style="width: 100%;"></div>
<script type="text/javascript">
	$(function() {
		var charts = {};
		var chartsName = [ "Temperature", "Humidity", "Battery Voltage",
				"Light 1", "Light 2" ];
		var dataName = [ "temperature", "humidity", "battery_voltage",
				"light1", "light2" ];
		var units = [ "°C", "%", "V", "lx", "lx" ];
		var chartsNumber = chartsName.length;

		var protocol = "ws://";
		if(document.location.protocol=='https:')
			protocol = "wss://";
		var split = document.location.pathname.split('/');
		split.shift();
		split.pop();
		var host = protocol+window.location.host+"/"+split.join('/')+"/liveStream/0";
		
		/*function traitementPoint(nameData) {
			var yaxis = new Array();
			for (var i = 0; i < tmp.length; i++) {
				if (typeof tmp[i][nameData] == 'undefined')
					continue;
				var j = 0;
				var exist = false;
				for (j; j < yaxis.length; j++) {
					if (tmp[i].mote == yaxis[j].name) {
						yaxis[j].data.push(new Array(tmp[i].timestamp,
								parseFloat(tmp[i][nameData])));
						exist = true;
					}
				}
				if (!exist) {
					yaxis.push(new Object({
						name : tmp[i].mote,
						data : [ new Array(tmp[i].timestamp,
								parseFloat(tmp[i][nameData])) ],
					}));
				}
			}
			return yaxis;
		}*/

		//using the computer hour for charts
		Highcharts.setOptions({
			global : {
				useUTC : false
			}
		});

		for (var i = 0; i < chartsNumber; i++) {
			var active = i == 0 ? ' class="active"' : "";
			$(
					'<dd'+active+'><a href="#'+dataName[i]+'_chart">'
							+ chartsName[i] + '</a></dd>').appendTo(
					'#chartTabs');

			active = i == 0 ? ' active' : '';
			//var yaxis = traitementPoint(dataName[i]);
			$(
					'<div id="'
							+ dataName[i]
							+ '_chart" class="content'
							+ active
							+ '" style="margin:auto;width:90%;height:500px;"></div>')
					.appendTo('#charts').highcharts(
							{
								chart : {
									zoomType : 'x',
									type : 'spline',
									animation : Highcharts.svg,
									marginRight : 10
								},
								title : {
									text : chartsName[i]
								},
								xAxis : {
									type : 'datetime',
									tickPixelInterval : 150,
								//data : xaxis,
								},
								yAxis : {
									title : {
										text : "Value (" + units[i] + ")"
									},
									plotLines : [ {
										value : 0,
										width : 1,
										color : '#808080'
									} ],
								},
								tooltip : {
									formatter : function() {
										return '<b>'
												+ this.series.name
												+ '</b><br/>'
												+ Highcharts.dateFormat(
														'%Y-%m-%d %H:%M:%S',
														this.x)
												+ '<br/>'
												+ Highcharts.numberFormat(
														this.y, 2);
									},
									valueSuffix : units[i]
								},
								plotOptions : {
									series : {
										marker : {
											enabled : false
										}
									}
								},
								legend : {
									enabled : true
								},
								exporting : {
									enabled : true,
									url:'http://localhost:8080/export'
								},
								series : []
							});
			charts[dataName[i]] = $('#' + dataName[i] + '_chart').highcharts();
			
		}
		if (!window.WebSocket) {
			console.log("Websocket unsupported");
		} else {
			var live;
			<c:if test="${param.live==true || live==true}">live=true</c:if>
			if(typeof live != 'undefined' && live){
		
			socket = new WebSocket(host);
			var motes = new Array();

			socket.onopen = function() {
				var secureLogo = protocol=='wss://' ? '<span class="fi-lock"></span>' : '';
				$('#status').html(
						secureLogo+" Connected to " + host);
				$('#status').css('color', 'green');
			};
			socket.onclose = function() {
				$('#status').text("disconnected from server");
				$('#status').css('color', 'red');
			};

			socket.onmessage = function(msg) {
				try {
					var data = JSON.parse(msg.data);

					console.log("Received data concerning mote " + data.mote);
					console.log(data);
					for (var i = 0; i < chartsNumber; i++) {
						if (typeof charts[dataName[i]] == 'undefined')
							continue;
					var exist = false;
					var j = 0;
					var index;
					for (j; j < charts[dataName[i]].series.length; j++) {
						if (data.mote == charts[dataName[i]].series[j].name) {
							index = j;
							charts[dataName[i]].series[index].addPoint([
									data.timestamp,
									parseFloat(data[dataName[i]]) ]);
							exist = true;
							//break;
						}
						;
					}
					if (!exist) {
						charts[dataName[i]].addSeries(new Object({
							name : data.mote,
							data : [ new Array(data.timestamp,
									parseFloat(data[dataName[i]])) ],
						}));
					}
						/* yaxis.push(new Object({
							name : data.mote,
							data : [ new Array(data.timestamp, parseFloat(data.temperature)) ],
						})); */
					}

					/*var index = $.inArray(data.mote, motes);
					if (index == -1) {
						motes[motes.length] = data.mote;
						console.log("New mote : " + data.mote);

						for (var i = 0; i < chartsNumber; i++) {
							var chartName = dataName[i];
							charts[chartName].addSeries({
								name : data.mote
							});
						}
						index = motes.length - 1;
					}
					var x = data.timestamp;
					for (var i = 0; i < chartsNumber; i++) {
						var chartName = dataName[i];
						if (typeof data[chartName] != "undefined") {
							var y = parseFloat(data[chartName]);

							var shift = charts[chartName].series[index].data.length > <c:out value="${nb}"/>;
							for(var i = 0; i<charts[chartName].series.lenght; i++)
							charts[chartName].series[index].addPoint([ x, y ],
									true, shift);
						} else {
							console.log("Unknown data : " + chartName);
						}

					}*/
				} catch (exception) {
					console.log(exception);
				}
			};
		}
	}

	});
</script>

<script type="text/javascript" src="js/Highcharts-3.0.10/highcharts.js"></script>
<script type="text/javascript" src="js/Highcharts-3.0.10/exporting.js"></script>
<script type="text/javascript" src="js/Highcharts-3.0.10/export-csv.js"></script>
