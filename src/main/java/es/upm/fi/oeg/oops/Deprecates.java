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

    public Deprecates(OntModel model) {

        // By annotation property value "deprecated"
        AnnotationProperty ann = model
                .getAnnotationProperty("http://www.w3.org/2003/06/sw-vocab-status/ns#term_status");
        ExtendedIterator<Resource> r1 = model.listSubjectsWithProperty(ann, "deprecated");

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r1.hasNext()) {
            Resource rcurrent = r1.next();

            boolean isOntResource = rcurrent.canAs(OntResource.class);

            if (isOntResource) {
                // System.out.println("Es OntResource y lo borro");

                OntResource ontResource = rcurrent.as(OntResource.class);
                ontResource.remove();
            }
        }

        // By annotation property value "Deprecated"
        ExtendedIterator<Resource> r2 = model.listSubjectsWithProperty(ann, "Deprecated");

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r2.hasNext()) {
            Resource rcurrent = r2.next();

            boolean isOntResource = rcurrent.canAs(OntResource.class);

            if (isOntResource) {
                // System.out.println("Es OntResource y lo borro");

                OntResource ontResource = rcurrent.as(OntResource.class);
                ontResource.remove();
            }
        }

        // By annotation owl:deprecated value "true"
        Property owlDep = model.getProperty("http://www.w3.org/2002/07/owl#deprecated");
        ExtendedIterator<Resource> r3 = model.listSubjectsWithProperty(owlDep);

        // System.out.println (" clase de owl: " + deprecatedClass.getURI());

        while (r3.hasNext()) {
            Resource rcurrent = r3.next();

            // cojo los valores
            ExtendedIterator<RDFNode> values = model.listObjectsOfProperty(rcurrent, owlDep);
            boolean allTrue = true;

            while (values.hasNext()) {
                String valueDep = values.next().toString();
                if (valueDep.contains("false") || valueDep.contains("False")) {
                    // si algun valor no es true no lo borro
                    allTrue = false;
                }
            }

            // si todos son true entonces lo borro
            if (allTrue) {

                boolean isOntResource = rcurrent.canAs(OntResource.class);

                if (isOntResource) {
                    OntResource ontResource = rcurrent.as(OntResource.class);
                    ontResource.remove();
                }
            }
        }
    }
}
