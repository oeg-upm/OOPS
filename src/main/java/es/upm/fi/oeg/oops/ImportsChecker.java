/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntModel;

public class ImportsChecker {

    public List<String> listImportsFailing = new ArrayList<>();

    public ImportsChecker(OntModel model) {

        model.listImportedOntologyURIs().toArray().toString();

        throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<String> getImportsFailing() {
        return this.listImportsFailing;
    }
}
