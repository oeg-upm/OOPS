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
import es.upm.fi.oeg.oops.RuleScope;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P35 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(35, null),
            Set.of(new PitfallCategoryId('N', 41)), Importance.IMPORTANT, "Untyped property",
            "An ontology element is used as a property " + "without having been explicitly declared as such "
                    + "using the primitives rdf:Property, owl:ObjectProperty or owl:DatatypeProperty. "
                    + "This pitfall is related with the common problems listed in [8].",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Set<OntResource> preResults = new HashSet<>();
        for (final Statement sta : new ExtIterIterable<>(model.listStatements())) {
            final Resource subject = sta.getSubject();
            final Property predicate = sta.getPredicate();
            final String predicateUri = predicate.getURI();

            // System.out.println("\tPre:"+predicate_name); String valor =sta.toString();
            // System.out.println("tus:"+valor);
            if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#domain")
                    || predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#range")) {
                analyze(model, preResults, subject);
            } else if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")
                    || predicateUri.equals("http://www.w3.org/2002/07/owl#propertyDisjointWith")
                    || predicateUri.equals("http://www.w3.org/2002/07/owl#equivalentProperty")
                    || predicateUri.equals("http://www.w3.org/2002/07/owl#inverseOf")) {
                analyze(model, preResults, subject);
                final RDFNode object = sta.getObject();
                analyze(model, preResults, object);
            }

            // // if it is the predicate itself
            // if ((subject.isURIResource()) && (object.isURIResource() || object.isLiteral())) {
            // OntProperty isProperty = model.getOntProperty(predicateUri);
            // AnnotationProperty isAnn = model.getAnnotationProperty(predicateUri);
            // if (isProperty == null && isAnn == null &&
            // !Checker.fromModels(model.getOntResource(predicateUri)) &&
            // !preResults.contains(predicateUri)) {
            // preResults.add(isAnn);
            // } else {
            // // System.out.println("The property is null: " + k);
            // }
            // }
        }

        context.addResultsIndividual(PITFALL_INFO, preResults);
    }

    private void analyze(final OntModel model, final Set<OntResource> preResults, final RDFNode node) {
        if (node.isURIResource()) {
            final String nodeUri = node.toString();
            final OntResource res = model.getOntResource(nodeUri);
            final OntProperty ontProp = model.getOntProperty(nodeUri);
            final boolean isProperty = ontProp != null;
            if (!isProperty && !Checker.fromModels(res)) {
                preResults.add(res);
            } else {
                // System.out.println("The property is null: " + k);
            }
        }
    }

    // /**
    // * Returns a hashmap with the results after finding out the P35's pitfalls of the ontology model.
    // *
    // * @return a hashmap whose
    // * <li>
    // * <ol>
    // * first component is the resource's uri. That have to be the type property but it is not.
    // * </ol>
    // * <li>
    // * <ol>
    // * second component is the property's uri which contents the resource named in first component.
    // * </ol>
    // */
    // public HashMap<String, String> getPreListResults() {
    // return preListResults;
    // }
}
