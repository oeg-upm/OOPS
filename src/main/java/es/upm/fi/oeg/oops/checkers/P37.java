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
import es.upm.fi.oeg.oops.ModelLoader;
import es.upm.fi.oeg.oops.Pitfall;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SerializationFormats;
import es.upm.fi.oeg.oops.SrcSpec;
import es.upm.fi.oeg.oops.SrcType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.kohsuke.MetaInfServices;

/**
 * Look whether there the ontology is available. If there is an RDF model, we can also create the model and provide it
 * for the rest of the checkers. If the input is the RDF source, it can not be checked.
 */
@MetaInfServices(Checker.class)
public class P37 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(37, null),
            Set.of(new PitfallCategoryId('N', 51), new PitfallCategoryId('N', 7)), Importance.CRITICAL,
            "Ontology not available on the Web",
            "This pitfall occurs when the ontology code (OWL encoding) "
                    + "or its documentation (HTML document) is missing when looking up its URI. "
                    + "This pitfall deals with the first point from the Linked Data star system "
                    + "that states \"On the web\" ([10] and [11]). "
                    + "Guidelines in [12] also recommends to \"Publish your vocabulary on the Web at a stable URI\". "
                    + "This pitfall is also related to the problems listed in [8] and [5].",
            RuleScope.ONTOLOGY, Arity.ZERO,
            "It is crucial you make your ontology available on the Web, "
                    + "if you want to contribute to the Semantic Web.\n" + "\n"
                    + "You can follow the guidelines at <a href=\"http://www.w3.org/TR/swbp-vocab-pub/\" target=\"_blank\">"
                    + "\"Best Practice Recipes for Publishing RDF Vocabularies\"</a> " + "to publish your ontology.\n"
                    + "\n"
                    + "If your ontology is correctly published and we have made an error detecting this pitfall,\n"
                    + "please let us know, and we will solve it a.s.a.p",
            AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final SrcSpec origSrcSpec = context.getSrcModel().getSrcSpec();
        final String urlToFetch;
        switch (origSrcSpec.getSrcType()) {
            case RDF_CODE :
                context.addOntologyWarning(INFO, "Ontology IRI is required to run this check. Skipping check.");
                return;
            case URI :
                urlToFetch = origSrcSpec.getIri();
                break;
            default :
                throw new UnsupportedOperationException("Not yet implemented");
        }

        // get the value in this.thereIsHTML
        boolean thereIsHTML = askForHTML(urlToFetch);

        // prepare content type to load it in Jena later
        final String followedContentType = FollowRedirectRDF.follow(urlToFetch).getContentType();
        final SerializationFormats parsedRdfCType;
        try {
            parsedRdfCType = SerializationFormats.from(followedContentType);
        } catch (final IllegalArgumentException exc) {
            context.addOntologyWarning(INFO, exc.getMessage());
            return;
        }
        final SrcSpec srcSpec = new SrcSpec(SrcType.URI, urlToFetch, null, parsedRdfCType);
        final Model model = ModelLoader.loadJenaBasic(srcSpec);
        final int elements = countElements(model);
        final boolean thereIsRDF = elements > 0;

        if (!thereIsHTML && !thereIsRDF) {
            context.addResult(new BadNamespaceUrlContentPitfall(urlToFetch));
        }
    }

    public int countElements(final Model model) {

        int nC = 0;
        int nOP = 0;
        // int nC = this.model.listObjects().toList().size();
        // int nOP = this.model.listSubjects().toList().size();
        int nDP = model.listStatements().toList().size();
        final int elements = nC + nOP + nDP;

        return elements;
    }

    public boolean askForHTML(final String urlToFetch) {

        try {
            final URL url = URI.create(urlToFetch).toURL();
            final String content = P39.download(url, null);
            // look for the HTML tag
            if (content != null && content.contains("<!DOCTYPE html") || content.contains("<html>")
                    || content.contains("<head>")) {
                // System.out.println("HTML in: " + url.toString());
                return true;
            } else {
                // System.out.println("other in: " + url.toString());
            }
        } catch (final MalformedURLException exc) {
            System.err.println("1) " + exc.getMessage());
        }

        return false;
    }

    private static class BadNamespaceUrlContentPitfall implements Pitfall {

        private static String OUT_FMT = "Ontology not available on the web from: '%s'";

        private final String url;

        public BadNamespaceUrlContentPitfall(final String url) {
            this.url = url;
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
            return String.format(OUT_FMT, url);
        }

        @Override
        public String toHtml() {
            return String.format(OUT_FMT, Pitfall.toHtmlLink(url));
        }

        // @Override
        // public void toRdf(RdfOutputContext context) {
        // // TODO Auto-generated method stub
        // }
    }
}
