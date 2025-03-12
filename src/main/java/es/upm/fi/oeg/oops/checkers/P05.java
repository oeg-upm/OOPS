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
import es.upm.fi.oeg.oops.FilterInverses;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.filter.PropertyFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

// import com.sun.xml.internal.org.jvnet.fastinfoset.RestrictedAlphabet;

@MetaInfServices(Checker.class)
public class P05 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(5, null),
            Set.of(new PitfallCategoryId('N', 2), new PitfallCategoryId('S', 4)), Importance.CRITICAL,
            "Defining wrong inverse relationships",
            "Two relationships are defined as inverse relations when they are not necessarily inverse.",
            RuleScope.PROPERTY, Arity.TWO);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws IOException {

        // create lists for elements containing the pitfall
        final List<ObjectProperty> propertiesWithPitfall = new ArrayList<ObjectProperty>();

        for (final ObjectProperty property1 : new ExtIterIterable<>(context.getModel().listObjectProperties())) {
            final Set<? extends OntResource> setDomain1 = property1.listDomain().toSet();
            final Set<? extends OntResource> setRange1 = property1.listRange().toSet();

            List<ObjectProperty> listOfInverses;
            try {
                // si funciona el listInverseOf voy por aqui
                ExtendedIterator<? extends OntProperty> inverses = property1.listInverseOf();
                listOfInverses = (List<ObjectProperty>) inverses.toList();
            } catch (ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<ObjectProperty> listInv = FilterInverses.factory(ObjectProperty.class)
                        .apply(property1);
                listOfInverses = listInv.filter(context);
            }

            for (final ObjectProperty property2 : listOfInverses) {
                // System.out.println("ANALIZANDO: " + property1.getLocalName() +
                // " con " + property2.getLocalName());
                final Set<? extends OntResource> setDomain2 = property2.listDomain().toSet();
                final Set<? extends OntResource> setRange2 = property2.listRange().toSet();

                // Primero analizo el dominio1 y rango2
                // si no son null los analizo Si alguno o los dos es null no hay pitfall
                if ((!setDomain1.isEmpty()) && (!setRange2.isEmpty())) {
                    final boolean equalDomRan = new EqualGroupOfAxiomsAndOr(property1.listDomain(),
                            property2.listRange(), context, INFO).getEqualGroup();
                    if (!equalDomRan && !propertiesWithPitfall.contains((ObjectProperty) property1)
                            && !Checker.fromModels(property1) && !Checker.fromModels(property2)) {
                        propertiesWithPitfall.add(property1);
                        context.addResult(PITFALL_INFO, property1, property2);
                    }
                } else {
                    // System.out.println("Uno de los dos es vacio.");
                }

                // Ahora toca rango1 y dominio2
                // si no son null los analizo Si alguno o los dos es null no hay pitfall
                if ((!setDomain2.isEmpty()) && (!setRange1.isEmpty())) {
                    final boolean equalDomRan = new EqualGroupOfAxiomsAndOr(property1.listRange(),
                            property2.listDomain(), context, INFO).getEqualGroup();
                    if (!equalDomRan && !propertiesWithPitfall.contains((ObjectProperty) property1)
                            && !Checker.fromModels(property1) && !Checker.fromModels(property2)) {
                        propertiesWithPitfall.add(property1);
                        context.addResult(PITFALL_INFO, property1, property2);
                    }
                } else {
                    // System.out.println("Uno de los dos es vacio.");
                }
            }
        }
    }
}
