/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;

/**
 * A Report of a run of all (configured) pitfall checkers.
 */
public class Report {

    private final int numClasses;
    private final int numProperties;
    private final int numPitfalls;
    private final List<Throwable> exceptions;
    // @JsonFormat(pattern = "MILLIS")
    // private final Duration executionTime;
    private final long executionTimeMillis;
    private final Map<PitfallId, List<Pitfall>> pitfalls;
    private final Map<WarningType, List<Warning>> warnings;
    // private final List<ObjectProperty> symmetricOrTransitiveSuggestion;
    // private final List<String> importsFailing;
    private final String xmlOutput;
    private final Model outputModel;

    public Report(final int numClasses, final int numProperties, final Map<PitfallId, List<Pitfall>> pitfalls,
            final List<Throwable> exceptions, final Duration executionTime,
            final Map<WarningType, List<Warning>> warnings, final String xmlOutput, final Model outputModel) {
        this.numClasses = numClasses;
        this.numProperties = numProperties;
        this.pitfalls = pitfalls;
        this.numPitfalls = pitfalls.entrySet().stream().map(entry -> entry.getValue().size()).reduce(0,
                (a, b) -> a + b);
        this.exceptions = exceptions;
        this.executionTimeMillis = executionTime.toMillis();
        this.warnings = warnings;
        // this.symmetricOrTransitiveSuggestion = symmetricOrTransitiveSuggestion;
        // this.importsFailing = importsFailing;
        this.xmlOutput = xmlOutput;
        this.outputModel = outputModel;
    }

    private static ObjectMapper createJsonMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final SimpleModule ontResModule = new SimpleModule();
        ontResModule.addSerializer(OntResource.class, new OntResourceSerializer());
        mapper.registerModule(ontResModule);
        // return JsonMapper.builder()
        // .addModule(new JavaTimeModule())
        // // .findAndAddModules()
        // .build();
        return mapper;
    }

    private static ObjectMapper createXmlMapper() {
        final ObjectMapper mapper = new XmlMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // SerializationFeature.FAIL_ON_EMPTY_BEANS
        final SimpleModule ontResModule = new SimpleModule();
        ontResModule.addSerializer(OntResource.class, new OntResourceSerializer());
        mapper.registerModule(ontResModule);
        // return JsonMapper.builder()
        // .addModule(new JavaTimeModule())
        // // .findAndAddModules()
        // .build();
        return mapper;
    }

    public String toJson() throws IOException {
        final ObjectMapper mapper = createJsonMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public void toJson(final File file) throws IOException {
        final ObjectMapper mapper = createJsonMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    public String toXml() throws IOException {
        final ObjectMapper mapper = createXmlMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public void toXml(final File file) throws IOException {
        final ObjectMapper mapper = createXmlMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    public Map<PitfallId, List<Pitfall>> getPitfalls() {
        return pitfalls;
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public int getNumPitfalls() {
        return numPitfalls;
    }

    public Map<WarningType, List<Warning>> getWarnings() {
        return warnings;
    }

    // /**
    // * TODO FIXME This is not currently functional.
    // *
    // * @return
    // */
    // public List<String> getImportsFailing() {
    // return importsFailing;
    // }

    // public List<ObjectProperty> getSymmetricOrTransitiveSuggestion() {
    // return symmetricOrTransitiveSuggestion;
    // }

    public int getNumClasses() {
        return numClasses;
    }

    public int getNumProperties() {
        return numProperties;
    }

    public String getXmlOutput() {
        return xmlOutput;
    }

    public Model getOutputModel() {
        return outputModel;
    }

    public String getContents(String outputFormat) throws IOException {
        if (outputFormat.equals("XML")) {
            return xmlOutput;
        }
        OutputStream os = new ByteArrayOutputStream();
        outputModel.write(os, outputFormat);
        return os.toString();
    }
}
