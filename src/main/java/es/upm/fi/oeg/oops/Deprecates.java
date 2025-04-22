/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

public class Deprecates {

    private static final String URI_TERM_STATUS = "http://www.w3.org/2003/06/sw-vocab-status/ns#term_status";

    private void removeSubjectsByTermStatus(final OntModel model, final String termStatus) {

        final AnnotationProperty ann = model.getAnnotationProperty(URI_TERM_STATUS);
        final ExtendedIterator<Resource> subjectsToRemove = model.listSubjectsWithProperty(ann, termStatus);

        while (subjectsToRemove.hasNext()) {
            final Resource resToRemove = subjectsToRemove.next();
            final boolean isOntResource = resToRemove.canAs(OntResource.class);
            if (isOntResource) {
                final OntResource ontResource = resToRemove.as(OntResource.class);
                ontResource.remove();
            }
        }
    }

    private void removeSubjectsByOwlDeprecated(final OntModel model) {

        // By annotation owl:deprecated value "true"
        final Property owlDep = model.getProperty("http://www.w3.org/2002/07/owl#deprecated");
        final ExtendedIterator<Resource> subjectsToCheck = model.listSubjectsWithProperty(owlDep);

        while (subjectsToCheck.hasNext()) {
            final Resource subj = subjectsToCheck.next();

            final ExtIterIterable<RDFNode> depValues = new ExtIterIterable<>(model.listObjectsOfProperty(subj, owlDep));
            boolean allTrue = true;
            for (final RDFNode depValue : depValues) {
                final String depValueStr = depValue.toString().toLowerCase().trim();
                if (depValueStr == "false") {
                    // if just one value is false, do not remove
                    allTrue = false;
                }
            }

            // if all are true, remove
            if (allTrue) {
                final boolean isOntResource = subj.canAs(OntResource.class);
                if (isOntResource) {
                    final OntResource ontResource = subj.as(OntResource.class);
                    ontResource.remove();
                }
            }
        }
    }

    /**
     * Removes all deprecated subjects from the given ontology model.
     * @param model
     *     to be cleaned of subjects marked as deprecated
     */
    public Deprecates(final OntModel model) {

        removeSubjectsByTermStatus(model, "deprecated");
        removeSubjectsByTermStatus(model, "Deprecated");
        removeSubjectsByOwlDeprecated(model);
    }
}
