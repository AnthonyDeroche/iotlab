<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
$(function(){
	$('.date').datetimepicker();
	$("#radio").buttonset();
	$( "label" ).tooltip();
	$("#interval").numeric();
	$("#circleG").hide();
	$( "#submit" )
    .button()
    .click(function( event ) {
  	var condition = "?config=date&";  
  	$('#radio>input').each(function(i){
  		if($(this).is(':checked'))
  			condition+=$(this).attr('table')+"="+$(this).attr('id').substring(3, $(this).attr('id').length)+"&";
  	});
  	
  	$('.date').each(function(i) {
  		if($(this).val()!=""){
  			condition+=$(this).attr('name')+"="+$(this).val().replace(" ", "")+"&";
  		}
  	});
  	
  /* 	$(function(){
  		if($('#interval').val()!=""){
  			condition+='interval='+$('#interval').val()+"&";
  		}
  	}); */
  	<%--event.preventDefault();--%>
      $(this).attr('href', condition);
      $("#circleG").show();
    });	
});
</script>


	<p>Measures :
	<form>
	<div id="radio">
		<c:forEach var="i" items="${listExp}">
			<input type="radio" id="exp${i.id}" table="experiment" name='radio'>
			<label title="${i.description}" for="exp${i.id}">${i.comments}</label>
		</c:forEach>
	</div>
	</form>
	</p>
	<p>Select date : <br/>
		Begin : <input type="text" name="date" class="date" value="" />
		End : <input type="text" name="dateEnd" class="date" value="" />		
	</p>
<%-- <p>
	Specify research interval (in seconds) :  
		<input type="text" name="interval" value="" id="interval" />
	<br/> <b> Note : </b> If there isn't value for a mote in this interval, no color will appear around this one.
	</p>--%>	
	<p>
	<a href="" title="Submit" id="submit">Submit</a>
	</p>
	
	 <div id="circleG">
<div id="circleG_1" class="circleG">
</div>
<div id="circleG_2" class="circleG">
</div>
<div id="circleG_3" class="circleG">
</div>
</div>
