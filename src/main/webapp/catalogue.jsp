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

	<title>OOPS! - OntOlogy Pitfall Scanner! - Pitfall Catalog</title>

</head>
<body>

<%@ include file="part-menu.html" %>

	<div id="wrap">
		<div id=main>
			<h1>Pitfall Catalog</h1>
			<br> 
			
			<div class="npitmenu" style= "z-index:100" >
				<div class="nav-item">
					<a class="nav-link" href="#main">1-10</a>
				</div>
				<div class="nav-item">
					<a class="nav-link" href="#panelsStayOpen-heading9">11-20</a>
				</div>
				<div class="nav-item">
					<a class="nav-link" href="#panelsStayOpen-heading19">21-30</a>
				</div>
				<div class="nav-item">
					<a class="nav-link" href="#panelsStayOpen-heading29">31-41</a>
				</div>
				<div class="nav-item">
					<a class="nav-link" href="#references">References</a>
				</div>
			</div>
			
			 <br>
			<div id="accordion-wrap" data-bs-spy="scroll" data-bs-target="#navbar-example2" data-bs-spy="scroll"  data-bs-offset="0" class="scrollspy-example" tabindex="0">
				
				<div class="accordion" id="accordionPanelsStayOpenExample">
					<a href="javascript:void(0)" class="toggle-accordion active" accordion-id="#accordion"></a>
					<%
					final List<PitfallInfo> pitfallInfos = CheckersCatalogue.getPitfallInfosAll();
					for (final PitfallInfo pInfo : pitfallInfos) {
						final PitfallId pfId = pInfo.getId();
						final int pfIdNum = pfId.getNumeral();
						final Integer key = pfId.getNumeral();
						final String title = pInfo.getTitle();
						final String explanation = pInfo.getExplanation();
						final Importance importanceLevel = pInfo.getImportance();
						%>
					<div class="accordion-item">
						<h2 class="accordion-header" id="panelsStayOpen-heading<%out.print(pfId);%>">
							<button class="accordion-button collapsed" style="display:inline;" type="button"
								data-bs-toggle="collapse"
								data-bs-target="#panelsStayOpen-collapse<%out.print(pfId);%>"
								aria-expanded="false" aria-controls="panelsStayOpen-collapse<%out.print(pfId);%>">
									<%
									if (List.of(1, 9, 14, 15, 16, 17, 18).contains(pfIdNum)) {
										out.print(pfId + ". " + title);
									} else if (pfIdNum == 3) {
										out.print("<strong>" + pfId + ". Creating the relationship \"is\" instead of using <br> \"rdfs:subClassOf\", \"rdf:type\" or \"owl:sameAs \" </strong>");
									} else if (pfIdNum == 23)
									{
										out.print(pfId + ". Duplicating a datatype already provided by the implementation language");
									} else {
										out.print("<strong>" + pfId + ". " + title + "</strong>");
									}

									switch (importanceLevel) {
									case CRITICAL:
										%><span class="badge bg-danger"  style="float: right;">Critical</span><%
										break;
									case IMPORTANT:
										%><span class="badge" style="background-color: #ff8000 !important; float: right;">Important</span><%
										break;
									case MINOR:
										%><span class="badge bg-warning"  style="float: right;">Minor</span><%
										break;
									default:
										throw new UnsupportedOperationException();
									}
									%>

								</button>
						</h2>
						<div id="panelsStayOpen-collapse<%out.print(pfId);%>"
							class="accordion-collapse collapse"
							aria-labelledby="panelsStayOpen-heading<%out.print(pfId);%>">
							<div class="accordion-body">
								<%out.println(explanation);%>
							</div>
						</div>
					</div>
					<%
					}
					%>
				</div>

<%@ include file="part-references.html" %>

			</div>
		</div>
	</div>
	<br>
	<br>

<jsp:include page="part-how-to-cite.jsp" />

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
