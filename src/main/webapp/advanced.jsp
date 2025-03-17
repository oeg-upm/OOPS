<%--
SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8" import="es.upm.fi.oeg.oops.*, java.util.*" errorPage=""%>

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

	<title>OOPS! - OntOlogy Pitfall Scanner!</title>

</head>
<body>

<%@ include file="part-menu.html" %>

	<div id="load" class="loading">
		<div id="loadElement">
		</div>
		<br>
	</div>
	<div id="wrap">
		<div id=main>
			<h1>Advanced Evaluation</h1>
			
			<br><div class="txt">
					<p>If you want to use OOPS! to scan an ontology from it URI you can use our  <a href="https://hub.docker.com/r/mpovedavillalon/oops" target="_blank">OOPS! Docker Image</a> just running:</p>
					<ul>
						<li><b>docker run -p 80:8080 mpovedavillalon/oops:v1 </b></li>
						<li>Download and mount Wordnet and run: docker run -v ./WordNet:/usr/local/tomcat/WordNet -p 80:8080 mpovedavillalon/oops:v1   </li>
						<li>Then go to http://localhost/OOPS/ </li>
					</ul>
					
					<p>You can also download the image using: <b>docker pull mpovedavillalon/oops:v1</b></p>
				</div>
				
			
			<br>
			<p>Enter your ontology to scan:</p>

			<%
			//String uri = request.getParameter("uri");
			final String rdf = request.getParameter("RDF");
			final String button = request.getParameter("button");
			final String buttonLINK = request.getParameter("buttonLINK");

			boolean byURI = false;
			boolean byRDF = false;
			boolean byLINK = false;

			// removing the option to include an URI as paremeter
			//if (uri != null) {
			//	byURI = true;
			//} else 
			if (rdf != null) {
				byRDF = true;
			} else if (buttonLINK != null) {
				byLINK = true;
			}
			%>

			<form name="form1" id="form1" method="post" action="report-advanced.jsp">

				<!--	<input type="text" name="uri" id="uriEx"
						placeholder="	Enter a URI:"<%//out.println(uri);%>>
					<div id=example>
						<p
							onclick="example1('http://oops.linkeddata.es/example/swc_2009-05-09.rdf')">
							Example: http://oops.linkeddata.es/example/swc_2009-05-09.rdf</p>
					</div> -->


					<textarea class="medio" cols="85" rows="8" name="RDF"
						placeholder="
	Enter a direct input:&#10;&#10;	If you include just RDF code, the following Pitfalls will not be checked:&#10;		P36. URI contains file extension&#10;		P37.Ontology not available&#10;		P40. Namespace hijacking"><%if ((byLINK || byRDF)){out.println(rdf);}%></textarea>

				<p style="font-size: 10px;"><input type="checkbox" name="saveOntology" value="saveOntology" > Uncheck this checkbox if you don't want us to keep a copy of your ontology.</p>

				<br>
				<div id="selCat">
					<div><input type="button" name="evaluation" id="pitfall" value="Select Pitfalls for Evaluation" onclick="showPit()"> </div>
					<div><input type="button" name="evaluation" id="categoryDim" value="Classification by Dimension" onclick="showDim()"></div>
					<div><input type="button" name="evaluation" id="categoryEvCirt" value="Classification by Evaluation Criteria" onclick="showEC()"></div>
				</div>
				<br><br>
				<div id="div1" style="display: none">
					<p class="pits">
				<%
				int pInfoIdx = 0;
				for (final PitfallInfo pInfo : CheckersCatalogue.getPitfallInfosAll()) {
					%>
					<span class="pittab" title="<%out.print(pInfo.getId());%>: <%out.print(pInfo.getTitle());%>">
						 <input type="checkbox" id="pitcheck_<%out.print(pInfo.getId());%>" name="pitfalls" value="<%out.print(pInfo.getId());%>"> <% out.print(pInfo.getId());%>
					</span>
					<%
					if (((pInfoIdx + 1) % 10) == 0) {
						%><br><%
					}
					pInfoIdx++;
				}
				%>
					<br>
					<div id="selCat" style="justify-content:center;">
						<div style="justify-content:center;padding:10px;"><span class="pittab"><input type="button" name="CheckAll" value="Select All" onClick="checkAll(document.form1.pitfalls)"></div>
						<div style="justify-content:center;padding:10px;"><input type="button" name="UncheckAll" value="Clear All" onClick="uncheckAll(document.form1.pitfalls)"></span></div>
					</div><br>

				</div>

				<div id="div2" style="display: none" class="classifications" style="width:700px">

					<div id="accordion-wrap" style="display: flex;justify-content: center;">
					<div class="accordion" id="accordionExample">
				
				<input type="submit" class="button" name="button" value="Scan" onclick="doValidation()">

				<input type="submit"
					class="submitLink" name="buttonLINK"
					onclick='this.form.action="index.jsp";'
					value="Simple evaluation">
				<br>

			</form>
		</div>
	</div>
	</div>

<jsp:include page="part-how-to-cite.jsp" />

	<script type="text/javascript">
		function showPit() {
			document.getElementById('div1').style.display = 'block';
			document.getElementById('div2').style.display = 'none';
			document.getElementById('div3').style.display = 'none';
		}
		function showDim(){
			document.getElementById('div2').style.display = 'block';
			document.getElementById('div1').style.display = 'none';
			document.getElementById('div3').style.display = 'none';
		}
		function showEC(){
			document.getElementById('div3').style.display = 'block';
			document.getElementById('div1').style.display = 'none';
			document.getElementById('div2').style.display = 'none';
		}
	</script>

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
