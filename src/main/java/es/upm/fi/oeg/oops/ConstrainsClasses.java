/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import org.apache.jena.ontology.OntClass;

/**
 * <p>
 * Description: The <tt>ConstrainsClasses</tt> class represents an ontology constrains classes' subset. These constrains
 * refer to if two <tt>RDF/XML ontology classes</tt> are equivalent.
 * <p>
 * Contains methods for performings if two classes have:
 * <li>
 * <ol>
 * The <tt>sameAs</tt> connection.
 * </ol>
 * <li>
 * <ol>
 * The <tt>equivalent</tt> connection.
 * </ol>
 * </p>
 *
 * @author Ana Maria Ruiz Jimenez
 *
 * @version 1.0
 */

public class ConstrainsClasses {

    /**
     * Finds out if two <tt>OntClass</tt> have the <tt>sameAs</tt> connection o relationship.
     *
     * @param class_1
     *     Ontology class to be compared with.
     * @param class_2
     *     Ontology class to be compared.
     *
     * @return <tt>true</tt> if and only if these ontology classes have the <tt>sameAs</tt> relationship, <tt>false</tt>
     * in other fact.
     */
    public static boolean areSameAs(OntClass class_1, OntClass class_2) {
        return class_1.isSameAs(class_2);
    }

    /**
     * Recognizes if two <tt>OntClass</tt> have the <tt>equivalent</tt> connection o relationship.
     *
     * @param class_1
     *     Ontology class to be compared with.
     * @param class_2
     *     Ontology class to be compared.
     *
     * @return <tt>true</tt> if and only if these ontology classes have the <tt>equivalent</tt> relationship,
     * <tt>false</tt> in other fact.
     */
    public static boolean areEquivalentClasses(OntClass class_1, OntClass class_2) {
        boolean equivalent = false;
        final Iterable<OntClass> list_equivalent_classes = new ExtIterIterable<OntClass>(
                class_1.listEquivalentClasses());
        for (final OntClass equivalent_class : list_equivalent_classes) {
            String uri_class_2 = class_2.getURI();
            String uri_equivalent_class = equivalent_class.getURI();

            if (uri_class_2.equals(uri_equivalent_class)) {
                equivalent = true;
                break;
            }
        }
        return equivalent;
    }

    public static boolean isSubclassOf(OntClass class_1, OntClass class_2) {
        boolean equivalent = false;
        final Iterable<OntClass> list_sub_classes = new ExtIterIterable<OntClass>(class_1.listSubClasses());
        for (final OntClass sub_class : list_sub_classes) {
            final String uri_class_2 = class_2.getURI();
            final String uri_sub_class = sub_class.getURI();

            if (uri_class_2.equals(uri_sub_class)) {
                equivalent = true;
                break;
            }
        }
        return equivalent;
    }
}
