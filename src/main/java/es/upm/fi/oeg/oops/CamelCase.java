/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.function.UnaryOperator;

public class CamelCase implements UnaryOperator<String> {

    @Override
    public String apply(final String input) {
        return toCamelCase(input);
    }

    public static String toCamelCase(final String input) {

        if (!input.contains(" ")) {
            return input;
        }

        final StringBuffer output = new StringBuffer();
        final String[] tokens = input.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            final String intermediate = tokens[i];
            final char first = intermediate.charAt(0);
            final char firstUpper = Character.toUpperCase(first);

            output.append(firstUpper).append(intermediate.substring(1));
        }

        return output.toString();
    }
}
