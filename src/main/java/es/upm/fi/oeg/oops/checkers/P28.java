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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;

/**
 * When a relation is symmetric, but its range and its domain are not the same.
 */
@MetaInfServices(Checker.class)
public class P28 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(
            new PitfallId(28, null), Set.of(new PitfallCategoryId('N', 2)), Importance.CRITICAL,
            "Defining wrong symmetric relationships", "A relationship is defined as symmetric, "
                    + "using owl:SymmetricProperty, " + "when the relationship is not necessarily symmetric.",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {
        scanInternal(context, (final ObjectProperty prop) -> prop.isSymmetricProperty(), PITFALL_INFO);
    }

    public static void scanInternal(final CheckingContext context, final Predicate<ObjectProperty> selector,
            final PitfallInfo pitfallInfo) {

        final OntModel model = context.getModel();

        // create lists for elements containing the pitfall
        final List<ObjectProperty> propertiesWithPitfall = new ArrayList<>();
        for (final ObjectProperty property1 : new ExtIterIterable<>(model.listObjectProperties())) {
            final Set<? extends OntResource> setDomain1 = property1.listDomain().toSet();
            final Set<? extends OntResource> setRange1 = property1.listRange().toSet();

            if (property1.isTransitiveProperty()) {
                // Primero analizo el dominio1 y rango1

                // si no son null los analizo Si alguno o los dos es null no hay pitfall
                if ((!setDomain1.isEmpty()) && (!setRange1.isEmpty())) {
                    final boolean equalDomRan = new EqualGroupOfAxiomsAndOr(property1.listDomain(),
                            property1.listRange(), context, INFO).getEqualGroup();
                    final boolean sameOrEq = Checker.sameOrEquivalent(setDomain1, setRange1, model);

                    if (!sameOrEq && !equalDomRan && !propertiesWithPitfall.contains((ObjectProperty) property1)
                            && !Checker.fromModels(property1)) {
                        propertiesWithPitfall.add((ObjectProperty) property1);
                        context.addResult(pitfallInfo, property1);
                    }
                } else {
                    // System.out.println("One of the two is empty.");
                }
            }
        }
    }
}
