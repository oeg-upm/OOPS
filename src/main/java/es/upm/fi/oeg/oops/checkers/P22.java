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
import es.upm.fi.oeg.oops.Tokenizer;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

/**
 * Esta pitfall no se da en un elemento concreto, se da, o no se da, en la ontolog�a.
 */
@MetaInfServices(Checker.class)
public class P22 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(22, null),
            Set.of(new PitfallCategoryId('N', 6)), Importance.MINOR,
            "Using different naming conventions in the ontology",
            "The ontology elements are not named following the same convention "
                    + "(for example CamelCase or use of delimiters as \"-\" or \"_\"). "
                    + "Some notions about naming conventions are provided in [2].",
            RuleScope.ONTOLOGY, Arity.TWO,
            "There are terms following different naming conventions, for example using differnet casing as for example:\n"
                    + "%s\n" + "and %s.\n" + "\n"
                    + "Note that some of the namespaces involved in this pitfall may be out of your\n"
                    + "control so you can't correct it. In that case, you should check whether this pitfall\n"
                    + "also appear in the namespaces under your control or ignore it if it only affects elements\n"
                    + "that you can't modify.",
            AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();
        final TypeContext typeContext = new TypeContext();

        analyze(context, typeContext, () -> model.listNamedClasses());
        analyze(context, typeContext, () -> model.listObjectProperties());
        analyze(context, typeContext, () -> model.listDatatypeProperties());
    }

    private static class TypeContext {

        public OntResource elementWithHyphen = null;
        public OntResource elementWithUnderscore = null;
        public OntResource elementWithNoDelimiter = null;
        public String nsElementWithHyphen = null;
        public String nsElementWithNoHyphen = null;
        public boolean hasHyphen = false;
        public boolean hasNoHyphen = false;
        public OntResource elementWithL = null;
        public OntResource elementWithU = null;
        public String nsElementWithL = null;
        public String nsElementWithU = null;
    }

    private <PT extends OntResource> void analyze(final CheckingContext context, final TypeContext typeContext,
            final Supplier<ExtendedIterator<PT>> allResGen) {

        // TODO FIXME The logic in here needs revising, taking into control single-word-terms, that classes should start
        // with U nad everythign else with L, and check for _ and _ mixing. Maybe more.
        boolean startsL = false;
        boolean startsU = false;
        for (final PT ontoProperty : new ExtIterIterable<>(allResGen.get())) {
            final String localName = ontoProperty.getLocalName();

            if (localName == null || localName.isEmpty()) {
                continue;
            }

            if (!Checker.fromModels(ontoProperty)) {
                final List<String> tokens = Tokenizer.tokenize(localName);
                if (tokens.size() > 1) {
                    if (localName.contains("_") || localName.contains("-")) {
                        typeContext.hasHyphen = true;
                        typeContext.elementWithHyphen = ontoProperty;
                        typeContext.nsElementWithHyphen = ontoProperty.getNameSpace();
                    } else if (!localName.contains("_") || !localName.contains("-")) {
                        typeContext.hasNoHyphen = true;
                        typeContext.elementWithNoDelimiter = ontoProperty;
                        typeContext.nsElementWithNoHyphen = ontoProperty.getNameSpace();
                    }
                    if (typeContext.hasHyphen && typeContext.hasNoHyphen) {
                        context.addResult(PITFALL_INFO, typeContext.elementWithHyphen,
                                typeContext.elementWithNoDelimiter);
                        break;
                    }
                }
            }

            final Character firstChar = localName.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                startsL = true;
                typeContext.elementWithL = ontoProperty;
                typeContext.nsElementWithL = ontoProperty.getNameSpace();
            } else if (Character.isUpperCase(firstChar)) {
                startsU = true;
                typeContext.elementWithU = ontoProperty;
                typeContext.nsElementWithU = ontoProperty.getNameSpace();
            }
            if (startsL && startsU && !Checker.fromModels(ontoProperty)) {
                // if (nsElementWithL.equals(nsElementWithU) ){
                context.addResult(PITFALL_INFO, typeContext.elementWithL, typeContext.elementWithU);
                break;
                // }
            }
        }
    }
}
