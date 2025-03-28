/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

public enum SerializationFormats {
    TURTLE, N_TRIPLES, RDF_XML, N3, JSON_LD, RDF_JSON;

    public OWLDocumentFormat toOwl() {
        switch (this) {
            case TURTLE :
                return new TurtleDocumentFormat();
            case N_TRIPLES :
                return null;
            case RDF_XML :
                return new RDFXMLDocumentFormat();
            case N3 :
                return null;
            case JSON_LD :
                return null;
            case RDF_JSON :
                return null;
            default :
                throw new UnsupportedOperationException();
        }
    }

    public String toJena() {
        switch (this) {
            case TURTLE :
                return "Turtle";
            case N_TRIPLES :
                return "N-Triples";
            case RDF_XML :
                return "RDF/XML";
            case N3 :
                return "N3";
            case JSON_LD :
                return "JSON-LD";
            case RDF_JSON :
                return "RDF/JSON";
            default :
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return toJena();
    }

    public static SerializationFormats from(final String format) {
        switch (format.toLowerCase()) {
            case "turtle" :
            case "ttl" :
                return TURTLE;
            case "n-triples" :
            case "n-triple" :
            case "nt" :
                return N_TRIPLES;
            case "rdf/xml" :
            case "rdf" :
            case "xml" :
            case "rdfxml" :
            case "rdf-xml" :
            case "rdf_xml" :
                return RDF_XML;
            case "n3" :
            case "notation3" :
            case "notation-3" :
                return N3;
            case "json-ld" :
            case "json_ld" :
            case "jsonld" :
                return JSON_LD;
            case "rdf/json" :
            case "rdf-json" :
            case "rdf_json" :
            case "rdfjson" :
            case "rj" :
                return RDF_JSON;
            default :
                throw new IllegalArgumentException("Unknown RDF serialization format \"" + format + "\"");
        }
    }

    public String toMimeType() {
        switch (this) {
            case TURTLE :
                return "text/turtle";
            case N_TRIPLES :
                return "application/n-triples";
            case RDF_XML :
                return "application/rdf+xml";
            case N3 :
                return "text/n3";
            case JSON_LD :
                return "application/ld+json";
            case RDF_JSON :
                return "application/rdf+json";
            default :
                throw new UnsupportedOperationException();
        }
    }

    public String toFileExtension() {
        switch (this) {
            case TURTLE :
                return "ttl";
            case N_TRIPLES :
                return "nt";
            case RDF_XML :
                return "rdf";
            case N3 :
                return "n3";
            case JSON_LD :
                return "jsonld";
            case RDF_JSON :
                return "rj";
            default :
                throw new UnsupportedOperationException();
        }
    }
}
