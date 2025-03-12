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

    public static List<String> tokenize(final String input) {

        final List<String> tokens = new ArrayList<>();

        final String delimiter;
        if (input.contains("_")) {
            delimiter = "_";
        } else if (input.contains("-")) {
            delimiter = "-";
        } else {
            delimiter = "_";
        }

        // System.out.println("el input en tokeniar is: " + input);

        String stringIn = input;
        if (input.startsWith(delimiter)) {
            stringIn = input.substring(1);
        }

        if (stringIn.contains(delimiter)) {
            tokens.addAll(Arrays.asList(stringIn.split(delimiter)));
        } else {
            boolean firstWord = true;
            char charActual;
            String word = new String();
            while (stringIn.length() > 0) {
                charActual = stringIn.charAt(0);
                stringIn = stringIn.substring(1);

                if (Character.toString(charActual).equals(delimiter)) {
                    if (word.length() > 0) {
                        tokens.add(word);

                        // numWords++;
                        word = new String();
                    }
                } else if (Character.isUpperCase(charActual)) {
                    if (firstWord) {
                        word = word.concat(Character.toString(charActual));
                    } else if (word.length() > 0) {
                        tokens.add(word);

                        // numWords++;
                        word = new String();
                        word = word.concat(Character.toString(charActual));
                    }
                } else if (Character.isLowerCase(charActual)) {
                    word = word.concat(Character.toString(charActual));
                } else {
                    // System.out.println("Es un caracter raro: " + charActual);
                    word = word.concat(Character.toString(charActual));
                }
                firstWord = false;
            }
            tokens.add(word);
        }

        return tokens;
    }

    public static String tokenizedString(final String input) {
        return tokenize(input).stream().collect(Collectors.joining(" "));
    }
}
