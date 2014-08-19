<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<style>
.select{
width: 140px;
}

#min{
width: 140px;
display: initial;
}
#max{
width: 140px;
display: initial;
}

form{
margin:0 0 0
}
</style>
<script>
$(function(){
	$("#submit").button();
	$(".select").tooltip();
});
</script>
<form method="post" action=admin>
<select id="select_mote" class="select" name="select_mote" title="Mote">
<c:forEach var="i" items="${listMote}">
<option value=${i.id}>${i.ipv6}</option>
</c:forEach>
</select>

<select id="select_sensor" class="select" name="select_sensor" title="Sensor">
<c:forEach var="i" items="${listLabel}">
<option value=${i.label_id}>${i.label}</option>
</c:forEach>
</select>

<input type="number" id="min" name="min" placeholder="Min value"> </input>
<input type="number" id="max" name="max" placeholder="Max value"> </input>
<INPUT type="submit" id="submit" value="Add rule">
</form>

<TABLE border="1">
<TR>
<TH> Rule n° </TH>
<TH> Mote </TH>
<TH> Sensor </TH>
<TH> Minimum </TH>
<TH> Maximum </TH>
<TH> Action </TH>
 </TR>
<c:forEach var="i" items="${listMR}">
<tr>
<td>${i.id}</td>
<td>${i.mote.ipv6}</td>
<td>${i.label.label}</td>
<td>${i.minVal}</td>
<td>${i.maxVal}</td>
<td> <form method="post" action="admin"><input type="HIDDEN" value="${i.id}" name="idDel"/> <input type="submit" value="Delete"></form></td>
</tr>
</c:forEach>
</TABLE>