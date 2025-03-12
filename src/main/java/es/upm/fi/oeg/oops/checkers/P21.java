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
import es.upm.fi.oeg.oops.Tokenizer;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P21 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(21, null),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('S', 6)), Importance.MINOR,
            "Using a miscellaneous class",
            "This pitfall refers to the creation of a class " + "with the only goal of classifying the instances "
                    + "that do not belong to any of its sibling classes "
                    + "(classes with which the miscellaneous problematic class " + "shares a common direct ancestor).",
            RuleScope.CLASS, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    private static List<String> MISC_TOKENS = List.of("other", "misc", "miscellanea", "miscellaneous", "miscellany");

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final ExtIterIterable<OntClass> classes = new ExtIterIterable<>(context.getModel().listNamedClasses());
        for (final OntClass ontoClass : classes) {
            final String localName = ontoClass.getLocalName();
            for (final String token : Tokenizer.tokenize(localName)) {
                final String tokenLower = token.toLowerCase();
                if (MISC_TOKENS.contains(tokenLower) && !Checker.fromModels(ontoClass)) {
                    context.addResult(PITFALL_INFO, ontoClass);
                }
            }
        }
    }
}
