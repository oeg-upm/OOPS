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
import es.upm.fi.oeg.oops.FilterInverses;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.filter.PropertyFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

/**
 * Nov-2012: Cada pitfall tiene su salida independiente asi que se quita la condici�n de que no sea simetrica.
 *
 * marzo-2012???��? cuando una relaci�n esta definida como inversa de si misma y no es simetrica. Si es simetrica e
 * inversa de algo se trata en la extensi�n 2
 */
@MetaInfServices(Checker.class)
public class P25 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(25, null),
            Set.of(new PitfallCategoryId('N', 1)), Importance.IMPORTANT, "Defining a relationship as inverse to itself",
            "A relationship is defined as inverse of itself. " + "In this case, "
                    + "this relationship could have been defined as owl:SymmetricProperty instead.",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        // create lists for elements containing the pitfall
        final List<ObjectProperty> withPitfall = new ArrayList<>();
        for (final ObjectProperty property1 : new ExtIterIterable<>(model.listObjectProperties())) {
            // nuevo codigo para sacar inversas si falla el de jena
            List<ObjectProperty> inverses;
            try {
                // si funciona el listInverseOf voy por aqui
                inverses = ((ExtendedIterator<ObjectProperty>) property1.listInverseOf()).toList();
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<ObjectProperty> listInv = FilterInverses.factory(ObjectProperty.class)
                        .apply(property1);
                inverses = listInv.filter(context);
            }

            for (final ObjectProperty property2 : inverses) {
                final String uri1 = property1.getURI();
                final String uri2 = property2.getURI();

                if (uri1 != null && uri2 != null && uri1.equals(uri2) && !withPitfall.contains(property1)
                        && !Checker.fromModels(property1)) {
                    // && !property1.isSymmetricProperty()){ // esto se cambio en nov 2012
                    withPitfall.add(property1);
                    // System.out.println(property1.getURI() + " may be defined as symmetric " );
                }
            }
        }

        context.addResultsIndividual(PITFALL_INFO, withPitfall);
    }
}
