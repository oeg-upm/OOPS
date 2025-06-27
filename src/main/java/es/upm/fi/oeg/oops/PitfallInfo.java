/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Set;
import java.util.stream.Collectors;

public class PitfallInfo {

    public enum AccompPer {
        INSTANCE, TYPE,
    }

    private final PitfallId id;
    private final Set<PitfallCategory> categories;
    private final Importance importance;
    private final String title;
    private final String explanation;
    private final RuleScope scope;
    /**
     * Amount of resources per detected pitfall.
     */
    private final Arity resourceArity;
    /**
     * Formating string for human-readable output of one pitfall instance or the whole set of detected pitfalls of this
     * type. It should contain either one "%s" that will be substituted by a single resource per pitfall, or the list of
     * all resources, or two %s, for pitfalls that cover exactly two resources (e.g. a pair of properties). See also
     * `accompPer`.
     *
     * @return
     */
    private final String accomp;
    private final AccompPer accompPer;
    /**
     * This gets output at the very end of the pitfalls output, or even at the end of all the pitfalls, in case of the
     * pitfalls being sorted/ordered by touched resource. It only gets put out, if the pitfall was triggered at least
     * once.
     */
    private final String extraOutput;

    public PitfallInfo(final PitfallId id, final Set<PitfallCategoryId> categoryIds, final Importance importance,
            final String title, final String explanation, final RuleScope scope, final Arity resourceArity,
            final String accomp, final AccompPer accompPer, final String extraOutput) {
        this.id = id;
        this.categories = categoryIds.stream().map(PitfallCategory::get).collect(Collectors.toSet());
        this.importance = importance;
        this.title = title;
        this.explanation = explanation;
        this.scope = scope;
        this.resourceArity = resourceArity;
        this.accomp = accomp;
        this.accompPer = accompPer;
        this.extraOutput = extraOutput;
    }

    public PitfallInfo(final PitfallId id, final Set<PitfallCategoryId> categoryIds, final Importance importance,
            final String title, final String explanation, final RuleScope scope, final Arity resourceArity,
            final String accomp, final AccompPer accompPer) {
        this(id, categoryIds, importance, title, explanation, scope, resourceArity, accomp, accompPer, null);
    }

    public PitfallInfo(final PitfallId id, final Set<PitfallCategoryId> categoryIds, final Importance importance,
            final String title, final String explanation, final RuleScope scope, final Arity resourceArity) {
        this(id, categoryIds, importance, title, explanation, scope, resourceArity, null, null, null);
    }

    // public PitfallInfo(final PitfallId id, final Importance importance, final String title, final String explanation)
    // {
    // this(id, importance, title, explanation, RuleScope.RESOURCE, title, AccompPer.INSTANCE);
    // }

    public PitfallId getId() {
        return id;
    }

    public Set<PitfallCategory> getCategory() {
        return categories;
    }

    public Importance getImportance() {
        return importance;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public RuleScope getScope() {
        return scope;
    }

    public Arity getResourceArity() {
        return resourceArity;
    }

    public String getAccomp() {
        // This code is commented out, because it is easier to handle this
        // in the jsp page.

        // String accompNonNull = (accomp != null) ? accomp : title + " appears in the elements: %s";
        // if (getScope() == RuleScope.ONTOLOGY) {
        //     accompNonNull = accompNonNull
        //             + "\n\n*This pitfall applies to the ontology in general instead of specific elements.";
        // }
        //return accompNonNull;
        return accomp;
    }

    public AccompPer getAccompPer() {
        return (accompPer != null) ? accompPer : AccompPer.TYPE;
    }

    public String getExtraOutput() {
        return extraOutput;
    }
}
