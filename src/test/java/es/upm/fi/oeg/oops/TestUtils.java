/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestUtils {

    @Test
    public void testEscapeForHtml() {
        assertEquals("a<br>\nb", Utils.escapeForHtml("a\nb"));
    }
}
