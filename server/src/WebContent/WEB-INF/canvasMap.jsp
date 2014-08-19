<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 1000) %></c:set>

    <style>


.canvas {
	display: inline-block;
	overflow: hidden;
	background-color: white;
	background-image: url("<c:url value='img/map?${rand}'/>");
	background-repeat: no-repeat;
}

</style>

<c:if test="${param.config == 'date'}">
<label for="slider">Date:</label> <input type="text" id="amount"/>
	<div id="slider"></div>
</c:if>
	<div id="${param.nameDiv}" class="canvas"></div>
	<script defer="defer">
	image = new Image();
	image.src = "<c:url value='img/map'/>";
	image.onload = function(){
		var date = parseFloat("${minTime}");
		//date = dateToTimestamp(date);
		radius = 40;
		var lastData = ${lastData};
		$("#slider").slider({
			min: parseFloat("${minTime}"),
			max: parseFloat("${maxTime}"),
		    slide: function( event, ui ) {
		    	var tmp = new Date(ui.value);
		    	/*tmp.getDate+1 est causé par le décalage du numero du mois allant de 0 à 11 dans l'objet Date*/
		          $( "#amount" ).val( tmp.getFullYear()+"/"+(tmp.getMonth()+1)+"/"+tmp.getDate()+" " + tmp.getHours() + ":"+ tmp.getMinutes() + ":" + tmp.getSeconds() );
		          
		    	changeDate(ui.value, lastData)
		        },
		    change: function(event, ui){
		    	changeDate(ui.value, lastData);
		    },
		       create: function(event, ui){
		    	   var tmp = new Date(date);
			          $( "#amount" ).val( tmp.getFullYear()+"/"+(tmp.getMonth()+1)+"/"+tmp.getDate()+" " + tmp.getHours() + ":"+ tmp.getMinutes() + ":" + tmp.getSeconds() );
		       },
			value : date,
			step : 1000
		});
		
		$("#amount").change(function(){
			changeDate(new Date($(this).val()).getTime(), lastData);
			$("#slider").slider("value", new Date($(this).val()).getTime());
		});
		
		tabForme = new Array();
		motes = ${motes};
		console.log(motes);
		var stage = new Kinetic.Stage({
			container : '${param.nameDiv}'
		});
		/*Adaptation de la taille du canvas à l'image de fond*/
			stage.width(image.width+radius*2+20);
			stage.height(image.height);
		layer = new Kinetic.Layer();
		
		var lastData = ${lastData};
		var dataOnDate = new Array();
		
		var dx = image.width+3;
		var dy = 10;
		/*Création de toutes les formes représentant les motes*/
		for (var i = 0; i < motes.length; i++) {

			tabForme[i] = new Kinetic.Circle({
				x : motes[i].lon,
				y : motes[i].lat,
				radius : 7,
				id: motes[i].ip,
				fill : 'black',
			});
			/*Si les coordonnées du motes n'ont jamais été définie, placement du mote à droite de l'image de façon ordonnée*/
			if(tabForme[i].x() == 0 && tabForme[i].y()==0){
				tabForme[i].x(dx);
				tabForme[i].y(dy);
				dy+=radius*2+10;
				if(dy> image.height-5){
					console.log(image.height)
					dy = 10;
					dx+=radius*2+10;
					stage.width(stage.getWidth()+radius*2+10);
				}
			}
			
			tabForme[i].ring = new Kinetic.Ring({
				x : tabForme[i].x(),
				y : tabForme[i].y(),
				innerRadius : tabForme[i].radius(),
				outerRadius : 0,
				opacity : 0.4,
				shadowBlur : 30,
				shadowOpacity : 1,
			});

			tabForme[i].text = new Kinetic.Text({
				x : tabForme[i].x() + 9,
				y : tabForme[i].y() + 6,
				text : motes[i].ip,
				fill : 'black',
				fontSize : '15'
			});
			
			tabForme[i].textTmp = new Kinetic.Text({
				x : tabForme[i].x() + 9,
				y : tabForme[i].y() - 12,
				fill : 'black',
				fontSize : '15'
			});
			
			var group = new Kinetic.Group({
				draggable : true
			});
			group.add(tabForme[i]);
			group.add(tabForme[i].ring);
			group.add(tabForme[i].textTmp);
			group.add(tabForme[i].text);
			layer.add(group);
			
			/*Recherche de la donnée la plus proche de la date voulue pour chaque mote*/
			dataOnDate[i] = auPlusProche(motes[i].ip, date, lastData);
			
		}
		stage.add(layer);
console.log(dataOnDate);
console.log(lastData);
/*Génération de l'affichage des cercles de couleurs pour chaque motes avec les données les plus proches de la date voulue*/
for(var i=0; i<dataOnDate.length;i++){
	if(typeof dataOnDate[i] != 'undefined')
	traitementNewData(dataOnDate[i]);
}
stage.draw();
var maxTmp, minTmp, ecart, cste;
var protocol = "ws://";
if(document.location.protocol=='https:')
	protocol = "wss://";
var split = document.location.pathname.split('/');
split.shift();
split.pop();
var host = protocol+window.location.host+"/"+split.join('/')+"/liveStream/0";
if (!window.WebSocket) {
	console.log("Websocket unsupported");
} else {
	var live;
	<c:if test="${param.live==true || live==true}">live=true</c:if>
	if(typeof live != 'undefined' && live){
	socket = new WebSocket(host);
	socket.onopen = function() {
		$('.status').text(
				"Connected to " + host
						+ " through Web Socket Protocol.");
		$('.status').css('color', 'green');
	};
	socket.onclose = function() {
		$('.status').text("disconnected from server");
		$('.status').css('color', 'red');
	};
	socket.onmessage = function(msg) {
		try {
			var data = JSON.parse(msg.data);
			console.log("Received data concerning mote " + data.mote);
			console.log(data);
			traitementNewData(data);
			stage.draw();
		}
		catch(exception){
			console.log(exception);
			}
		}
	}
}

/*Fonction traitant l'arrivée d'une nouvelle donnée*/
function traitementNewData(data){
	forme = trouveForme(data);
	forme.textTmp.text(data.temperature + "°C");
	if(forme.temperature == maxTmp && parseFloat(data.temperature)<maxTmp || forme.temperature == minTmp && parseFloat(data.temperature)>minTmp){
		forme.temperature = parseFloat(data.temperature);
		MajMaxMin();
	}
	else
		forme.temperature = parseFloat(data.temperature);
	traitementBorneTemp(forme);
	//layer.draw();
};

/*Fonction retrouvant la température max et min si celle ci à changé
 * Par exemple passage du mote avec la température max à un température inférieure
 */
function MajMaxMin(){
	var max, min;
	for(var i=0; i<tabForme.length; i++){
		if(typeof tabForme[i].temperature!='undefined' && (typeof max == 'undefined' ||  tabForme[i].temperature > max))
		{
			max = tabForme[i].temperature;
		}
		if(typeof tabForme[i].temperature!='undefined' && (typeof min == 'undefined' || tabForme[i].temperature < min)){
			min = tabForme[i].temperature;
		}
	}
	if(typeof max != 'undefined')
		maxTmp = max;
	if(typeof min != 'undefined')
		minTmp = min;
	chgtBorne();
		}
/*Fonction cherchant la forme concernant la donnée dans le tableau de forme*/
function trouveForme(data){
	var forme;
	for(var i=0; i<tabForme.length; i++){
		if(tabForme[i].id() == data.mote){
			forme = tabForme[i];
			break;
		}
	}
	return forme;
}
/*Vérification de la nouvelle température du mote par rapport aux borne max et min*/
function traitementBorneTemp(forme){
	var change = false;
	if(typeof maxTmp == 'undefined' || forme.temperature > maxTmp){
		maxTmp = forme.temperature;
		change = true;
	}
	if(typeof minTmp == 'undefined' || forme.temperature < minTmp){
		minTmp = forme.temperature;
		change = true;
	}
	if(change)
		chgtBorne();
	else
		updateColor(forme);
}
/*Fonction traitement le changement de la borne max ou inf*/
function chgtBorne(){
	ecart=maxTmp-minTmp;
	if(ecart!=0){
	cste=255/(ecart*100);
	}
	else
		cste = 1;
	for(var i=0; i<tabForme.length;i++){
		updateColor(tabForme[i]);
	}
}
/*Fonction mettant à jour la couleur d'un mote en fonction de sa température*/
function updateColor(forme){
	forme.ring.outerRadius(radius);
	if(typeof forme.temperature != 'undefined'){
		var bleu = Math.round(255-(forme.temperature-minTmp)*100*cste);
		if(bleu<16){
			bleu = bleu.toString(16);
			bleu = "0" + bleu;
		}
		else
			bleu = bleu.toString(16);
		var rouge = Math.round(255-(maxTmp-forme.temperature)*100*cste);
		if(rouge<16){
			rouge = rouge.toString(16);
			rouge="0"+rouge;
		}
		else
			rouge = rouge.toString(16);
		color = "#" + rouge + "00" + bleu;
		forme.ring.fill(color);
		forme.ring.shadowColor(color);
		}
	}
	
	/*Fonction retournant l'objet le plus proche (par rapport au temps) de la liste*/
	function auPlusProche(id, date, liste){
		var distTemps, obj;
		for(var j = 0; j<liste.length; j++){
			if(liste[j].mote == id){
				ecartTmp = Math.abs(date-liste[j].timestamp)
				if(typeof distTemps == 'undefined' || ecartTmp<distTemps){
					distTemps=ecartTmp;
					obj = liste[j];
				}
 				if(ecartTmp>distTemps){
					//break;
				} 
			}
		}
		return obj
	}
	/*Change l'heure donné en paramètre sous la forme HH:mm:ss en date timestamps*/
	function dateToTimestamp(date){
		var ind = date.lastIndexOf("/");
		var newDate = date.replace(date.substring(ind+1, ind+5), date.substring(ind+1, ind+5)+" ");
		var times = new Date(date);
		console.log(newDate + " ==> " + times.getTime());
		return times.getTime();
		}
	
	/*Fonction traitant un changement de date*/
	function changeDate(date, liste){
		var tabTmp = new Array();
		for(var k=0; k<motes.length; k++){
			tabTmp.push(auPlusProche(motes[k].ip, date, liste));
		}
		for(var i=0; i<tabTmp.length;i++){
			if(typeof tabTmp[i] != 'undefined')
			traitementNewData(tabTmp[i]);
		}
		stage.draw();
	}
	
	}
</script>
<div class="status" class="row" style="margin: 10px;"></div>
