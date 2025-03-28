/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Collections;
import java.util.Set;
import org.apache.jena.ontology.OntResource;

/**
 * The result of a linting rule check.
 */
public class WarningImpl implements Warning {

    private final WarningType type;
    private final CheckerInfo checkerInfo;
    private final Set<OntResource> scope;
    private final String msg;

    public WarningImpl(final WarningType type, final CheckerInfo checkerInfo, final Set<OntResource> scope,
            final String msg) {
        this.type = type;
        this.checkerInfo = checkerInfo;
        this.scope = scope;
        this.msg = msg;
    }

    @Override
    public WarningType getType() {
        return type;
    }

    @Override
    public CheckerInfo getCheckerInfo() {
        return checkerInfo;
    }

    @Override
    public Set<OntResource> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    @Override
    public String toString() {
        String str = String.format("P%02d: related cases: %d", checkerInfo.getId().getNumeral(), scope.size());
        if (msg != null && !msg.isEmpty()) {
            str += " - " + msg;
        }
        return str;
    }
}
