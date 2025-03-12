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
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.Set;
import org.apache.jena.ontology.ObjectProperty;
import org.kohsuke.MetaInfServices;

/**
 * When a relation is transitive, but its range and its domain are not the same.
 */
@MetaInfServices(Checker.class)
public class P29 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(
            new PitfallId(29, null), Set.of(new PitfallCategoryId('N', 2)), Importance.CRITICAL,
            "Defining wrong transitive relationships", "A relationship is defined as transitive, "
                    + "using owl:TransitiveProperty, " + "when the relationship is not necessarily transitive.",
            RuleScope.PROPERTY, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {
        P28.scanInternal(context, (final ObjectProperty prop) -> prop.isTransitiveProperty(), PITFALL_INFO);
    }
}
