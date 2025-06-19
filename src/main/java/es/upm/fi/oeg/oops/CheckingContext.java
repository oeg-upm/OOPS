/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.extjwnl.JWNLException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.OWLOntology;

public class CheckingContext {

    private final SrcModel srcModel;
    private final Model outputModel;

    private final Map<PitfallId, List<Pitfall>> results;

    // private List<String> cWarning = new ArrayList<>();
    // private List<String> pWarning = new ArrayList<>();
    private List<Warning> warnings = new ArrayList<>();

    private final SynsetHelp dictionary;

    public CheckingContext(final SrcModel srcModel, final Model outputModel) throws IOException, JWNLException {
        this.srcModel = srcModel;
        this.outputModel = outputModel;
        this.results = new HashMap<>();
        dictionary = new SynsetHelp();
    }

    public Model getOutputModel() {
        return this.outputModel;
    }

    public void addResult(final Pitfall pitfall) {
        final PitfallId id = pitfall.getInfo().getId();
        final List<Pitfall> pitfalls;
        if (this.results.containsKey(id)) {
            pitfalls = this.results.get(id);
        } else {
            pitfalls = new ArrayList<>();
            this.results.put(id, pitfalls);
        }
        pitfalls.add(pitfall);
    }

    public <RT extends Resource> void addResult(final PitfallInfo info, final RT resource) {
        this.addResult(new PitfallImpl(info, Set.of(resource)));
    }

    public <RT extends OntResource> void addResult(final PitfallInfo info, final RT resource1, final RT resource2) {
        this.addResult(new PitfallImpl(info, Set.of(resource1, resource2)));
    }

    public <RT extends OntResource> void addResult(final PitfallInfo info, final Collection<RT> resources) {
        this.addResult(new PitfallImpl(info, new HashSet<>(resources)));
    }

    public <RT extends OntResource> void addResultsIndividual(final PitfallInfo info, final Collection<RT> resources) {
        for (final RT resource : resources) {
            this.addResult(info, resource);
        }
    }

    public <RT extends OntResource, CT extends Collection<RT>> void addResultsIndividualSets(final PitfallInfo info,
            final Collection<CT> resourceSets) {
        for (final CT resourceSet : resourceSets) {
            this.addResult(info, resourceSet);
        }
    }

    // public void addResult(final List<? extends OntResource> ress) {
    // this.results.add(ress.stream().map(res -> res.getURI()).collect(Collectors.joining(" ")));
    // }

    // public void addResult(final OntProperty prop1, final OntProperty prop2) {
    // this.results.add(prop1.getURI() + " " + prop2.getURI());
    // }

    // public void addResult(final OntResource res) {
    // this.results.add(res.getURI());
    // }

    // public void addClassWarning(final String msg) {
    // this.cWarning.add(msg);
    // }

    // public void addPropertyWarning(final String msg) {
    // this.pWarning.add(msg);
    // }

    public void addWarning(final Warning warning) {
        this.warnings.add(warning);
    }

    public void addOntologyWarning(final CheckerInfo checkerInfo, final String msg) {
        addWarning(new WarningImpl(WarningType.ONTOLOGY, checkerInfo, Set.of(), msg));
    }

    public void addClassWarning(final CheckerInfo checkerInfo, final OntClass cls, final String msg) {
        addWarning(new WarningImpl(WarningType.CLASS, checkerInfo, Set.of(cls), msg));
    }

    public void addClassWarning(final CheckerInfo checkerInfo, final OntClass cls1, final OntClass cls2,
            final String msg) {
        addWarning(new WarningImpl(WarningType.CLASS, checkerInfo, Set.of(cls1, cls2), msg));
    }

    public void addPropertyWarning(final CheckerInfo checkerInfo, final OntProperty prop, final String msg) {
        addWarning(new WarningImpl(WarningType.PROPERTY, checkerInfo, Set.of(prop), msg));
    }

    public SrcModel getSrcModel() {
        return this.srcModel;
    }

    public OntModel getModel() {
        return this.srcModel.getModel();
    }

    public OWLOntology getModelOwl() {
        return this.srcModel.getModelOWL();
    }

    public boolean detectedPitfalls() {
        return !this.results.isEmpty();
    }

    public Map<PitfallId, List<Pitfall>> getResults() {
        return this.results;
    }

    public List<Warning> getWarnings() {
        return this.warnings;
    }

    public SynsetHelp getDictionary() {
        return this.dictionary;
    }

    // public List<String> getClassWarnings() {
    // return this.cWarning;
    // }

    // public List<String> getPropertyWarnings() {
    // return this.pWarning;
    // }
}
