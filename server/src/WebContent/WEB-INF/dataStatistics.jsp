

<div class="row">
		<table class="tableCenter" style="width:100%; margin-top:30px;">
			<thead>
				<tr>
					<th>Mote</th>
					<th>Temperature (°C)</th>
					<th>Humidity (%)</th>
					<th>Luminosity (lm)</th>
					<th>Packet loss ratio (%)</th>
				</tr>
			</thead>
			<tbody id="tableBody">
		
			</tbody>
		</table>
</div>

<hr />

<div class="row" id="chart">

</div>

<script type="text/javascript">
	$(function() {
		
		$('#chart').highcharts({
            chart: {
                type: 'column',
            },
            title: {
                text: 'Packet loss ratio'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: [
                  'Motes'
                ]
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Loss ratio (%)'
                }
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            exporting : {
				enabled : true,
				url:'http://localhost:8080/export'
			},
            series: []
        });
    	var chart = $('#chart').highcharts();
    	var exist = new Array();
		var k=0;
		var refresh = function() {
			
		
			var data = new Array();
			var lossRatios = {};
			var r = 0;
			
			$('#loading').css('display','inline');			  
			
			$.get("rest/info/stats/1", function(data){
				lossRatios = data.stats;
				var dataSeries = [];
				var i=0;
				for(var mac in lossRatios){
					dataSeries[i]={
						name:mac,
						data:[parseFloat(lossRatios[mac])*100]
					};
					
					if(typeof exist[mac] == 'undefined'){
						exist[mac]=k++;
						chart.addSeries(dataSeries[i]);
					}
						
					chart.series[exist[mac]].setData(dataSeries[i].data,true);
					i++;
				}
				
				r++;
				fillTable();
				
			});
			
			$.get('rest/data/1/temperature-humidity-light1/1', function(content) {
				var d = content.data;
				for ( var i = 0; i < d.length; i++) {
					if (typeof data[d[i].mote] == "undefined")
						data[d[i].mote] = new Array();
					data[d[i].mote]["mean"+d[i].label] = d[i].value;
				}
				r++;
				fillTable();
			});
	
			var fillTable = function(){
				if(r!=2){
					return;
				}
					$('#loading').css('display','none');
					$('#tableBody').empty();
					for ( var mote in data)
						$('<tr><td style="font-weight:bold;">' + mote + '</td>'+
						'<td>'+data[mote]["meantemperature"]+'</td>'+
						'<td>'+data[mote]["meanhumidity"]+'</td>'+
						'<td>'+data[mote]["meanlight1"]+'</td>'+
						'<td>'+parseFloat(lossRatios[mote])*100+'</td></tr>')
								.appendTo('#tableBody');
				
				
			};    
		
		window.setTimeout(function(){refresh();},3000);
		};
		refresh();
	});
</script>

<script type="text/javascript" src="js/Highcharts-3.0.10/highcharts.js"></script>
<script type="text/javascript" src="js/Highcharts-3.0.10/exporting.js"></script>
<script type="text/javascript" src="js/Highcharts-3.0.10/export-csv.js"></script>