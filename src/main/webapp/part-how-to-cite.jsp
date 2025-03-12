<%--
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
--%>

<%@ page contentType="text/html; charset=utf-8" import="java.util.Set" errorPage=""%>

	<div id="htc">
		<div id="wrap">
			<h3>How to cite OOPS!</h3>
			<br>
			<p>
				Poveda-Villalón, María;  Gómez-Pérez, Asunción; and Suárez-Figueroa, Mari Carmen.<br>
				"OOPS! (OntOlogy Pitfall Scanner!): An on-line tool for ontology evaluation."<br>
				International Journal on Semantic Web and Information Systems (IJSWIS) 10.2 (2014): 7-34.
			</p>
			<p>BibTex:</p>

			<div class="code">
				<pre>
@article{poveda2014oops,
	title={{OOPS! (OntOlogy Pitfall Scanner!): An On-line Tool for Ontology Evaluation}},
	author={Poveda-Villal{\'o}n, Mar{\'i}a and G{\'o}mez-P{\'e}rez, Asunci{\'o}n and Su{\'a}rez-Figueroa, Mari Carmen},
	journal={International Journal on Semantic Web and Information Systems (IJSWIS)},
	volume={10},
	number={2},
	pages={7--34},
	year={2014},
	publisher={IGI Global}
}</pre>
			</div>
		</div>
		<br>
	</div>
<%
final Set<String> FALSE_VARIANTS_LOWER = Set.of("false", "no", "0");
final String logos = request.getParameter("logos");
final boolean showLogos = (logos == null) || !FALSE_VARIANTS_LOWER.contains(logos.toLowerCase());
if (showLogos) {
%>
	<div>
		<br> <br>
		<div id="logos" class="logos">
			<div>
				<span>
					<a href="https://oeg.fi.upm.es/" target="_blank"><img
						src="images/logoOEG.png" alt="OEG logo"
						style="width: 78%;" /></a>
				</span>
			</div>
			<div>
				<div>
					<span>
						<a href="https://www.fi.upm.es/" target="_blank"><img
							src="images/LogoETSIINF.png" alt="ESTIINF logo"
							style="width: 78%;" /></a>
					</span>
				</div>
				<div>
					<p>
						<br>
						Escuela Técnica<br>
						Superior de<br>
						Ingenieros Informáticos
					</p>
				</div>
			</div>
			<div>
				<div>
					<span>
						<a href="https://www.upm.es/" target="_blank">
						<img src="images/LogoUPM.png" alt="UPM logo" style="width: 78%;" /></a>
					</span>
				</div>
				<div>
					<p>
						<br>
						Universidad<br>
						Politécnica<br>
						de Madrid
					</p>
				</div>
			</div>
		</div>
<%
}
%>
		<div id="contact">
			<p>
				<a href="http://purl.org/net/mpoveda" target="_blank">María Poveda</a>
			</p>
			<p>
				Contact email:
					oops(at)delicias.dia.fi.upm.es
			</p>
		</div>
	</div>
