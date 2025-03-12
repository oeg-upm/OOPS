/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.text.StringEscapeUtils;

public class Utils {

    public static String urlEncode(final String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException exc) {
            throw new IllegalStateException();
        }
    }

    public static String escapeForHtml(final String input) {
        return StringEscapeUtils.escapeHtml4(input).replace("\n", "<br>\n");
    }
}
