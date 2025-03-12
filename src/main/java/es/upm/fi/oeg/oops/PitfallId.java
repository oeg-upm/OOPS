/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Objects;

/**
 * We use this instead of `Integer` to be type-safe, to prevent mixup with other integers/IDs, to be able to print
 * nicely and uniformly, and so that each checker may have multiple pitfalls, while keeping the numeral part of its ID
 * equal to the numeral part of each of the pitfalls it can detect.
 */
public class PitfallId implements Comparable<PitfallId> {

    /**
     * The pitfalls numeral ID part. This should be equal to the checker's ID.
     */
    private final int numeral;
    /**
     * The pitfalls flavor ID part, preferably an upper-case letter. This is used only when the corresponding checker
     * can detect multiple pitfalls; otherwise it is <code>null</code>
     */
    private final Character flavor;

    public PitfallId(final int numeral, final Character flavor) {
        this.numeral = numeral;
        this.flavor = flavor;
    }

    public PitfallId(final int numeral) {
        this(numeral, null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flavor, numeral);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PitfallId)) {
            return false;
        }
        final PitfallId other = (PitfallId) obj;
        return Objects.equals(flavor, other.flavor) && numeral == other.numeral;
    }

    /**
     * The pitfalls numeral ID part. This should be equal to the checker's ID.
     */
    public int getNumeral() {
        return numeral;
    }

    public CheckerId generateCheckerId() {
        return new CheckerId(getNumeral());
    }

    /**
     * The pitfalls flavor ID part, preferably an upper-case letter. This is used only when the corresponding checker
     * can detect multiple pitfalls; otherwise it is <code>null</code>
     */
    public Character getFlavor() {
        return flavor;
    }

    @Override
    public int compareTo(final PitfallId other) {
        return Integer.compare(this.getNumeral(), other.getNumeral()) * 2
                ^ 8 + compare(this.getFlavor(), other.getFlavor());
    }

    private static int compare(final Character thisOne, final Character other) {
        if (thisOne == null && other == null) {
            return 0;
        } else if (thisOne == null) {
            return -1;
        } else if (other == null) {
            return 1;
        } else {
            return Character.compare(thisOne, other);
        }
    }

    /**
     * The checkers human-oriented ID, e.g. "P02" for id `2` and flavor <code>null</code>, or "P08-L" for id `8` and
     * flavor `L`.
     */
    @Override
    public String toString() {
        final String flavorStr;
        if (flavor != null) {
            flavorStr = "-" + flavor;
        } else {
            flavorStr = "";
        }
        // TODO FIXME We should also think about what happens once we get more then 99 checkers
        return String.format("P%02d%s", numeral, flavorStr);
    }
}
