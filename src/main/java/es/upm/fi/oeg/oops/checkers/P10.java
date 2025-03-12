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
import java.util.Collections;
import java.util.Set;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Esta pitfall no se da en un elemento concreto, se da, o no se da, en la ontolog�a si no hay disjoints.
 */
@MetaInfServices(Checker.class)
public class P10 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(10, null),
            Set.of(new PitfallCategoryId('N', 4), new PitfallCategoryId('S', 5)), Importance.IMPORTANT,
            "Missing disjoint-ness",
            "The ontology lacks disjoint axioms between classes "
                    + "or between properties that should be defined as disjoint. "
                    + "This pitfall is related to the guidelines provided in [6], [2] and [7].",
            RuleScope.ONTOLOGY, Arity.ZERO, "There are no owl:disjointWith axioms defined.", AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();
        final OWLOntology onto = context.getModelOwl();

        // if there are less then 2 classes,
        // there is no possibility for a lack of disjointness.
        if (model.listNamedClasses().toList().size() < 2) {
            return;
        }

        ExtendedIterator<OntClass> classes = model.listNamedClasses();
        while (classes.hasNext()) {
            final OntClass ontoClass = classes.next();
            final ExtendedIterator<OntClass> disjoints = ontoClass.listDisjointWith();

            do {
                try {
                    if (disjoints.hasNext()) {
                        // if there is even a single disjoint,
                        // there is no pitfall
                        return;
                    }
                } catch (final ConversionException exc) {
                    final String classUri = exc.getMessage().split(" ")[3];
                    final OntClass cAux = context.getModel().createClass(classUri);
                    context.addClassWarning(INFO, cAux,
                            "The attached resources do not have `rdf:type owl:Class` or equivalent");
                    classes = model.listNamedClasses();
                    continue;
                }
            } while (false);
        }

        // ahora probamos con owl2 OWL API
        for (final OWLAxiom owlAxiom : onto.getAxioms()) {
            // final AxiomType axiomType = owlAxiom.getAxiomType();
            // Only whether the axiom is a SubPropertyChainOf axiom
            final boolean isDisjointClasses = owlAxiom.isOfType(AxiomType.DISJOINT_CLASSES);
            final boolean isDisjointUnion = owlAxiom.isOfType(AxiomType.DISJOINT_UNION);

            if (isDisjointClasses || isDisjointUnion) {
                return;
            }
        }

        context.addResult(PITFALL_INFO, Collections.emptySet());
    }
}
