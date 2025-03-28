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
import es.upm.fi.oeg.oops.SynsetHelp;
import es.upm.fi.oeg.oops.Tokenizer;
import java.util.HashSet;
import java.util.Set;
import net.sf.extjwnl.JWNLException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P07 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(7, null),
            Set.of(new PitfallCategoryId('N', 1), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 4)),
            Importance.MINOR, "Merging different concepts in the same class",
            "A class whose name refers to two or more different concepts is created.", RuleScope.CLASS, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws JWNLException {

        final OntModel model = context.getModel();
        final SynsetHelp dictionary = context.getDictionary();

        final ExtIterIterable<OntClass> classes = new ExtIterIterable<>(model.listNamedClasses());

        // create lists for elements containing the pitfall
        final Set<OntClass> withPitfall = new HashSet<>();
        for (final OntClass ontoClass : classes) {
            final String localName = ontoClass.getLocalName();
            final String tokenizedString = Tokenizer.tokenizedString(localName);
            for (final String token : Tokenizer.tokenize(localName)) {
                if (token.equalsIgnoreCase("and") || token.equalsIgnoreCase("or")) {
                    // TODO FIXME Should this not rather check token vs tokenizedString?
                    final boolean hasSynSets = dictionary.hasSynsets(tokenizedString);

                    if (!Checker.fromModels(ontoClass) && hasSynSets) {
                        withPitfall.add(ontoClass);
                    }
                }
            }
        }

        context.addResultsIndividual(PITFALL_INFO, withPitfall);
    }
}
