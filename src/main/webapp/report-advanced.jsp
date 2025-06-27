<%--
SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8"
	import="es.upm.fi.oeg.oops.*, java.util.*, java.util.function.Function, java.util.stream.Collectors, java.io.PrintStream, org.apache.jena.rdf.model.Resource, org.apache.jena.ontology.OntResource" errorPage=""%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

	<meta name="Author" content="María Poveda Villalón" />
	<meta name="Language" content="English" />
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

	<title>OOPS! - OntOlogy Pitfall Scanner! - Results </title>
	<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

</head>
<body>

<%@ include file="part-menu.html" %>

	<div id="wrap">
		<div id=main>
			<%!
			public String asLink(final Resource res) {
				return String.format("<a href=\"%s\" target=\"_blank\">%s</a>", res.getURI(), res.getLocalName());
			}
			%>
			<%///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			String uri =""; // Uri is never null
			// commenting the next line because we don't accept URI anymore
			// String param=request.getParameter("uri");
			// uri= uri+param;
			final String param2 = request.getParameter("RDF");
			final String rdf = param2; // rdf is never null
			final String button = request.getParameter("button");
			final String buttonLINK = request.getParameter("buttonLINK");
			final String save = request.getParameter("saveOntology");

			int pos = 0;

			final List<String> classKeys = new ArrayList<>();
			final List<String> classificationParam = new ArrayList<>();

			int tam = classKeys.size();

			boolean saveOntology = true;

			if (save == null) {
				saveOntology = false;
			}

			boolean byURI = false;
			boolean byRDF = false;
			boolean byLINK = false;
		/*
		if (uri != null) {
			byURI = true;
		}
		if (rdf != null) {
			byRDF = true;
		} */
			if (buttonLINK != null) {
				byLINK = true;
			}
			%>
			<br><br>

			<h1>Evaluation results</h1>

			<!-- Los parámetros que le pasa el cliente en la petición se
			obtienen del objeto implícito request -->
			<%
			//Clase model OWL con pWarningArrayList, cWarningArrayList, sugesstions, importFailing, totalP, numberCases
			// ModelOWL model = null;

			//	System.out.println("URI en response.jsp: "+ uri);
			//	System.out.println("RDF en response.jsp: "+ rdf);
			if (uri.isEmpty()) {
				if (rdf == null || rdf.isEmpty() || rdf.equals("null")) {
					//System.out.println("ni uri ni rdf");
					%>
					<p> Please, paste your RDF/XML source code in the box.
					</p>
					<%
				} else {
					byRDF = true; // If there is rdf and not uri
				}
			}

			final Map<PitfallCategoryId, Set<PitfallCategory.TreeChild>> pfCatTree = PitfallCategory.getTree();

			// commenting the next line because we don't accept URI anymore
			//else if (rdf.isEmpty() && !uri.isEmpty())byURI = true;//If there is uri and not rdf
			//else byURI=true;//if there is both, uri and rdf, it chooses uri
			//System.out.println("Los parametros que se van a mandar son: URI: "+uri+" mode:2 y SaveOntology:"+ saveOntology);
			if (byRDF || byURI) { // If there is ontology
				boolean excNoHttp = false;
				// boolean exc = false;
				// boolean exc2 = false;

				int pits = 0;

				final SrcSpec srcSpec;
				if (byRDF) {
					srcSpec = new SrcSpec(SrcType.RDF_CODE, null, rdf, null);
				} else {
					if (!uri.startsWith("http")){
						excNoHttp = true;
						srcSpec = null;
						System.out.println("The URI does not start with http or contains an attack");
					}
					else {
						srcSpec = new SrcSpec(SrcType.URI, uri, null, null);
					}
				}

				// if (byRDF) {	 // by RDF code is 1
				// 	ModelOWL model = new ModelOWL(rdf, 1, saveOntology);
				// 	//Object ModelOWL recibes: texto(uri o rdf), mode (1:rdf) and boolean if saves the ontology
				// 	//Al crearlo ya analiza??
				// 	//System.out.println("by rdf code");
				// 	exc = model.getExc();
				// 	exc2 = model.getExc2();
				// 	pits=model.getTotalP();

				// }

				// commenting the next lines because we don't accept URI anymore
				//	else if (byURI){ //for some reason by uri is 2
						//System.out.println("Los parametros que se van a mandar son: URI: "+uri+" mode:2 y SaveOntology:"+ saveOntology);
						//Aqui imprime: Los parametros que se van a mandar son: URI: http://oops.linkeddata.es/example/swc_2009-05-09.rdf mode:2 y SaveOntology:false POR QUE DA ERROR?????????????????????????
				//		if (!uri.startsWith("http")){
				//			excNoHttp = true;
				//			System.out.println("The uri does not start with http or contains an attack");
	
				//		}
				//		else {
				//			model = new ModelOWL(uri, 2, saveOntology); //if URI ModelOWL.mode: 2
				//			exc = model.getExc();
				//			exc2 = model.getExc2();
				//			pits=model.getTotalP();
				//		}
				//	}

// 				String importenceLevel = null;
				final String selectedClassification = request.getParameter("classification");
				String selectedEvaluation = request.getParameter("evaluation");

				final String[] pCheckIdsRaw = request.getParameterValues("pitfalls");
				final List<CheckerId> checkIds;
				if (pCheckIdsRaw != null) {
					checkIds = Arrays.stream(pCheckIdsRaw).map((str) -> new CheckerId(Integer.valueOf(str))).toList();
					selectedEvaluation = "pitfalls";
				} else {
					checkIds = null;
				}

// 				List<String> keysAux2 = new ArrayList<>();
				Map<String, String[]> parameters = request.getParameterMap();
				for (String parameter : parameters.keySet()) {
					//System.out.println("ln: parameter : "+parameter);
					if (parameter.contains("classification")) {
						selectedEvaluation = "category";
// 						keysAux2.add(parameter.replace("classification", ""));
					}
				}

				final Map<PitfallId, PitfallInfo> pitfallInfos;
				final Report report;
				if (srcSpec != null) {
					final Linter linter = new Linter();
					final SrcModel srcModel = ModelLoader.load(srcSpec);
					final List<Checker> checkers;
					if (checkIds == null) {
						checkers = CheckersCatalogue.getAllCheckers();
					} else {
						checkers = CheckersCatalogue.getCheckers(checkIds);
					}
					report = linter.partialExecution(srcModel, null, checkers);
					pitfallInfos = checkers.stream().map((checker) -> checker.getInfo().detectsPitfalls())
							.flatMap(Collection::stream).collect(Collectors.toMap(PitfallInfo::getId, Function.identity()));
				} else {
					report = null;
					pitfallInfos = null;
				}

				if (excNoHttp) {
					// Si salta la escepcion en el ModelOWL y no devuelve pitfalls, envía el mensaje de error
					%>

						<div class=sww>
							<div>
								<span>
									<a href="index.jsp"><img src="images/logoWhite65.png" alt="Logo OOPS!" style= "width: 110%;" /></a>
								</span>
							</div>
							<div>
								<h1>Something went wrong.</h1>
							</div>
							<br>
						</div>
						<div class=txt>
							<div>If you have entered a URI make sure that it is available on-line using, for example, <a href="http://validator.linkeddata.org/vapour">Vapour</a>
						or <a href="http://www.hyperthing.org/">Hyperthing</a>.<br>
						<br>Also make sure that your ontology RDF or OWL code is correct using an RDF validator as <a href="http://www.w3.org/RDF/Validator/">http://www.w3.org/RDF/Validator/</a>..
						<br><br>If the URI and the ontology are correct but OOPS! is not able to analyze it, please,
						let us know by email (oops(at)delicias.dia.fi.upm.es) attaching the file or indicating the ontology URI and
						we will find out what was wrong within OOPS!.</div></div><br>

					<%
				} else if (report == null || (!report.getExceptions().isEmpty() && report.getPitfalls().isEmpty())) {
					//Si salta la excepcion en el ModelOWL y no devuelve pitfalls, envía el mensaje de error
					%>
					<div class=sww>
						<div>
							<span>
								<a href="index.jsp"><img src="images/logoWhite65.png" alt="Logo OOPS!" style= "width: 110%;" /></a>
							</span>
						</div>
						<div>
							<h1>Something went wrong.</h1>
						</div>
						<br>
					</div>
					<div class=txt>
						<div>If you have entered a URI make sure that it is available on-line using, for example, <a href="http://validator.linkeddata.org/vapour">Vapour</a>
					or <a href="http://www.hyperthing.org/">Hyperthing</a>.<br>
					<br>
					Also make sure that your ontology RDF or OWL code is correct using an RDF validator
					as <a href="http://www.w3.org/RDF/Validator/">http://www.w3.org/RDF/Validator/</a>.
					<br>
					<br>
					If the URI and the ontology are correct but OOPS! is not able to analyze it, please,
					let us know by email (oops(at)delicias.dia.fi.upm.es) attaching the file or indicating the ontology URI and
					we will find out what was wrong within OOPS!.</div></div><br>
					<%
				} else {
					final Map<PitfallId, List<Pitfall>> pitfalls = report.getPitfalls();
					// final HashMap <String, Integer> cases = model.getNumberCases();

					// Pitfalls pit = new Pitfalls();

					// String keys [] = pit.getKeys();
					// String titles [] = pit.getTitles();
					// String explanations [] = pit.getExplanations();
					// List<Integer> checkIds = new ArrayList<>();
					// List<String> titlesAux = new ArrayList<String>();
					// List<String> explanationsAux = new ArrayList<String>();
					boolean none = true;
					if (selectedEvaluation == null) {
						selectedEvaluation = "none";
						none = true;
					}
					if (selectedEvaluation.equalsIgnoreCase("pitfalls")) {
						// Para pitfalls, no cambia
// 						checkIds = Arrays.asList(select);

// 						for (int i = 0; i < keys.length; i++) {
// 							if (checkIds.contains(keys[i])) {
// 								titlesAux.add(titles[i]);
// 								explanationsAux.add(explanations[i]);
// 							}
// 						}
					} else if (selectedEvaluation.equalsIgnoreCase("category")) {
						for (final String category : parameters.get("classification")) {
							final PitfallCategoryId selectedCategoryId = PitfallCategoryId.parse(category);
							final Set<PitfallId> pitfallIds = PitfallCategory.getLeaves(selectedCategoryId);
							checkIds.addAll(pitfallIds.stream().map(PitfallId::generateCheckerId).collect(Collectors.toSet()));
							Collections.sort(checkIds);
						}
					}
					if (!pitfalls.isEmpty()) {
						none = false;
					}

					if (none) {
						%>
						<div class=sww>
							<div>
								<h1>Congratulations! No pitfalls detected.</h1>
							</div>
						</div>
						<br>
						<div class=txt>
							<div>
								Your ontology does not contain any bad practice detectable by
								OOPS!. Remember that there are pitfalls that depend on the
								domain being modeled or the requirements specified for each
								particular ontology. Up to now, OOPS! can identify
								semi-automatically those pitfalls in the catalog with the title
								in <strong>bold</strong>. We encourage you to keep an eye of those
								pitfalls that OOPS! is not able to detect yet. It is a good idea to
								revise the ontology manually looking for them.<br> If your
								ontology is free of errors, you can use the following conformance
								badge in your ontology documentation: <br><br>
								<a href="index.jsp"><img
									src="images/conformance/oops_free.png" style="float:initial;" alt="Free of pitfalls"
									height="69.6" width="100" /></a>
								<br>
								You can use the following HTML code:<br>
								<br>
								<pre><code>&lt;a href="http://oops.linkeddata.es"&gt;&lt;img <br>
									src="images/conformance/oops_free.png"<br>
									alt="Free of pitfalls" height="69.6" width="100" /&gt;</code>
								</pre>
							</div>
						</div>
						<%
					} else {
						%>
					<br>
					<div class="txt">
						There are three levels of importance in pitfalls according to
							their impact on the ontology:
						<ul>
							<li>
								<span class="badge bg-danger" >Critical</span>
								It is crucial to correct the pitfall. Otherwise, it could affect
								the ontology consistency, reasoning, applicability, etc.
							</li>
							<li>
								<span class="badge" style="background-color: #ff8000 !important;">Important</span>
								Though not critical for ontology function, it is important to correct this type of pitfall.
							</li>
							<li>
								<span class="badge bg-warning" >Minor</span>
								It is not really a problem, but by correcting it we will make the ontology nicer.
							</li>
						</ul>
					</div>
					<br>
					<h3>Pitfalls detected:</h3>
					<br>
					<div class="accordion" id="accordionPanelsStayOpenExample">
						<%
						Importance highestTriggeredImportance = null;
						for (final Map.Entry<PitfallId, List<Pitfall>> pfEntry : pitfalls.entrySet()) {
	// 						String key = keys[i];
	// 						String title = titles[i];
	// 						String explanation = explanations[i];
							boolean divs = false;
							final PitfallId pfId = pfEntry.getKey();
							final Integer key = pfId.getNumeral();
							final PitfallInfo info = pitfallInfos.get(pfId);
							final String title = Utils.escapeForHtml(info.getTitle());
							final String explanation = Utils.escapeForHtml(info.getExplanation());
							final List<Pitfall> pfs = pfEntry.getValue();

							divs = true;
							%>

						<div class="accordion-item">
						<h2 class="accordion-header" id="panelsStayOpen-heading<% out.print(pfId); %>">
							<button class="accordion-button collapsed" style="display:inline;" type="button"
								data-bs-toggle="collapse"
								data-bs-target="#panelsStayOpen-collapse<% out.print(pfId); %>"
								aria-expanded="false"
								aria-controls="panelsStayOpen-collapse<% out.print(pfId); %>">
							<%
							out.println("		Results for " + pfId + ": " + title + ".");
							final Importance importance = info.getImportance();
							if (highestTriggeredImportance == null || highestTriggeredImportance.compareTo(importance) < 0) {
								highestTriggeredImportance = importance;
							}

							switch (importance) {
							case CRITICAL:
								%><span class="badge bg-danger" style="float: right;">Critical</span><%
								break;
							case IMPORTANT:
								%><span class="badge" style="background-color: #ff8000 !important;float: right;">Important</span><%
								break;
							case MINOR:
								%><span class="badge bg-warning" style="float: right;">Minor</span><%
								break;
							default:
								throw new IllegalStateException();
							}
							final String scopeText;
							if (info.getScope() == RuleScope.ONTOLOGY) {
								scopeText = "Ontology*";
							} else if (pfs.size() == 1) {
								scopeText = "1 case";
							} else {
								scopeText = pfs.size() + " cases";
							}
							%>
							<span style="float: right; padding-right:15px;"><% out.print(scopeText); %></span>
							</button>
					</h2>
					<div id="panelsStayOpen-collapse<%out.print(pfId);%>"
						class="accordion-collapse collapse"
						aria-labelledby="panelsStayOpen-heading<%out.print(pfId);%>">
						<div class="accordion-body">
							<%out.println(explanation);%>
							<br>
						<%
						// las que aplican a la onto en general
						if (info.getScope() == RuleScope.ONTOLOGY) {
							%>
							<br>
							*This pitfall applies to the ontology in general instead of specific elements.
							<br>
							<%
						}

						// las que aplican a un solo elemento, se muestra la lista, no pares.
						else {
							// TODO Extend this, covering all the cases dictated by PitfallInfo
							%>
							<br>

							&bull; This pitfall appears in the following elements:

							<br>
							<%
							for (final Pitfall pf : pfs) {
								for (final Resource res : pf.getResources()) {
								final String linkRes = asLink(res);
								%> &rsaquo; <%out.print(linkRes);%>
								<br>
								<%
							}
						}
					} // divs below: Acordion item content end

					if (divs) {
						%>
						</div>
					</div>
				</div>
						<%
						divs = false;
					} // <!-- Acordion item end -->FIN DEL ELEMENTO DEL ACORDEON---------------------------------------------------------------------------------------------------------
				} // For end

				%>
			</div> <!-- Acordion end -->








 					<%
					if (report != null && !report.getWarnings().isEmpty()) {
						final Map<WarningType, List<Warning>> warnings = report.getWarnings();
	 					%>
						<br>
						<h3>Warnings:</h3>
						<br>
						<div class="accordion" id="accordionPanelsStayOpenExample">
	 					<%
	 					for (final Map.Entry<WarningType, List<Warning>> wEntry : warnings.entrySet()) {
	 						final WarningType wType = wEntry.getKey();
	 						final List<Warning> wTWarnings = wEntry.getValue();
		 					%>
							 <div class="accordion-item">
								<h2 class="accordion-header" id="panelsStayOpen-headingSug">
									<button class="accordion-button collapsed" type="button"
										data-bs-toggle="collapse"
										data-bs-target="#panelsStayOpen-collapseSug"
										aria-expanded="false" aria-controls="panelsStayOpen-collapseSug">
										<%out.print(wType);%> related warnings
										<span style="float: right; ">
							|
	 						<%
							out.println(wTWarnings.size());
							out.print(" case");
							if (wTWarnings.size() > 1) {
								out.println("s");
							}
	 						%>
												</span>
											</button>
									</h2>
									<div id="panelsStayOpen-collapseSug"
										class="accordion-collapse collapse"
										aria-labelledby="panelsStayOpen-headingSug">
										<div class="accordion-body">
<!-- 												The domain and range axioms are equal for each of the following object properties. Could they be symmetric or transitive? -->
	
<!-- 											<br> -->
							<%
							for (final Warning warning : wTWarnings) {
								final CheckerInfo wCheckerInfo = warning.getCheckerInfo();
								final CheckerId wCheckerId = wCheckerInfo.getId();
								final String wText = Utils.escapeForHtml(warning.toString());
								final Set<OntResource> scope = warning.getScope();
								out.print(wText);
								%>
								<br>
								<br>
								Origin: Checker <%out.print(wCheckerId);%><br>
								<%
								if (!scope.isEmpty()) {
									%>
								<ul>
									<%
									for (final OntResource wRes : scope) {
									%><li><%out.print(asLink(wRes));%></li><%
									}
									%>
								</ul>
									<%
								}
							}
							%>
										</div>
									</div>
								</div>
							<%
	 					}
					}
					%>
						</div>






				<br>
				<br>
				<div class=txt>
					<p>
						According to the highest importance level of pitfall found in your
						ontology, the conformance badge suggested is "<%out.print(highestTriggeredImportance);%>
						pitfalls" (see below). You can use the following HTML code to insert
						the badge within your ontology documentation:
					</p>
					<div class=codeLogo>
						<a href="http://oops.linkeddata.es"><img
							src="images/conformance/oops_<%out.print(highestTriggeredImportance.name().toLowerCase());%>.png"
							alt="<%out.print(highestTriggeredImportance);%> pitfalls were found" height="69.6"
							width="100" /></a><br>
						<div class=code>
							<pre>
&lt;p&gt;
&lt;a href="http://oops.linkeddata.es"&gt;
&lt;img src="http://oops.linkeddata.es/resource/image/oops_<%out.print(highestTriggeredImportance.name().toLowerCase());%>.png"
alt="<%out.print(highestTriggeredImportance);%> pitfalls were found" height="69.6" width="100" /&gt;&lt;/a&gt;
&lt;/p&gt;</pre>
						</div>
					</div>

				</div>

<%@ include file="part-references.html" %>

				<br>

					<%
					}
				}
 			}
			%>
			<br>

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
				<p style="font-size: 10px;"><input type="checkbox" name="saveOntology" value="saveOntology">
					Uncheck this checkbox if you don't want us to keep a copy of your ontology.
				</p>

				<br>
				<div id="selCat">
					<div>
						<input type="button" name="evaluation" id="pitfall" value="Select Pitfalls for Evaluation" onclick="showPit()">
					</div>
					<div>
						<input type="button" name="evaluation" id="categoryDim" value="Classification by Dimension" onclick="showDim()">
					</div>
<!-- 					<div> -->
<!-- 						<input type="button" name="evaluation" id="categoryEvCirt" value="Classification by Evaluation Criteria" onclick="showEC()"> -->
<!-- 					</div> -->
				</div>
				<br>
				<br>
				<div id="pitfall_selectors" style="display: none">
					<p class="pits">
				<%
				int pInfoIdx = 0;
				for (final PitfallInfo pInfo : CheckersCatalogue.getPitfallInfosAll()) {
					final PitfallId pId = pInfo.getId();
					final String pTitle = Utils.escapeForHtml(pInfo.getTitle());
					%>
					<span class="pittab" title="<%out.print(pId);%>: <%out.print(pTitle);%>">
						 <input type="checkbox" id="pitcheck_<%out.print(pId);%>" name="pitfalls" value="<%out.print(pId);%>"> <% out.print(pId);%>
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
						<div style="justify-content:center;padding:10px;">
							<span class="pittab">
								<input type="button" name="CheckAll" value="Select All" onClick="checkAll(document.form1.pitfalls)">
							</span>
						</div>
						<div style="justify-content:center;padding:10px;">
							<span class="pittab">
								<input type="button" name="UncheckAll" value="Clear All" onClick="uncheckAll(document.form1.pitfalls)">
							</span>
						</div>
					</div>
					<br>
				</div>

				<div id="pitfall_categories_selectors_n" style="display: none" class="classifications">

				<%
				final PitfallCategory.TreeChild root = pfCatTree.get(null).iterator().next();
				%>
					<div id="accordion-wrap" style=width:500px;>
					<div class="accordion" id="accordionExample">
				<%
				for (final PitfallCategory.TreeChild cNode : pfCatTree.get(root.getCategory())) {
					for (final PitfallCategory.TreeChild sNode : pfCatTree.get(cNode.getCategory())) {
						final PitfallCategoryId sCatId = sNode.getCategory();
						final PitfallCategory sCat = PitfallCategory.get(sCatId);
						final String sCatTitle = Utils.escapeForHtml(sCat.getTitle());
						%>

						<div class="accordion-item">
							<h2 class="accordion-header" id="headingOne">
							<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
							<input type="checkbox" id="<%out.print(sCatId);%>" value="<%out.print(sCatId);%>" name="classification">
							<label for="<%out.print(sCatId);%>"><%out.print(sCatTitle);%></label>
							</button>
							</h2>
							<div id="collapseOne" class="accordion-collapse collapse" aria-labelledby="headingOne" data-bs-parent="#accordionExample">
								<div class="accordion-body">

						<%
						for (final PitfallCategory.TreeChild nOrPNode : pfCatTree.get(sCatId)) {
							final String nOrPIdStr;
							final String nOrPTitle;
							final String nOrPDescription;
							if (nOrPNode.isCategory()) {
								final PitfallCategoryId nCatId = nOrPNode.getCategory();
								final PitfallCategory nCat = PitfallCategory.get(nCatId);
								nOrPIdStr = nCatId.toString();
								nOrPTitle = Utils.escapeForHtml(nCat.getTitle());
								nOrPDescription = Utils.escapeForHtml(pfCatTree.get(nCatId).stream().map(PitfallCategory.TreeChild::getPitfall).map(PitfallId::toString).collect(Collectors.joining(", ")));
							} else {
								final PitfallId pId = nOrPNode.getPitfall();
								final PitfallInfo pInfo = CheckersCatalogue.getPitfallInfo(pId);
								nOrPIdStr = pId.toString();
								nOrPTitle = Utils.escapeForHtml(pInfo.getTitle());
								nOrPDescription = Utils.escapeForHtml(pInfo.getExplanation());
							}
							%>
									<input type="checkbox" id="<%out.print(nOrPIdStr);%>" value="<%out.print(nOrPIdStr);%>" name="classification">
									<label for="<%out.print(nOrPIdStr);%>"><strong><%out.print(sCatTitle);%></strong> - <%out.print(nOrPDescription);%></label>
									<br>
									<br>
							<%
						}
						%>
								</div>
							</div>
						</div>
						<%
					}
				}
				%>
					</div>
					</div>
				</div>

				<br>
				<input type="submit" class="button" name="button" value="Scan" onclick="doValidation()">
				<br>
				<input type="submit"
					class="submitLink" name="buttonLINK"
					onclick='this.form.action="advanced.jsp";'
					value="Advanced evaluation">
				<br>
			</form>

		</div>
	</div>

<jsp:include page="part-how-to-cite.jsp" />

	<script type="text/javascript">
		function showPit() {
			document.getElementById('pitfall_selectors').style.display = 'block';
			document.getElementById('pitfall_categories_selectors_n').style.display = 'none';
			document.getElementById('pitfall_categories_selectors_s').style.display = 'none';
		}
		function showDim() {
			document.getElementById('pitfall_categories_selectors_n').style.display = 'block';
			document.getElementById('pitfall_selectors').style.display = 'none';
			document.getElementById('pitfall_categories_selectors_s').style.display = 'none';
		}
		function showEC() {
			document.getElementById('pitfall_categories_selectors_s').style.display = 'block';
			document.getElementById('pitfall_selectors').style.display = 'none';
			document.getElementById('pitfall_categories_selectors_n').style.display = 'none';
		}
	</script>

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
