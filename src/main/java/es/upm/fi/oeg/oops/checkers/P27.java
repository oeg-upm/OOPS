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
import es.upm.fi.oeg.oops.FilterEquivalents;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.Linter;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.filter.PropertyFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.kohsuke.MetaInfServices;

/**
 * If two relations are equivalent, but they do not have the same domain and range.
 */
@MetaInfServices(Checker.class)
public class P27 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(27, null),
            Set.of(new PitfallCategoryId('N', 2)), Importance.CRITICAL, "Defining wrong equivalent properties",
            "Two object properties or two datatype properties are defined as equivalent, "
                    + "using owl:equivalentProperty, " + "even though they do not have the same semantics.",
            RuleScope.PROPERTY, Arity.TWO, "Possibly inequivalent properties", null);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        analyze(context, () -> model.listObjectProperties(), FilterEquivalents.factory(ObjectProperty.class), true);
        analyze(context, () -> model.listDatatypeProperties(), FilterEquivalents.factory(DatatypeProperty.class),
                false);
    }

    private <PT extends OntProperty> void addToOutput(CheckingContext context, PT property1, PT property2) {
        Model outputModel = context.getOutputModel();

        final Resource wrongEquivalentPropertyType = outputModel
                .createResource(Linter.NS_OOPS_DEF + "wrongEquivalentProperty");
        final Property valueProp = outputModel.createProperty(Linter.NS_OOPS_DEF + "hasAffectedElement");

        final Resource res = outputModel.createResource(Linter.NS_OOPS_DATA + UUID.randomUUID().toString());
        outputModel.add(res, RDF.type, wrongEquivalentPropertyType);

        res.addProperty(valueProp, property1);
        res.addProperty(valueProp, property2);

        context.addResult(PITFALL_INFO, res);
    }

    private <PT extends OntProperty> void analyze(final CheckingContext context,
            final Supplier<ExtendedIterator<PT>> allResGen, final Function<PT, PropertyFilter<PT>> equivalentsListerFac,
            final boolean compareRange) {

        final List<PT> resWithPitfall = new ArrayList<>();
        final List<String> resWithPitfall2 = new ArrayList<>();

        for (final PT property1 : new ExtIterIterable<>(allResGen.get())) {
            final Set<? extends OntResource> setDomain1 = property1.listDomain().toSet();
            final Set<? extends OntResource> setRange1 = property1.listRange().toSet();

            final PropertyFilter<PT> listEquivalents = equivalentsListerFac.apply(property1);
            final List<PT> equivalents = listEquivalents.filter(context);
            for (final PT property2 : equivalents) {
                // System.err.println("ANALIZANDO: " + property1.getLocalName() + " con " + property2.getLocalName());
                final Set<? extends OntResource> setDomain2 = property2.listDomain().toSet();
                final Set<? extends OntResource> setRange2 = property2.listRange().toSet();

                final String prop1prop2 = property1.getURI().toString() + property2.getURI().toString();
                final String prop2prop1 = property2.getURI().toString() + property1.getURI().toString();

                // Primero analizo el dominio1 y dominio2
                // si no son null los analizo Si alguno o los dos es null no hay pitfall
                if ((!setDomain1.isEmpty()) && (!setDomain2.isEmpty())) {
                    final boolean equalDomRan = new EqualGroupOfAxiomsAndOr(property1.listDomain(),
                            property2.listDomain(), context, INFO).getEqualGroup();
                    final boolean sameOrEq = Checker.sameOrEquivalent(setDomain1, setDomain2);

                    if (!sameOrEq && !equalDomRan && !resWithPitfall2.contains(prop1prop2)
                            && !Checker.fromModels(property1) && !Checker.fromModels(property2)) {
                        resWithPitfall.add((PT) property1);

                        resWithPitfall2.add(prop1prop2);
                        resWithPitfall2.add(prop2prop1);

                        addToOutput(context, property1, property2);

                        //context.addResult(PITFALL_INFO, property1, property2);
                    }
                } else {
                    // System.out.println("One of the two is empty.");
                }

                // Ahora toca rango1 y rango2
                // si no son null los analizo Si alguno o los dos es null no hay pitfall
                if (!setRange2.isEmpty() && !setRange1.isEmpty()) {
                    final boolean equalDomRan = new EqualGroupOfAxiomsAndOr(property1.listRange(),
                            property2.listRange(), context, INFO).getEqualGroup();
                    final boolean sameOrEq;
                    if (compareRange) {
                        sameOrEq = Checker.sameOrEquivalent(setRange1, setRange2);
                    } else {
                        sameOrEq = Checker.sameOrEquivalent(setDomain1, setDomain2);
                    }

                    if (!sameOrEq && !equalDomRan && !resWithPitfall2.contains(prop1prop2)
                            && !Checker.fromModels(property1) && !Checker.fromModels(property2)) {
                        resWithPitfall.add((PT) property1);

                        resWithPitfall2.add(prop1prop2);
                        resWithPitfall2.add(prop2prop1);

                        //context.addResult(PITFALL_INFO, property1, property2);
                        addToOutput(context, property1, property2);
                    }
                } else {
                    // System.out.println("One of the two is empty.");
                }
            }
        }
    }

}
