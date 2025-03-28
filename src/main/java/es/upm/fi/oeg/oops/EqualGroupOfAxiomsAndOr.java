/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Iterator;
import java.util.List;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * compara si dos clases compuestas por uniones y conjunciones es la misma. ignora cardinalidades y axiomas de
 * restricci�n universales y existenciales.
 */
public class EqualGroupOfAxiomsAndOr {

    private boolean isEqualGroup = false;

    public EqualGroupOfAxiomsAndOr(final ExtendedIterator<? extends OntResource> resources1,
            final ExtendedIterator<? extends OntResource> resources2, final CheckingContext context,
            final CheckerInfo ruleInfo) {
        this(resources1.toList(), resources2.toList(), context, ruleInfo);
    }

    public EqualGroupOfAxiomsAndOr(final List<? extends OntResource> listResources1,
            final List<? extends OntResource> listResources2, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        if (listResources1.size() != listResources2.size()) {
            // no tienen el mismo numero de operandos, no pueden ser iguales
            // podr�an serlo en terminos l�gicos pero no lo voy a tratar en este momento
            isEqualGroup = false;
        } else {
            final Iterator<OntResource> resources11 = (Iterator<OntResource>) listResources1.iterator();
            boolean allMatch = true;

            while (resources11.hasNext() && allMatch) {
                OntResource class1Aux = resources11.next();
                boolean oneToOne = false;

                int i = 0;
                while (i < listResources2.size() && !oneToOne) {
                    OntResource class2Aux = listResources2.get(i++);
                    oneToOne = new EqualAxiomAndOr(class1Aux, class2Aux, context, ruleInfo).getEquals();
                    if (oneToOne) {
                        // si se ha encontrado un recurso que encaja lo elimino de la lista
                        listResources2.remove(class2Aux);
                    }
                }

                if (!oneToOne) {
                    allMatch = false;
                    isEqualGroup = false;
                }
            }

            isEqualGroup = allMatch;
        }
    }

    public boolean getEqualGroup() {
        return isEqualGroup;
    }
}
