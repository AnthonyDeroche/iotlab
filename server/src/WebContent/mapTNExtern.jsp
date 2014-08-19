
	<div id="container" style="background-image:url('img/tn-et2.png');background-repeat:no-repeat;margin:10px;"></div>
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
				var motes = content.motes;
				var stage = new Kinetic.Stage({
					container : 'container',
					width : 900,
					height : 1700
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
				var data = new Array();
				for (var i = 0; i < motes.length; i++) {
					circles[i] = new Kinetic.Circle({
						x : motes[i].lon,
						y : motes[i].lat,
						radius : 15,
						fill : 'gray',
						draggable : true,
						id : motes[i].mac
					});
					
					circles[i].text = new Kinetic.Text({
						x : circles[i].x() - 30,
						y : circles[i].y() - 40,
						text : motes[i].mac,
						fill : 'purple',
						fontSize : '17'
					});
					
					circles[i].ring = new Kinetic.Ring({
						x : circles[i].x(),
						y : circles[i].y(),
						innerRadius : circles[i].radius(),
						outerRadius : 20,
						opacity : 0.3,
						shadowColor : 'gray',
						shadowBlur : 30,
						shadowOpacity : 1,
						fill : 'gray',
					});
					
					circles[i].on('mouseover',function(){
						$('#tooltip').css('left',(mouseX+10)+'px').css('top',(mouseY+10)+'px');
						var mote = this.text.getText();
						var d = data[mote];
						$('#tooltip>.title').text("Mote "+mote);
						$('#tooltip>.timestamp').text(new Date(d['temperature'].timestamp).toLocaleString());
						$('#tooltip>.temperature').text("Temperature : "+d['temperature'].value+" °C");
						$('#tooltip>.humidity').text("Humidity : "+d['humidity'].value+" %");
						$('#tooltip>.light1').text("Light : "+d['light1'].value+" lm");
						$('#tooltip').fadeIn('fast');
						document.body.style.cursor = 'pointer';
					});
					
					circles[i].on('mousemove',function(){
						$('#tooltip').css('left',(mouseX+10)+'px').css('top',(mouseY+10)+'px');
					});
					
					circles[i].on('mouseout',function(){
						$('#tooltip').fadeOut('fast');
						document.body.style.cursor = 'default';
					});
					
					circles[i].on('dragstart',function(){
						$('#tooltip').fadeOut('fast');
					});
					
					circles[i].on('dragend',function(){
						for(var i=0; i<motes.length; i++){
							motes[i].lat=circles[i].y();
							motes[i].lon=circles[i].x();
						}
						$.ajax({
							type: "POST",
							url: 'rest/info/motes',
							data: JSON.stringify(motes),
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
					group.add(circles[i]);
					group.add(circles[i].ring);
					group.add(circles[i].text);

					circles[i].on('dragmove', function() {
						this.text.x(this.x() -30);
						this.text.y(this.y() -40);
						this.ring.x(this.x());
						this.ring.y(this.y());
					});
					
					layer.add(circles[i]);
					layer.add(circles[i].text);
					layer.add(circles[i].ring);
					
				}
				stage.add(layer);
				
				var refresh = function(){
					$.get('rest/data/1/temperature-humidity-light1/last',function(content){
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
									var circle = stage.find('#'+d[i].mote)[0];
									circle.fill(color);
									circle.ring.fill(color);
									layer.draw();
								}
									
						}
						
					});
					window.setTimeout(function(){refresh();},10000);
				};
				refresh();
				
				
			});
			
		});
		
	</script>
	<script src="js/map/kinetic-v5.1.0.min.js"></script>
