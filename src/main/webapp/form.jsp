<%--
SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8" import="es.upm.fi.oeg.oops.*,
  java.util.*" errorPage=""%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="Author" content="María Poveda Villalón" />
	<meta name="Languaje" content="English" />
	<meta name="Keywords" content="ontology, ontology evaluation, pitfalls" />
	<meta name="Description" content="This web provide online access to OOPS! - OntOlogy Pitfall Scanner!" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
	<link href="css/style.css" rel="stylesheet" type="text/css" />
	<title>OOPS! - OntOlogy Pitfall Scanner! - Feedback</title>

</head>
<body>

<%@ include file="part-menu.html" %>

	<div id="wrap">
		<div id=main>
			<h1>Feedback</h1>
			<br> 
			<div id="accordion-wrap">
			<p style="text-align:justify;">Thank you for helping us to improve OOPS! Let us know which
				features would you add, whether it has been useful or the bugs you
				have encountered while using OOPS!. Please, fill in the form above
				and press the "Submit" button when you have finished.</p>
				</div>
			<br>
			<iframe
				src="https://docs.google.com/spreadsheet/embeddedform?formkey=dEFuYWVpeUJvcXFxOXBOM3NsOW8zYkE6MA"
				width="100%" height="4265" frameborder="0" marginheight="0"
				marginwidth="0">Loading...</iframe>

		</div>
	</div>

<jsp:include page="part-how-to-cite.jsp" />

<%@ include file="part-footer-scripts.html" %>

</body>

</html>
