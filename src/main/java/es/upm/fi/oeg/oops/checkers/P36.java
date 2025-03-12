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
import es.upm.fi.oeg.oops.Pitfall;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RdfOutputContext;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SrcSpec;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P36 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(36, null),
            Set.of(new PitfallCategoryId('N', 51)), Importance.MINOR, "URI contains file extension",
            "This pitfall occurs if file extensions such as " + "\".owl\", " + "\".rdf\", " + "\".ttl\", " + "\".n3\" "
                    + "and \".rdfxml\" " + "are included in an ontology URI. "
                    + "This pitfall is related with the recommendations provided in [9].",
            RuleScope.ONTOLOGY, Arity.ZERO);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final SrcSpec srcSpec = context.getSrcModel().getSrcSpec();
        final String uri;
        switch (srcSpec.getSrcType()) {
            case RDF_CODE :
                // decide how to treat this and change accordingly
                context.addOntologyWarning(INFO, "Ontology IRI is required to run this check. Skipping check.");
                return;
            case URI :
                uri = srcSpec.getIri();
                break;
            default :
                throw new UnsupportedOperationException("Not yet implemented");
        }
        // TODO Actually parse the URI, get the file name and then the extension, and compare with ends_with
        if (uri.contains(".owl") || uri.contains(".rdf") || uri.contains(".n3") || uri.contains(".ttl")) {
            context.addResult(new UriContainsFileExtPitfall(uri));
        }
    }

    private static class UriContainsFileExtPitfall implements Pitfall {

        private static String OUT_FMT = "URI with file extension: '%s'";

        private final String uri;

        public UriContainsFileExtPitfall(final String uri) {
            this.uri = uri;
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
            return String.format(OUT_FMT, uri);
        }

        @Override
        public String toHtml() {
            return String.format(OUT_FMT, Pitfall.toHtmlLink(uri));
        }

        // @Override
        // public void toRdf(RdfOutputContext context) {
        // // TODO Auto-generated method stub
        // }
    }
}
