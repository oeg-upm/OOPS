/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import org.apache.jena.ontology.OntModel;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * The already in-memory model of an ontology to be analyzed/checked/linted.
 */
public class SrcModel {

    private final SrcSpec srcSpec;
    private final int elements;
    // private final Model model;
    private final OntModel model;
    private final OWLOntology modelOWL;

    public SrcModel(final SrcSpec srcSpec, final int elements, final OntModel model, final OWLOntology modelOWL) {
        this.srcSpec = srcSpec;
        this.elements = elements;
        this.model = model;
        this.modelOWL = modelOWL;
    }

    public SrcModel(final SrcSpec srcSpec, final OntModel model, final OWLOntology modelOWL) {
        this(srcSpec, -1, model, modelOWL);
    }

    public SrcSpec getSrcSpec() {
        return this.srcSpec;
    }

    public int getElements() {
        return this.elements;
    }

    public OntModel getModel() {
        return this.model;
    }

    public OWLOntology getModelOWL() {
        return this.modelOWL;
    }
}
