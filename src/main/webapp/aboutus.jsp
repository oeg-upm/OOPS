<%--
SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8"
	import="es.upm.fi.oeg.oops.*, java.util.*" errorPage=""%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

	<meta name="Author" content="María Poveda Villalón" />
	<meta name="Languaje" content="English" />
	<meta name="Keywords" content="ontology, ontology evaluation, pitfalls" />
	<meta name="Description"
		content="This web provide online access to OOPS! - OntOlogy Pitfall Scanner!" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link
		href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
		rel="stylesheet"
		integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
		crossorigin="anonymous">
	<link href="css/style.css" rel="stylesheet" type="text/css" />
	<link rel="icon" type="image/png" href="images/favicon.png">

	<title> OOPS! - About Us </title>

</head>
<body>

<%@ include file="part-menu.html" %>

	<div id="wrap">
		<div id=main>
			<br><br><h1>About Us</h1>
			<br><br>
		</div >
		<div class="cont">
		
			<div class="abtu1" style="background: #edf2fa; float: left;">
				<div class="log">
					<img src="images/logoWhite65.png" alt="Logo OOPS!"
						style="width: 120%;" />
				</div>
				<div class="autxt">
					<h3>OntOlogy Pitfall Scanner (OOPS!)</h3>
					<p>
						Modeling ontologies has become one of the main topics of research
						within ontological engineering because of the difficulties it involves.
						Developers must tackle a wide range of difficulties and handicaps when modelling ontologies
						that can imply the appearance of anomalies or errors in ontologies.
						Therefore, it is important to evaluate the ontologies in order to detect those potential problems.
						OOPS! is an online tool for evaluating ontologies,
						helps you to detect some of the most common pitfalls appearing when developing ontologies.
						This system is an independent tool from the ontology editor used
						and performs a greater number of automatic checks than the rest of the existing tools
						(33 of the 41 pitfalls defined in the catalog).
						The independence of specific editors as well as its simple interface make the system an accessible tool
						as well as easy to use and understand for users who are not experts in semantic technologies.
						In addition, the functionalities of OOPS! can be integrated into external systems through the available web service.
					</p>
				</div>
			</div>
	
			<div class="abtu1" style="background: white;float: right;">
	
				<div class="autxt">
					<h3>Ontology Engineering Group (OEG)</h3>
					<p>
						The Ontology Engineering Group (OEG) is based at the Computer Science School at Universidad Politécnica de Madrid (UPM).
						Founded in 1995, it ranks eighth among the two hundred research groups from UPM
						and it is widely recognised in Europe in the areas of Ontology Engineering, Semantic Infrastructure, Linked Data, and Data Integration.
						Our main research areas are: Ontological Engineering,
						(Social) Semantic Web and Linked Data, Natural Language,
						Semantic e-Science and the Future Internet.
						Maria Poveda-Villalón , assistant professor, Asunción Gómez-Pérez,
						full professor and Mari-Carmen Suárez-Figueroa, researchers of this group,
						have developed the OOPS! tool.
					</p>
				</div>
				<div class="log">
					<img src="images/logoOEG.png" alt="OEG logo" style=" width: 100%;" />
				</div>
			</div>
			
			<div class="abtu1" style="background: #edf2fa;float: left;">
				<div class="log">
					<img src="images/LogoETSIINF.png" alt="ESTIINF logo" style=" width: 110%;" />
				</div>
				<div class="autxt">
					<h3>Escuela Técnica Superior de Ingenieros Informaticos (ETSIINF)</h3>
					<p>
						In the Higher Technical School of Computer Engineering
						is where the ontological engineering research group mainly carries out its work.
						It is located on the Montegacedo campus,
						in Boadilla del Monte, Madrid, Spain.
						This school is the old computer science faculty of the UPM,
						it has taught computer science for more than 50 years.
						In this school there are several departments,
						including artificial intelligence,
						a department to which the OEG belongs.
					</p>
				</div>
			</div>
			
			<div class="abtu1" style="background: white;float: right;">
	
				<div class="autxt">
					<h3>Universidad Politécnica Madrid (UPM)</h3>
					<p>
						Universidad Politécnica de Madrid (UPM) is the oldest and largest of all Technical Universities in Spain.
						It has more than 3,000 faculty members,
						around 35,000 undergraduate students,
						and over 8,000 graduate students.
						UPM is made up of 21 Technical Schools and Faculties covering Architecture,
						Engineering, Sport and Fashion Design disciplines.<br>
						UPM has a strong commitment to Research and Innovation.
						Participation in competitive European and National research programs provides 40% of the sponsored research funds,
						and research services and contracts with the industry supply the rest.
					</p>
				</div>
				<div class="log">
					<img src="images/LogoUPM.png" alt="UPM logo"
						style="width: 100%;" />
				</div>
			</div>
	</div>
	<br> <br> <br> <br> <br> <br> <br> <br> <br>
	</div>

<jsp:include page="part-how-to-cite.jsp">
	<jsp:param name="logos" value="false"/>
</jsp:include>

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
