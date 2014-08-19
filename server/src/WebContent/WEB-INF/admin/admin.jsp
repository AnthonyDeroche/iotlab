<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<dl class="tabs" data-options="deep_linking:true;scroll_to_content: false" data-tab >
	<dd class="active">
		<a href="#network-monitoring" class="fi-monitor"> Network monitoring</a>
	</dd>
	<dd>
		<a href="#accounts" class="fi-torsos-all"> Accounts</a>
	</dd>
	<dd>
		<a href="#filters" class="fi-filter"> Filters</a>
	</dd>
	<dd>
		<a href="#strategies" class="fi-link"> Strategies</a>
	</dd>
	<dd>
		<a href="#labels" class="fi-clipboard-pencil"> Labels</a>
	</dd>
	<dd>
		<a href="#types" class="fi-list-thumbnails"> Types</a>
	</dd>
	<dd>
		<a href="#logs" class="fi-book"> Logs</a>
	</dd>
	<dd>
		<a href="#rules" class="fi-wrench"> Monitoring Rules</a>
	</dd>
	<dd>
		<a href="#geolocation" class="fi-arrows-in"> Geolocation</a>
	</dd>
	<dd>
		<a href="#dbstats_config" class="fi-database"> DB stats - config</a>
	</dd>
</dl>
<div class="tabs-content">
	<div class="content active" id="network-monitoring">
		<span class="fi-refresh" id="refreshNetworkMonitoring" style="cursor:pointer; color:dodgerblue; font-size:2em;"></span>
		<div class="row">
		    <div class="small-3 columns">
		    	<h3 style="text-align:center;">Commands</h3>
		    	
		    		<div class="row collapse">
		    			<div class="small-6 columns">
			         	 	<a href="#start" class="button postfix command" style="width:100%; background-color:limegreen;">START</a>
			          	</div>
						<div class="small-6 columns">
			          		<select id="start_select">
					          <option value="ALL" selected>ALL</option>
					          <option value="DATA">DATA</option>
					          <option value="GEOLOC">GEOLOC</option>
				        	</select>
			          	</div>
		          	</div>
		          	
		          	<div class="row collapse">
		          		<div class="small-6 columns">
			         	 	<a href="#stop" class="button postfix command" style="width:100%; background-color:#dd2222;">STOP</a>
			          	</div>
						<div class="small-6 columns">
			          		<select id="stop_select">
					          <option value="ALL" selected>ALL</option>
					          <option value="DATA">DATA</option>
					          <option value="GEOLOC">GEOLOC</option>
				        	</select>
			          	</div>
			          	
		          	</div>
		          	
					<a class="button command" href="#sink_id_dvn" style="width:100%; background-color:dodgerblue;">SINKS INFO</a><br />
					<a class="button command" href="#global_repair"  style="width:100%; background-color:darkorange;">GLOBAL REPAIR</a><br />
					
					<div class="row collapse">
						<div class="small-8 columns">
			          		<input type="text" placeholder="DATA PERIOD (s)" id="periodValue" title="Expected an integer lower than 65535">
			          	</div>
			          	<div class="small-4 columns">
			         	 	<a href="#data_period" class="button postfix command" style="background-color:dodgerblue;">CHANGE</a>
			          	</div>
		          	</div>
		          	
		          	<div class="row collapse">
						<div class="small-8 columns">
			          		<input type="text" placeholder="TX POWER [1,31]" id="txpowerValue" title="Expected an integer between 1 and 31">
			          	</div>
			          	<div class="small-4 columns">
			         	 	<a href="#txpower" class="button postfix command" style="background-color:dodgerblue;">CHANGE</a>
			          	</div>
		          	</div>
			</div>
			
			<div class="small-5 columns">
				<h3 style="text-align:center;">Sinks</h3>
				<table id="sinksTable" class="tableCenter" style="width:100%;">
					<thead>
						<tr >
							<th>Id</th>
							<th>Sinks</th>
							<th>Dodag version number</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
				</table>
	        </div>
	        
	        <div class="small-4 columns">
	        	<h3 style="text-align:center;">Senders</h3>
				<table id="sendersTable" class="tableCenter" style="width:100%;">
					<thead>
						<tr>
							<th>Id</th>
							<th>Senders</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
	        </div>
	        
		</div>
	</div>
	<div class="content" id="accounts">
		<div class="row">
			<div class="row">
				<a href="#" class="button" data-reveal-id="addAccountModal">Create an account</a>
			</div>
			<table id="accountsTable" style="width:100%;margin:auto;">
				<thead>
					<tr><th>Username</th><th>Password</th><th>Email</th><th>Admin</th><th></th><th></th></tr>
				</thead>
				<tbody>
				
				</tbody>
			</table>
		</div>
	</div>
	<div class="content" id="filters">
		<div class="row">
			<a href="#" class="button" data-reveal-id="addFilterModal">Add a filter</a>
		</div>
		<div id="filtersTables">
		
		</div>
	</div>
	<div class="content" id="strategies">
		<div class="row">
			<a href="#" class="button" data-reveal-id="addStrategyModal">Add a strategy</a>
		</div>
		<div id="strategiesTables">
		
		</div>
	</div>
	<div class="content" id="labels">
		<div class="row">
			<a href="#" class="button" data-reveal-id="addLabelModal">Add a label</a>
		</div>
		<div id="labelsTables">
		
		</div>
	</div>
	<div class="content" id="types">
		<div class="row">
			<a href="#" class="button" data-reveal-id="addTypeModal">Add a type</a>
		</div>
		<table id="typesTable" class="tableCenter">
			<thead>
				<tr>
					<th>Id</th>
					<th>Description</th>
					<th>Stream name</th>
					<th>Minimun data number</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
	<div class="content" id="logs">
		<span class="fi-refresh" id="refreshLogs" style="cursor:pointer;"></span>
		<table id="logsTable" style="width:100%;">
			<thead><tr><th>Date</th><th>Member</th><th>Module</th><th>Message</th><th>IP</th><th>level</th></tr></thead>
			<tbody></tbody>
		</table>
	</div>
	<div class="content" id="rules">
	
	</div>
	<div class="content" id="geolocation">
	    <span class="fi-refresh" id="refreshGeo" style="cursor:pointer;"></span>
		<div class="row">
			<div class="column medium-3">
				<h3>Config</h3>
				<table id="g_config">
					<thead><tr><th>Service / Algorithm</th><th>State</th></tr></thead>
					<tbody></tbody>
				</table>
			</div>
			<div class="column medium-4" style="max-height:500px;overflow:auto;">
				<h3>Calibration</h3>
				<table id="calibrationMeasures">
					<thead><tr><th>src</th><th>dest</th><th>Wall Nb</th></tr></thead>
					<tbody></tbody>
				</table>
			</div>
			<div class="column medium-5">
				<h3>Add / remove anchors</h3>
				<div class="column medium-6">
					<table id="motesTable" style="width:100%;">
						<thead><tr><th colspan="2">Motes (no anchors)</th></tr></thead>
						<tbody></tbody>
					</table>
				</div>
				<div class="column medium-6">
					<table id="anchorsTable" style="width:100%;">
						<thead><tr><th colspan="2" class="fi-anchor"> Anchors</th></tr></thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="content" id="dbstats_config">
		<div class="row">
			<div class="columns large-7" id="dashboardStats"></div>
			<div class="columns large-5" id="dashboardConfig"></div>
		</div>
	</div>
</div>

<!--------------------------------------------  LABELS  ------------------------------------------------------- -->
<div id="addLabelModal" class="reveal-modal" data-reveal>
	<form data-abide id="addLabelForm">
		<div class="label-field">
			<label>The label <small>required</small> <input type="text"
				id="labelFieldAdd" required pattern="[a-zA-Z0-9]+">
			</label> <small class="error">Label is required and must be a string
				with or without numbers.</small>
		</div>
		<button class="submit" type="submit">Submit</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<div id="deleteLabelModal" class="reveal-modal" data-reveal>
	<form id="deleteLabelForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>The dependencies with this label such as filters and data might prevent you from deleting it.</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>
<!--------------------------------------------  TYPES  ------------------------------------------------------- -->
<div id="addTypeModal" class="reveal-modal" data-reveal>
	<form data-abide id="addTypeForm">
		<div class="type-field">
			<label>Type <small>required</small> <input type="text"
				id="idTypeAdd" required pattern="[0-9]+">
			</label> <small class="error">Type number is required and must be an integer</small>
		</div>
		<div class="type-field">
			<label>Description<small>required</small> <input type="text"
				id="descriptionTypeAdd" required pattern="[a-zA-Z0-9]+">
			</label> <small class="error">Type description is required</small>
		</div>
		<div class="type-field">
			<label>Stream name<small>required</small> <input type="text"
				id="streamNameTypeAdd" required pattern="[a-zA-Z0-9]+">
			</label> <small class="error">Stream name is required</small>
		</div>
		<div class="type-field">
			<label>Minimun data number <small>required</small> <input type="text"
				id="minimunDataNumberTypeAdd" required pattern="[0-9]+">
			</label> <small class="error">A minimun data number is required and must be an integer</small>
		</div>
		<button class="submit" type="submit">Submit</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<div id="deleteTypeModal" class="reveal-modal" data-reveal>
	<form id="deleteTypeForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>The dependencies with this type such as filters might prevent you from deleting it.</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<!--------------------------------------------  STRATEGIES  ------------------------------------------------------- -->


<div id="addStrategyModal" class="reveal-modal" data-reveal>
	<form data-abide id="addStrategyForm">
		<div class="label-field">
			<label>Java class name <small>required</small> <input type="text"
				id="classNameStrategyAdd" required pattern="[a-zA-Z0-9]+">
			</label> <small class="error">Java class name is required and must be a string
				with or without numbers.</small>
		</div>
		<button class="submit" type="submit">Submit</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<div id="deleteStrategyModal" class="reveal-modal" data-reveal>
	<form id="deleteStrategyForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>The dependencies with this strategy such as filters might prevent you from deleting it.</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<!--------------------------------------------  FILTERS  ------------------------------------------------------- -->

<div id="addFilterModal" class="reveal-modal" data-reveal>
	<form data-abide id="addFilterForm">
		<div class="filter-field">
			<label>Type <small>required</small></label>
			<select id="typeFilterAdd" required>
	          
	        </select>
		</div>
		<div class="filter-field">
			<label>Offset <small>required</small>
			<input type="text" id="offsetFilterAdd" required pattern="[0-9]+"/>
			</label><small class="error">Offset is required and must be an integer.</small>
		</div>
		<div class="filter-field">
			<label>Label <small>required</small></label>
			<select id="labelFilterAdd" required>
	          
	        </select>
		</div>
		<div class="filter-field">
			<label>Strategy <small>required</small></label>
			<select id="strategyFilterAdd" required>
	          
	        </select>
		</div>
		
		<button class="submit" type="submit">Submit</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<div id="deleteFilterModal" class="reveal-modal" data-reveal>
	<form id="deleteFilterForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>The dependencies with this label such as filters and data might prevent you from deleting it.</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<!--------------------------------------------  NETWORK MONITORING  ------------------------------------------------------- -->

<div id="deleteMoteModal" class="reveal-modal" data-reveal>
	<form id="deleteMoteForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>All the data related to the mote will be also deleted</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<!--------------------------------------------  ACCOUNTS  ------------------------------------------------------- -->

<div id="addAccountModal" class="reveal-modal" data-reveal>
	<form data-abide id="addAccountForm">
		<div class="account-field">
			<label>Username <small>required</small>
			<input type="text" id="usernameAccountAdd" required pattern=".{3,}"/>
			</label><small class="error">Username is required and must contain at least 3 characters</small>
		</div>
		<div class="account-field">
			<label>Password <small>required</small>
			<input type="password" id="passwordAccountAdd" required pattern=".{6,}"/>
			</label><small class="error">Password is required and must contain at least 6 characters</small>
		</div>
		<div class="account-field">
			<label>Email <small>required</small>
			<input type="email" id="emailAccountAdd" required />
			</label><small class="error">Email is required</small>
		</div>
		
		<button class="submit" type="submit">Submit</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<div id="removeAccountModal" class="reveal-modal" data-reveal>
	<form id="removeAccountForm">
		<div class="panel callout radius"> 
			<h5>Warning</h5> 
			<p>This action is irreversible and will remove all data related to this account</p> 
		</div>
		<button class="submit" type="submit">Confirm deletion</button>
		<img class="loader"
			src="img/ajax-loader.gif" alt="" />
	</form>
	<a class="close-reveal-modal">&#215;</a>
</div>

<!------------------------------------------ -->

<script src="js/admin.js"></script>