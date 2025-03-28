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

    public Deprecates(final OntModel model) {

        // By annotation property value "deprecated"
        final AnnotationProperty ann = model
                .getAnnotationProperty("http://www.w3.org/2003/06/sw-vocab-status/ns#term_status");
        final ExtendedIterator<Resource> r1 = model.listSubjectsWithProperty(ann, "deprecated");

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r1.hasNext()) {
            final Resource resCurrent = r1.next();
            final boolean isOntResource = resCurrent.canAs(OntResource.class);
            if (isOntResource) {
                // System.out.println("Es OntResource y lo borro");
                final OntResource ontResource = resCurrent.as(OntResource.class);
                ontResource.remove();
            }
        }

        // By annotation property value "Deprecated"
        final ExtendedIterator<Resource> r2 = model.listSubjectsWithProperty(ann, "Deprecated");

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r2.hasNext()) {
            final Resource resCurrent = r2.next();
            final boolean isOntResource = resCurrent.canAs(OntResource.class);
            if (isOntResource) {
                // System.out.println("Es OntResource y lo borro");
                final OntResource ontResource = resCurrent.as(OntResource.class);
                ontResource.remove();
            }
        }

        // By annotation owl:deprecated value "true"
        final Property owlDep = model.getProperty("http://www.w3.org/2002/07/owl#deprecated");
        final ExtendedIterator<Resource> r3 = model.listSubjectsWithProperty(owlDep);

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r3.hasNext()) {
            final Resource resCurrent = r3.next();

            // cojo los valores
            final ExtendedIterator<RDFNode> values = model.listObjectsOfProperty(resCurrent, owlDep);
            boolean allTrue = true;

            while (values.hasNext()) {
                final String valueDep = values.next().toString();
                if (valueDep.contains("false") || valueDep.contains("False")) {
                    // si algun valor no es true no lo borro
                    allTrue = false;
                }
            }

            // si todos son true entonces lo borro
            if (allTrue) {
                final boolean isOntResource = resCurrent.canAs(OntResource.class);
                if (isOntResource) {
                    final OntResource ontResource = resCurrent.as(OntResource.class);
                    ontResource.remove();
                }
            }
        }
    }
}
