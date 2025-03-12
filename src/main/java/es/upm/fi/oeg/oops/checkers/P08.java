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
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P08 implements Checker {

    private static final PitfallInfo PITFALL_INFO_A = new PitfallInfo(new PitfallId(8, 'A'),
            Set.of(new PitfallCategoryId('N', 6), new PitfallCategoryId('N', 7)), Importance.MINOR,
            "Missing annotations - Label & Comment",
            "This pitfall consists in creating an ontology element "
                    + "and failing to provide human readable annotations attached to it. "
                    + "Consequently, ontology elements lack annotation properties that label them "
                    + "(e.g. rdfs:label, lemon:LexicalEntry, skos:prefLabel or skos:altLabel) "
                    + "or that define them (e.g. rdfs:comment or dc:description). "
                    + "This pitfall is related to the guidelines provided in [5].",
            RuleScope.RESOURCE, Arity.ONE,
            "These resources have neither `rdfs:label` nor `rdfs:comment` (nor `skos:definition`) defined",
            AccompPer.TYPE);

    private static final PitfallInfo PITFALL_INFO_C = new PitfallInfo(new PitfallId(8, 'C'),
            Set.of(new PitfallCategoryId('N', 6), new PitfallCategoryId('N', 7)), Importance.MINOR,
            "Missing annotations - Comment", PITFALL_INFO_A.getExplanation(), RuleScope.RESOURCE, Arity.ONE,
            "These resources have neither `rdfs:comment` or `skos:definition` defined", AccompPer.TYPE);

    private static final PitfallInfo PITFALL_INFO_L = new PitfallInfo(new PitfallId(8, 'L'),
            Set.of(new PitfallCategoryId('N', 6), new PitfallCategoryId('N', 7)), Importance.MINOR,
            "Missing annotations - Label", PITFALL_INFO_A.getExplanation(), RuleScope.RESOURCE, Arity.ONE,
            "These resources have no `rdfs:label` defined", AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo("Missing annotations",
            Set.of(PITFALL_INFO_A, PITFALL_INFO_C, PITFALL_INFO_L));

    private OntProperty skosDef;
    private OntProperty dctDesc;

    public P08() {
        final OntModel annotations = ModelFactory.createOntologyModel();
        annotations.createObjectProperty("http://www.w3.org/2004/02/skos/core#definition");
        annotations.createObjectProperty("http://purl.org/dc/terms/description");

        this.skosDef = annotations.getOntProperty("http://www.w3.org/2004/02/skos/core#definition");
        this.dctDesc = annotations.getOntProperty("http://purl.org/dc/terms/description");
    }

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws IOException {

        final OntModel model = context.getModel();

        final Set<OntResource> resNoAnnotation = new HashSet<>();
        final Set<OntResource> resComment = new HashSet<>();
        final Set<OntResource> resLabel = new HashSet<>();

        final ExtendedIterator<OntClass> cToAnalyze = model.listNamedClasses();
        final ExtendedIterator<ObjectProperty> rToAnalyze = model.listObjectProperties();
        final ExtendedIterator<DatatypeProperty> aToAnalyze = model.listDatatypeProperties();

        analyze(cToAnalyze, resNoAnnotation, resComment, resLabel);
        analyze(rToAnalyze, resNoAnnotation, resComment, resLabel);
        analyze(aToAnalyze, resNoAnnotation, resComment, resLabel);

        // annotations.close();

        context.addResultsIndividual(PITFALL_INFO_A, resNoAnnotation);
        context.addResultsIndividual(PITFALL_INFO_C, resComment);
        context.addResultsIndividual(PITFALL_INFO_L, resLabel);
    }

    private void analyze(final ExtendedIterator<? extends OntResource> toAnalyze,
            final Set<OntResource> resNoAnnotation, final Set<OntResource> resComment,
            final Set<OntResource> resLabel) {

        while (toAnalyze.hasNext()) {
            final OntResource resource = toAnalyze.next();
            // try {
            final String label = resource.getLabel(null);
            final String comment = resource.getComment(null);

            String definition = null;
            if (resource.getPropertyValue(skosDef) != null) {
                definition = resource.getPropertyValue(skosDef).toString();
            }

            String dctDescription = null;
            if (resource.getPropertyValue(dctDesc) != null) {
                dctDescription = resource.getPropertyValue(dctDesc).toString();
            }

            // System.out.println("valor de skos def: " + resource.getPropertyValue(skosDef));

            if (!Checker.fromModels(resource)) {
                if ((label == null) && (comment == null) && (definition == null) && (dctDescription == null)) {
                    // no annotations
                    resNoAnnotation.add(resource);
                } else if ((label == null) && ((comment != null) || (definition != null) || (dctDescription != null))) {
                    // only comment or skos:definition or dct:description
                    resNoAnnotation.add(resource);
                } else if ((label != null) && (comment == null) && (definition == null) && (dctDescription == null)) {
                    // only label
                    resNoAnnotation.add(resource);
                }
            }
            // } catch (final LiteralRequiredException exc) {
            // System.err.println("DETECTADA: " + exc.getMessage());
            // }
        }
    }
}
