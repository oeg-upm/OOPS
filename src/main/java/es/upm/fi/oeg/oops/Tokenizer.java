/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tokenizer {

    private Tokenizer() {
    }

    public static List<String> tokenize(final String input) {

        final List<String> tokens = new ArrayList<>();

        final char delimiter;
        if (input.contains("_")) {
            delimiter = '_';
        } else if (input.contains("-")) {
            delimiter = '-';
        } else {
            delimiter = '_';
        }
        final String delimiterStr = Character.toString(delimiter);

        final String stringIn;
        if (input.startsWith(delimiterStr)) {
            stringIn = input.substring(1);
        } else {
            stringIn = input;
        }

        if (stringIn.contains(delimiterStr)) {
            tokens.addAll(Arrays.asList(stringIn.split(delimiterStr)));
        } else {
            boolean firstWord = true;
            final StringBuffer word = new StringBuffer();
            for (final char charActual : stringIn.toCharArray()) {
                if (charActual == delimiter) {
                    if (word.length() > 0) {
                        tokens.add(word.toString());
                        word.setLength(0);
                    }
                } else if (Character.isUpperCase(charActual)) {
                    if (firstWord) {
                        word.append(charActual);
                    } else if (!word.isEmpty()) {
                        tokens.add(word.toString());
                        word.setLength(0);
                        word.append(charActual);
                    }
                } else if (Character.isLowerCase(charActual)) {
                    word.append(charActual);
                }
                firstWord = false;
            }
            tokens.add(word.toString());
        }

        return tokens;
    }

    public static String tokenizedString(final String input) {
        return tokenize(input).stream().collect(Collectors.joining(" "));
    }
}
