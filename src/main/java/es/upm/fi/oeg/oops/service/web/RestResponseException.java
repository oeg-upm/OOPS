/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.fi.oeg.oops.Utils;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RestResponseException extends Exception {

    public enum Type {
        INVALID_PARAMETERS, BAD_INPUT, BAD_EXECUTION,
    }

    private final Logger logger = LoggerFactory.getLogger(RestResponseException.class);

    private final Type type;
    private final List<String> msgs;
    private final String title;

    // We do not want internal errors to leak to clients
    // private RestResponseException(final String errorMessage, Throwable err) {
    // super(errorMessage, err);
    // }

    private RestResponseException(final Type type, final List<String> msgs, final String title) {
        this.type = type;
        this.msgs = msgs;
        this.title = title;
    }

    private RestResponseException(final Type type, final String msg, final String title) {
        this(type, List.of(msg), title);
    }

    public String toJson() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (final IOException exc) {
            logger.error("Failed to convert REST response object to JSON", exc);
            return "{ \"error\": \"An error happened during processing of the request, and we failed to convert that error into JSON.\"}";
        }
    }

    public String toXml() {
        final StringBuffer output = new StringBuffer();
        final String description = type.name().toLowerCase();
        output.append("<rdf:RDF\n");
        output.append("        xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
        output.append("        xmlns:oops=\"http://www.oeg-upm.net/oops#\">\n");
        output.append("    <rdf:Description rdf:about=\"http://www.oeg-upm.net/oops/");
        output.append(description);
        output.append("\">\n");
        if (title != null) {
            output.append("    <oops:hasTitle rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">");
            output.append(title);
            output.append("</oops:hasTitle>\n");
        }
        for (final String msg : msgs) {
            output.append("    <oops:hasMessage rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">");
            output.append(Utils.escapeForHtml(msg));
            output.append("</oops:hasMessage>\n");
        }
        output.append("    <rdf:type rdf:resource=\"http://www.oeg-upm.net/oops#response\"/>\n");
        output.append("    </rdf:Description>\n");
        output.append("</rdf:RDF>\n");
        return output.toString();
    }

    @Override
    public String toString() {
        final StringBuffer output = new StringBuffer();
        output.append("# ERROR - ").append(type).append("\n\n");
        output.append("## ").append(title).append("\n\n");
        for (final String msg : msgs) {
            output.append("- ").append(msg);
        }
        return output.toString();
    }

    public static RestResponseException createInvalidArguments() {
        final String title = "Invalid arguments";
        final String msg = "No ontology URL or ontology RDF content found in the input parameters. "
                + "Please check your request parameters.";
        return new RestResponseException(Type.INVALID_PARAMETERS, msg, title);
    }

    public static RestResponseException createNonHttpUri() {
        final String title = "Invalid arguments";
        final String msg = "The ontology URI does not start with http.";
        return new RestResponseException(Type.INVALID_PARAMETERS, msg, title);
    }

    public static RestResponseException createRequestByUriNotAllowed() {
        final String title = "Invalid arguments";
        final String msg = "Execution by ontology URI is not longer allowed.";
        return new RestResponseException(Type.INVALID_PARAMETERS, msg, title);
    }

    public static RestResponseException createBadInput() {
        final String title = "Problem with the input ontology";
        final String msg = "There was an unexpected error with your input.";
        return new RestResponseException(Type.BAD_INPUT, msg, title);
    }

    public static RestResponseException createErrorWhileAnalyzing() {
        final String title = "Error while analyzing";
        final List<String> msgs = List.of(
                "If you have entered a URI, make sure that it is available on-line using, "
                        + "for example, Vapour or Hyperthing.",
                "Also make sure that your ontology RDF or OWL code is correct "
                        + "using an RDF validator like <http://www.w3.org/RDF/Validator/> "
                        + "or an OWL validator like <http://www.mygrid.org.uk/OWL/Validator>.\n"
                        + "If the URI and the ontology are correct, but OOPS! is not able to analyze it, "
                        + "please, let us know by email (oops(at)delicias.dia.fi.upm.es), "
                        + "attaching the file or indicating the ontology URI "
                        + "and we will find out what was wrong within OOPS!.");
        return new RestResponseException(Type.BAD_EXECUTION, msgs, title);
    }

    public static RestResponseException createFailedToSerializeOutput() {
        final String title = "Error while creating output";
        final List<String> msgs = List
                .of("Failed to serialize the already finished report to the requested output format");
        return new RestResponseException(Type.BAD_EXECUTION, msgs, title);
    }
}
