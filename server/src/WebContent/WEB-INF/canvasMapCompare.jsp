<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 1000) %></c:set>
    

	<div id="${param.nameDiv}" class="canvas"></div>
	<script defer="defer">
	/*Page dérivée de canvasMap, la présence de plusieurs canvas sur la même page 
	 *obligeant un traitement légèrement différent*/
	window['maxTmp']=null;
	window['minTmp']=null;
	window['ecart']=null;
	window['cste']=null;
	window['tabForme'] = new Array();
	//var maxTmp, minTmp, ecart, cste;
	$(function(){
		var lastData = ${lastData};
		var date = "${param.date}";
		if(date=""){
			date="00:00:00";
		}
		
		var dataOnDate = new Array();
		var firstByExp = ${firstByExp};
		var firstTime;
		var tempsMax = ${tempsMax};
		console.log(tempsMax);
		for(var i=0; i<firstByExp.length; i++){
			if(lastData.length != 0 && firstByExp[i].experiment_id== lastData[i].experiment_id)
				firstTime = firstByExp[i].timestamp;
		}		
		console.log(firstTime);
		date = dateToTimestamp(date);
		$("#slider${param.count}").slider({
			min: 0,
			max: tempsMax, 
		    slide: function( event, ui ) {
		    	var tmp = new Date(ui.value-3600000);
		          $( ".amount" ).val( tmp.getHours() + ":"+ tmp.getMinutes() + ":" + tmp.getSeconds() );
		    	changeDate(ui.value, lastData);
		    	//$(".slider").slider("value", ui.value);
		        },
		    change: function(event, ui){
		    	console.log(ui.value);
		    	changeDate(ui.value, lastData);
		    },
		       create: function(event, ui){
		    	   var tmp = new Date(date);
			          $( ".amount" ).val( tmp.getHours() + ":"+ tmp.getMinutes() + ":" + tmp.getSeconds() );
		       },
		       stop: function(event, ui){
		    	   $(".slider").slider("value", ui.value);
		       },
			value : date,
			step : 1000
		});
		
		$(".amount").change(function(){
			changeDate(dateToTimestamp($(this).val()), lastData);
			$("#slider${param.count}").slider("value", dateToTimestamp($(this).val())+3600000);
			$(".amount").val($(this).val());
		});
		
		radius = 40;
		image = new Image();
		image.src = "<c:url value='/img/map'/>";
		/*window['tabForme'] = new Array();*/
		
		motes = ${motes};
		var stage = new Kinetic.Stage({
			container : '${param.nameDiv}'
		});
		image.onload = function(){
			stage.width(image.width+radius*2+20);
			stage.height(image.height);
		}
		layer = new Kinetic.Layer();
		
		
		var dx = image.width+3;
		var dy = 10;
		var tabFormelen = tabForme.length;
		for (var i = tabFormelen; i < motes.length+tabFormelen; i++) {
			window.tabForme[i] = new Kinetic.Circle({
				x : motes[i-tabFormelen].lon,
				y : motes[i-tabFormelen].lat,
				radius : 7,
				id: motes[i-tabFormelen].ip,
				fill : 'black'
			});
			
			for(var j=0; j<lastData.length; j++){
				if(lastData[j].mote == tabForme[i].id()){
					window.tabForme[i].id(tabForme[i].id()+lastData[j].experiment_id);
					break;
				}
			}
			
			if(tabForme[i].x() == 0 && tabForme[i].y()==0){
				tabForme[i].x(dx);
				tabForme[i].y(dy);
				dy+=radius*2+10;
				if(dy> image.height-5){
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
				text : motes[i-tabFormelen].ip,
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
			
			dataOnDate.push(auPlusProche(tabForme[i].id(), date+firstTime+3600000, lastData));
		}
		stage.add(layer);
		


console.log(lastData);

for(var i=0; i<dataOnDate.length;i++){
	if(typeof dataOnDate[i] != 'undefined')
	traitementNewData(dataOnDate[i]);
}
 stage.draw();
 
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
	//stage.draw();
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
		window['maxTmp'] = max;
	if(typeof min != 'undefined')
		window['minTmp'] = min;
	chgtBorne();
		}
		
/*Fonction cherchant la forme concernant la donnée dans le tableau de forme*/
function trouveForme(data){
	var forme;
	for(var i=0; i<tabForme.length; i++){
		if(tabForme[i].id() == data.mote+data.experiment_id){
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
		window['maxTmp'] = forme.temperature;
		change = true;
	}
	if(typeof minTmp == 'undefined' || forme.temperature < minTmp){
		window['minTmp'] = forme.temperature;
		change = true;
	}
	if(change)
		chgtBorne();
	else
		updateColor(forme);
}

/*Fonction traitement le changement de la borne max ou inf*/
function chgtBorne(){
	window['ecart']=maxTmp-minTmp;
	if(ecart!=0){
	window['cste']=255/(ecart*100);
	}
	else
		window['cste'] = 1;
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
		//layer.draw();
		
		}
	}
	
/*Fonction retournant l'objet le plus proche (par rapport au temps) de la liste*/
function auPlusProche(id, date, liste){
	var distTemps, obj;
	for(var j = 0; j<liste.length; j++){
		if(liste[j].mote+liste[j].experiment_id == id){
			ecartTmp = Math.abs(date-liste[j].timestamp)
			if(typeof distTemps == 'undefined' || ecartTmp<distTemps){
				distTemps=ecartTmp;
				obj = liste[j];
			}
				if(ecartTmp>distTemps){
				break;
			} 
		}
	}
	return obj
}

/*Change l'heure donné en paramètre sous la forme HH:mm:ss en date timestamps*/
function dateToTimestamp(date){
	var times = new Date("01/01/1970 "+ date);
	times = new Date(times.getTime());
	console.log(date + " ==> " + times.getTime());
	return times.getTime();
	}
	
	/*Fonction traitant un changement de date*/
function changeDate(date, liste){
	var tabTmp = new Array();
	console.log(new Date(date+firstTime))
	for(var k=tabFormelen; k<tabFormelen+motes.length; k++){
		tabTmp.push(auPlusProche(tabForme[k].id(), date+firstTime, liste));
	}
	for(var i=0; i<tabTmp.length;i++){
		if(typeof tabTmp[i] != 'undefined')
		traitementNewData(tabTmp[i]);
	}
	stage.draw();
}
	});
</script>
<div class="status" class="row" style="margin: 10px;"></div>
