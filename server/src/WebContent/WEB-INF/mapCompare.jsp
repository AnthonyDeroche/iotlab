<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<div>
		<c:set var="count" value="1"/>
			<c:forEach var="i" items="${listGlobal}">
	<label for="slider">Date:</label> <input type="text" class="amount"/>
	<div class="slider" id="slider${count}"></div>
			<c:set var="lastData" value="${i}" scope="request"/>
				<jsp:include page="canvasMapCompare.jsp">
					<jsp:param value="false" name="live" />
					<jsp:param value="container${count}" name="nameDiv" />
					<jsp:param value="${count}" name="count"/>
				</jsp:include>
				<c:set var="count" value="${count+1}"/>
			</c:forEach>
	</div>
