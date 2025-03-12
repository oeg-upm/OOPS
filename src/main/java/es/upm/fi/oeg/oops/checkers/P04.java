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
import es.upm.fi.oeg.oops.FlattenAxiom;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.jena.ontology.BooleanClassDescription;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P04 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(4, null),
            Set.of(new PitfallCategoryId('N', 4), new PitfallCategoryId('N', 5), new PitfallCategoryId('S', 5)),
            Importance.MINOR, "Creating unconnected ontology elements",
            "Ontology elements (classes, object properties and datatype properties) exist in isolation, "
                    + "with no relation to the rest of the ontology.",
            RuleScope.CLASS, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        // create lists for elements containing the pitfall
        final List<OntClass> classWithPitfall = new ArrayList<>();

        // Aqui voy guardando las clases que aparecen en los axiomas que voy recorriendo
        final Set<OntClass> involvedSet = new HashSet<>();

        // A�adir los dominios y los rangos de las relaciones
        for (final ObjectProperty prop : new ExtIterIterable<>(model.listObjectProperties())) {
            // quiza de problemas con los cuantificadores
            involvedSet.addAll(prop.listDomain().toList().stream().map((res) -> (OntClass) res).toList());
            involvedSet.addAll(prop.listRange().toList().stream().map((res) -> (OntClass) res).toList());
        }

        // A�adir los dominios de los atributos
        for (final DatatypeProperty att : new ExtIterIterable<>(model.listDatatypeProperties())) {
            involvedSet.addAll(att.listDomain().toList().stream().map((res) -> (OntClass) res).toList());
        }

        // cojo las clases que esten en los axiomas de la onto
        final FlattenAxiom flatten = new FlattenAxiom();
        flatten.calculateClassesInAxiomsOnto(context, INFO);
        final Set<OntClass> classesInAxioms = flatten.getClassesInAxiomsOnto();
        involvedSet.addAll(classesInAxioms);

        final Set<? extends OntClass> classesInvolved = flatten(context, involvedSet);

        // System.out.println("involvedset es DESPUES de aplanar es: " + classesInvolved);

        final Set<OntClass> classesUnconnected = new HashSet<>();
        final Set<OntClass> classesConnected = new HashSet<>();
        final Set<OntClass> connectedCopy = new HashSet<>();

        // NOTE We need to create a list, instead of using an ExtIterable,
        // because we create new classes in the model within the following loop,
        // and thus using the iterator would cause `java.util.ConcurrentModificationException`.
        final List<OntClass> frozenOntClasses = model.listNamedClasses().toList();
        for (final OntClass ontoClass : frozenOntClasses) {
            // compruebo si tiene axiomas en equivalentes o en super-class LO CONSIDERO OTRA FORMA DE CONEXI�N
            final List<OntClass> axiomsAll = new ArrayList<>();

            // ExtendedIterator<OntClass> superClasses = ontoClass.listSuperClasses();
            final List<Function<OntClass, ExtendedIterator<OntClass>>> classesExtractors = List.of(
                    (OntClass ontCls) -> ontCls.listSuperClasses(), (OntClass ontCls) -> ontCls.listSubClasses(),
                    (OntClass ontCls) -> ontCls.listEquivalentClasses());
            for (final Function<OntClass, ExtendedIterator<OntClass>> clsExt : classesExtractors) {
                try {
                    axiomsAll.addAll(clsExt.apply(ontoClass).toList());
                } catch (final ConversionException exc) {
                    final String classUri = exc.getMessage().split(" ")[3];
                    final OntClass cAux = model.createClass(classUri);
                    context.addClassWarning(INFO, cAux,
                            "The attached resources do not have `rdf:type owl:Class` or equivalent");
                }
            }

            boolean hasComplexAxiom = false;
            for (final OntClass axiom : axiomsAll) {
                if (!axiom.isURIResource()) {
                    hasComplexAxiom = true;
                    break;
                }
            }

            final boolean isHierarchyRoot;
            try {
                isHierarchyRoot = ontoClass.isHierarchyRoot();
            } catch (ConversionException exc) {
                final String classUri = exc.getMessage().split(" ")[3];
                final OntClass cAux = model.createClass(classUri);
                context.addClassWarning(INFO, cAux,
                        "The attached resources do not have `rdf:type owl:Class` or equivalent");
                continue;
            }

            if (!classesInvolved.contains(ontoClass) && isHierarchyRoot
                    && !ontoClass.hasURI("http://www.w3.org/2002/07/owl#Thing") && !hasComplexAxiom) {
                classesUnconnected.add(ontoClass);
            } else {
                classesConnected.add(ontoClass);
                connectedCopy.add(ontoClass);
            }
        }

        // Coger las clases equivalentes de las clases conectadas
        final Set<OntClass> involvedEqui = new HashSet<OntClass>();
        for (final OntClass cls : connectedCopy) {
            // cojo la clase y annado sus equivalentes a las
            final Set<OntClass> equivalents = cls.listEquivalentClasses().toSet();

            if (!equivalents.isEmpty()) {
                involvedEqui.addAll(equivalents);
            }
        }

        // por cada una no conectada miro si tiene equivalentes en las conectadas o esta en los equvilaentes de las
        // conectadas
        for (final OntClass cls2 : classesUnconnected) {
            boolean connected = false;

            // si no estan en las equivalentes de las conectadas
            if (!involvedEqui.contains(cls2)) {
                // miro si tiene equivalentes conectadas
                for (final OntClass cls3 : new ExtIterIterable<OntClass>(cls2.listEquivalentClasses())) {
                    if (classesConnected.contains(cls3)) {
                        connected = true;
                        break;
                    }
                }
                if (!connected && !classWithPitfall.contains(cls2) && (cls2.getLocalName() != null)
                        && !Checker.fromModels(cls2)) {
                    classWithPitfall.add((OntClass) cls2);
                    context.addResult(PITFALL_INFO, cls2);
                }
            }
        }
        // System.out.println("Results for pitfall P4. Creating unconnected ontology elements: ");
    }

    private Set<OntClass> flatten(final CheckingContext context, final Set<OntClass> setInput) {

        final OntModel model = context.getModel();

        final Set<OntClass> outSet = new HashSet<>();
        if (setInput != null) {
            // final Iterator<OntResource> iteratorInput = setInput.iterator();

            for (final OntClass classToFlatten : setInput) {
                // while (iteratorInput.hasNext()) {
                // OntClass classToFlatten = (OntClass) iteratorInput.next();

                if (classToFlatten.isUnionClass()) {
                    ExtendedIterator<OntClass> unionIt = (ExtendedIterator<OntClass>) ((BooleanClassDescription) classToFlatten
                            .asUnionClass()).listOperands();

                    boolean exc = true;
                    while (exc) {
                        try {
                            Set<? extends OntClass> unionSet = unionIt.toSet();
                        } catch (ConversionException exc2) {
                            final String classUri = exc2.getMessage().split(" ")[3];
                            final OntClass cAux = model.createClass(classUri);
                            context.addClassWarning(INFO, cAux,
                                    "The attached resources do not have `rdf:type owl:Class` or equivalent");
                            continue;
                        }
                        exc = false;
                    }

                    unionIt = (ExtendedIterator<OntClass>) ((BooleanClassDescription) classToFlatten.asUnionClass())
                            .listOperands();
                    Set<? extends OntClass> unionSet = unionIt.toSet();
                    outSet.addAll(unionSet);
                }
                if (classToFlatten.isIntersectionClass()) {
                    ExtendedIterator<OntClass> intersectionIt = (ExtendedIterator<OntClass>) ((BooleanClassDescription) classToFlatten
                            .asIntersectionClass()).listOperands();

                    boolean exc = true;
                    while (exc) {
                        try {
                            Set<? extends OntClass> intersectionSet = intersectionIt.toSet();
                        } catch (ConversionException exc2) {
                            final String classUri = exc2.getMessage().split(" ")[3];
                            final OntClass cAux = model.createClass(classUri);
                            context.addClassWarning(INFO, cAux,
                                    "The attached resources do not have `rdf:type owl:Class` or equivalent");
                            continue;
                        }
                        exc = false;
                    }

                    intersectionIt = (ExtendedIterator<OntClass>) ((BooleanClassDescription) classToFlatten
                            .asIntersectionClass()).listOperands();
                    Set<? extends OntClass> intersectionSet = intersectionIt.toSet();

                    outSet.addAll(intersectionSet);
                } else if (classToFlatten.isAnon()) {
                } else {
                    outSet.add(classToFlatten);
                }
            }
        } else {
            // System.out.println(setInput);
        }
        return outSet;
    }
}
