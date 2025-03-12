/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import java.util.Set;

public class CheckerInfo {

    private final CheckerId id;
    // private final Importance importance;
    private final String title;
    // private final String explanation;
    // private final RuleScope scope;
    private final Set<PitfallInfo> detectedPitfalls;

    public CheckerInfo(final PitfallInfo detectedPitfall) {
        this.id = new CheckerId(detectedPitfall.getId().getNumeral());
        // this.importance = importance;
        this.title = detectedPitfall.getTitle();
        // this.explanation = explanation;
        // this.scope = scope;
        this.detectedPitfalls = Set.of(detectedPitfall);
    }

    public CheckerInfo(final CheckerId id, /* final Importance importance, */ final String title,
            /*
             * final String explanation, final RuleScope scope,
             */ final Set<PitfallInfo> detectedPitfalls) {
        this.id = id;
        // this.importance = importance;
        this.title = title;
        // this.explanation = explanation;
        // this.scope = scope;
        this.detectedPitfalls = detectedPitfalls;

        for (final PitfallInfo pfInfo : detectedPitfalls) {
            if (pfInfo.getId().getNumeral() != id.getNumeral()) {
                throw new IllegalStateException(String.format(
                        "Detected pitfall ID (%s) does not match its checkers ID (%s); "
                                + "NOTE: This is an issue with the software, not its user!",
                        pfInfo.getId().getNumeral(), id.getNumeral()));
            }
        }
    }

    public CheckerInfo(final String title, final Set<PitfallInfo> detectedPitfalls) {
        this(new CheckerId(detectedPitfalls.iterator().next().getId().getNumeral()), title, detectedPitfalls);
    }

    public CheckerInfo(final CheckerId id, /* final Importance importance, */ final String title,
            /*
             * final String explanation, final RuleScope scope,
             */ final PitfallInfo detectedPitfall) {
        this(id, /* importance, */ title, /* explanation, scope, */ Set.of(detectedPitfall));
    }

    public CheckerInfo(final CheckerId id, final Set<PitfallCategoryId> categoryIds, final Importance importance,
            final String title, final String explanation, final RuleScope scope, final Arity resourceArity,
            final String accomp, final Character flavor) {
        this(id, /* importance, */ title, /* explanation, scope, */
                new PitfallInfo(new PitfallId(id.getNumeral(), flavor), categoryIds, importance, title, explanation,
                        scope, resourceArity, accomp, AccompPer.INSTANCE));
    }

    public CheckerInfo(final CheckerId id, final Set<PitfallCategoryId> categoryIds, final Importance importance,
            final String title, final String explanation, final Arity resourceArity) {
        this(id, categoryIds, importance, title, explanation, RuleScope.RESOURCE, resourceArity, title, null);
    }

    public CheckerId getId() {
        return id;
    }

    // public Importance getImportance() {
    // return importance;
    // }

    public String getTitle() {
        return title;
    }

    // public String getExplanation() {
    // return explanation;
    // }

    // public RuleScope getScope() {
    // return scope;
    // }

    /**
     * Pitfalls detected by this checker.
     *
     * @return info about each pitfall-type this checker can detect.
     */
    public Set<PitfallInfo> detectsPitfalls() {
        return detectedPitfalls;
    }
}
