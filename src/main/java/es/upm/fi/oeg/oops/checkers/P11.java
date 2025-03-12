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
import java.util.Set;
import java.util.function.Supplier;
import org.apache.jena.ontology.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P11 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(11, null),
            Set.of(new PitfallCategoryId('N', 3), new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 5)),
            Importance.IMPORTANT, "Missing domain or range in properties",
            "Object and/or datatype properties missing domain or range "
                    + "(or both of them) are included in the ontology.",
            RuleScope.PROPERTY, Arity.ONE, null, AccompPer.TYPE,
            "<b>Tip:</b> Solving this pitfall may lead to new results for other pitfalls and suggestions.\n"
                    + "We encourage you to solve all cases when needed, "
                    + "and see what else you can get from this software.");

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        // create an iterator over the root classes
        // that are not anonymous class expressions
        analyze(context, () -> model.listObjectProperties());
        analyze(context, () -> model.listDatatypeProperties());
    }

    private <PT extends OntProperty> void analyze(final CheckingContext context,
            final Supplier<ExtendedIterator<PT>> allResGen) {

        final ExtIterIterable<PT> props = new ExtIterIterable<>(allResGen.get());
        for (final PT prop : props) {
            final boolean domain = prop.hasDomain(null);
            final boolean range = prop.hasRange(null);

            if ((!domain || !range) && !Checker.fromModels(prop)) {
                context.addResult(PITFALL_INFO, prop);
            }
        }
    }
}
