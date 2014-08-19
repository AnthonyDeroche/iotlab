<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script>
/*Script JQuery pour les éléments de la page.
 * Le bouton submit récupère tous les éléments sélectionnés ou insrit par l'utilisateur et les ajoute dans son Href pour être récupérés par la servlet
 */
	$(function() {
		check=false;
		$('.period').datetimepicker();
		$(".check").button();
		$("#live").button();
		$(".format").buttonset();
		$( "label" ).tooltip();
		$( "#compare").button();
		$(".help").tooltip();
		$("#inter").hide();
		$("#circleG").hide();
		$("#inter input").timepicker({
			timeFormat: 'HH:mm:ss',
			hourMax: 99
		});
		
		$( "#submit" )
	      .button()
	      .click(function( event ) {
	    	var condition = "?";  
	    	$('.format:first>input').each(function(i){
	    		if($(this).is(':checked'))
	    			condition+=$(this).attr('table')+"="+$(this).attr('id').substring(3, $(this).attr('id').length)+"&";
	    	});
	    	$('.format:not(:first)>input').each(function(i){
	    		if($(this).is(':checked'))
	    			condition+=$(this).attr('table')+"="+$(this).button("option", "label").toLowerCase()+"&";
	    	});
	    	$('#perioddiv>*').each(function(i) {
	    		if($(this).val()!=""){
	    			condition+=$(this).attr('name')+"="+$(this).val().replace(" ", "")+"&";
	    		}
	    	});
	    	$('#compare').each(function(i){
	    		if($(this).is(':checked'))
	    			condition+="mode=compare&";
	    	});
	    	$("#live").each(function(i){
	    		if(!$('#compare').is(':checked') && $(this).is(':checked')){
	    			condition+="live=true&"
	    		}
	    	})
	    	$("#inter input").each(function(i){
	    		if($('#compare').is(':checked') && $(this).val()!=""){
	    			condition+=$(this).attr('name')+"="+$(this).val()+"&";
	    		}
	    	});
	    	<%--if ( $('input[id=check11]').is(':checked')){
	    		console.log("Test ok");
	    	}
	        event.preventDefault();--%>
	        $(this).attr('href', condition);
	        $("#circleG").show();
	      });
	$("#compare").click(function(){
			$("#perioddiv input").val("");
			if(check){
				$("#perioddiv input").prop('disabled', false);
				$("#inter").hide(350);
				$("#divLive").show(350);
				$("#inter input").val("");
			}
			else{
				$("#perioddiv input").prop('disabled', true);
				$("#inter").show(350);
				$("#divLive").hide(350);
			}
			check=!check;
		});
	});
</script>

</head>
<body>

	<p>
	Measures :
	<div class="format">
	<c:forEach var="i" items="${listExp}">
		<input class="check" type="checkbox" id="exp${i.id}" table="experiment"><label title="${fn:escapeXml(i.description)}" for="exp${i.id}">${fn:escapeXml(i.comments)}</label>
	</c:forEach>
	</div>
	</p>
	<p>
		Select period : 
		<img class="help" src="<c:url value='/img/help-icon.png'/>" title="Click for help" onclick="$.prompt('No value at all means all datas')"/>
		<div id='perioddiv'>
		<input type="text" name="date_deb" class="period" value="" />
		<input type="text" name="date_fin" class="period" value="" />
		</div>
	</p>
	<p>Sensor :
	<div class="format">
		<input type="checkbox" id="check1" table="label"><label for="check1">Temperature</label>
		<input type="checkbox" id="check2" table="label"><label for="check2">Humidity</label>
		<input type="checkbox" id="check3" table="label"><label for="check3">Battery_Voltage</label>
		<input type="checkbox" id="check4" table="label"><label for="check4">Light1</label> 
		<input type="checkbox" id="check5" table="label"><label for="check5">Light2</label>
		</div>
	<br/>
	</p>
	<p>
	<div id="divLive">
	<input type="checkbox" id="live"><label for="live">Live</label>
	</div> 
	</p>
	<p>
	<input type="checkbox" id="compare"><label for="compare">Comparison mode</label>
	<img class="help" src="<c:url value='/img/help-icon.png'/>" title="Click for help" 
		onclick="$.prompt('In this mode the real date is ignored, the begin is defined by the begin of each measure <br/> The format is HH:mm:ss, if you want hours over 99, please type it manually')"/>
	<div id="inter">
	Begin : 
	<input type="text" name="beginCompare" id="init" class="periodRel" value="" />
	End : 
	<input type="text" name="endCompare" id="end" class="periodRel" value="" />
	</div>
	</p>
	<div id='submitDiv'>
	<a href="#" title="Submit" id="submit">Submit</a> <img class="help" src="<c:url value='/img/warning-icon.png'/>" title="Lot of data could take several minutes to load"/>
	</div>
	<p>
	
 <div id="circleG">
<div id="circleG_1" class="circleG">
</div>
<div id="circleG_2" class="circleG">
</div>
<div id="circleG_3" class="circleG">
</div>
</div>
<%-- <button id="create-user">Create new measure</button> --%>
