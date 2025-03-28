/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StopWords {

    private final List<String> stopWords;

    public StopWords() throws IOException {
        this.stopWords = loadDefault();
    }

    public List<String> load(final InputStream input) throws IOException {

        final List<String> stopWordsTmp = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWordsTmp.add(line);
            }
        }
        return stopWordsTmp;
    }

    public List<String> loadDefault() throws IOException {
        final InputStream defLoc = StopWords.class.getResourceAsStream("/stop-words.csv");
        return load(defLoc);
    }

    public boolean isStopWord(final String word) {
        return this.stopWords.contains(word.toLowerCase());
    }
}
