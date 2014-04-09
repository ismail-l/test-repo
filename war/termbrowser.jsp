<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Department of Arts and Culture National Termbank</title>

<script type="text/javascript" language="javascript" charset="UTF-8" src="tms2.TermBrowser/tms2.TermBrowser.nocache.js"></script>

<link href="css/GWT-default.css" rel="stylesheet" type="text/css" /> <!--  Probeer om hierdie te remove!! -->
<link href="css/styles.css" rel="stylesheet" type="text/css" />

</head>

<body>
<input type="hidden" id="pageName" value="termbrowser" />
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
  	<td id="searchresults" valign="top"></td>
	<td id="mainFrame" valign="top" align="left" width="100%">
		<table width="100%" height="23px" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="controlbarSpacer" height="23px" align="left" valign="top" id="controlbar"></td>  				  				
  			<td class="controlbarSpacer" height="23px"  align="left" valign="top" id="infobar"></td>	  
  		</tr>
		</table>
		<table width="100%" height="90px" border="0" cellpadding="0" cellspacing="0">
		  <tr>
		  	<td class ="termbrowser_repeat" width="362px" height="90px" align="left" valign="middle"><img src="images/left_termbrowser.png" alt="TermBrowserLogo" height="90px" width="362px"/></td>    
		    <td class ="termbrowser_repeat" width="50%" height="90px" align="left" valign="middle"></td>		      	 	       
	        <td class= "termbrowser_repeat" width="50%" height ="90px" align="right" valign="middle" id="navigation"></td>
	      </tr>
	      <tr>
	      	
	      </tr>
		</table>										
		<div id="recordnavigation"></div>		
		<div id="content"></div>			
	</td>
  </tr>
  <tr width="100%" height="50px"></tr>
</table>
<div id="footer"></div>
</body>
</html>