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
import es.upm.fi.oeg.oops.ExtIterIterable;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

/**
 * Creating synonyms as classes.
 */
@MetaInfServices(Checker.class)
public class P02 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(2, null),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 6)),
            Importance.MINOR, "Creating synonyms as classes",
            "Several classes whose identifiers are synonyms "
                    + "are created and defined as equivalent (owl:equivalentClass) " + "in the same namespace. "
                    + "This pitfall is related to the guidelines presented in [2], "
                    + "which explain that synonyms for the same concept " + "do not represent different classes.",
            RuleScope.CLASS, Arity.TWO);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        // NOTE We need to create a list, instead of using an ExtIterable,
        // because we create new classes in the model within the following loop,
        // and thus using the iterator would cause `java.util.ConcurrentModificationException`.
        final List<OntClass> frozenOntClasses = model.listNamedClasses().toList();
        for (final OntClass ontoClass : frozenOntClasses) {
            final String localName = ontoClass.getLocalName();
            // System.out.println("Analizando inverse of: " + localName);

            // get the equivalent classes for a given class
            ExtendedIterator<OntClass> equivalents = null;
            do {
                try {
                    equivalents = ontoClass.listEquivalentClasses();
                    equivalents.toList();
                } catch (final ConversionException exc) {
                    final String classUri = exc.getMessage().split(" ")[3];
                    // TODO Check: Is it not a problem to create new classes in the source model,
                    //             which then are analyzed also by other pitfalls?
                    //             It very likely is, so we would need to separate this,
                    //             or re-load the model for each pitfall,
                    //             which might be OK, performance wise! -> test!
                    final OntClass cAux = model.createClass(classUri);
                    context.addClassWarning(INFO, cAux,
                            "The attached resources do not have `rdf:type owl:Class` or equivalent");
                    continue;
                }
            } while (false);

            for (final OntClass equivalent : new ExtIterIterable<>(equivalents)) {
                if (equivalent.getURI() != null) {
                    final String localNameEq = equivalent.getLocalName();
                    final String namespace1 = ontoClass.getURI().replace(localName, "");
                    final String namespace2 = equivalent.getURI().replace(localNameEq, "");

                    if (namespace1.equals(namespace2) || ontoClass.getNameSpace().equals(equivalent.getNameSpace())) {
                        final String lowerClass = localName.toLowerCase();
                        final String lowerEq = localNameEq.toLowerCase();

                        // si no son el mismo string es pitfall.
                        // Supongo que son sinonimos.
                        if (!lowerClass.equals(lowerEq) && !Checker.fromModels(ontoClass)) {
                            context.addResult(PITFALL_INFO, ontoClass, equivalent);
                        }
                    }
                }
            }
        }
    }
}
