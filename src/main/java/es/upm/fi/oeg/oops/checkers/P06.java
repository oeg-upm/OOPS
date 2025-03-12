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
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P06 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(6, null),
            Set.of(new PitfallCategoryId('N', 2), new PitfallCategoryId('S', 4)), Importance.CRITICAL,
            "Including cycles in a class hierarchy",
            "A cycle between two classes in a hierarchy is included in the ontology. "
                    + "A cycle appears when some class A has a subclass (directly or indirectly) B, "
                    + "and at the same time B is a superclass (directly or indirectly) of A. "
                    + "This pitfall was first identified in [3]. "
                    + "Guidelines presented in [2] also provide recommendations to avoid this pitfall.",
            RuleScope.CLASS, Arity.ONE_PLUS, "These classes are involved in a cycle", AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws IOException {

        scanClasses(context);
    }

    private void scanClasses(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Set<OntClass> withPitfall = new HashSet<>();
        final List<OntClass> classes = model.listNamedClasses().toList();
        for (int i = 0; i < classes.size(); i++) {
            OntClass ontoClass = classes.get(i);
            if (ontoClass.hasURI("http://www.w3.org/2002/07/owl#Thing")) {
                // TODO FIXME Too many i++ around here; looks fishy
                if (classes.size() > i++) {
                    ontoClass = classes.get(i++);
                } else {
                    break;
                }
            }

            final List<OntClass> superClasses = new ArrayList<>();
            for (final OntClass superClass : new ExtIterIterable<>(ontoClass.listSuperClasses())) {
                if (superClass.isURIResource()) {
                    superClasses.add(superClass);
                }
            }

            // System.out.println("Para la clase: " + ontoClass.getLocalName());
            for (final OntClass currentClass : superClasses) {
                // por cada una de las clases de la lista miro sus subclases
                ExtIterIterable<OntClass> superClasses2 = null;
                while (true) {
                    try {
                        superClasses2 = new ExtIterIterable<>(currentClass.listSuperClasses());
                        break;
                    } catch (final ConversionException exc2) {
                        final String classUri = exc2.getMessage().split(" ")[3];
                        final OntClass cAux = context.getModel().createClass(classUri);
                        context.addClassWarning(INFO, cAux,
                                "The attached resources do not have `rdf:type owl:Class` or equivalent");
                        continue;
                    }
                }

                for (final OntClass classToAdd : superClasses2) {
                    if (!superClasses.contains(classToAdd) && classToAdd.isURIResource()) {
                        superClasses.add(classToAdd); // TODO FIXME This will cause `java.util.ConcurrentModificationException`
                    }
                }

                // System.out.println("Esto es una subclase: " + currentClass.getLocalName());

                final boolean repeatedClass = superClasses.contains(ontoClass);
                final boolean repeatedName = containsClassWithName(superClasses, ontoClass);
                if (repeatedClass || repeatedName) {
                    withPitfall.add(ontoClass);
                    break;
                }
                if (repeatedClass) {
                    break;
                }
            }
        }

        if (withPitfall.size() > 0) {
            final Map<String, List<OntClass>> groups = new HashMap<>();
            for (final OntClass currentC : withPitfall) {
                // iterate through the lists of lists
                boolean isInAtLeastOneHierarchy = false;
                for (final List<OntClass> classesInGroup : groups.values()) {
                    if (isInHierarchyWithAll(classesInGroup, currentC)) {
                        classesInGroup.add(currentC);
                        isInAtLeastOneHierarchy = true;
                    }
                }

                if (!isInAtLeastOneHierarchy) {
                    // if not is not in at least one hierarchy it creates a new group.
                    groups.put(currentC.getURI(), new ArrayList<>(List.of(currentC)));
                }
            }

            // store the result-lists
            for (final List<OntClass> value : groups.values()) {
                context.addResult(PITFALL_INFO, value);
            }
        }
    }

    private boolean containsClassWithName(final List<OntClass> subClasses, final OntClass classToCompare) {

        // String nameClassToCompare = classToCompare.getLocalName();
        final String clsToCompare = classToCompare.getURI();

        boolean contains = false;
        for (final OntClass currentClass : subClasses) {
            if (currentClass.isURIResource()) {
                // String nameOtherClass = currentClass.getLocalName();
                final String clsOther = currentClass.getURI();
                if (clsToCompare.equalsIgnoreCase(clsOther)) {
                    contains = true;
                    break;
                    // System.out.println(nameClasstoCompare + " Igual a " + nameOtherClass);
                    // System.out.println(classToCompare.getNameSpace() + " Igual a " + currentClass.getNameSpace());
                }
            }

        }
        return contains;
    }

    private boolean isInHierarchyWithAll(final List<OntClass> classes, final OntClass classToCompare) {

        boolean inHierarchy = true;
        for (final OntClass c : classes) {
            final boolean isSub = classToCompare.hasSubClass(c, false);
            final boolean isSuper = classToCompare.hasSuperClass(c, false);

            if (!isSub && !isSuper) {
                inHierarchy = false;
                break;
            }
        }
        return inHierarchy;
    }
}
