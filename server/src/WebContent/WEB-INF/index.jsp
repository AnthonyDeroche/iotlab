<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<% pageContext.setAttribute("pages", new String[][] {
		new String[]{"login","/WEB-INF/account/login.jsp","Login"},
		new String[]{"logout","/WEB-INF/account/logout.jsp","Log out"},
		new String[]{"admin","/WEB-INF/admin/admin.jsp","Administration"},
		new String[]{"data","/WEB-INF/data.jsp","Data"},
		new String[]{"mapTN","/WEB-INF/mapTN.jsp","Map TELECOM Nancy"},
		new String[]{"api","/WEB-INF/api.jsp","API documentation"},
		new String[]{"dataStatistics","/WEB-INF/dataStatistics.jsp","Stats"},
		new String[]{"map-live","/WEB-INF/mapEditor.jsp","Mote map"},
		new String[]{"map-date","/WEB-INF/mapSimpleView.jsp","Map view on specific date"},
		new String[]{"map-compare","/WEB-INF/mapCompare.jsp","Map compare"},
		new String[]{"map-config-date","/WEB-INF/mapConfigDate.jsp","Config mote map"},
		new String[]{"map-config-compare","/WEB-INF/mapConfigCompare.jsp","Config map compare"},
		new String[]{"home","/WEB-INF/home.jsp","Param Choice"},
		new String[]{"geolocation","/WEB-INF/geolocation/home.jsp","Geolocation"},
		new String[]{"403","/WEB-INF/error/403.html","Forbidden"},
		} , pageContext.PAGE_SCOPE);   %>

<c:set var="page" scope="page" value="${param.page}" />

	<c:forEach var="p" items="${pages}">
			<c:if test="${p[0] == page}">
				<c:set var="import" value="${p[1]}" />
				<c:set var="title" value="${p[2]}" />
			</c:if>
			<c:if test="${empty title }">
				<c:set var="title" value="IoT Lab - Home" />
			</c:if>
	</c:forEach>
	
<!DOCTYPE html>
<!--[if IE 9]><html class="lt-ie10" lang="en" > <![endif]-->
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><c:out value="${title}" /></title>

<link rel="stylesheet" href="css/normalize.css"/>
<link rel="stylesheet" href="css/foundation.min.css"/>
<link rel="stylesheet" href="css/foundation-icons/foundation-icons.css"/>
<link rel="stylesheet" href="css/nprogress.css"/>
<link rel="stylesheet" href="css/style.css"/>
<script src="js/vendor/modernizr.js"></script>
<script src="js/vendor/jquery.js"></script>
<script src="js/vendor/fastclick.js"></script>
<script type="text/javascript" src="js/foundation.min.js"></script>

<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>

<script src="js/map/kinetic-v5.1.0.min.js"></script> <!-- canvasMapCompare.jsp -->

<link rel="stylesheet" href="css/jquery-ui-timepicker-addon.css"> <!-- home.jsp -->
<link rel="stylesheet" href="css/jquery-impromptu.min.css">
<script src="js/home/jquery-ui-timepicker-addon.js"></script>
<script src="js/home/jquery-impromptu.min.js"></script>

<script src="js/home/globalize.js"></script> <!-- mapConfigCompare.jsp -->
<script src="js/home/globalize.culture.de-DE.js"></script>
<script src="js/home/dialog_new_exp.js"></script>


<script src="js/jquery.numeric.js"></script> <!-- mapConfigDate.jsp -->


</head>
<body>
<nav class="top-bar" data-topbar> 
<ul class="title-area"> 
	<li class="name"> 
		<h1><a href="home" class="fi-home"> Home</a></h1> 
	</li>  
	<li class="toggle-topbar menu-icon"><a href="#"><span>Menu</span></a></li> 
</ul> 
 <section class="top-bar-section">

    <ul class="right">
    	<c:choose>
	    	<c:when test="${empty sessionScope.user}">
	    		<li><a href="login">Log in</a></li>
	    	</c:when>
	    	<c:otherwise>
	    		
	    		<li style="line-height:45px;color:lime;">
	    			<c:if test="${sessionScope.user.admin == true}"><span style="color:red;" class="fi-torso"></span></c:if>
	    			<c:out value="${sessionScope.user.username}"/>
	    		</li>
	    		<li><a href="logout" id="logout">Log out</a></li>
	    		<script type="text/javascript">
	    			$('#logout').on('click',function(e){
	    				e.preventDefault();
	    				$.get("rest/account/logout",function(content){
	    					if(content.success==1)
	    						window.location.href='home';
	    				});
	    			});
	    		</script>
	    	</c:otherwise>
    	</c:choose>
    	
      	<li><a href="admin">Administration</a></li>
    </ul>

    <ul class="left">
    
      <li><a href="live" class="fi-graph-trend"> Live</a></li>
      
      <li class="has-dropdown">
        <a href="#">Map</a>
        <ul class="dropdown">
          <li><a href="map?config=live">Live and edit</a></li>
          <li><a href="map?mode=date">On specific date</a></li>
          <li><a href="map?mode=compare">Comparison</a></li>
        </ul>
      </li>
      <li class="has-dropdown">
        <a href="#">Statistics</a>
        <ul class="dropdown">
          <li><a href="stats">Overview</a></li>
          <li><a href="stats#chart">Loss ratio</a></li>
        </ul>
      </li>
      <li><a href="geolocation"> Geolocation</a></li>
      <li><a href="api">API documentation</a></li>
      <li><a href="mapTN">Map TELECOM Nancy</a></li>
      <li>
      	<img id="main-loader" src="img/main-loader.gif" alt="" style="margin-top:6px;visibility:hidden;"/>
      </li>
    </ul>
  </section>
</nav>
	<div id="content">
		<c:if test="${not empty import}">
			<c:import url="${import}"></c:import>
		</c:if>
		<c:if test="${empty import}">
			<div>
				<h1 style="text-align:center;">Internet of Things lab</h1>
			</div>
			<div style="width:70%;margin:auto;">
				<div id="img">
					<a href="http://www.telecomnancy.eu/" target="_blank"><img alt="" src="img/telecom_nancy.png"></a>
					<a href="http://www.inria.fr/" target="_blank"><img alt="" src="img/inria.jpg"></a>
					<a href="http://www.loria.fr" target="_blank"><img alt="" src="img/logo_loria.jpg"> </a>
					<a href="http://iut-charlemagne.univ-lorraine.fr/" target="_blank"><img alt="" src="img/Logo IUT-UL.jpg"></a>
				</div>
				
			</div>
			<div class="row" style="margin-top:50px;">
				<h3>Contributors :</h3> 
				<ul>
					<li>Anthony Deroche - TELECOM Nancy</li>
					<li>Thierry Duhal - TELECOM Nancy</li>
					<li>Arthur Garnier - IUT Nancy Charlemagne</li>
				</ul>
				
				<h3>Supervisors :</h3> 
				<ul>
					<li><a href="http://thibault.cholez.free.fr/" target="_blank">Thibault Cholez</a> - Associate Professor - INRIA (team : MADYNES)</li>
					<li>Emmanuel Nataf - Associate Professor - INRIA (team : MADYNES)</li>
				</ul>
			</div>
		</c:if>
	</div>
	<script>
		$(document).foundation();
	</script>
</body>
</html>
