/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.ontology.OntResource;

/**
 * The result of a linting rule check.
 */
public class PitfallImpl implements Pitfall {

    private final PitfallInfo info;
    private final List<OntResource> resources;

    public PitfallImpl(final PitfallInfo info, final List<OntResource> resources) {
        this.info = info;
        this.resources = resources;
    }

    public PitfallImpl(final PitfallInfo info, final Set<OntResource> resources) {
        this.info = info;
        this.resources = new ArrayList<>(resources);
    }

    // public PitfallImpl(final RuleInfo ruleInfo, final OntResource resource) {
    // this(ruleInfo, Collections.singleton(resource));
    // }

    @Override
    public PitfallInfo getInfo() {
        return info;
    }

    @Override
    public List<OntResource> getResources() {
        return resources;
    }

    @Override
    public String toString() {

        final StringBuffer out = new StringBuffer();

        final PitfallInfo locInfo = getInfo();

        out.append("### ").append(locInfo.getId()).append(": ").append(locInfo.getTitle()).append("\n\n");
        out.append("Severity: ").append(locInfo.getImportance()).append("\n");
        out.append("Categories: ")
                .append(locInfo.getCategory().stream().map(PitfallCategory::toString).collect(Collectors.joining(", ")))
                .append("\n\n");
        out.append(locInfo.getExplanation()).append("\n\n");

        for (final OntResource res : this.getResources()) {
            out.append("- ").append(res.getURI()).append("\n");
        }

        final String locExtraOutput = locInfo.getExtraOutput();
        if (locExtraOutput != null) {
            out.append("\n").append(locExtraOutput);
        }

        return out.toString();
    }

    @Override
    public String toHtml() {
        // TODO This should probably be more elaborate.
        return toString().replace("\n", "<br>\n").replaceAll("https?://[^> \t\r\n\\]\\)}]+", Pitfall.toHtmlLink("\0"));
    }

    @Override
    public void toRdf(final RdfOutputContext context) {
        // TODO Implement this
        throw new UnsupportedOperationException("Needs to be implemented still");
    }
}
