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
import es.upm.fi.oeg.oops.EqualGroupOfAxiomsAndOr;
import es.upm.fi.oeg.oops.ExtIterIterable;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P19 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(19, null),
            Set.of(new PitfallCategoryId('N', 2), new PitfallCategoryId('S', 4)), Importance.CRITICAL,
            "Defining multiple domains or ranges in properties",
            "The domain or range (or both) of a property (relationships and attributes) "
                    + "is defined by stating more than one rdfs:domain or rdfs:range statements. "
                    + "In OWL multiple rdfs:domain or rdfs:range axioms are allowed, "
                    + "but they are interpreted as conjunction, therefore being equivalent to the construct owl:intersectionOf. "
                    + "This pitfall is related to the common error that appears "
                    + "when defining domains and ranges described in [7].",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        scanProperties(context);
        scanAttributes(context);
    }

    private void scanProperties(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Set<ObjectProperty> withPitfall = new HashSet<>();
        for (final ObjectProperty ontoProperty : new ExtIterIterable<>(model.listObjectProperties())) {
            if (!Checker.fromModels(ontoProperty)) {
                // ExtendedIterator<? extends OntResource> itDomain = ontoProperty.listDomain();
                final List<? extends OntResource> listDomain = ontoProperty.listDomain().toList();
                boolean allEqualD = true;
                int i1 = 0;
                while (i1 + 1 < listDomain.size()) {
                    final List<OntResource> itDomainNi = List.of(listDomain.get(i1));
                    final List<OntResource> itDomainNi1 = List.of(listDomain.get(i1 + 1));
                    final boolean equalDomains = new EqualGroupOfAxiomsAndOr(itDomainNi, itDomainNi1, context, INFO)
                            .getEqualGroup();
                    final boolean sameOrEq = sameOrEquivalent(listDomain.get(i1), listDomain.get(i1 + 1));

                    if (!sameOrEq && !equalDomains) {
                        // System.out.println("Caso 1.2 Pitfall en : " + property1.getLocalName());
                        allEqualD = false;
                        break;
                    }
                    i1++;
                }

                // ExtendedIterator<? extends OntResource> itRange = ontoProperty.listRange();
                final List<? extends OntResource> listRange = ontoProperty.listRange().toList();
                boolean allEqualR = true;
                int i2 = 0;
                while (i2 + 1 < listRange.size()) {
                    final List<OntResource> itRangeNi = List.of(listRange.get(i2));
                    final List<OntResource> itRangeNi1 = List.of(listRange.get(i2 + 1));
                    final boolean equalRanges = new EqualGroupOfAxiomsAndOr(itRangeNi, itRangeNi1, context, INFO)
                            .getEqualGroup();
                    final boolean sameOrEq = sameOrEquivalent(listRange.get(i2), listRange.get(i2 + 1));

                    if (!sameOrEq && !equalRanges) {
                        // System.out.println("Caso 1.2 Pitfall en : " + property1.getLocalName());
                        allEqualR = false;
                        break;
                    }
                    i2++;
                }

                if (!allEqualD || !allEqualR) {
                    withPitfall.add(ontoProperty);
                }
            }
        }

        context.addResultsIndividual(PITFALL_INFO, withPitfall);
    }

    private void scanAttributes(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Set<DatatypeProperty> withPitfall = new HashSet<>();
        for (final DatatypeProperty ontoAttribute : new ExtIterIterable<>(model.listDatatypeProperties())) {
            final List<? extends OntResource> listDomain = ontoAttribute.listDomain().toList();
            boolean allEqualD = true;
            int i1 = 0;
            while (allEqualD && (i1 + 1 < listDomain.size())) {
                final List<OntResource> itDomainNi = List.of(listDomain.get(i1));
                final List<OntResource> itDomainNi1 = List.of(listDomain.get(i1 + 1));
                final boolean equalDomains = new EqualGroupOfAxiomsAndOr(itDomainNi, itDomainNi1, context, INFO)
                        .getEqualGroup();
                final boolean sameOrEq = sameOrEquivalent(listDomain.get(i1), listDomain.get(i1 + 1));

                if (!sameOrEq && !equalDomains) {
                    // System.out.println("Caso 1.2 Pitfall en : " + property1.getLocalName());
                    allEqualD = false;
                    withPitfall.add(ontoAttribute);
                }
                i1++;
            }

            final Set<? extends OntResource> setRange = ontoAttribute.listRange().toSet();
            if (setRange.size() > 1 && !Checker.fromModels(ontoAttribute)) {
                withPitfall.add(ontoAttribute);
            }
        }

        context.addResultsIndividual(PITFALL_INFO, withPitfall);
    }

    // private boolean isSubclassOf(final OntClass class1, final OntClass class2, final CheckingContext context) {

    // boolean isSubclass = false;
    // int i = 0;

    // List<OntClass> subClasses = class2.listSubClasses().toList();

    // // System.out.println("Para la clase: " + ontoClass.getLocalName());

    // while ((subClasses.size() > i) && !isSubclass) {
    // OntClass currentClass = subClasses.get(i);

    // boolean exc = true;
    // while (exc) {
    // try {
    // // por cada una de las clases de la lista miro sus subclases
    // List<OntClass> subClasses2 = currentClass.listSubClasses().toList();
    // } catch (org.apache.jena.ontology.ConversionException b) {
    // final String classUri = b.getMessage().split(" ")[3];
    // final OntClass cAux = context.getModel().createClass(classUri);
    // context.addClassWarning(INFO, cAux);
    // // System.err.println("WARNING: the class " + classUri + " does not have rdf:type
    // // owl:Class or equivalent");
    // continue;
    // }
    // exc = false;
    // }

    // currentClass = subClasses.get(i);
    // List<OntClass> subClasses2 = currentClass.listSubClasses().toList();
    // i++;

    // while (!subClasses2.isEmpty()) {
    // OntClass classToAdd = subClasses2.get(0);
    // subClasses2.remove(0);

    // if (!subClasses.contains(classToAdd)) {
    // subClasses.add(classToAdd);
    // }
    // }

    // isSubclass = subClasses.contains(class1);
    // }
    // return isSubclass;
    // }

    // private boolean isEquivalentOf(OntClass class1, OntClass class2, final CheckingContext context) {

    // boolean isEqClass = false;
    // int i = 0;

    // List<OntClass> eqClasses = class2.listEquivalentClasses().toList();

    // // System.out.println("Para la clase: " + ontoClass.getLocalName());

    // while ((eqClasses.size() > i) && !isEqClass) {

    // OntClass currentClass = eqClasses.get(i);

    // boolean exc = true;
    // while (exc) {

    // try {
    // // por cada una de las clases de la lista miro sus subclases
    // List<OntClass> subClasses2 = currentClass.listEquivalentClasses().toList();
    // } catch (org.apache.jena.ontology.ConversionException b) {
    // final String classUri = b.getMessage().split(" ")[3];
    // final OntClass cAux = context.getModel().createClass(classUri);
    // context.addClassWarning(INFO, cAux);
    // // System.err.println("WARNING: the class " + classUri + " does not have rdf:type
    // // owl:Class or equivalent");
    // continue;
    // }
    // exc = false;

    // }

    // currentClass = eqClasses.get(i);
    // List<OntClass> eqClasses2 = currentClass.listEquivalentClasses().toList();
    // i++;

    // while (!eqClasses2.isEmpty()) {
    // OntClass classToAdd = eqClasses2.get(0);
    // eqClasses2.remove(0);

    // if (!eqClasses.contains(classToAdd)) {
    // eqClasses.add(classToAdd);
    // }
    // }

    // isEqClass = eqClasses.contains(class1);
    // }
    // return isEqClass;
    // }

    private boolean sameOrEquivalent(final OntResource resource1, final OntResource resource2) {

        if (resource1.isClass() && resource2.isClass()) {
            final OntClass class1 = resource1.asClass();
            final OntClass class2 = resource2.asClass();
            if (class1.isURIResource() && class2.isURIResource() && (class1.hasEquivalentClass(class2)
                    || class2.hasEquivalentClass(class1) || class1.getURI().equals(class2.getURI()))) {
                return true;
            }
        }

        return false;
    }
}
