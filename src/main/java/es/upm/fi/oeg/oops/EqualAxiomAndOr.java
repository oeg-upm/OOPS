/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.BooleanClassDescription;
import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * compara si dos clases compuestas por uniones y conjunciones es la misma. ignora cardinalidades y axiomas de
 * restricci�n universales y existenciales.
 */
public class EqualAxiomAndOr {

    private final Logger logger = LoggerFactory.getLogger(EqualAxiomAndOr.class);

    boolean isEqual = false;

    public EqualAxiomAndOr(final OntResource resource1, final OntResource resource2, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        // pueden ser clases?
        if (resource1.isClass() && resource2.isClass()) {
            final OntClass class1 = resource1.asClass();
            final OntClass class2 = resource2.asClass();
            if (class1.isURIResource() && class2.isURIResource()) {
                if (class1.getURI().equals(class2.getURI())) {
                    isEqual = true;
                }
            } else {
                isEqual = equalAxiom(class1, class2, context, ruleInfo);
            }
        } else {
            isEqual = false;
        }
    }

    private boolean equalAxiom(final OntClass class1, final OntClass class2, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        if (class1.isURIResource() && class2.isURIResource()) {
            return class1.getURI().equals(class2.getURI());
        } else if (class1.isUnionClass() && class2.isUnionClass()) {
            // tengo que comparar todos los operandos con todos los operandos
            final OntClass union1 = class1.asUnionClass();
            final OntClass union2 = class2.asUnionClass();

            final ExtendedIterator<OntClass> classesUnion1 = (ExtendedIterator<OntClass>) ((BooleanClassDescription) union1)
                    .listOperands();
            final List<OntClass> classesUnion2 = (List<OntClass>) ((BooleanClassDescription) union2).listOperands()
                    .toList();

            final Set<OntClass> listOperands1 = (Set<OntClass>) ((BooleanClassDescription) union1).listOperands()
                    .toSet();
            final Set<OntClass> listOperands2 = (Set<OntClass>) ((BooleanClassDescription) union2).listOperands()
                    .toSet();

            if (listOperands1.size() != listOperands2.size()) {
                // no tienen el mismo numero de operandos, no pueden ser iguales
                // podr�an serlo en terminos l�gicos pero no lo voy a tratar en este momento
                return false;
            } else {
                boolean allMatch = true;

                while (classesUnion1.hasNext() && allMatch) {
                    final OntClass class1Aux = classesUnion1.next();
                    boolean oneToOne = false;
                    int i = 0;

                    while (i < classesUnion2.size() && !oneToOne) {
                        final OntClass class2Aux = classesUnion2.get(i++);

                        oneToOne = new EqualAxiomAndOr(class1Aux, class2Aux, context, ruleInfo).getEquals();
                        if (oneToOne) {
                            // si se ha encontrado una clase que encaja la elimino de la lista
                            classesUnion2.remove(class2Aux);
                        }
                    }

                    if (!oneToOne) {
                        allMatch = false;
                        return false;
                    }
                }

                return allMatch;
            }
        } else if (class1.isIntersectionClass() && class2.isIntersectionClass()) {
            final OntClass intersection1 = class1.asIntersectionClass();
            OntClass intersection2 = class2.asIntersectionClass();

            final ExtendedIterator<OntClass> classesIntersection1 = (ExtendedIterator<OntClass>) ((BooleanClassDescription) intersection1)
                    .listOperands();
            List<OntClass> classesIntersection2 = new ArrayList<>();

            do {
                try {
                    classesIntersection2 = (List<OntClass>) ((BooleanClassDescription) intersection2).listOperands()
                            .toList();
                } catch (ConversionException exc) {
                    final String classUri = exc.getMessage().split(" ")[3];
                    logger.warn("Creating new class '{}', required because of '{}'", classUri, class1);
                    final OntClass cAux = context.getModel().createClass(classUri);

                    context.addClassWarning(ruleInfo, cAux,
                            "The attached resources do not have `rdf:type` `owl:Class` or equivalent");
                    // System.err.println("WARNING: the class " + classUri + " does not have rdf:type
                    // owl:Class or equivalent");
                    continue;
                }
                intersection2 = class2.asIntersectionClass();
                classesIntersection2 = new ArrayList<OntClass>();
            } while (false);

            final Set<OntClass> listOperands1 = (Set<OntClass>) ((BooleanClassDescription) intersection1).listOperands()
                    .toSet();
            final Set<OntClass> listOperands2 = (Set<OntClass>) ((BooleanClassDescription) intersection1).listOperands()
                    .toSet();

            if (listOperands1.size() != listOperands2.size()) {
                // no tienen el mismo numero de operandos, no pueden ser iguales
                // podr�an serlo en terminos l�gicos pero no lo voy a tratar en este momento
                return false;
            } else {
                boolean allMatch = true;

                while (classesIntersection1.hasNext() && allMatch) {
                    final OntClass class1Aux = classesIntersection1.next();
                    boolean oneToOne = false;
                    int i = 0;
                    while (i < classesIntersection2.size() && !oneToOne) {
                        final OntClass class2Aux = classesIntersection2.get(i++);

                        oneToOne = new EqualAxiomAndOr(class1Aux, class2Aux, context, ruleInfo).getEquals();
                        if (oneToOne) {
                            // si se ha encontrado una clase que encaja la elimino de la lista
                            classesIntersection2.remove(class2Aux);
                        }
                    }

                    if (!oneToOne) {
                        allMatch = false;
                        return false;
                    }
                }

                return allMatch;
            }
        } else if (class1.isRestriction() && class2.isRestriction()) {
            final Restriction restr1 = class1.asRestriction();
            final Restriction restr2 = class2.asRestriction();

            boolean equal = false;

            if (restr1.isSomeValuesFromRestriction() && restr2.isSomeValuesFromRestriction()) {
                final SomeValuesFromRestriction some1 = restr1.asSomeValuesFromRestriction();
                final SomeValuesFromRestriction some2 = restr2.asSomeValuesFromRestriction();

                final OntProperty onProp1 = some1.getOnProperty();
                final OntProperty onProp2 = some2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    equal = new EqualAxiomAndOr((OntResource) some1.getSomeValuesFrom(),
                            (OntResource) some2.getSomeValuesFrom(), context, ruleInfo).getEquals();
                }
            } else if (restr1.isAllValuesFromRestriction() && restr2.isAllValuesFromRestriction()) {
                final AllValuesFromRestriction all1 = restr1.asAllValuesFromRestriction();
                final AllValuesFromRestriction all2 = restr2.asAllValuesFromRestriction();

                final OntProperty onProp1 = all1.getOnProperty();
                final OntProperty onProp2 = all2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    equal = new EqualAxiomAndOr((OntResource) all1.getAllValuesFrom(),
                            (OntResource) all2.getAllValuesFrom(), context, ruleInfo).getEquals();
                }
            } else if (restr1.isMinCardinalityRestriction() && restr2.isMinCardinalityRestriction()) {
                final MinCardinalityRestriction min1 = restr1.asMinCardinalityRestriction();
                final MinCardinalityRestriction min2 = restr2.asMinCardinalityRestriction();

                final OntProperty onProp1 = min1.getOnProperty();
                final OntProperty onProp2 = min2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    int minCard1 = min1.getMinCardinality();
                    int minCard2 = min2.getMinCardinality();

                    if (minCard1 == minCard2) {
                        equal = true;
                    }
                }
            } else if (restr1.isMaxCardinalityRestriction() && restr2.isMaxCardinalityRestriction()) {
                final MaxCardinalityRestriction max1 = restr1.asMaxCardinalityRestriction();
                final MaxCardinalityRestriction max2 = restr2.asMaxCardinalityRestriction();

                final OntProperty onProp1 = max1.getOnProperty();
                final OntProperty onProp2 = max2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    int maxCard1 = max1.getMaxCardinality();
                    int maxCard2 = max2.getMaxCardinality();

                    if (maxCard1 == maxCard2) {
                        equal = true;
                    }
                }
            } else if (restr1.isCardinalityRestriction() && restr2.isCardinalityRestriction()) {
                final CardinalityRestriction card1 = restr1.asCardinalityRestriction();
                final CardinalityRestriction card2 = restr2.asCardinalityRestriction();

                final OntProperty onProp1 = card1.getOnProperty();
                final OntProperty onProp2 = card2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    int exactCard1 = card1.getCardinality();
                    int exactCard2 = card2.getCardinality();

                    if (exactCard1 == exactCard2) {
                        equal = true;
                    }
                }
            } else if (restr1.isHasValueRestriction() && restr2.isHasValueRestriction()) {
                final HasValueRestriction hasValue1 = restr1.asHasValueRestriction();
                final HasValueRestriction hasValue2 = restr2.asHasValueRestriction();

                final OntProperty onProp1 = hasValue1.getOnProperty();
                final OntProperty onProp2 = hasValue2.getOnProperty();

                if (onProp1.getURI().equals(onProp2.getURI())) {
                    final RDFNode value1 = hasValue1.getHasValue();
                    final RDFNode value2 = hasValue2.getHasValue();

                    if (value1.isURIResource() && value2.isURIResource()) {
                        if (value1.asResource().getURI().equals(value2.asResource().getURI())) {
                            equal = true;
                        }
                    } else if (value1.isLiteral() && value2.isLiteral()) {
                        final Literal literal1 = value1.asLiteral();
                        final Literal literal2 = value2.asLiteral();

                        if (literal1.asLiteral().getDatatype() == literal2.getDatatype()
                                && literal1.getValue().equals(literal2.getValue())) {
                            equal = true;
                        }

                    }
                }
            }

            return equal;
        } else {
            // System.err.println("esta restriccion no se ha tenido en cuenta: " + );
            return false;
        }
    }

    boolean getEquals() {
        return isEqual;
    }
}
