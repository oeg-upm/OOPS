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
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SrcSpec;
import es.upm.fi.oeg.oops.Utils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P40 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(40, null),
            Set.of(new PitfallCategoryId('N', 51)), Importance.CRITICAL, "Namespace hijacking",
            "It refers to reusing or referring to terms from another namespace "
                    + "that are not defined in such namespace. " + "This is an undesirable situation "
                    + "as no information can be retrieved when looking up those undefined terms. "
                    + "This pitfall is related to the Linked Data publishing guidelines provided in [11]: "
                    + "\"Only define new terms in a namespace that you control\" "
                    + "and to the guidelines provided in [5].",
            RuleScope.RESOURCE, Arity.ONE, null, null,
            "For detecting this pitfall, we rely on TripleChecker. " + "See more results for this ontology at the "
                    + "<a href=\"http://graphite.ecs.soton.ac.uk/checker/?uri=%s\" target=\"_blank\">TripleChecker website</a>.\n"
                    + "Up to now, this pitfall is only available for the \"Scanner by URI\" option.\n");

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    public P40() {
    }

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        String urlTripleChecker = "http://graphite.ecs.soton.ac.uk/checker/?uri=";

        try {
            final SrcSpec srcSpec = context.getSrcModel().getSrcSpec();
            final String urlToFetch;
            switch (srcSpec.getSrcType()) {
                case RDF_CODE :
                    context.addOntologyWarning(INFO, "Ontology IRI is required to run this check. Skipping check.");;
                    return;
                case URI :
                    urlToFetch = srcSpec.getIri();
                    break;
                default :
                    throw new UnsupportedOperationException("Not implemented");
            }
            final String toAppend = Utils.urlEncode(urlToFetch);

            // System.err.println("Trying Triple Checker with: " + urlTripleChecker+toAppend);

            final URL url = URI.create(urlTripleChecker + toAppend).toURL();
            final String content = P39.download(url, null);
            if (content != null && content.contains("<h2>Terms</h2>")) {
                final String contentNew = content.split("<h2>Terms</h2>")[1];

                if (contentNew.contains("<tr class='bad'>") || contentNew.contains("<td class='legit'>ERROR</td>")) {
                    final String[] splitContent = contentNew.split("<td class='legit'>ERROR</td>");

                    for (int i = 0; i < splitContent.length - 1; i++) {
                        final int lastHref = splitContent[i].lastIndexOf("a href='");
                        final String href = splitContent[i].substring(lastHref + 8);
                        final int ends = href.indexOf("'");
                        final String element = href.substring(0, ends);
                        final OntResource pAux = context.getModel().getOntResource(element);
                        context.addResult(PITFALL_INFO, pAux);
                    }
                }
            }
        } catch (final MalformedURLException exc) {
            // System.err.println ("2) "+ exc.getMessage());
            // System.out.println ("2) en TripleCheckerFail"+ exc.getMessage());
        }
    }
}
