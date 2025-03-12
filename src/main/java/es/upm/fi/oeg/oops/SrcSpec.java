/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

/**
 * The already in-memory model of an ontology to be analyzed/checked/linted.
 */
public class SrcSpec {

    private final SrcType srcType;
    private final String iri;
    private final String content;
    private final SerializationFormats contentType;

    public SrcSpec(final SrcType srcType, final String iri, final String content,
            final SerializationFormats contentType) {
        this.srcType = srcType;
        this.iri = iri;
        this.content = content;
        this.contentType = contentType;
    }

    public SrcType getSrcType() {
        return this.srcType;
    }

    public String getIri() {
        return this.iri;
    }

    public String getContent() {
        return this.content;
    }

    public SerializationFormats getContentType() {
        return this.contentType;
    }
}
