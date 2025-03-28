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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P12 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(12, null),
            Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 5)),
            Importance.IMPORTANT, "Equivalent properties not explicitly declared",
            "The ontology lacks information about equivalent properties (owl:equivalentProperty) "
                    + "in the cases of duplicated relationships and/or attributes.",
            RuleScope.PROPERTY, Arity.TWO_PLUS, "These properties could be defined as equivalent", AccompPer.INSTANCE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        analyze(context, () -> model.listObjectProperties());
        analyze(context, () -> model.listDatatypeProperties());
    }

    private <PT extends OntProperty> void analyze(final CheckingContext context,
            final Supplier<ExtendedIterator<PT>> allPropsGen) {

        final Map<String, Set<OntProperty>> mapResource = new HashMap<>();
        for (final PT element1 : new ExtIterIterable<>(allPropsGen.get())) {
            // Si no tiene equivalentes ni es equivalente de ninguna entonces la analizo
            if (!element1.listEquivalentProperties().hasNext() && !isEquivalentOfAny(element1, allPropsGen)) {
                String localName1 = element1.getLocalName();
                if (localName1 != null) {
                    localName1 = localName1.replaceAll("\\W", "");
                    localName1 = localName1.replaceAll("-", "");
                    localName1 = localName1.replaceAll("_", "");
                    localName1 = localName1.toLowerCase();

                    for (final PT element2 : new ExtIterIterable<>(allPropsGen.get())) {
                        final boolean isSubP12 = this.isSubProperty(element1, element2, context);
                        final boolean isSupP21 = this.isSubProperty(element2, element1, context);
                        // si no son la misma y no son subproperties
                        // Si no tiene equivalentes ni es equivalente de ninguna entonces las analizo
                        if (!element1.getURI().contains(element2.getURI()) && !(isSubP12 || isSupP21)
                                && !element2.listEquivalentProperties().hasNext()) {
                            String localName2 = element2.getLocalName();
                            if (localName2 != null) {
                                localName2 = localName2.replaceAll("\\W", "");
                                localName2 = localName2.replaceAll("-", "");
                                localName2 = localName2.replaceAll("_", "");
                                localName2 = localName2.toLowerCase();

                                if (localName1.equalsIgnoreCase(localName2) && !Checker.fromModels(element1)
                                        && !Checker.fromModels(element2)) {
                                    final Set<OntProperty> values;
                                    if (!mapResource.containsKey(localName1)) {
                                        values = new HashSet<OntProperty>();
                                    } else {
                                        values = mapResource.get(localName1);
                                    }
                                    values.add(element1);
                                    values.add(element2);
                                    mapResource.put(localName1, values);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (final Set<OntProperty> values : mapResource.values()) {
            context.addResult(PITFALL_INFO, values);
        }
    }

    public <PT extends OntProperty> boolean isEquivalentOfAny(final PT property,
            final Supplier<ExtendedIterator<PT>> allPropsGen) {

        boolean isEquivalent = false;
        for (final PT propertyToAnalyze : new ExtIterIterable<>(allPropsGen.get())) {
            if (propertyToAnalyze.hasEquivalentProperty(property)) {
                // System.out.println(property.getLocalName() + " esta definida como eq de " +
                // propertyToAnalyze.getLocalName());
                isEquivalent = true;
                break;
            }
        }
        return isEquivalent;
    }

    boolean isSubProperty(final OntProperty property1, final OntProperty property2, final CheckingContext context) {

        // get properties
        ExtendedIterator<? extends OntProperty> p = property1.listSubProperties(false);

        do {
            try {
                p.toList();
            } catch (final ConversionException exc) {
                final String classUri = exc.getMessage().split(" ")[3];
                final OntClass cAux = context.getModel().createClass(classUri);
                context.addClassWarning(INFO, cAux,
                        "The attached resources do not have `rdf:type owl:Class` or equivalent");
                continue;
            }
            p = property1.listSubProperties(false);
        } while (false);
        final List<OntProperty> subPList = (List<OntProperty>) p.toList();

        final List<OntProperty> subPListVisited = new ArrayList<OntProperty>();
        // extend list of sub-properties until the visited list
        // is equal to the sub-properties list
        int i = 0;
        while (subPList.size() > subPListVisited.size()) {
            final OntProperty propertyToExtend = subPList.get(i);
            subPList.addAll(propertyToExtend.listSubProperties(false).toList());
            subPListVisited.add(propertyToExtend);
            i++;
        }

        final boolean isSubProperty = subPList.contains(property2);
        return isSubProperty;
    }
}
