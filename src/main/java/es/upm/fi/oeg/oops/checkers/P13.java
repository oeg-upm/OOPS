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
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P13 implements Checker {

    private static final String TITLE = "Inverse relationships not explicitly declared";

    private static final PitfallInfo PITFALL_INFO_Y = new PitfallInfo(new PitfallId(13, 'Y'),
            Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 5)),
            Importance.MINOR, TITLE + " - With Suggestion",
            "This pitfall appears when any relationship "
                    + "(except for those that are defined as symmetric properties using owl:SymmetricProperty) "
                    + "does not have an inverse relationship (owl:inverseOf) defined within the ontology.",
            RuleScope.PROPERTY, Arity.TWO, "Suggestions for relationships without inverse: '%s'", AccompPer.INSTANCE);

    private static final PitfallInfo PITFALL_INFO_N = new PitfallInfo(new PitfallId(13, 'N'),
            Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 5)),
            Importance.MINOR, TITLE + " - No Suggestion",
            "This pitfall appears when any relationship "
                    + "(except for those that are defined as symmetric properties using owl:SymmetricProperty) "
                    + "does not have an inverse relationship (owl:inverseOf) defined within the ontology.",
            RuleScope.PROPERTY, Arity.ONE, "No suggestions for these relationships without inverse: '%s'",
            AccompPer.INSTANCE);

    private static final PitfallInfo PITFALL_INFO_S = new PitfallInfo(new PitfallId(13, 'S'),
            Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 5)),
            Importance.MINOR, TITLE + " - Symmetric or Transitive?",
            "This pitfall appears when any relationship "
                    + "(except for those that are defined as symmetric properties using owl:SymmetricProperty) "
                    + "does not have an inverse relationship (owl:inverseOf) defined within the ontology.",
            RuleScope.PROPERTY, Arity.ONE,
            // // TODO This message is a stub, make it real:
            "Symmetric or transitive suggestion: '%s'", AccompPer.INSTANCE);

    public static final CheckerInfo INFO = new CheckerInfo(TITLE,
            Set.of(PITFALL_INFO_Y, PITFALL_INFO_N, PITFALL_INFO_S));

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        final List<ObjectProperty> noTransitiveNoInverse = new ArrayList<>();

        // para cuando el dominio y el rango es el mismo
        final List<ObjectProperty> symmetricOrTransitiveSuggestion = new ArrayList<>();
        final List<ObjectProperty> withSuggestion = new ArrayList<>();

        // create lists for elements containing the pitfall
        final List<ObjectProperty> withPitfall = new ArrayList<>();

        for (final ObjectProperty property1 : new ExtIterIterable<>(model.listObjectProperties())) {
            // System.err.println("pitfall 13 primer bucle, hay " + numero + " relaciones y he hecho este bucle estas
            // veces " + ++i + " -->" + property1.getLocalName() );

            if (property1.getURI() != null) {
                // Si no es inversa de ninguna y no es simetrica la meto en el saco de todas con error
                if (!property1.hasInverse() && !isInverseOfAny(property1, model) && !property1.isSymmetricProperty()
                        && !Checker.fromModels(property1)) {
                    noTransitiveNoInverse.add(property1);
                }

                // miro a ver si le puedo encontrar pareja
                for (final ObjectProperty property2 : new ExtIterIterable<>(model.listObjectProperties())) {
                    if (property2.getURI() != null) {
                        if (!property1.getURI().contains(property2.getURI()) && !Checker.fromModels(property2)) {
                            // Si ninguna de las dos tiene inversas
                            if (property1.hasInverse() || property2.hasInverse()) {
                                // System.out.println("una de las dos tiene inversa definida");
                            } else if (isInverseOfAny(property1, model) || isInverseOfAny(property2, model)) {
                                // System.out.println("una de las dos esta definida como inversa");
                            } else if (property1.isSymmetricProperty() || property2.isSymmetricProperty()) {
                                // System.out.println("una de las dos es symetrica");
                            } else {
                                final boolean domain1 = property1.hasDomain(null);
                                final boolean range1 = property1.hasRange(null);

                                final boolean domain2 = property2.hasDomain(null);
                                final boolean range2 = property2.hasRange(null);

                                if (domain1 && range1 && domain2 && range2) {
                                    final boolean domain1range2 = new EqualGroupOfAxiomsAndOr(property1.listDomain(),
                                            property2.listRange(), context, INFO).getEqualGroup();
                                    final boolean domain2range1 = new EqualGroupOfAxiomsAndOr(property1.listRange(),
                                            property2.listDomain(), context, INFO).getEqualGroup();

                                    if (domain1range2 && domain2range1 && !withPitfall.contains(property1)
                                            && !withPitfall.contains(property2)) {
                                        withPitfall.add((ObjectProperty) property1);

                                        context.addResult(PITFALL_INFO_Y, property1, property2);

                                        // guardo por separado todas las que tenga sugerencia para luego restarlas
                                        withSuggestion.add(property1);
                                        withSuggestion.add(property2);
                                    }
                                }
                            }
                        } else { // son la misma --> Proponer symetrica?
                            if (!property1.isSymmetricProperty() && !property1.isTransitiveProperty()) {
                                // No es simetrica, vamos a ver si tiene igual dominio y rango
                                final boolean domain1 = property1.hasDomain(null);
                                final boolean range1 = property1.hasRange(null);

                                if (domain1 && range1) {
                                    final boolean domain1range1 = new EqualGroupOfAxiomsAndOr(property1.listDomain(),
                                            property1.listRange(), context, INFO).getEqualGroup();

                                    // se proponen si no es functional o asimétrico (no puedo preguntar por esto
                                    // en esta version de Jena)
                                    if (domain1range1 && !symmetricOrTransitiveSuggestion.contains(property1)
                                            && !property1.isFunctionalProperty()) {
                                        symmetricOrTransitiveSuggestion.add(property1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // final int numberWithSoTSuggestion = noTransitiveNoInverse.size();
        noTransitiveNoInverse.removeAll(withSuggestion);

        for (final ObjectProperty prop : noTransitiveNoInverse) {
            // context.addResult(new TransitiveNoInversePitfall(prop));
            context.addResult(PITFALL_INFO_N, prop);
        }

        for (final ObjectProperty prop : symmetricOrTransitiveSuggestion) {
            // context.addResult(new SymmetricOrTransitiveSuggestionPitfall(prop));
            context.addResult(PITFALL_INFO_S, prop);
        }
    }

    private boolean isInverseOfAny(final ObjectProperty property, final OntModel model) {

        boolean isInverse = false;
        for (final ObjectProperty propertyToAnalyze : new ExtIterIterable<>(model.listObjectProperties())) {
            try {
                if (propertyToAnalyze.listInverse().toSet().contains(property)) {
                    // System.out.println(property.getLocalName() + " esta definida como inversa de " +
                    // propertyToAnalaize.getLocalName());
                    isInverse = true;
                    break;
                }
            } catch (Exception exc) {
                // System.err.println("Exception in P13.isInverseOfAny en onto: " + model.getNsPrefixURI("") + "
                // property: " + property.getURI());
            }
        }
        return isInverse;
    }

    // public static class TransitiveNoInversePitfall implements Pitfall {
    //
    // private final List<OntResource> resources;
    //
    // public TransitiveNoInversePitfall(final ObjectProperty prop) {
    // this.resources = Collections.singletonList(prop);
    // }
    //
    // @Override
    // public PitfallInfo getInfo() {
    // return INFO;
    // }
    //
    // @Override
    // public List<OntResource> getResources() {
    // return resources;
    // }
    //
    // @Override
    // public String toString() {
    // // TODO This message is a stub, make it real:
    // return String.format("Transitive no inverse: '%s'", resources.get(0));
    // }
    // }
    //
    // public static class SymmetricOrTransitiveSuggestionPitfall implements Pitfall {
    //
    // private final List<OntResource> resources;
    //
    // public SymmetricOrTransitiveSuggestionPitfall(final ObjectProperty prop) {
    // this.resources = Collections.singletonList(prop);
    // }
    //
    // @Override
    // public PitfallInfo getInfo() {
    // return INFO;
    // }
    //
    // @Override
    // public List<OntResource> getResources() {
    // return resources;
    // }
    //
    // @Override
    // public String toString() {
    // // TODO This message is a stub, make it real:
    // return String.format("Symmetric or transitive suggestion: '%s'", resources.get(0));
    // }
    // }
}
