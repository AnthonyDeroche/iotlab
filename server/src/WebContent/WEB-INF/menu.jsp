<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script src="js/home/dialog_new_exp.js"></script>
<script type="text/javascript">
$(function() {
	$("#menu").menu();
});
</script>
<ul id="menu">
  <li>
    <a href="#">Chart</a>
    <ul>
      <li><a href="live">Live</a></li>
      <li><a href="get">Analysis & Comparison</a></li>
    </ul>
  </li>
  <li>
    <a href="#">Map</a>
    <ul>
      <li><a href="map?config=live">Live&Edit</a></li>
      <li><a href="map?mode=date">On specific date</a></li>
      <li><a href="map?mode=compare">Comparison</a></li>
    </ul> 
  </li>
  <li>
  <a href="#">Tools</a>
  <ul>
  	<li><a href="#" id="create-user">Create new measure</a></li>
  	</ul>
  </li>
</ul>
<div id=manageExp>

<div id="dialog-form" title="Create new measure">
  <p class="validateTips">All form fields are required.</p>
 
  <form>
  <fieldset>
    <label for="label">Name of measure</label>
    <input type="text" name="label" id="label" class="text ui-widget-content ui-corner-all">
    <label for="desc">Description</label>
    <input type="text" name="desc" id="desc" value="" class="text ui-widget-content ui-corner-all">
  </fieldset>
  </form>
  	</div>
</div>