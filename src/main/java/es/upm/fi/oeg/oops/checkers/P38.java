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
import es.upm.fi.oeg.oops.PitfallCategory;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SrcSpec;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO FIXME important! We need to differentiate between when I get the code or the URI
@MetaInfServices(Checker.class)
public class P38 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(38, null),
            Set.of(new PitfallCategoryId('N', 41), new PitfallCategoryId('N', 51), new PitfallCategoryId('N', 8)),
            Importance.IMPORTANT, "No OWL ontology declaration",
            "This pitfall consists in not declaring an owl:Ontology resource, "
                    + "which provides the ontology metadata. "
                    + "The owl:Ontology tag aims at gathering metadata about a given ontology "
                    + "such as version information, license, provenance, creation date, and so on. "
                    + "It is also used to declare the inclusion of other ontologies.",
            RuleScope.ONTOLOGY, Arity.ZERO, "The RDF has no owl:Ontology declaration", AccompPer.INSTANCE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    private final Logger logger = LoggerFactory.getLogger(P38.class);

    public P38() {
    }

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        final SrcSpec srcSpec = context.getSrcModel().getSrcSpec();
        try {
            final String content;
            switch (srcSpec.getSrcType()) {
                case RDF_CODE :
                    content = srcSpec.getContent();
                    break;
                case URI :
                    // URL url = new URL(onto);
                    //
                    // String acceptHeaderValue = StrUtils.strjoin(",",
                    // "application/rdf+xml",
                    // "application/turtle;q=0.9",
                    // "application/x-turtle;q=0.9",
                    // "text/n3;q=0.8",
                    // "text/turtle;q=0.8",
                    // "text/rdf+n3;q=0.7",
                    // "application/xml;q=0.5",
                    // "text/xml;q=0.5",
                    // "text/plain;q=0.4", // N-triples
                    // "*/*;q=0.2") ;
                    // final String content = P39.download(url, acceptHeaderValue);

                    // Write the onto from the owl model
                    final StringWriter stringWriter = new StringWriter();
                    model.write(stringWriter, "TURTLE");
                    content = stringWriter.toString();
                    break;
                default :
                    throw new UnsupportedOperationException("Not implemented");
            }

            // System.out.println(content);
            // here I got the content by the Code or the URI, it is in content anyways

            final String about = P39.extractAbout(content);
            if (about == null) {
                context.addResult(PITFALL_INFO, Collections.emptySet());
            }
        } catch (final Exception exc) {
            logger.warn("Can;t find content (file) to check for  the existence of owl:Ontology", exc);
        }
    }

    // private static class NoOntologyDeclarationPitfall implements Pitfall {
    //
    // public NoOntologyDeclarationPitfall() {
    // }
    //
    // @Override
    // public PitfallInfo getInfo() {
    // return INFO;
    // }
    //
    // @Override
    // public List<OntResource> getResources() {
    // return Collections.emptyList();
    // }
    //
    // @Override
    // public String toString() {
    // return "The RDF has no owl:Ontology declaration";
    // }
    // }
}
