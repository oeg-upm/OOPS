/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.checkers;

import es.upm.fi.oeg.oops.Arity;
import es.upm.fi.oeg.oops.Checker;
import es.upm.fi.oeg.oops.CheckerInfo;
import es.upm.fi.oeg.oops.CheckingContext;
import es.upm.fi.oeg.oops.FollowRedirectRDF;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.Pitfall;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SrcSpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P39 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(39, null),
            Set.of(new PitfallCategoryId('N', 51)), Importance.CRITICAL, "Ambiguous namespace",
            "This pitfall consists in declaring neither the ontology URI " + "nor the base namespace. "
                    + "If this is the case, " + "the ontology namespace is matched to the file location. "
                    + "This situation is not desirable, "
                    + "as the location of a file might change while the ontology should remain stable, "
                    + "as proposed in [12].",
            RuleScope.ONTOLOGY, Arity.ZERO);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    public P39() {
    }

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final SrcSpec srcSpec = context.getSrcModel().getSrcSpec();
        final String content;
        switch (srcSpec.getSrcType()) {
            case RDF_CODE :
                content = srcSpec.getContent();
                break;
            case URI :
                content = fetchUrl(srcSpec.getIri());
                break;
            default :
                throw new UnsupportedOperationException("Not implemented");
        }

        if (content == null) {
            context.addOntologyWarning(INFO,
                    "Failed to check for ambiguous namespace, because no ontology content could be fetched");
            return;
        }

        final String about = extractAbout(content);
        final String base = extractBase(content);

        // check ambiguity
        if (base == null) {
            if (about == null) {
                context.addResult(new AmbiguousNamespacePitfall(1, base, about));
            } else if (about.isEmpty()) {
                context.addResult(new AmbiguousNamespacePitfall(2, base, about));
            }
        } else if (base.isEmpty()) {
            if (about == null) {
                context.addResult(new AmbiguousNamespacePitfall(3, base, about));
            } else if (about.isEmpty()) {
                context.addResult(new AmbiguousNamespacePitfall(4, base, about));
            }
        }
    }

    private static String fetchUrl(final String onto) {
        return download(onto, FollowRedirectRDF.ACCEPT_HEADER_VALUE);
    }

    public static String download(final URL url, final String acceptHeaderValue) {

        try {
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            if (acceptHeaderValue != null) {
                conn.setRequestProperty("Accept", acceptHeaderValue);
            }
            conn.setInstanceFollowRedirects(true);
            conn.connect();

            try (final InputStream rawStream = conn.getInputStream()) {
                if (rawStream != null) {
                    try (final Scanner contentAggregator = new Scanner(rawStream, StandardCharsets.UTF_8.toString())
                            .useDelimiter("\\A")) {
                        return contentAggregator.hasNext() ? contentAggregator.next() : "";
                    }
                }
            }
        } catch (final MalformedURLException exc) {
            System.err.println("1) " + exc.getMessage());
            System.out.println("1) en OntologyDeclaration" + exc.getMessage());
        } catch (final IOException exc) {
            System.err.println("2) " + exc.getMessage());
            System.out.println("2) en OntologyDeclaration " + exc.getMessage());
        } catch (final Exception exc) {
            exc.printStackTrace();
            System.out.println("Failed to download");
        }

        return null;
    }

    public static String download(final String urlStr, final String acceptHeaderValue) {

        try {
            final URL url = URI.create(urlStr).toURL();
            return download(url, acceptHeaderValue);
        } catch (final MalformedURLException exc) {
            System.err.println("1) " + exc.getMessage());
            System.out.println("1) en OntologyDeclaration" + exc.getMessage());
        }

        return null;
    }

    public static String extractAbout(final String content) {

        // define patterns to look for owl:ontology
        final Pattern pattern3 = Pattern.compile("a(\\s+)owl:Ontology(\\s*);");
        final Matcher matcher3 = pattern3.matcher(content);

        final Pattern pattern4 = Pattern.compile("rdf:type(\\s+)owl:Ontology(\\s*);");
        final Matcher matcher4 = pattern4.matcher(content);

        final Pattern pattern5 = Pattern.compile("a(\\s+)owl:Ontology(\\s*),");
        final Matcher matcher5 = pattern5.matcher(content);

        final Pattern pattern6 = Pattern.compile("rdf:type(\\s+)owl:Ontology(\\s*),");
        final Matcher matcher6 = pattern6.matcher(content);

        // TODO FIXME This whole checking that follows should be possible to do more elegantly
        final boolean noOntoDeclaration;
        final String about;
        if (content.contains("<owl:Ontology rdf:about=\"")) {
            // System.out.println("Tiene owl:Ontology 1");
            noOntoDeclaration = false;

            final String secondPartAbout = content.split("<owl:Ontology rdf:about=\"")[1];
            about = secondPartAbout.split("\"")[0];
            // System.out.println("about: " + about);
        } else if (content.contains("<Ontology rdf:about=\"")) {
            // System.out.println("Tiene owl:Ontology 1.5");
            noOntoDeclaration = false;

            final String secondPartAbout = content.split("<Ontology rdf:about=\"")[1];
            about = secondPartAbout.split("\"")[0];
            // System.out.println("about: " + about);
        } else if (content.contains("<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Ontology\"/>")) {
            // System.out.println("Tiene owl:Ontology 2");
            noOntoDeclaration = false;

            final String firstPartAbout = content
                    .split("<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Ontology\"/>")[0];
            final int last = firstPartAbout.lastIndexOf("<rdf:Description rdf:about=\"");
            final String lastPartAbout = firstPartAbout.substring(last).replace("<rdf:Description rdf:about=\"", "");
            final int first = lastPartAbout.indexOf("\"");
            about = lastPartAbout.substring(0, first);
        } else if (matcher3.find()) {
            // System.out.println("Tiene owl:Ontology 3");
            noOntoDeclaration = false;

            final String firstPartAbout = content.split("a(\\s+)owl:Ontology(\\s*);")[0];
            final int start = firstPartAbout.lastIndexOf("<");

            final String aux = firstPartAbout.substring(start + 1);
            final int end = aux.indexOf(">");

            about = aux.substring(0, end);
        } else if (matcher4.find()) {
            // System.out.println("Tiene owl:Ontology 4");
            noOntoDeclaration = false;

            final String firstPartAbout = content.split("rdf:type(\\s+)owl:Ontology(\\s*);")[0];
            final int start = firstPartAbout.lastIndexOf("<");

            final String aux = firstPartAbout.substring(start + 1);
            final int end = aux.indexOf(">");

            about = aux.substring(0, end);
        } else if (matcher5.find()) {
            // System.out.println("Tiene owl:Ontology 5");
            noOntoDeclaration = false;

            final String firstPartAbout = content.split("a(\\s+)owl:Ontology(\\s*),")[0];
            final int start = firstPartAbout.lastIndexOf("<");

            final String aux = firstPartAbout.substring(start + 1);
            final int end = aux.indexOf(">");

            about = aux.substring(0, end);

            // about = firstPartAbout.substring(last+1).replace(">", "");
            // System.out.println("about: " + about);
        } else if (matcher6.find()) {
            // System.out.println("Tiene owl:Ontology 6");
            noOntoDeclaration = false;

            final String firstPartAbout = content.split("rdf:type(\\s+)owl:Ontology(\\s*),")[0];
            final int start = firstPartAbout.lastIndexOf("<");

            final String aux = firstPartAbout.substring(start + 1);
            final int end = aux.indexOf(">");

            about = aux.substring(0, end);
        } else if (content.contains("<owl:Ontology>")) {
            // System.out.println("Tiene <owl:Ontology>");
            noOntoDeclaration = false;

            about = "";
        } else {
            // System.out.println("NO Tiene owl:Ontology --> comprobar");
            noOntoDeclaration = true;
            about = null;
        }

        return about;
    }

    private static String extractBase(final String content) {

        final String base;
        if (content.contains("xml:base=\"")) {
            String secondPartBase = content.split("xml:base=\"")[1];
            base = secondPartBase.split("\"")[0];
            // System.out.println("base: " + base);
        } else {
            base = null;
        }

        return base;
    }

    private static class AmbiguousNamespacePitfall implements Pitfall {

        private static final String OUT_FMT = "The ontology has ambiguous namespace type %d) base & about: '%s' & '%s'";

        private final int type;
        private final String about;
        private final String base;

        AmbiguousNamespacePitfall(final int type, final String about, final String base) {
            this.type = type;
            this.about = about;
            this.base = base;
        }

        @Override
        public PitfallInfo getInfo() {
            return PITFALL_INFO;
        }

        @Override
        public List<OntResource> getResources() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return String.format(OUT_FMT, type, base, about);
        }

        @Override
        public String toHtml() {
            return String.format(OUT_FMT, type, Pitfall.toHtmlLink(base), about);
        }

        // @Override
        // public void toRdf(RdfOutputContext context) {
        // // TODO Auto-generated method stub
        // }
    }
}
