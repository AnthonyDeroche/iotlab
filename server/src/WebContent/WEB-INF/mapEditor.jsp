<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
$(function() {
	$("#save")
	.button()
	.click(function( event ) {
		var tab = Array();
		for(var i=0; i<tabForme.length; i++){
			tab[i]= tabForme[i].text.text()+":"+(tabForme[i].x()+tabForme[i].parent.x())+":"+(tabForme[i].y()+tabForme[i].parent.y());
		}
		var form = $('<form action="map" method="post">' +
				  '<input type="text" name="tab_mote" value="'+ tab +'" />' +
				  '</form>');
				$('body').append(form);
				//alert('wait');
				$(form).submit();
	});
	
$("input").button();
});
</script>
<link rel="stylesheet" href="css/style.css">
<meta http-equiv="Cache-Control" content="no-store" />
</head>
<body>
	
		<a href="#" title="Save" id="save">Save configuration</a>
		<!-- <form action="<c:url value='/upload' />" method="post" enctype="multipart/form-data">
                <label for="fichier">Emplacement de la map</label>
                <input type="file" id="fichier" name="fichier" accept="image/*" />
                <input type="submit" value="Envoyer" class="sansLabel" />
        </form>-->
	<br />
<jsp:include page="canvasMap.jsp" >
	<jsp:param value="true" name="live"/>
	<jsp:param value="container" name="nameDiv"/>
</jsp:include>
