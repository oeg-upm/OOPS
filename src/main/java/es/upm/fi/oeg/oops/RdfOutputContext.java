/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import org.apache.jena.ontology.OntModel;

public class RdfOutputContext {

    private final SrcModel srcModel;
    private final OntModel outModel;

    public RdfOutputContext(final SrcModel srcModel, final OntModel outModel) {
        this.srcModel = srcModel;
        this.outModel = outModel;
    }

    public SrcModel getSrcModel() {
        return this.srcModel;
    }

    public OntModel getOutModel() {
        return this.outModel;
    }
}
