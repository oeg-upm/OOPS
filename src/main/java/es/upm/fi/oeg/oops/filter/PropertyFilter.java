/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.filter;

import com.github.jsonldjava.shaded.com.google.common.base.Supplier;
import es.upm.fi.oeg.oops.CheckingContext;
import es.upm.fi.oeg.oops.ExtIterIterable;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;

public interface PropertyFilter<PT extends OntProperty> {
    boolean accept(PT prop);

    /**
     * Provides the concrete property type.
     *
     * @return The concrete class of <code>PT</code>.
     */
    Class<PT> getPropertyTypeClass();

    default Supplier<Iterable<PT>> allPropsGen(final CheckingContext context) {

        final OntModel model = context.getModel();

        return new Supplier<>() {
            @Override
            public Iterable<PT> get() {
                final Class<PT> propertyTypeClass = getPropertyTypeClass();
                final ExtendedIterator<PT> allPropsIter;
                if (propertyTypeClass.equals(ObjectProperty.class)) {
                    allPropsIter = (ExtendedIterator<PT>) model.listObjectProperties();
                } else if (propertyTypeClass.equals(DatatypeProperty.class)) {
                    allPropsIter = (ExtendedIterator<PT>) model.listDatatypeProperties();
                } else if (propertyTypeClass.equals(OntProperty.class)) {
                    allPropsIter = (ExtendedIterator<PT>) model.listAllOntProperties();
                } else {
                    throw new UnsupportedOperationException("Not implemented");
                }
                return new ExtIterIterable<PT>(allPropsIter);
            }
        };
    }

    default List<PT> filter(final CheckingContext context) {

        final List<PT> includedProperties = new ArrayList<>();
        final Iterable<PT> allProps = allPropsGen(context).get();
        for (final PT prop : allProps) {
            if (accept(prop)) {
                includedProperties.add(prop);
            }
        }

        return includedProperties;
    }
}
