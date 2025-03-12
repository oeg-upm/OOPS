<%--
SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8" errorPage=""%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

	<meta name="Author" content="María Poveda Villalón"/>
	<meta name="Languaje" content="English"/>
	<meta name="Keywords" content="ontology, ontology evaluation, pitfalls"/>
	<meta name="Description" content="This web provide online access to OOPS! - OntOlogy Pitfall Scanner!"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link href="css/estilos.css" rel="stylesheet" type="text/css" />
	<link href="css/bootstrap.css" rel="stylesheet" type="text/css" />
	<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />

	<!--[if lt IE 7]>
	<link href="estilosie.css" rel="stylesheet" type="text/css" />
	<![endif]--> 

	<title> OOPS! - OntOlogy Pitfall Scanner! - Good Practice Catalog</title>

</head>
<body>

<div id="wrap">

<div id="header">
	<!--<h1><a href="index.jsp">OOPS! </a></h1>
	<h2>OntOlogy Pitfall Scanner!</h2>-->
	<span style="float: left;"><a href="index.jsp"><img src="images/logoWhite65.png" alt="Logo OOPS!" /></a></span>
	<p><span>Ontology Pitfall Scanner!</span></p>
</div>

<div id="load" class="loading"> 
	<p><img src="images/loading.gif" alt="scanning" > </p>
	<br>
	<p>OOPS! is scanning...</p>
</div>

<div id="intro"> 
<p><strong>OOPS! (OntOlogy Pitfall Scanner!)</strong> helps you to detect some of the most common
pitfalls appearing when developing ontologies.</p>  

<p>To try it, enter a URI or paste an OWL 
document into the text field above. A list of pitfalls and the elements of your ontology
 where they appear will be displayed. </p>

</div>

<div id="intro2"> 
	<div class="textbox-big">
		<form method="post" action="response.jsp" >
			<p> Scanner by URI:
			<input type="text" name="uri" id="uriEx">  
			<input type="submit" value="Scanner by URI" onclick="doValidation()"> 
			</p>
			<div id=example>
				<p onclick="example1('http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf')">
				Example: http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf
				</p>
			</div>
			
		</form>
		
		<br>
		
		<form method="post" action="response.jsp" >
			<p> Scanner by direct input:
			   <textarea class="medio" cols="78" rows="10" name="RDF"></textarea>
		       <input type="submit" value="Scanner by RDF" onclick="doValidation()"> </p>
  
		</form>				
	</div>	
</div>
		
<div id="content">
<div class="left"> 

<div class="articles">

<h2>Catalogue of good practices</h2>

<p>Here you can find a catalogue of good practices that usually appear when developing ontologies. Some of them are
very common and have been identified by several works about ontology evaluation 
(see <a href="#references">References</a>). </p>

<br>

<p>We would like to help you to find as many pitfalls as possible in your ontology developments. However, some
of them depend on the domain being modelled or the requirements specified for each particular ontology.
Up to now, OOPS! can identify semi-automatically those pitfalls in the catalogue with the title in <strong>bold</strong>. 
We encourage you to keep an eye of those pitfalls that OOPS! is not able to detect yet. 
It is a  good idea to revise the ontology manually looking for them.</p>

<br>

<ul> 
	 <li>GP1. Creating polysemous elements: an ontology element whose name has different meanings is included 
	 in the ontology to represent more than one conceptual idea. For example, the class “Theatre” is used to 
	 represent both the artistic discipline and the place in which a play is performed. 
	</li> 
	 <li><strong>P2. Creating synonyms as classes:</strong> several classes whose identifiers are synonyms are created and 
	 defined as equivalent. As an example we could define “Car”, “Motorcar” and “Automobile” as equivalent 
	 classes. Another example is to define the classes “Waterfall” and “Cascade” as equivalents. This pitfall 
	 is related to the guidelines presented in [2] which explain that synonyms for the same concept do not 
	 represent different classes. 
	</li> 
    <li><i class="icon-star"></i> <span class="ld">(Linked Data Feature) </span><strong>P37. Ontology not available:</strong>
     This bad practice is about not meeting LOD1 from Linked Data star system that 
    stars “On the web” and LDV1 that says “Publish your vocabulary on the Web at a stable URI”. An example of this
     pitfall could be the following case: “Ontology Security (ontosec)” which URI is 
     http://www.semanticweb.org/ontologies/2008/11/OntologySecurity.owl and it is not available online as 
     RDF nor as HTML (at the moment of carrying out this work).
	</li>	
</ul>

<br>
<p>
 Not enough? <a href="submissions.jsp"><strong>Suggest new pitfalls</strong></a> to enlarge the current catalogue!
</p>
 
 <br>
 
<p><a name="references"></a>References:</p>

<ul>
	<li>
	[1] Gómez-Pérez, A. ''Ontology Evaluation''. Handbook on Ontologies. S. Staab and R. Studer Editors. Springer. International Handbooks on Information Systems. Pp: 251-274. 2004.
	</li> 
	<li>
	[2] Noy, N.F., McGuinness. D. L. ''Ontology development 101: A guide to creating your first ontology.'' Technical Report SMI-2001-0880, Standford Medical Informatics. 2001.
	</li> 
	<li>
	[3] Rector, A., Drummond, N., Horridge, M., Rogers, J., Knublauch, H., Stevens, R.,; Wang, H., Wroe, C. ''Owl pizzas: Practical experience of teaching owl-dl: Common errors and common patterns''. In Proc. of EKAW 2004, pp: 63–81. Springer. 2004.
	</li>
	<li>
	[4] Hogan, A., Harth, A., Passant, A., Decker, S., Polleres, A. Weaving the Pedantic Web. Linked Data on the Web Workshop LDOW2010 at WWW2010 (2010).
	</li>
 
</ul>

<br>

<p>
Please, help us making OOPS! better.
<a href="form.jsp"><strong>Feedback</strong></a>
is more than welcome!
</p>

</div>

</div>

<div class="right">  

<h2>Want to help?</h2>
<ul>
	<li><a href="submissions.jsp">Suggest new pitfalls</a></li> 
	<li><a href="form.jsp">Provide feedback</a></li> 
</ul>

<h2>Documentation:</h2>
<ul>
	<li><a href="catalogue.jsp">Pitfall catalogue</a></li> 
	<li><a href="OOPSUserGuidev1.pdf" target="_blank">User guide</a></li> 
	<li><a href="http://oa.upm.es/10195/1/OOPS_technical_report_v0.2.pdf" target="_blank">Technical report</a></li> 
</ul>

<h2>Related papers:</h2>
<ul>	
	<li><a href="http://www.igi-global.com/article/oops-ontology-pitfall-scanner/116450" target="_blank">IJSWIS 2014</a></li> 
	<li><a href="http://www.springerlink.com/content/nn684334014507n4/" target="_blank">EKAW 2012</a></li> 
	<li><a href="http://2012.eswc-conferences.org/sites/default/files/eswc2012_submission_322.pdf" target="_blank">ESWC 2012 Demo</a></li> 
	<li><a href="http://oa.upm.es/5413/1/A_DOUBLE_CLASSIFICATION_OF_COMMON_PITFALLS_IN_ONTOLOGIES.pdf" target="_blank">Ontoqual 2010</a></li> 
	<li><a href="http://oa.upm.es/6115/1/CAEPIA09_-_Common_Pitfalls_in_Ontology_Development_-_final_version_fixed.pdf" target="_blank">CAEPIA 2009</a></li> 
</ul>

<h2>Web services:</h2>
<ul>	
<li><a href="http://oops.linkeddata.es/webservice.html">RESTFul Web Service</a></li> 
</ul>

<h2>Developed by:</h2>
<a href="https://oeg.fi.upm.es/" target="_blank"><img src="images/logoOEG.png" alt="OEG logo" style="margin-left:3%;margin-right:5%;width:68%;" /></a>

<br>
<br>

<a href="https://twitter.com/OOPSoeg" class="twitter-follow-button" data-show-count="false">Follow @OOPSoeg</a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>

</div>
<div style="clear: both;"> </div>

</div>


<div id="footer">
<p><a href = "http://purl.org/net/mpoveda" target="_blank">María Poveda</a></p>
<p>Contact email: oops(at)delicias.dia.fi.upm.es. Latest revision April 2021</p>
<a href="http://www.templatesold.com/" target="_blank">Website Templates</a> by <a href="http://www.free-css-templates.com/" target="_blank">Free CSS Templates</a>
</div>
</div>

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
