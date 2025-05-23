/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import es.upm.fi.oeg.oops.filter.PropertyFilter;
import java.util.function.Function;
import org.apache.jena.ontology.OntProperty;

public class FilterEquivalents<PT extends OntProperty> implements PropertyFilter<PT> {

    private final PT baseP;
    private final Class<PT> cls;

    private FilterEquivalents(final PT baseP, final Class<PT> cls) {
        this.baseP = baseP;
        this.cls = cls;
    }

    public static <PT extends OntProperty> Function<PT, PropertyFilter<PT>> factory(final Class<PT> cls) {
        return (final PT baseP) -> new FilterEquivalents<PT>(baseP, cls);
    }

    @Override
    public Class<PT> getPropertyTypeClass() {
        return cls;
    }

    @Override
    public boolean accept(final PT prop) {
        return prop.hasEquivalentProperty(this.baseP) || this.baseP.hasEquivalentProperty(prop);
    }
}
