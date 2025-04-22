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
import java.util.Set;
import java.util.function.Supplier;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P20 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(20, null),
            Set.of(new PitfallCategoryId('N', 7)), Importance.MINOR, "Misusing ontology annotations",
            "The contents of some annotation properties are swapped or misused. "
                    + "This pitfall might affect annotation properties related to natural language information "
                    + "(for example, annotations for naming such as rdfs:label "
                    + "or for providing descriptions such as rdfs:comment). "
                    + "Other types of annotation could also be affected; "
                    + "temporal and versioning information, among others.",
            RuleScope.RESOURCE, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {
        scanResources(context, () -> context.getModel().listNamedClasses());
        scanResources(context, () -> context.getModel().listObjectProperties());
        scanResources(context, () -> context.getModel().listDatatypeProperties());
    }

    private <PT extends OntResource> void scanResources(final CheckingContext context,
            final Supplier<ExtendedIterator<PT>> allResGen) {

        // create lists for elements containing the pitfall
        // List<OntClass> classWithPitfall = new ArrayList<OntClass>();

        for (final PT ontoRes : new ExtIterIterable<>(allResGen.get())) {
            // try {
            final String label = ontoRes.getLabel(null);
            final String comment = ontoRes.getComment(null);
            if ((label != null) && (comment != null)) {
                boolean pitfall = false;
                if (label.isEmpty() || comment.isEmpty()) {
                    pitfall = true;
                    // System.out.println("el comment o el label esta vacio");
                    // System.out.println("                Comment: " + comment);
                    // System.out.println("                Label: " + label);
                } else if ((label.split(" ").length > comment.split(" ").length) && !Checker.fromModels(ontoRes)) {
                    pitfall = true;
                    // } else if ((label.length() > comment.length()) && !Checker.fromModels(ontoClass)) {
                    // pitfall = true;
                } else if (label.equalsIgnoreCase(comment)) {
                    // if they have the same content
                    pitfall = true;
                }
                if (pitfall) {
                    // if (comment.split(" ").length == 1) {
                    // System.out.println(propertyWithPitfall.get(numPWithPitfall).getURI() + " It is a one-word
                    // comment, maybe it is an annotation about the status");
                    // }
                    context.addResult(PITFALL_INFO, ontoRes);
                }
            }
            // } catch (final LiteralRequiredException exc) {
            // System.err.println("DETECTADA: " + exc.getMessage());
            // }
        }
    }
}
