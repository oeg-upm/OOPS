/*
 * SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import java.io.File;
import java.io.IOException;

public class RunSettings {

    private final String url;
    private final String content;
    private final String pitfalls;
    private final String outputFormat;

    @JsonCreator
    public RunSettings(@JsonProperty("OntologyUrl") final String url,
            @JsonProperty("OntologyContent") final String content, @JsonProperty("Pitfalls") final String pitfalls,
            @JsonProperty("OutputFormat") final String outputFormat) {
        this.url = url;
        this.content = content;
        this.pitfalls = pitfalls;
        this.outputFormat = outputFormat;
    }

    public String getUrl() {
        return url;
    }

    // public void setUrl(final String url) {
    // this.url = url;
    // }

    public String getContent() {
        return content;
    }

    // public void setContent(final String content) {
    // this.content = content;
    // }

    public String getPitfalls() {
        return pitfalls;
    }

    // public void setPitfalls(final String pitfalls) {
    // this.pitfalls = pitfalls;
    // }

    public String getOutputFormat() {
        return outputFormat;
    }

    // public void setOutputFormat(final String outputFormat) {
    // this.outputFormat = outputFormat;
    // }

    private static RunSettings from(final ObjectMapper mapper, final String content) throws IOException {
        // mapper.findAndRegisterModules();
        return mapper.readValue(content, RunSettings.class);
    }

    public static RunSettings from(final ObjectMapper mapper, final File file) throws IOException {
        // mapper.findAndRegisterModules();
        return mapper.readValue(file, RunSettings.class);
    }

    public static RunSettings fromJson(final String content) throws IOException {
        return from(new ObjectMapper(), content);
    }

    public static RunSettings fromJson(final File file) throws IOException {
        return from(new ObjectMapper(), file);
    }

    public String saveToJson() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public void saveToJson(final File file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    public static RunSettings fromXml(final File file) throws IOException {
        return from(new XmlMapper(), file);
    }

    public static RunSettings fromXml(final String content) throws IOException {
        return from(new XmlMapper(), content);
    }

    public String saveToXml() throws IOException {
        final XmlMapper mapper = new XmlMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public void saveToXml(final File file) throws IOException {
        final XmlMapper mapper = new XmlMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    public static RunSettings fromYaml(final File file) throws IOException {
        return from(new ObjectMapper(new YAMLFactory()), file);
    }

    public String saveToYaml() throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public void saveToYaml(final File file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }
}
