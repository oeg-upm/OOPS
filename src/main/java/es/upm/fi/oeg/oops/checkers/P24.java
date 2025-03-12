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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P24 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(24, null),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('S', 4)), Importance.IMPORTANT,
            "Using recursive definitions",
            "An ontology element (a class, an object property or a datatype property) "
                    + "is used in its own definition. " + "Some examples of this would be: "
                    + "(a) the definition of a class as the enumeration of several classes including itself; "
                    + "(b) the appearance of a class within its owl:equivalentClass or rdfs:subClassOf axioms; "
                    + "(c) the appearance of an object property in its rdfs:domain or range rdfs:range definitions; or "
                    + "(d) the appearance of a datatype property in its rdfs:domain definition.",
            RuleScope.RESOURCE, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        analyze(context, () -> model.listNamedClasses(), (OntClass ontoRes) -> {
            final FlattenAxiom flattenAxiom = new FlattenAxiom();
            flattenAxiom.calculateClassesInAxiomsClass(ontoRes, context, INFO);
            return flattenAxiom.getClassesInAxiomsOnto();
        });
        analyze(context, () -> model.listObjectProperties(), (ObjectProperty ontoRes) -> {
            final FlattenAxiom flattenAxiom = new FlattenAxiom();
            flattenAxiom.calculatePropInAxiomsProp(ontoRes, context, INFO);
            return flattenAxiom.getPropertiesInAxiomsProperty();
        });
        analyze(context, () -> model.listDatatypeProperties(), (DatatypeProperty ontoRes) -> {
            final FlattenAxiom flattenAxiom = new FlattenAxiom();
            flattenAxiom.calculateAttrInAxiomsProp(ontoRes, context, INFO);
            return flattenAxiom.getAttributesInAxiomsProperty();
        });
    }

    private <PT extends OntResource> void analyze(final CheckingContext context,
            final Supplier<ExtendedIterator<PT>> allResGen, final Function<PT, Set<PT>> flattener) {

        // create lists for elements containing the pitfall
        final Set<PT> withPitfall = new HashSet<>();
        for (final PT ontoRes : new ExtIterIterable<>(allResGen.get())) {
            // cojo las clases que esten en los axiomas de la clase que queremos analizar
            final Set<PT> resourcesInAxiomsInClass = flattener.apply(ontoRes);
            if (resourcesInAxiomsInClass.contains(ontoRes) && !Checker.fromModels(ontoRes)) {
                withPitfall.add(ontoRes);
            }
        }

        context.addResultsIndividual(PITFALL_INFO, withPitfall);
    }
}
