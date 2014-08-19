<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>



<div class="row">
	<c:if test="${not empty sessionScope.redirect}">
		<div style="padding:20px;">An authorization is required for the page ${sessionScope.redirect}</div>
	</c:if>
	<form id="loginForm" data-abide>
		<fieldset>
			<legend>Log in</legend>
				<div class="username-field"> 
					<label>Your name <small>required</small> 
						<input type="text" id="username" required pattern=".{3,}"> 
					</label> 
					<small class="error">Username is required and must contains at least 3 characters.</small> 
				</div> 
				<div class="password-field"> 
					<label>Password <small>required</small> 
						<input type="password" id="password" required pattern=".{6,}"> 
					</label> 
					<small class="error">Password is required and must contains at least 6 characters.</small> 
				</div>
				<input type="hidden" id="redirect" value="${sessionScope.redirect}"/>
				<c:set var="redirect" scope="session" value=""/>
				<button type="submit">Submit</button> 
				
			<p id="formError" style="color:red;">
			</p>
			<p id="formSuccess" style="color:green;">
			</p>
		</fieldset>
	</form>
</div>



<script type="text/javascript">
	$(function() {
		$("#loginForm").on("submit valid invalid",function(e){
			if(e.type=="valid"){
				var username = $("#username").val();
				var password = $("#password").val();
				$.ajax({ 	type: "POST",
							dataType: "json",
							url: "rest/account/login", 
							data: "username="+username+"&password="+password,
							beforeSend: function(){},
							complete: function(){},
							success: function(content){
								$("#formError").text("");
								$("#formSuccess").text("");
								if(content.success=="0") {
									$("#formError").text(content.message);
									$("#password").val("");
								}
								else {
									$("#formSuccess").text(content.message);
									var redirect = $('#redirect').val();
									if(redirect.length>0)
										window.location.href=redirect;
									else
										window.location.href="home";
								}
							}
				});
			}
			return false;
		});
	});
</script>
