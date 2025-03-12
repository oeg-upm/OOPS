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
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.Pitfall;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.kohsuke.MetaInfServices;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

@MetaInfServices(Checker.class)
public class P33 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(33, null),
            Set.of(new PitfallCategoryId('N', 1)), Importance.MINOR, "Creating a property chain with just one property",
            "The OWL 2 construct owl:propertyChainAxiom "
                    + "allows a property to be defined as the composition of several properties "
                    + "(see <http://www.w3.org/TR/owl2-new-features/F8:_Property_Chain_Inclusion> "
                    + "for additional details). " + "In this sense, when an individual \"a\" is connected "
                    + "with an individual \"b\" by a chain of two or more object properties "
                    + "(specified in the antecedent of the chain), " + "it is necessary to connect \"a\" with \"b\" "
                    + "by using the object property in the consequent of the chain. "
                    + "This pitfall consists in creating a property chain (owl:propertyChainAxiom) "
                    + "that includes only one property in the antecedent part.",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws IOException {

        final OWLOntology onto = context.getModelOwl();

        final HashMap<String, AxiomExtras> preResults = new HashMap<>();

        // Take the all axioms that appear on our ontology
        for (final OWLAxiom owlAxiom : onto.getAxioms()) {
            final AxiomType<?> axiomType = owlAxiom.getAxiomType();
            final String axiom_name = axiomType.getName();
            // Only if the axiom is a SubPropertyChainOf axiom
            if (axiom_name.equals("SubPropertyChainOf")) {
                /*
                 * The structure of owl axiom if it is SubPropertyChainOf's kind is
                 * SubObjectPropertyOf(ObjectPropertyChain( <http://uri/hasParent> <http://uri/hasParent> )
                 * <http://uri/hasGrandparent>)
                 */

                final String statementAxiom = owlAxiom.toString();
                final String[] partAxiom = statementAxiom.split("[\\(|\\)]", 0);
                /*
                 * After split with "(" and ")" tokens we have an array with 0. SubObjectPropertyOf 1.
                 * ObjectPropertyChain 2. <http://uri/hasParent> <http://uri/hasParent> 3. <http://uri/hasGrandparent>
                 */
                String propertyUri = null;
                String antecedentUri = null;
                // Now we are going to find out if the third component of part_axiom
                // has or not antecedent and consequent.
                // If part_axiom[2] is only a white space
                // then this ObjectPropertyChain has neither antecedent nor consequent.
                if (partAxiom[2].equals(" ")) {
                    propertyUri = partAxiom[3];
                } else {
                    final String[] antecedentConsequent = partAxiom[2].split(" <", 0);
                    // If part_axiom[2] only has one uri such as antecedent or consequent.
                    // The antecedent_consequent's length will be two,
                    // due to the fact to apply split function.
                    // The first antecedent_consequent array's element will be a white space.
                    // The second will be the uri.
                    if (antecedentConsequent.length == 2) {
                        // listResults.put(, );
                        propertyUri = partAxiom[3];
                        antecedentUri = partAxiom[2];
                    }
                }
                if (propertyUri != null) {
                    final String property_name = extractPropertyNameFromUri(propertyUri);
                    final String antecedent_name = extractPropertyNameFromUri(antecedentUri);
                    final OntProperty property = context.getModel().getOntProperty(propertyUri);
                    preResults.put(property_name, new AxiomExtras(property, antecedent_name));
                }
            }
        }

        for (final Map.Entry<String, AxiomExtras> pitfall : preResults.entrySet()) {
            final String propertyName = pitfall.getKey();
            final AxiomExtras extras = pitfall.getValue();
            context.addResult(new AxiomPitfall(propertyName, extras.property, extras.antecedentName));
        }
    }

    private static class AxiomExtras {

        final OntProperty property;
        final String antecedentName;

        public AxiomExtras(final OntProperty property, final String antecedentName) {
            this.property = property;
            this.antecedentName = antecedentName;
        }
    }

    private static class AxiomPitfall implements Pitfall {

        private static String OUT_FMT = "Single chained property-chain; property-name (property-URI), antecedent-name: '%s' ('%s'), '%s'";

        private final String propertyName;
        private final OntProperty property;
        private final String antecedentName;

        public AxiomPitfall(final String propertyName, final OntProperty property, final String antecedentName) {
            this.propertyName = propertyName;
            this.property = property;
            this.antecedentName = antecedentName;
        }

        @Override
        public PitfallInfo getInfo() {
            return PITFALL_INFO;
        }

        @Override
        public List<OntResource> getResources() {
            return List.of(this.property);
        }

        @Override
        public String toString() {
            return String.format(OUT_FMT, propertyName, property, antecedentName);
        }

        @Override
        public String toHtml() {
            return String.format(OUT_FMT, propertyName, Pitfall.toHtmlLink(property), antecedentName);
        }

        // @Override
        // public void toRdf(RdfOutputContext context) {
        // // TODO Auto-generated method stub
        //
        // }
    }

    /**
     * Finds out the uri without "<" and ">" signs.
     *
     * @param uri
     *     where search for.
     *
     * @return the last word or part of an uri.
     */
    private static String extractPropertyNameFromUri(final String uri) {

        if (uri == null) {
            return null;
        }

        // Replace any sequence of a word character
        // or a non-word character followed "/"
        final List<String> uris = Arrays.asList(uri.split("[<|>]", 0));
        String propertyUri = null;
        for (final String curUri : uris) {
            if (!curUri.equals(" ")) {
                propertyUri = curUri;
                break;
            }
        }

        return propertyUri;
    }
}
