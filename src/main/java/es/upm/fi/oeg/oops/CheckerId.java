/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Objects;

/**
 * We use this instead of `Integer` to be type-safe, to prevent mixup with other integers/IDs, to be able to print
 * nicely and uniformly, and to be able to extend/refactor easily in the future.
 */
public class CheckerId implements Comparable<CheckerId> {

    /**
     * The checkers numeral ID.
     */
    private final int numeral;

    public CheckerId(final int numeral) {
        this.numeral = numeral;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeral);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CheckerId)) {
            return false;
        }
        final CheckerId other = (CheckerId) obj;
        return numeral == other.numeral;
    }

    public int getNumeral() {
        return numeral;
    }

    @Override
    public int compareTo(final CheckerId other) {
        return Integer.compare(this.getNumeral(), other.getNumeral());
    }

    /**
     * The checkers human-oriented ID, e.g. "C02" for id `2`.
     */
    @Override
    public String toString() {
        // TODO FIXME We should also think about what happens once we get more then 99 checkers
        return String.format("C%02d", numeral);
    }
}
