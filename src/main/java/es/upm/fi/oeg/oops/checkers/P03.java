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
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

/**
 * Creating the relationship "is" instead of using <code>rdfs:subClassOf</code>, <code>rdf:type</code> or
 * <code>owl:sameAs</code>.
 */
@MetaInfServices(Checker.class)
public class P03 implements Checker {

    private static final List<String> IS_ALTS_LOWER = List.of("isa", "is", "is-a", "is_a");

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(3, null),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('S', 6)), Importance.CRITICAL,
            "Creating the relationship \"is\" instead of using \"rdfs:subClassOf\", "
                    + "\"rdf:type\" or \"owl:sameAs\"",
            "The relationship \"is\" is created in the ontology "
                    + "instead of using OWL primitives for representing the subclass relationship (rdfs:subClassOf), "
                    + "class membership (rdf:type), " + "or the equality between instances (owl:sameAs). "
                    + "When concerning a class hierarchy, " + "this pitfall is related to the guidelines "
                    + "for understanding the \"is-a\" relation provided in [2].",
            // TODO The above "[2]" (similarly appears in many other Checker implementations) seems to be a reference in
            // the OOPS! paper; That is not very useful here -> figure out a better way to link to the same info, that
            // works at least in HTML output
            RuleScope.CLASS, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        for (final ObjectProperty property : new ExtIterIterable<>(model.listObjectProperties())) {
            final String localName = property.getLocalName();
            if (localName != null && IS_ALTS_LOWER.contains(localName.toLowerCase()) && !Checker.fromModels(property)) {
                context.addResult(PITFALL_INFO, property);
            }
        }
    }
}
