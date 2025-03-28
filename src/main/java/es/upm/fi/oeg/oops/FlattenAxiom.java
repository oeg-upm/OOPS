/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import es.upm.fi.oeg.oops.filter.PropertyFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.ComplementClass;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

public class FlattenAxiom {

    private final Set<OntClass> classesInAxiomsOnto = new HashSet<>();
    private final Set<OntClass> classesInAxiomsClass = new HashSet<>();
    private final Set<ObjectProperty> propertiesInAxiomsProperty = new HashSet<>();
    private final Set<DatatypeProperty> attributesInAxiomsProperty = new HashSet<>();

    public FlattenAxiom() {
    }

    // Calcula las clases que aparecen en los axiomas DE LAS CLASES
    public void calculateClassesInAxiomsOnto(final CheckingContext context, final CheckerInfo ruleInfo) {

        // s�lo tiene en cuenta los axioma complejos, no las superclases o equivalencias simples
        ExtendedIterator<OntClass> c = context.getModel().listNamedClasses();
        List<OntClass> cList = c.toList();

        List<OntClass> axiomsAll = new ArrayList<OntClass>();

        for (int i = 0; i < cList.size(); i++) {
            OntClass class1 = cList.get(i);
            ExtendedIterator<OntClass> superClasses = class1.listSuperClasses();

            try {
                axiomsAll.addAll(superClasses.toList());
            } catch (final ConversionException exc) {
                final String classUri = exc.getMessage().split(" ")[3];
                final OntClass cAux = context.getModel().createClass(classUri);
                context.addClassWarning(ruleInfo, class1,
                        "The attached resources do not have `rdf:type` `owl:Class` or equivalent");
            }

            final ExtendedIterator<OntClass> equiClasses = class1.listEquivalentClasses();
            axiomsAll.addAll(equiClasses.toList());
        }

        for (int i = 0; i < axiomsAll.size(); i++) {
            final OntClass axiom = axiomsAll.get(i);

            if (axiom.isRestriction()) {
                final Restriction restriction = axiom.asRestriction();

                try {
                    boolean isOP = false;

                    if (restriction.getOnProperty() != null) {
                        isOP = restriction.getOnProperty().isObjectProperty();
                    }

                    if (isOP) {
                        if (restriction.isAllValuesFromRestriction()) {
                            final AllValuesFromRestriction rest2 = restriction.asAllValuesFromRestriction();
                            final Resource resource = rest2.getAllValuesFrom();

                            if (resource.isURIResource()) {
                                if (resource.canAs(OntClass.class)) {
                                    ;
                                }
                                classesInAxiomsOnto.add(resource.as(OntClass.class));
                                // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                            } else {
                                if (resource.canAs(OntClass.class)) {
                                    axiomsAll.add(resource.as(OntClass.class));
                                    // System.out.println("Lo pongo en cola como parte de allvalues ");
                                }
                            }
                        }
                        /*
                         * else if (restriction.isCardinalityRestriction()){
                         *
                         * CardinalityRestriction rest2 = restriction.asCardinalityRestriction();
                         *
                         * } else if (restriction.isHasValueRestriction()){
                         *
                         * HasValueRestriction rest2 = restriction.asHasValueRestriction();
                         *
                         * } else if (restriction.isMaxCardinalityRestriction()){
                         *
                         * MaxCardinalityRestriction rest2 = restriction.asMaxCardinalityRestriction();
                         *
                         * } else if (restriction.isMinCardinalityRestriction()){
                         *
                         * MinCardinalityRestriction rest2 = restriction.asMinCardinalityRestriction();
                         *
                         * }
                         */
                        else if (restriction.isSomeValuesFromRestriction()) {
                            final SomeValuesFromRestriction rest2 = restriction.asSomeValuesFromRestriction();
                            final Resource resource = rest2.getSomeValuesFrom();

                            if (resource.isURIResource()) {
                                if (resource.canAs(OntClass.class)) {
                                    ;
                                }
                                classesInAxiomsOnto.add(resource.as(OntClass.class));
                                // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                            } else {
                                if (resource.canAs(OntClass.class)) {
                                    axiomsAll.add(resource.as(OntClass.class));
                                    // System.out.println("Lo pongo en cola como parte de somevalues ");
                                }
                            }
                        } else {
                            // System.out.println("Es otro tipo de restricci�n");
                        }
                    } else {
                        // System.out.println("es en un atributo");
                    }
                } catch (final ConversionException exc) {
                    // cWarning.add(propUri);
                    // System.err.println("WARNING: the class " + exc.getMessage().split(" ")[3] + " does not have
                    // rdf:type owl:Class or equivalent");
                    // OntClass caux = model.createClass(propUri);
                }
            } else if (axiom.isComplementClass()) {
                final ComplementClass complement = axiom.asComplementClass();
                final OntResource resource = complement.getOperand();

                if (resource.isURIResource()) {
                    if (resource.canAs(OntClass.class)) {
                        ;
                    }
                    classesInAxiomsOnto.add(resource.as(OntClass.class));
                    // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                } else {
                    if (resource.canAs(OntClass.class)) {
                        axiomsAll.add(resource.as(OntClass.class));
                        // System.out.println("lo pongo en cola como parte de la negaci�n");
                    }
                }
            } else if (axiom.isIntersectionClass()) {
                final IntersectionClass intersection = axiom.asIntersectionClass();
                final ExtendedIterator<OntClass> classesIntersection = (ExtendedIterator<OntClass>) intersection
                        .listOperands();

                while (classesIntersection.hasNext()) {
                    try {
                        final OntResource resource = classesIntersection.next();

                        if (resource.isURIResource()) {
                            if (resource.canAs(OntClass.class)) {
                                ;
                            }
                            classesInAxiomsOnto.add(resource.as(OntClass.class));
                            // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                        } else {
                            if (resource.canAs(OntClass.class)) {
                                axiomsAll.add(resource.as(OntClass.class));
                                // System.out.println("lo pongo en cola como parte de la interseccion");
                            }
                        }
                    } catch (final ConversionException exc) {
                        final String classUri = exc.getMessage().split(" ")[3];
                        final OntClass cAux = context.getModel().createClass(classUri);
                        context.addClassWarning(ruleInfo, cAux,
                                "The attached resources do not have `rdf:type` `owl:Class` or equivalent");
                    }
                }
            } else if (axiom.isUnionClass()) {
                final UnionClass union = axiom.asUnionClass();
                final ExtendedIterator<OntClass> classesUnion = (ExtendedIterator<OntClass>) union.listOperands();

                while (classesUnion.hasNext()) {
                    try {
                        final OntResource resource = classesUnion.next();

                        if (resource.isURIResource()) {
                            if (resource.canAs(OntClass.class)) {
                                ;
                            }
                            classesInAxiomsOnto.add(resource.as(OntClass.class));
                            // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                        } else {
                            if (resource.canAs(OntClass.class)) {
                                axiomsAll.add(resource.as(OntClass.class));
                                // System.out.println("lo pongo en cola como parte de la union");
                            }
                        }
                    } catch (final ConversionException exc) {
                        final String classUri = exc.getMessage().split(" ")[3];
                        final OntClass cAux = context.getModel().createClass(classUri);
                        context.addClassWarning(ruleInfo, cAux,
                                "The attached resources do not have `rdf:type` `owl:Class` or equivalent");
                    }
                }
            }
        }
    }

    public void calculateClassesInAxiomsClass(final OntClass class1, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        final List<OntClass> axiomsAll = new ArrayList<OntClass>();

        final ExtendedIterator<OntClass> superClasses = class1.listSuperClasses();
        axiomsAll.addAll(superClasses.toList());

        final ExtendedIterator<OntClass> equiClasses = class1.listEquivalentClasses();
        axiomsAll.addAll(equiClasses.toList());

        int axiomsAllSize = axiomsAll.size();
        for (int i = 0; i < axiomsAllSize; i++) {
            final OntClass axiom = axiomsAll.get(i);

            if (axiom.isRestriction()) {
                final Restriction restriction = axiom.asRestriction();

                try {
                    final boolean isOP = restriction.getOnProperty().isObjectProperty();

                    if (isOP) {
                        if (restriction.isAllValuesFromRestriction()) {
                            final AllValuesFromRestriction rest2 = restriction.asAllValuesFromRestriction();
                            final Resource resource = rest2.getAllValuesFrom();

                            if (resource.isURIResource()) {
                                if (resource.canAs(OntClass.class)) {
                                    ;
                                }
                                classesInAxiomsClass.add(resource.as(OntClass.class));
                                // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                            } else {
                                if (resource.canAs(OntClass.class)) {
                                    axiomsAll.add(resource.as(OntClass.class));
                                    // System.out.println("Lo pongo en cola como parte de allvalues ");
                                }
                            }
                        }
                        /*
                         * else if (restriction.isCardinalityRestriction()){
                         *
                         * CardinalityRestriction rest2 = restriction.asCardinalityRestriction();
                         *
                         * } else if (restriction.isHasValueRestriction()){
                         *
                         * HasValueRestriction rest2 = restriction.asHasValueRestriction();
                         *
                         * } else if (restriction.isMaxCardinalityRestriction()){
                         *
                         * MaxCardinalityRestriction rest2 = restriction.asMaxCardinalityRestriction();
                         *
                         * } else if (restriction.isMinCardinalityRestriction()){
                         *
                         * MinCardinalityRestriction rest2 = restriction.asMinCardinalityRestriction();
                         *
                         * }
                         */
                        else if (restriction.isSomeValuesFromRestriction()) {
                            final SomeValuesFromRestriction rest2 = restriction.asSomeValuesFromRestriction();
                            final Resource resource = rest2.getSomeValuesFrom();

                            if (resource.isURIResource()) {
                                if (resource.canAs(OntClass.class)) {
                                    ;
                                }
                                classesInAxiomsClass.add(resource.as(OntClass.class));
                                // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                            } else {
                                if (resource.canAs(OntClass.class)) {
                                    axiomsAll.add(resource.as(OntClass.class));
                                    // System.out.println("Lo pongo en cola como parte de somevalues ");
                                }
                            }
                        } else {
                            // System.out.println("Es otro tipo de restricci�n");
                        }
                    } else {
                        // System.out.println("es en un atributo");
                    }
                } catch (final ConversionException exc) {
                    final String classUri = exc.getMessage().split(" ")[3];
                    final OntClass cAux = context.getModel().createClass(classUri);
                    context.addClassWarning(ruleInfo, cAux,
                            "The attached resources do not have `rdf:type` `owl:Class` or equivalent");
                }
            } else if (axiom.isComplementClass()) {
                final ComplementClass complement = axiom.asComplementClass();
                final Resource resource = complement.getOperand();

                if (resource.isURIResource()) {
                    if (resource.canAs(OntClass.class)) {
                        ;
                    }
                    classesInAxiomsClass.add(resource.as(OntClass.class));
                    // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                } else {
                    if (resource.canAs(OntClass.class)) {
                        axiomsAll.add(resource.as(OntClass.class));
                        // System.out.println("lo pongo en cola como parte de la negaci�n");
                    }
                }
            } else if (axiom.isIntersectionClass()) {
                final IntersectionClass intersection = axiom.asIntersectionClass();
                final ExtendedIterator<OntClass> classesIntersection = (ExtendedIterator<OntClass>) intersection
                        .listOperands();

                while (classesIntersection.hasNext()) {
                    final OntResource resource = classesIntersection.next();

                    if (resource.isURIResource()) {
                        if (resource.canAs(OntClass.class)) {
                            ;
                        }
                        classesInAxiomsClass.add(resource.as(OntClass.class));
                        // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                    } else {
                        if (resource.canAs(OntClass.class)) {
                            axiomsAll.add(resource.as(OntClass.class));
                            // System.out.println("lo pongo en cola como parte de la interseccion");
                        }
                    }
                }
            } else if (axiom.isUnionClass()) {
                final UnionClass union = axiom.asUnionClass();
                final ExtendedIterator<OntClass> classesUnion = (ExtendedIterator<OntClass>) union.listOperands();

                while (classesUnion.hasNext()) {
                    final OntResource resource = classesUnion.next();

                    if (resource.isURIResource()) {
                        if (resource.canAs(OntClass.class)) {
                            ;
                        }
                        classesInAxiomsClass.add(resource.as(OntClass.class));
                        // System.out.println("A�ado a la flatenaci�n " + resource.asClass().getLocalName());
                    } else {
                        if (resource.canAs(OntClass.class)) {
                            axiomsAll.add(resource.as(OntClass.class));
                            // System.out.println("lo pongo en cola como parte de la union");
                        }
                    }
                }
            } else if (axiom.isClass() && axiom.isURIResource()) {
                classesInAxiomsClass.add(axiom);
            }

            axiomsAllSize = axiomsAll.size();
        }
    }

    public void calculatePropInAxiomsProp(final OntProperty property1, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        final OntModel model = context.getModel();

        final List<OntResource> axiomsAll = new ArrayList<>();
        axiomsAll.addAll(property1.listDomain().toList());
        axiomsAll.addAll(property1.listRange().toList());
        final ExtendedIterator<? extends OntResource> equivalents = property1.listEquivalentProperties();

        do {
            try {
                axiomsAll.addAll(equivalents.toList());
            } catch (final ConversionException exc) {
                final String propUri = exc.getMessage().split(" ")[3];
                final OntProperty pAux = context.getModel().getOntProperty(propUri);
                if (pAux != null) {
                    // tiene propiedad con ese nombre
                    context.addPropertyWarning(ruleInfo, pAux,
                            "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                    model.createObjectProperty(propUri);
                }
                continue;
            }

            // aqui he reproducido lo hecho en la P05 pero el caso es distinto,
            // quiza hay que adaptarlo
            try {
                axiomsAll.addAll(property1.listEquivalentProperties().toList());
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<OntProperty> listEq = FilterEquivalents.factory(OntProperty.class)
                        .apply(property1);
                axiomsAll.addAll(listEq.filter(context));
            }
        } while (false);

        do {
            try {
                axiomsAll.addAll(property1.listSubProperties().toSet());
            } catch (final ConversionException exc) {
                final String propUri = exc.getMessage().split(" ")[3];
                final OntProperty pAux = context.getModel().getOntProperty(propUri);
                if (pAux != null) {
                    // tiene propiedad con ese nombre
                    context.addPropertyWarning(ruleInfo, pAux,
                            "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                    model.createObjectProperty(propUri);
                }
                continue;
            }

            // aqui he reproducido lo hecho en la P05 pero el caso es distinto,
            // quiza hay que adaptarlo
            try {
                axiomsAll.addAll(property1.listSubProperties().toList());
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<OntProperty> listSP = FilterSubProperties.factory(OntProperty.class)
                        .apply(property1);
                axiomsAll.addAll(listSP.filter(context));
            }
        } while (false);

        ExtendedIterator<? extends OntResource> inverses = property1.listInverse();
        do {
            try {
                axiomsAll.addAll(inverses.toList());
            } catch (final ConversionException exc) {
                final String propUri = exc.getMessage().split(" ")[3];
                final OntProperty pAux = context.getModel().getOntProperty(propUri);
                if (pAux != null) {
                    // tiene propiedad con ese nombre
                    context.addPropertyWarning(ruleInfo, pAux,
                            "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                    model.createObjectProperty(propUri);
                }
                continue;
            }

            // aqui he reproducido lo hecho en la P05 pero el caso es distinto, quiza hay que adaptarlo
            try {
                axiomsAll.addAll(property1.listInverse().toList());
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<OntProperty> listInv = FilterInverses.factory(OntProperty.class).apply(property1);
                axiomsAll.addAll(listInv.filter(context));
            }
        } while (false);

        int axiomsAllSize = axiomsAll.size();

        for (int i = 0; i < axiomsAllSize; i++) {
            final OntResource resource1 = axiomsAll.get(i);

            // if (resource1.isURIResource()){
            // System.out.println (property1.getURI() + " has involved: "+ resource1.getURI());
            // }

            if (resource1.isObjectProperty()) {
                propertiesInAxiomsProperty.add(resource1.asObjectProperty());
            } else if (resource1.isDatatypeProperty()) {
                // no hago nada, es un atributo
            } else if (resource1.isClass()) {
                final OntClass class1 = resource1.asClass();

                if (class1.isURIResource()) {
                    // si es una clase normal no hago nada
                } else if (class1.isRestriction()) {
                    final Restriction restriction = class1.asRestriction();

                    try {
                        restriction.getOnProperty();
                    } catch (final ConversionException exc) {
                        final String propUri = exc.getMessage().split(" ")[3];
                        final OntProperty pAux = context.getModel().getOntProperty(propUri);
                        if (pAux != null) {
                            // tiene propiedad con ese nombre
                            context.addPropertyWarning(ruleInfo, pAux,
                                    "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                            model.createObjectProperty(propUri);
                        }
                    }

                    // pregunto si la restricci�n es sobre una propiedad, si es atributo s�lo la segunda parte
                    if (restriction.getOnProperty().isObjectProperty()) {
                        final OntProperty propertyInvolved = restriction.getOnProperty();
                        propertiesInAxiomsProperty.add(propertyInvolved.asObjectProperty());

                        if (restriction.isAllValuesFromRestriction()) {
                            final AllValuesFromRestriction rest2 = restriction.asAllValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getAllValuesFrom();
                            axiomsAll.add(resource);
                        }
                        /*
                         * else if (restriction.isCardinalityRestriction()){
                         *
                         * CardinalityRestriction rest2 = restriction.asCardinalityRestriction();
                         *
                         * } else if (restriction.isHasValueRestriction()){
                         *
                         * HasValueRestriction rest2 = restriction.asHasValueRestriction();
                         *
                         * } else if (restriction.isMaxCardinalityRestriction()){
                         *
                         * MaxCardinalityRestriction rest2 = restriction.asMaxCardinalityRestriction();
                         *
                         * } else if (restriction.isMinCardinalityRestriction()){
                         *
                         * MinCardinalityRestriction rest2 = restriction.asMinCardinalityRestriction();
                         *
                         * }
                         */
                        else if (restriction.isSomeValuesFromRestriction()) {
                            final SomeValuesFromRestriction rest2 = restriction.asSomeValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getSomeValuesFrom();
                            axiomsAll.add(resource);
                        } else {
                            // System.out.println("Es otro tipo de restricci�n");
                        }
                    } else {
                        // System.out.println("es en un atributo");
                    }
                } else if (class1.isComplementClass()) {
                    final ComplementClass complement = class1.asComplementClass();

                    final OntResource resource = complement.getOperand();
                    axiomsAll.add(resource);
                } else if (class1.isIntersectionClass()) {
                    final IntersectionClass intersection = class1.asIntersectionClass();
                    final ExtendedIterator<OntClass> classesIntersection = (ExtendedIterator<OntClass>) intersection
                            .listOperands();

                    while (classesIntersection.hasNext()) {
                        final OntResource resource = classesIntersection.next();
                        axiomsAll.add(resource);
                    }
                } else if (class1.isUnionClass()) {
                    final UnionClass union = class1.asUnionClass();
                    final ExtendedIterator<OntClass> classesUnion = (ExtendedIterator<OntClass>) union.listOperands();

                    while (classesUnion.hasNext()) {
                        final OntResource resource = classesUnion.next();
                        axiomsAll.add(resource);
                    }
                }
            }

            axiomsAllSize = axiomsAll.size();
        }
    }

    public void calculateAttrInAxiomsProp(final OntProperty property1, final CheckingContext context,
            final CheckerInfo ruleInfo) {

        final OntModel model = context.getModel();

        final List<OntResource> axiomsAll = new ArrayList<OntResource>();
        axiomsAll.addAll(property1.listDomain().toList());

        final ExtendedIterator<? extends OntResource> equivalents = property1.listEquivalentProperties();
        do {
            try {
                axiomsAll.addAll(equivalents.toList());
            } catch (final ConversionException exc) {
                final String propUri = exc.getMessage().split(" ")[3];
                final OntProperty pAux = context.getModel().getOntProperty(propUri);
                if (pAux != null) {
                    // tiene propiedad con ese nombre
                    context.addPropertyWarning(ruleInfo, pAux,
                            "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                    model.createObjectProperty(propUri);
                }
                continue;
            }

            // aqui he reproducido lo hecho en la P05 pero el caso es distinto, quiza hay que adaptarlo
            try {
                axiomsAll.addAll(property1.listEquivalentProperties().toList());
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<OntProperty> listEq = FilterEquivalents.factory(OntProperty.class)
                        .apply(property1);
                axiomsAll.addAll(listEq.filter(context));
            }
        } while (false);

        ExtendedIterator<? extends OntResource> subs = property1.listSubProperties();
        do {
            try {
                axiomsAll.addAll(subs.toList());
            } catch (final ConversionException exc) {
                final String propUri = exc.getMessage().split(" ")[3];
                final OntProperty pAux = context.getModel().getOntProperty(propUri);
                if (pAux != null) {
                    // tiene propiedad con ese nombre
                    context.addPropertyWarning(ruleInfo, pAux,
                            "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                    model.createObjectProperty(propUri);
                }
                continue;
            }

            // aqui he reproducido lo hecho en la P05 pero el caso es distinto, quiza hay que adaptarlo
            try {
                axiomsAll.addAll(property1.listSubProperties().toList());
            } catch (final ConversionException exc) {
                // si no funciona las busco a mano
                final PropertyFilter<OntProperty> listSP = FilterSubProperties.factory(OntProperty.class)
                        .apply(property1);
                axiomsAll.addAll(listSP.filter(context));
            }
        } while (false);

        int axiomsAllSize = axiomsAll.size();
        for (int i = 0; i < axiomsAllSize; i++) {
            final OntResource resource1 = axiomsAll.get(i);
            // if (resource1.isURIResource()){
            // System.out.println ("Things in axiomasAll attr: "+ resource1.getURI());
            // }

            if (resource1.isDatatypeProperty()) {
                attributesInAxiomsProperty.add(resource1.asDatatypeProperty());
            } else if (resource1.isObjectProperty()) {
                // no hago nada, es una object
            } else if (resource1.isClass()) {
                OntClass class1 = resource1.asClass();

                if (class1.isURIResource()) {
                    // si es una clase normal no hago nada
                } else if (class1.isRestriction()) {
                    Restriction restriction = class1.asRestriction();

                    try {
                        restriction.getOnProperty();
                    } catch (final ConversionException exc) {
                        final String propUri = exc.getMessage().split(" ")[3];
                        final OntProperty pAux = context.getModel().getOntProperty(propUri);
                        if (pAux != null) {
                            // tiene propiedad con ese nombre
                            context.addPropertyWarning(ruleInfo, pAux,
                                    "The attached resources do not have `rdf:type` `owl:Property` or equivalent");
                            model.createObjectProperty(propUri);
                        }
                    }

                    // pregunto si la restricci�n es sobre una propiedad, si es atributo s�lo la segunda parte
                    if (restriction.getOnProperty().isObjectProperty()) {
                        if (restriction.isAllValuesFromRestriction()) {
                            final AllValuesFromRestriction rest2 = restriction.asAllValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getAllValuesFrom();
                            axiomsAll.add(resource);

                        }
                        /*
                         * else if (restriction.isCardinalityRestriction()){
                         *
                         * CardinalityRestriction rest2 = restriction.asCardinalityRestriction();
                         *
                         * } else if (restriction.isHasValueRestriction()){
                         *
                         * HasValueRestriction rest2 = restriction.asHasValueRestriction();
                         *
                         * } else if (restriction.isMaxCardinalityRestriction()){
                         *
                         * MaxCardinalityRestriction rest2 = restriction.asMaxCardinalityRestriction();
                         *
                         * } else if (restriction.isMinCardinalityRestriction()){
                         *
                         * MinCardinalityRestriction rest2 = restriction.asMinCardinalityRestriction();
                         *
                         * }
                         */
                        else if (restriction.isSomeValuesFromRestriction()) {
                            final SomeValuesFromRestriction rest2 = restriction.asSomeValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getSomeValuesFrom();
                            axiomsAll.add(resource);
                        } else {
                            // System.out.println("Es otro tipo de restricci�n");
                        }
                    } else {
                        // System.out.println("es en una atributo");
                        final OntProperty propertyInvolved = restriction.getOnProperty();
                        attributesInAxiomsProperty.add(propertyInvolved.asDatatypeProperty());

                        if (restriction.isAllValuesFromRestriction()) {
                            final AllValuesFromRestriction rest2 = restriction.asAllValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getAllValuesFrom();
                            axiomsAll.add(resource);
                        }
                        /*
                         * else if (restriction.isCardinalityRestriction()){
                         *
                         * CardinalityRestriction rest2 = restriction.asCardinalityRestriction();
                         *
                         * }
                         */
                        else if (restriction.isHasValueRestriction()) {
                            final HasValueRestriction rest2 = restriction.asHasValueRestriction();
                            final OntResource resource = (OntResource) rest2.getOnProperty();
                            axiomsAll.add(resource);
                        } /*
                           * else if (restriction.isMaxCardinalityRestriction()){
                           *
                           * MaxCardinalityRestriction rest2 = restriction.asMaxCardinalityRestriction();
                           *
                           * } else if (restriction.isMinCardinalityRestriction()){
                           *
                           * MinCardinalityRestriction rest2 = restriction.asMinCardinalityRestriction();
                           *
                           * }
                           */
                        else if (restriction.isSomeValuesFromRestriction()) {
                            final SomeValuesFromRestriction rest2 = restriction.asSomeValuesFromRestriction();

                            final OntResource resource = (OntResource) rest2.getSomeValuesFrom();
                            axiomsAll.add(resource);
                        } else {
                            // System.out.println("Es otro tipo de restricci�n");
                        }
                    }
                } else if (class1.isComplementClass()) {
                    final ComplementClass complement = class1.asComplementClass();

                    final OntResource resource = complement.getOperand();
                    axiomsAll.add(resource);
                } else if (class1.isIntersectionClass()) {
                    final IntersectionClass intersection = class1.asIntersectionClass();
                    axiomsAll.addAll(intersection.listOperands().toList());
                } else if (class1.isUnionClass()) {
                    final UnionClass union = class1.asUnionClass();
                    axiomsAll.addAll(union.listOperands().toList());
                }
            }

            axiomsAllSize = axiomsAll.size();
        }
    }

    public Set<OntClass> getClassesInAxiomsOnto() {
        return classesInAxiomsOnto;
    }

    public Set<OntClass> getClassesInAxiomsClass() {
        return classesInAxiomsClass;
    }

    public Set<ObjectProperty> getPropertiesInAxiomsProperty() {
        return propertiesInAxiomsProperty;
    }

    public Set<DatatypeProperty> getAttributesInAxiomsProperty() {
        return attributesInAxiomsProperty;
    }
}
