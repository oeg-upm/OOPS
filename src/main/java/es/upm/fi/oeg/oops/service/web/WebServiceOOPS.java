/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.service.web;

import es.upm.fi.oeg.oops.Checker;
import es.upm.fi.oeg.oops.CheckersCatalogue;
import es.upm.fi.oeg.oops.Linter;
import es.upm.fi.oeg.oops.ModelLoader;
import es.upm.fi.oeg.oops.Pitfall;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.Report;
import es.upm.fi.oeg.oops.RunSettings;
import es.upm.fi.oeg.oops.SrcModel;
import es.upm.fi.oeg.oops.SrcSpec;
import es.upm.fi.oeg.oops.SrcType;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;
import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

@Path("")
public class WebServiceOOPS {

    private final Logger logger = LoggerFactory.getLogger(WebServiceOOPS.class);

    // // This method is called if TEXT_PLAIN is request
    // @GET
    // @Produces(MediaType.TEXT_PLAIN)
    // public String sayPlainTextHello(@QueryParam("url") String url) {
    // String output = "";
    // System.out.println("LA URL DE ENTRADA ES:::" + url);
    // String urlReq = "http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf";
    // if (url != null && !url.equals(""))
    // urlReq = url;
    // try {
    // // ModelOWL model = new
    // // ModelOWL("http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf",
    // // 0);
    // ModelOWL model = new ModelOWL(urlReq, 0);
    // model.getExc();
    // System.out.println("Nº of pitfalls:" + model.getTotalP());
    // output = "Nº of pitfalls:" + model.getTotalP() + "\n";
    // HashMap<Object, Object> results = model.getResults();
    // for (Object key : results.keySet()) {
    // Object value = results.get(key);
    // System.out.println(key + "::" + value);
    // output = output + key + "::" + value + "\n";
    // }
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return output;
    // }

    // elimino esto para que no sature el servidoro creo q
    // @GET
    // public static String webServiceOOPSPost() {
    // String output = "<html><head>" +
    // "<meta name=\"Author\" content=\"Míguel Ángel García Delgado\"/>"+
    // "<meta name=\"Language\" content=\"English\"/>"+
    // "<meta name=\"Keywords\" content=\"ontology, ontology evaluation, pitfalls, web service\"/>"+
    // "<meta name=\"Description\" content=\"This web provide access via web service to OOPS! - OntOlogy Pitfall
    // Scanner!\"/>"+
    // "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"+
    // "<title>OOPS! - OntOlogy Pitfall Scanner! Web Service</title></head>";
    // output = output + "<body><h1>HTTP GET REQUEST NOT ALLOWED</h1>" +
    // "<p>For accessing the OOPS! Web Service, use a <b>HTTP POST request</b> using this URL. The body of the request
    // must be an XML with the following structure:</p>";
    // output = output + "<p>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;<br>"+
    // "&lt;OOPSRequest&gt;<br>"+
    // "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;OntologyUrl&gt;http://www.cc.uah.es/ie/ont/learning-resources.owl&lt;/OntologyUrl&gt;<br>"+
    // "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;OntologyContent&gt;&lt;/OntologyContent&gt;<br>"+
    // "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Pitfalls&gt;10&lt;/Pitfalls&gt;<br>"+
    // "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;OutputFormat&gt;&lt;/OutputFormat&gt;<br>"+
    // "&lt;/OOPSRequest&gt;</p>";
    // output = output + "</body></html>";
    // return output;
    // }

    private static boolean isNullOrEmpty(final String str) {
        return str == null || str.isEmpty();
    }

    private String req(final RunSettings runSettings,
            final IOAndRestResponseExceptionFunction<Report, String> serializer) throws RestResponseException {

        try {
            final String auxUrl = runSettings.getUrl();
            if (auxUrl != null && !auxUrl.isEmpty()) {
                // commenting next line and exiting when asking for URL,
                // this execution is not longer allowed
                // urlReq = auxUrl;
                throw RestResponseException.createRequestByUriNotAllowed();
            }

            final String auxContent = runSettings.getContent();
            if (isNullOrEmpty(auxUrl) && isNullOrEmpty(auxContent)) {
                throw RestResponseException.createInvalidArguments();
            }

            if (!auxUrl.startsWith("http") && auxContent == null) {
                throw RestResponseException.createNonHttpUri();
            }

            final List<Integer> optionsList = new ArrayList<Integer>();
            final SrcSpec srcSpec;
            if (auxContent.isEmpty()) {
                srcSpec = new SrcSpec(SrcType.URI, auxUrl, null, null);
            } else {
                srcSpec = new SrcSpec(SrcType.RDF_CODE, null, auxContent, null);
            }
            final Linter executor = new Linter();
            final SrcModel srcModel = ModelLoader.load(srcSpec);
            final List<Checker> allCheckers = CheckersCatalogue.getAllCheckers();
            final Report report = executor.partialExecution(srcModel, optionsList, allCheckers);
            if (report.getExceptions().isEmpty()) {
                try {
                    return serializer.apply(report);
                } catch (final IOException exc) {
                    logger.error("Failed to serialize output", exc);
                    throw RestResponseException.createFailedToSerializeOutput();
                }
            } else {
                throw RestResponseException.createErrorWhileAnalyzing();
            }
        } catch (final IOException exc) {
            throw RestResponseException.createBadInput();
        } catch (final OWLOntologyCreationException exc) {
            throw RestResponseException.createBadInput();
        }
    }

    // @POST
    // @Consumes(MediaType.APPLICATION_XML)
    // @Produces(MediaType.TEXT_XML)
    // @Produces({"application/rdf+xml; charset=UTF-8","application/xml; charset=UTF-8"})
    private static String toText(final Report report) throws RestResponseException {

        if (report.getExceptions().isEmpty()) {
            // if (aux.getOutputFormat().equals("XML"))
            // outputMessage = mmm.partialExecution(optionsList, aux.getOutputFormat());
            // else
            // outputMessage = mmm.partialExecution(optionsList);
            // System.out.println("PARTIAL Nº of pitfalls:" + report.getNumPitfalls());
            final StringBuffer output = new StringBuffer();
            output.append("PARTIAL Nº of pitfalls:").append(report.getNumPitfalls()).append('\n');
            final Map<PitfallId, List<Pitfall>> results = report.getPitfalls();
            for (final Map.Entry<PitfallId, List<Pitfall>> pfEntry : results.entrySet()) {
                // final PitfallId pfId = pfEntry.getKey();
                final List<Pitfall> pfs = pfEntry.getValue();
                for (final Pitfall pf : pfs) {
                    final String pfAsStr = pf.toString();
                    // System.out.println(pfAsStr);
                    output.append(pfAsStr).append('\n');
                }
            }
            return output.toString();
        }
        // else if (report.getTimeOut()) {
        // String text = "";
        // boolean add36 = false;
        // P36 p36 = null;
        // if (aux.getPitfalls().contains("36")) {
        // p36 = new P36(urlReq, 2);
        // add36 = p36.getNumberWithPitfall() > 0;
        // }

        // if (aux.getOutputFormat().equals("XML")) {
        // text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        // text = text + "<oops:OOPSResponse xmlns:oops=\"http://www.oeg-upm.net/oops\">\n";
        // if (add36) {
        // text = text + "<oops:Pitfall>\n";
        // text = text
        // + "<oops:Code>P36</oops:Code><oops:Name>URI contains file extension</oops:Name>\n";
        // text = text
        // + "<oops:Description>Guidelines in [5] suggest avoiding file extension in persistent URIs, particularly
        // those related to the technology used, as for example \".php\" or \".py\". In our case we have adapted it
        // to the ontology web languages used to formalized ontologies and their serializations. In this regard, we
        // consider as pitfall including file extensions as \".owl\", \".rdf\", \".ttl\", \".n3\" and \".rdfxml\" in
        // an ontology URI. An example of this pitfall (at 29th June, 2012) could be found in the \"BioPAX Level 3
        // ontology (biopax)\" ontologyÕs URI (http://www.biopax.org/release/biopax-level3.owl) that contains the
        // extension \".owl\" related to the technology used.</oops:Description>\n";
        // text = text + "<oops:Importance>Minor</oops:Importance>\n";
        // text = text + "<oops:Affects>\n";
        // for (String uri : p36.listResults)
        // text = text + "<oops:AffectedElement>" + uri + "</oops:AffectedElement>\n";
        // text = text + "</oops:Affects>\n";
        // text = text + "</oops:Pitfall>\n";
        // }
        // text = text + "<oops:Pitfall>\n";
        // text = text + "<oops:Code>P37</oops:Code><oops:Name>Ontology not available</oops:Name>\n";
        // text = text
        // + "<oops:Description>It is crucial you solve this if you want to contribute to the Semantic Web. You can
        // follow the guidelines at LinkToDiegoRecipes to publish your ontology. If your ontology is correctly
        // published and we have made an error detecting this pitfall please let us know and we will solve it
        // a.s.a.p.</oops:Description>\n";
        // text = text + "<oops:Importance>Critical</oops:Importance>\n";
        // text = text + "</oops:Pitfall>\n";
        // text = text + "</oops:OOPSResponse>";
        // } else {
        // String firstId = UUID.randomUUID().toString();
        // String secondId = UUID.randomUUID().toString();
        // String thirdId = UUID.randomUUID().toString();
        // text = "<rdf:RDF\n";
        // text = text + " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
        // text = text + " xmlns:oops=\"http://www.oeg-upm.net/oops#\" >\n";
        // text = text + " <rdf:Description rdf:about=\"http://www.oeg-upm.net/oops/" + firstId + "\">\n";
        // if (add36)
        // text = text + " <oops:hasPitfall rdf:resource=\"http://www.oeg-upm.net/oops/" + thirdId
        // + "\"/>\n";
        // text = text + " <oops:hasPitfall rdf:resource=\"http://www.oeg-upm.net/oops/" + secondId
        // + "\"/>\n";
        // text = text + " <rdf:type rdf:resource=\"http://www.oeg-upm.net/oops#response\"/>\n";
        // text = text + " </rdf:Description>\n";
        // if (add36) {
        // text = text + "<rdf:Description rdf:about=\"http://www.oeg-upm.net/oops/" + thirdId
        // + "\">\n";
        // for (String uri : p36.listResults)
        // text = text
        // + " <oops:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#anyURI\">"
        // + uri + "</oops:hasValue>\n";
        // text = text
        // + " <oops:hasDescription rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Guidelines in [5]
        // suggest avoiding file extension in persistent URIs, particularly those related to the technology used, as
        // for example \".php\" or \".py\". In our case we have adapted it to the ontology web languages used to
        // formalized ontologies and their serializations. In this regard, we consider as pitfall including file
        // extensions as \".owl\", \".rdf\", \".ttl\", \".n3\" and \".rdfxml\" in an ontology URI. An example of
        // this pitfall (at 29th June, 2012) could be found in the \"BioPAX Level 3 ontology (biopax)\" ontologyÃ•s
        // URI (http://www.biopax.org/release/biopax-level3.owl) that contains the extension \".owl\" related to the
        // technology used.</oops:hasDescription>\n";
        // text = text
        // + " <oops:hasCode rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">P36</oops:hasCode>\n";
        // text = text
        // + " <oops:hasName rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">URI contains file
        // extension</oops:hasName>\n";
        // text = text
        // + " <oops:hasImportanceLevel
        // rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Minor</oops:hasImportanceLevel>\n";
        // text = text + " <rdf:type rdf:resource=\"http://www.oeg-upm.net/oops#pitfall\"/>\n";
        // text = text + "</rdf:Description>\n";
        // }
        // text = text + " <rdf:Description rdf:about=\"http://www.oeg-upm.net/oops/" + secondId
        // + "\">\n";//
        // text = text
        // + " <oops:hasCode rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">P37</oops:hasCode>\n";
        // text = text
        // + " <oops:hasName rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Ontology not
        // available</oops:hasName>\n";
        // text = text
        // + " <oops:hasDescription rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">It is crucial you solve
        // this if you want to contribute to the Semantic Web. You can follow the guidelines at LinkToDiegoRecipes
        // to publish your ontology. If your ontology is correctly published and we have made an error detecting
        // this pitfall please let us know and we will solve it a.s.a.p.</oops:hasDescription>\n";
        // text = text
        // + " <oops:hasImportanceLevel
        // rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Critical</oops:hasImportanceLevel>\n";
        // text = text + " <rdf:type rdf:resource=\"http://www.oeg-upm.net/oops#pitfall\"/>\n";
        // text = text + " </rdf:Description>\n";
        // text = text + "</rdf:RDF>";
        // }

        // // Escribimos aqui en usos-web
        // File dir1 = new File(".");
        // String path = dir1.getCanonicalPath();
        // FileWriter csvWriter = new FileWriter(path + "/usos-web.csv", true);

        // csvWriter.write(new Date().toString() + ";");
        // if (auxContent.equals("")) {
        // csvWriter.write(urlReq + ";");
        // } else {
        // csvWriter.write("" + ";");
        // }
        // for (int i = 0; i < 27; i++) {
        // csvWriter.write("-;");
        // }
        // if (add36)
        // csvWriter.write("1;");
        // else
        // csvWriter.write("-;");
        // csvWriter.write("1;");
        // csvWriter.write("-;");
        // csvWriter.write("-;");
        // csvWriter.write("-;");
        // if (add36) {
        // csvWriter.write("2;\n");
        // } else {
        // csvWriter.write("1;\n");
        // }
        // csvWriter.close();
        // outputMessage = text;
        // }
        else {
            throw RestResponseException.createErrorWhileAnalyzing();
        }
        // registerCall(aux.getOutputFormat());
        // return outputMessage;
    }

    @FunctionalInterface
    public interface IOExceptionFunction<T, R> {
        R apply(T t) throws IOException;
    }

    @FunctionalInterface
    public interface IOAndRestResponseExceptionFunction<T, R> {
        R apply(T t) throws IOException, RestResponseException;
    }

    private String reqToJson(final String body, final IOExceptionFunction<String, RunSettings> parser) {

        try {
            try {
                final RunSettings runSettings = parser.apply(body);
                return req(runSettings, Report::toJson);
            } catch (final IOException exc) {
                throw RestResponseException.createBadInput();
            }
        } catch (final RestResponseException exc) {
            return exc.toJson();
        }
    }

    private String reqToXml(final String body, final IOExceptionFunction<String, RunSettings> parser) {

        try {
            try {
                final RunSettings runSettings = parser.apply(body);
                return req(runSettings, Report::toXml);
            } catch (final IOException exc) {
                throw RestResponseException.createBadInput();
            }
        } catch (final RestResponseException exc) {
            return exc.toXml();
        }
    }

    private String reqToText(final String body, final IOExceptionFunction<String, RunSettings> parser) {

        try {
            try {
                final RunSettings runSettings = parser.apply(body);
                return req(runSettings, WebServiceOOPS::toText);
            } catch (final IOException exc) {
                throw RestResponseException.createBadInput();
            }
        } catch (final RestResponseException exc) {
            return exc.toString();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String reqJsonJson(final String body) {
        return reqToJson(body, RunSettings::fromJson);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public String reqJsonXml(final String body) {
        return reqToXml(body, RunSettings::fromJson);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String reqJsonText(final String body) {
        return reqToText(body, RunSettings::fromJson);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public String reqXmlJson(final String body) {
        return reqToJson(body, RunSettings::fromXml);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String reqXmlXml(final String body) {
        return reqToXml(body, RunSettings::fromXml);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String reqXmlText(final String body) {
        return reqToText(body, RunSettings::fromXml);
    }

    public void registerCall(final String requestedFormat) {

        final ServiceApi serviceApi = new ServiceApiDriver("c6041650eca6b1a4303bf5758abd633f");

        // the parameters of your call
        final ParameterMap params = new ParameterMap();
        final String userKey = "2cde1723b1b2f9ff5ddebdccc8de7960";
        params.add("userKey", userKey);
        // Add the service id of your application
        params.add("service_id", "2555417713092");

        // Add a metric to the call
        final ParameterMap usage = new ParameterMap();

        if (requestedFormat.equals("XML")) {
            usage.add("req_xml", "1");
        } else {
            usage.add("req_rdf", "1");
        }
        // Se le pasa al padre hits un cero porque si no cuenta dos veces
        usage.add("hits", "0");

        // metrics belong inside the usage parameter
        params.add("usage", usage);

        // the 'preferred way' of calling the backend: authrep
        try {
            final AuthorizeResponse response = serviceApi.authrep(params);
            logger.info("AuthRep on User Key Success: {}", response.success());
            if (response.success()) {
                logger.info("Your API access got authorized and the traffic added to 3scale backend");
                logger.info("Plan: {}", response.getPlan());
            } else {
                logger.error("Your API access did not authorized, check why");
                logger.error("Error: {}", response.getErrorCode());
                logger.error("Reason: {}", response.getReason());
            }
        } catch (final ServerError serverError) {
            logger.error("Failed to verify API access", serverError);
        }
    }
}
