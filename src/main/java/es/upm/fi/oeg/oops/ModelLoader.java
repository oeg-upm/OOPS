/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public final class ModelLoader {

    private ModelLoader() {
    }

    public static Model loadJenaBasic(final SrcSpec srcSpec) {
        // System.out.println("Load model");

        // try to load the file in final uri
        // create an empty model
        final OntDocumentManager dm = new OntDocumentManager();
        dm.setProcessImports(false);

        final OntModelSpec spec = new OntModelSpec(null, dm, null, ProfileRegistry.OWL_LANG);
        final Model model = spec.createBaseModel();

        switch (srcSpec.getSrcType()) {
            case URI :
                final String uri = srcSpec.getIri();
                final SerializationFormats cType = srcSpec.getContentType();
                final String jenaLang = cType == null ? null : cType.toJena();
                model.read(uri, jenaLang);
                break;
            case RDF_CODE :
                throw new UnsupportedOperationException("Not Implemented; requires IRI");
            default :
                throw new UnsupportedOperationException("Not Implemented");
        }

        return model;
    }

    public static OntModel loadJena(final SrcSpec srcSpec) throws IOException {

        OntModel model;
        try {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            switch (srcSpec.getSrcType()) {
                case RDF_CODE :
                    System.out.println(srcSpec.getContent());
                    final byte[] contentBytes = srcSpec.getContent().trim().getBytes();
                    final InputStream contentStream = new ByteArrayInputStream(contentBytes);
                    final SerializationFormats cType = srcSpec.getContentType();
                    final String jenaLang = cType == null ? null : cType.toJena();
                    model.read(contentStream, null, jenaLang);
                    break;
                case URI :
                    model.read(srcSpec.getIri());
                    break;
                default :
                    throw new UnsupportedOperationException("Not yet implemented");
            }
        } catch (final JenaException exc) {
            if (exc.getMessage().contains("UTF-8 sequence")) {
                model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                final byte[] bytes = srcSpec.getContent().getBytes("UTF-8");
                final InputStream bytesStream = new ByteArrayInputStream(bytes);
                model.read(bytesStream, null);
            } else {
                throw exc;
            }
        }

        return model;
    }

    public static OWLOntology loadOwl(final SrcSpec srcSpec) throws IOException, OWLOntologyCreationException {

        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final SerializationFormats cType = srcSpec.getContentType();
        final OWLDocumentFormat owlLang = cType == null ? null : cType.toOwl();
        final String mimeType = cType == null ? null : cType.toMimeType();
        final OWLOntologyDocumentSourceBase docSrc;
        switch (srcSpec.getSrcType()) {
            case RDF_CODE :
                final byte[] contentBytes = srcSpec.getContent().trim().getBytes();
                final InputStream contentStream2 = new ByteArrayInputStream(contentBytes);
                docSrc = new StreamDocumentSource(contentStream2, "inputstream:ontology", owlLang, mimeType);
                break;
            case URI :
                // Let's load an ontology from the web
                final IRI iri = IRI.create(srcSpec.getIri());
                docSrc = new IRIDocumentSource(iri, owlLang, mimeType);
                break;
            default :
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return manager.loadOntologyFromOntologyDocument(docSrc);
    }

    public static SrcModel load(final SrcSpec srcSpec) throws IOException, OWLOntologyCreationException {
        return new SrcModel(srcSpec, loadJena(srcSpec), loadOwl(srcSpec));
    }
}
