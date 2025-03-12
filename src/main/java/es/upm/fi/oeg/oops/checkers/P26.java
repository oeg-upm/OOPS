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
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

/**
 * cuando una relaci�n esta definida como simetrica y ademas tiene inversas definidas.
 */
@MetaInfServices(Checker.class)
public class P26 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(26, null),
            Set.of(new PitfallCategoryId('N', 1)), Importance.IMPORTANT,
            "Defining inverse relationships for a symmetric one", "A symmetric object property (owl:SymmetricProperty) "
                    + "is defined as inverse of another object property " + "(using owl:inverseOf).",
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
        final List<ObjectProperty> propertiesWithPitfall = new ArrayList<>();
        for (final ObjectProperty property1 : new ExtIterIterable<>(model.listObjectProperties())) {
            if (property1.isSymmetricProperty()) {
                final PropertyFilter<ObjectProperty> listInv = FilterInverses.factory(ObjectProperty.class)
                        .apply(property1);
                final List<ObjectProperty> inverses = listInv.filter(context);

                if ((inverses.size() > 0) && !propertiesWithPitfall.contains(property1)
                        && !Checker.fromModels(property1)) {
                    propertiesWithPitfall.add(property1);
                    context.addResult(PITFALL_INFO, property1);
                }

                // for (int i1 = 0; i1 < listOfInverses.size() ; i1++){
                //
                // ObjectProperty property2 = (ObjectProperty) listOfInverses.get(i1);
                //
                // // en este punto es simetrica y tiene inversas, luego mostramos los dos casos por separado
                // if (first){
                // first = false;
                // }
                //
                // //inversa de si misma
                // if (property1.getURI().equals(property2.getURI()) && !propertiesWithPitfall.contains(property1)
                // && !fromModels(property1)){
                //
                // propertiesWithPitfall.add(property1);
                // numPropWithPitfall++;
                //
                // listResults.add(property1.getURI());
                //
                // }
                //
                // //inversa de otra relaci�n
                // else if (!property1.getURI().equals(property2.getURI()) && !propertiesWithPitfall.contains(property1)
                // && !fromModels(property1)){
                //
                // propertiesWithPitfall.add(property1);
                // numPropWithPitfall++;
                //
                // listResults.add(property1.getURI());
                //
                // }
                // }
            }
        }
    }
}
