/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Set;
import org.apache.jena.ontology.OntResource;

/**
 * If any exceptional thing is detected during a check, which does not result in a pitfall being detected. This might
 * happen, if the preconditions to check for a pitfall are not given, for example: We want to analyze the download URL,
 * but the ontology is given as content text, vs through its URL/IRI.
 */
public interface Warning {

    WarningType getType();

    CheckerInfo getCheckerInfo();

    Set<OntResource> getScope();

    String toString();
}
