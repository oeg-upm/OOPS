/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;

public interface Checker {

    List<String> MODEL_BASES = List.of("http://www.w3.org/2002/07/owl", "http://www.w3.org/1999/02/22-rdf-syntax-ns",
            "http://www.w3.org/2000/01/rdf-schema", "http://www.w3.org/2006/12/owl2-xml",
            "http://www.w3.org/2001/XMLSchema");

    /**
     * Searches an ontology model for this pitfall.
     *
     * @param context
     *     Ontology model to search in and reporting functionality.
     *
     * @throws IOException
     */
    void check(CheckingContext context) throws Exception;

    CheckerInfo getInfo();

    static boolean fromModels(OntResource resource) {
        if (!resource.isURIResource()) {
            return false;
        }

        final String resourceUri = resource.getURI();
        final String[] parts = resourceUri.split("#");
        if (parts.length < 2) {
            return false;
        }
        return MODEL_BASES.contains(parts[0]);
    }

    static boolean sameOrEquivalent(final Set<? extends OntResource> set1, final Set<? extends OntResource> set2,
            final OntModel model) {

        if (set1.size() == 1 && set2.size() == 1) {
            final OntResource resource1 = set1.iterator().next();
            final OntResource resource2 = set2.iterator().next();

            if (resource1.isClass() && resource2.isClass()) {
                final OntClass class1 = resource1.asClass();
                final OntClass class2 = resource2.asClass();

                if (class1.isURIResource() && class2.isURIResource() && (class1.hasEquivalentClass(class2)
                        || class2.hasEquivalentClass(class1) || class1.getURI().equals(class2.getURI()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
