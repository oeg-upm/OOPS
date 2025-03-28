/*
 * SPDX-FileCopyrightText: 2012 - 2013 Ontology Engineering Group, Universidad Politecnica de Madrid, Spain
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Daniel Garijo
 */
public class Constants {
    // Licensius service
    // public static final String licensiusServiceLicenseRaw = "http://licensius.appspot.com/getLicenseRaw?rdf=";
    // public static final String licensiusServiceLicenseRaw =
    // "http://www.licensius.appspot.com/api/license/getLicense";
    public static final String licensiusServiceLicenseRaw = "http://www.licensius.appspot.com/api/license/getLicense";
    // public static final String licensiusURIServiceLicense = "http://licensius.appspot.com/getLicense?content=";
    // public static final String licensiusURIServiceLicense =
    // "http://www.licensius.appspot.com/api/license/getLicense?content=";
    public static final String licensiusURIServiceLicense = "http://www.licensius.com/api/license/findlicenseinrdf?uri=";
    public static final String licensiusServiceGuess = "http://licensius.appspot.com/licenseguess?txt=";
    public static final String licensiusURIServiceLicenseInfo = "http://www.licensius.com/api/license/getlicenseinfo?uri=";// "http://licensius.appspot.com/getLicenseTitle?content=";
    public static final int licensiusTimeOut = 10000;

    public static final int oopsTimeOut = 30000;

    public static final String[] vocabPossibleSerializations = { "application/rdf+xml", "text/turtle", "text/n3" };
    /**
     * Constants for the Step 2 (table)
     */

    public static final String abstractSectionContent = "abstract";
    public static final String ontTitle = "ontologyTitle";
    public static final String ontName = "ontologyName";
    public static final String ontPrefix = "ontologyPrefix";
    public static final String ontNamespaceURI = "ontologyNamespaceURI";
    public static final String dateOfRelease = "dateOfRelease";
    public static final String thisVersionURI = "thisVersionURI";
    public static final String latestVersionURI = "latestVersionURI";
    public static final String previousVersionURI = "previousVersionURI";
    public static final String ontologyRevision = "ontologyRevisionNumber";
    public static final String authors = "authors";
    public static final String authorsURI = "authorsURI";
    public static final String authorsInstitution = "authorsInstitution";
    public static final String authorsInstitutionURI = "authorsInstitutionURI";
    public static final String contributors = "contributors";
    public static final String contributorsURI = "contributorsURI";
    public static final String contributorsInstitution = "contributorsInstitution";
    public static final String contributorsInstitutionURI = "contributorsInstitutionURI";
    public static final String publisher = "publisher";
    public static final String publisherURI = "publisherURI";
    public static final String publisherInstitution = "publisherInstitution";
    public static final String publisherInstitutionURI = "publisherInstitutionURI";
    public static final String importedOntologyNames = "importedOntologyNames";
    public static final String importedOntologyURIs = "importedOntologyURIs";
    public static final String extendedOntologyNames = "extendedOntologyNames";
    public static final String extendedOntologyURIs = "extendedOntologyURIs";
    public static final String licenseName = "licenseName";
    public static final String licenseURI = "licenseURI";
    public static final String licenseIconURL = "licenseIconURL";
    public static final String citeAs = "citeAs";
    public static final String doi = "DOI";
    public static final String rdf = "RDFXMLSerialization";
    public static final String ttl = "TurtleSerialization";
    public static final String n3 = "N3Serialization";
    public static final String json = "JSONLDSerialization";
    // public static final String defaultSerialization="defaultSerialization";
    public static final String status = "status";

    public static final String opening = "<!DOCTYPE html>\n<html prefix=\"dc: http://purl.org/dc/terms/ schema: http://schema.org/ prov: http://www.w3.org/ns/prov# foaf: http://xmlns.com/foaf/0.1/ owl: http://www.w3.org/2002/07/owl#\">\n"
            + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n";
    // missing specialization. Missing alternate

    public static final String[] POSSIBLE_VOCAB_SERIALIZATIONS = { "application/rdf+xml", "text/turtle", "text/n3",
            "application/ld+json" };

    public static String getChangeLogSection(final Properties lang) {
        return lang.getProperty("changeLog");
    }

    public static final String ending = "</body></html>";

    public static String getNameSpaceDeclaration(final Map<String, String> prefixNamespaces, final Properties lang) {
        final StringBuffer ns = new StringBuffer();
        ns.append("<div id=\"namespacedeclarations\">\n").append("<h3 id=\"ns\" class=\"list\">")
                .append(lang.getProperty("ns")).append(lang.getProperty("nsText"));
        for (final Map.Entry<String, String> entry : prefixNamespaces.entrySet()) {
            final String prefix = entry.getKey();
            final String namespace = entry.getValue();
            ns.append("<tr><td><b>").append(prefix).append("</b></td><td>&lt;").append(namespace)
                    .append("&gt;</td></tr>\n");
        }
        ns.append("</tbody>\n</table>\n</div>\n</div>\n");
        return ns.toString();
    }

    /**
     * Method that writes an htaccess file according to the W3C best practices. Note that hash is different than slash
     *
     * @param c
     *
     * @return
     */

    /**
     * Text for the 406 page
     *
     * @param c
     * @param lang
     *
     * @return the content of the 406 page
     */

}
