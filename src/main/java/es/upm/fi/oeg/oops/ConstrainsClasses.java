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
     * @param class1
     *     Ontology class to be compared with.
     * @param class2
     *     Ontology class to be compared.
     *
     * @return <tt>true</tt> if and only if these ontology classes have the <tt>sameAs</tt> relationship, <tt>false</tt>
     * in other fact.
     */
    public static boolean areSameAs(final OntClass class1, final OntClass class2) {
        return class1.isSameAs(class2);
    }

    /**
     * Recognizes if two <tt>OntClass</tt> have the <tt>equivalent</tt> connection o relationship.
     *
     * @param class1
     *     Ontology class to be compared with.
     * @param class2
     *     Ontology class to be compared.
     *
     * @return <tt>true</tt> if and only if these ontology classes have the <tt>equivalent</tt> relationship,
     * <tt>false</tt> in other fact.
     */
    public static boolean areEquivalentClasses(final OntClass class1, final OntClass class2) {
        boolean equivalent = false;
        final Iterable<OntClass> equivalentClasses = new ExtIterIterable<OntClass>(class1.listEquivalentClasses());
        for (final OntClass equivalentClass : equivalentClasses) {
            String uriClass2 = class2.getURI();
            String uriEquivalentClass = equivalentClass.getURI();

            if (uriClass2.equals(uriEquivalentClass)) {
                equivalent = true;
                break;
            }
        }
        return equivalent;
    }

    public static boolean isSubclassOf(final OntClass class1, final OntClass class2) {
        boolean equivalent = false;
        final Iterable<OntClass> listSubClasses = new ExtIterIterable<OntClass>(class1.listSubClasses());
        for (final OntClass subClass : listSubClasses) {
            final String uriClass2 = class2.getURI();
            final String uriSubClass = subClass.getURI();

            if (uriClass2.equals(uriSubClass)) {
                equivalent = true;
                break;
            }
        }
        return equivalent;
    }
}
