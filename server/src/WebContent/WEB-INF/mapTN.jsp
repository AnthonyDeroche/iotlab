<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div id="container" style="background-image:url('img/loria.png');background-repeat:no-repeat;margin:10px;"></div>
	<br /><br /><br /><br /><br />
	<ul class="pricing-table" id="tooltip" style="position:absolute;display:none;box-shadow:0px 0px 10px 1px #333;"> 
		<li class="title"></li> 
		<li class="bullet-item timestamp"></li> 
		<li class="bullet-item temperature"></li> 
		<li class="bullet-item humidity"></li> 
		<li class="bullet-item light1"></li> 
	</ul>
	<script>
		$(function(){
			$.get('rest/info/motes',function(content){
				var scale = [16,19,22,25];
				var colors = ['#000065','#3939FF','#6B00FF','#AA0036','#FF0022'];
				var senders = content.sender;
				var sinks = content.sink;
				var stage = new Kinetic.Stage({
					container : 'container',
					width : 3000,
					height : 3000
				});
				
				var mouseX=0,mouseY=0;
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
				var lines = new Array();
				var data = new Array();
				
				for (var i = 0; i < sinks.length; i++) {
					circles[sinks[i].mac] = new Kinetic.Star({
						  x: sinks[i].lon,
						  y: sinks[i].lat,
						  numPoints: 5,
						  innerRadius: 7,
						  outerRadius: 14,
						  fill: "orange",
						  stroke: "orangered",
						  strokeWidth: 2,
						  draggable : true,
						  mac : sinks[i].mac
						});
					
					circles[sinks[i].mac].text = new Kinetic.Text({
						x : circles[sinks[i].mac].x() - 25,
						y : circles[sinks[i].mac].y() - 30,
						text : sinks[i].mac,
						fill : 'purple',
						fontSize : '17'
					});
					
					circles[sinks[i].mac].on('dragend',function(){
						for(var i=0; i<sinks.length; i++){
							sinks[i].lat=circles[sinks[i].mac].y();
							sinks[i].lon=circles[sinks[i].mac].x();
						}
						$.ajax({
							type: "POST",
							url: 'rest/info/motes',
							data: JSON.stringify(sinks),
							dataType: "application/json",
							contentType: "application/json",
							success: function(content){
								console.log(content);
							}
							});
					});
					
					circles[sinks[i].mac].on('dragmove', function() {
						this.text.x(this.x() -25);
						this.text.y(this.y() -30);
					});
					
					var group = new Kinetic.Group({
						draggable : true
					});
					group.add(circles[sinks[i].mac]);
					group.add(circles[sinks[i].mac].text);
					layer.add(circles[sinks[i].mac]);
					layer.add(circles[sinks[i].mac].text);
				}
				
				for (var i = 0; i < senders.length; i++) {
					
					
					circles[senders[i].mac] = new Kinetic.Circle({
						x : senders[i].lon,
						y : senders[i].lat,
						radius : 10,
						fill : 'gray',
						draggable : true,
						mac : senders[i].mac
					});
					
					circles[senders[i].mac].text = new Kinetic.Text({
						x : circles[senders[i].mac].x() - 25,
						y : circles[senders[i].mac].y() - 35,
						text : senders[i].mac,
						fill : 'purple',
						fontSize : '17'
					});
					
					circles[senders[i].mac].ring = new Kinetic.Ring({
						x : circles[senders[i].mac].x(),
						y : circles[senders[i].mac].y(),
						innerRadius : circles[senders[i].mac].radius(),
						outerRadius : 14,
						opacity : 0.3,
						shadowColor : 'gray',
						shadowBlur : 30,
						shadowOpacity : 1,
						fill : 'gray',
						
					});
					
					circles[senders[i].mac].on('mouseover',function(){
						$('#tooltip').css('left',(mouseX+10)+'px').css('top',(mouseY+10)+'px');
						var mote = this.text.getText();
						var d = data[mote];
						if (typeof d != 'undefined'){
							
						$('#tooltip>.title').text("Mote "+mote);
						$('#tooltip>.timestamp').text(new Date(d['temperature'].timestamp).toLocaleString());
						$('#tooltip>.temperature').text("Temperature : "+d['temperature'].value+" °C");
						$('#tooltip>.humidity').text("Humidity : "+d['humidity'].value+" %");
						$('#tooltip>.light1').text("Light : "+d['light1'].value+" lm");
						$('#tooltip').fadeIn('fast');
						document.body.style.cursor = 'pointer';
						
						<c:if test = "${option == 'network'}">
						if(typeof lines[this.attrs.mac] != "undefined"){
							lines[this.attrs.mac].stroke('red');
							layer.draw();
						}
						</c:if>
						}
						
					});
					
					circles[senders[i].mac].on('mousemove',function(){
						$('#tooltip').css('left',(mouseX+10)+'px').css('top',(mouseY+10)+'px');
					});
					
					circles[senders[i].mac].on('mouseout',function(){
						$('#tooltip').fadeOut('fast');
						document.body.style.cursor = 'default';
						
						<c:if test = "${option == 'network'}">
						if(typeof lines[this.attrs.mac] != "undefined"){
							lines[this.attrs.mac].stroke('green');
							layer.draw();
						}
						</c:if>
					});
					
					circles[senders[i].mac].on('dragstart',function(){
						$('#tooltip').fadeOut('fast');
					});
					
					circles[senders[i].mac].on('dragend',function(){
						for(var i=0; i<senders.length; i++){
							senders[i].lat=circles[senders[i].mac].y();
							senders[i].lon=circles[senders[i].mac].x();
						}
						$.ajax({
							type: "POST",
							url: 'rest/info/motes',
							data: JSON.stringify(senders),
							dataType: "application/json",
							contentType: "application/json",
							success: function(content){
								console.log(content);
							}
							});
					});

					var group = new Kinetic.Group({
						draggable : true
					});
					group.add(circles[senders[i].mac]);
					group.add(circles[senders[i].mac].ring);
					group.add(circles[senders[i].mac].text);

					circles[senders[i].mac].on('dragmove', function() {
						this.text.x(this.x() -25);
						this.text.y(this.y() -35);
						this.ring.x(this.x());
						this.ring.y(this.y());
					});
					
					layer.add(circles[senders[i].mac]);
					layer.add(circles[senders[i].mac].text);
					layer.add(circles[senders[i].mac].ring);
					
					
				}
				stage.add(layer);
				
				var refreshUrl;
				refreshUrl = 'rest/data/1/temperature-humidity-light1/last';
				<c:if test = "${option == 'network'}">
					refreshUrl = 'rest/data/1/temperature-humidity-light1-best_neighbor/last';
			   </c:if>
				
				var refresh = function(){
					$.get(refreshUrl,function(content){
						var d = content.data;
						for(var i=0;i<d.length;i++){
								if(typeof data[d[i].mote] == "undefined")
									data[d[i].mote] = new Array();
								data[d[i].mote][d[i].label]={
										value:d[i].value,
										timestamp:d[i].timestamp
								};
								if(d[i].label=='temperature'){
									var color=colors[colors.length-1];
									var k=0;
									while(scale[k]<d[i].value && k<scale.length){
										k++;
									}
									color = colors[k];
									var circle = circles[d[i].mote];
									circle.fill(color);
									circle.ring.fill(color);
								}
								<c:if test = "${option == 'network'}">
									if(d[i].label=='best_neighbor'){
										var best_neighbor = (d[i].value & 0xff) + "." + ((d[i].value >> 8) & 0xff);
										var circle = circles[d[i].mote];
										var posX;
										var posY;
										if(typeof circles[best_neighbor] != "undefined"){
											var circle_neighbor =  circles[best_neighbor];
											posX = circle_neighbor.x();
											posY = circle_neighbor.y();
											
										}
										var removed = false;
										if(typeof(lines[d[i].mote])!="undefined" && (lines[d[i].mote].points()[2]!=posX || lines[d[i].mote].points()[3]!=posY)){
											console.log(d[i].mote+" changed best neighgour");
											lines[d[i].mote].remove();
											removed=true;
										}
										
										if(typeof(lines[d[i].mote])=="undefined" || removed)
										{
											  lines[d[i].mote] = new Kinetic.Line({
										        points: [circle.x(), circle.y(), posX, posY],
										        stroke: 'green',
										        strokeWidth: 1,
										        lineCap: 'round',
										        lineJoin: 'round',
										        id:"line-"+d[i].mote
										      });	
											layer.add(lines[d[i].mote]);
										}
									}
								</c:if>
									
						}
						layer.draw();
					});
					window.setTimeout(function(){refresh();},10000);
				};
				refresh();
				
				
			});
			
		});
		
	
		
	</script>
	<script src="js/map/kinetic-v5.1.0.min.js"></script>
