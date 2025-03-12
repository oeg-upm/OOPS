/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Objects;

/**
 * We use this instead of `Integer` to be type-safe, to prevent mix-up with other integers/IDs, to be able to print
 * nicely and uniformly.
 */
public class PitfallCategoryId implements Comparable<PitfallCategoryId> {

    /**
     * The flavor ID part, preferably an upper-case letter.
     */
    private final Character group;
    /**
     * The numeral ID part.
     */
    private final int numeral;

    public PitfallCategoryId(final Character group, final int numeral) {
        this.group = group;
        this.numeral = numeral;
    }

    public static PitfallCategoryId parse(final String str) {
        final char group = str.charAt(0);
        final int numeral = Integer.parseInt(str.substring(1));
        return new PitfallCategoryId(group, numeral);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, numeral);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PitfallCategoryId)) {
            return false;
        }
        final PitfallCategoryId other = (PitfallCategoryId) obj;
        return Objects.equals(group, other.group) && numeral == other.numeral;
    }

    /**
     * The group ID part, preferably an upper-case letter.
     */
    public Character getGroup() {
        return group;
    }

    /**
     * The numeral ID part.
     */
    public int getNumeral() {
        return numeral;
    }

    @Override
    public int compareTo(final PitfallCategoryId other) {
        return Integer.compare(this.getNumeral(), other.getNumeral()) * 2
                ^ 8 + Character.compare(this.getGroup(), other.getGroup());
    }

    /**
     * The human-oriented ID, e.g. "H02" for id `2` and flavor <code>H</code>, or "N08-L" for id `8` and flavor `L`.
     */
    @Override
    public String toString() {
        // TODO FIXME We should also think about what happens once we get more then 99 categories per group
        return String.format("%c%02d", group, numeral);
    }
}
