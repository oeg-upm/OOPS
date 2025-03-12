/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.List;
import org.apache.jena.ontology.OntResource;

/**
 * The result of a linting rule check.
 */
public interface Pitfall {

    public static String HTML_LINK_FMT = "<a href=\"%s\" target=\"_blank\">%s</a>";

    PitfallInfo getInfo();

    List<OntResource> getResources();

    String toString();

    String toHtml();

    default void toRdf(final RdfOutputContext context) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public static String toHtmlLink(final String url, final String text) {
        return String.format(HTML_LINK_FMT, url, text);
    }

    public static String toHtmlLink(final String url) {
        return toHtmlLink(url, url);
    }

    public static String toHtmlLink(final OntResource res) {
        return toHtmlLink(res.getURI(), res.getLocalName());
    }
}
