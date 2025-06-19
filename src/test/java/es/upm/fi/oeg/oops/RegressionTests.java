/*
 * SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class RegressionTests {

    static final String DATA_DIR = "src/test/resources/data/";
    static final String INPUT_DIR = DATA_DIR + "input/";
    static final String OUTPUT_DIR = DATA_DIR + "output/";

    Model applyConstructQuery(Model model, String query) {
        return QueryExecutionFactory.create(query, model).execConstruct();
    }

    Model normalize(Model model) {
        return normalizeConstructQuery(model);
    }

    Model normalizeConstructQuery(Model model) {
        String query = "PREFIX oops: <http://oops.linkeddata.es/def#>\n" + "CONSTRUCT {\n" + "  ?s2 ?p ?o2 .\n" + "}\n"
                + "WHERE {\n" + "  ?s ?p ?o .\n" + "\n"
                + "  # Replace ?s with a blank node if it starts with the specified prefix\n" + "  BIND(\n"
                + "    IF(\n" + "      STRSTARTS(STR(?s), \"http://oops.linkeddata.es/data/\"),\n" + "      BNODE(),\n"
                + "      ?s\n" + "    ) AS ?s2\n" + "  )\n" + "\n"
                + "  # Replace ?o with a blank node if it is an IRI or literal with a UUID-like pattern\n" + "  BIND(\n"
                + "    IF(\n" + "      (STRSTARTS(STR(?o), \"http://oops.linkeddata.es/data/\") &&\n"
                + "       REGEX(STR(?o), \"[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\")),\n"
                + "      BNODE(),\n" + "      ?o\n" + "    ) AS ?o2\n" + "  )\n" + "}";
        return applyConstructQuery(model, query);
    }

    Model normalizeURN(Model model) {
        Model normalizedModel = ModelFactory.createDefaultModel();
        Map<String, Resource> bnodeCache = new HashMap<>();

        // Helper to map URI to a deterministic BlankNode
        Function<String, Resource> toBNode = uri -> bnodeCache.computeIfAbsent(uri,
                u -> normalizedModel.createResource(AnonId.create(DigestUtils.sha256Hex(u))));

        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt = it.next();
            Resource s = stmt.getSubject();
            RDFNode o = stmt.getObject();
            Property p = stmt.getPredicate();

            Resource newSubject = (s.isURIResource() && s.getURI().startsWith("http://oops.linkeddata.es/data/"))
                    ? toBNode.apply(s.getURI())
                    : s;

            RDFNode newObject;
            if (o.isURIResource() && o.asResource().getURI().startsWith("http://oops.linkeddata.es/data/")) {
                newObject = toBNode.apply(o.asResource().getURI());
            } else {
                newObject = o;
            }

            normalizedModel.add(newSubject, p, newObject);
        }

        return normalizedModel;
    }

    Model skolemize(Model model) {
        String query = "PREFIX oops: <http://oops.linkeddata.es/def#>\n" + "CONSTRUCT {\n" + "  ?s2 ?p ?o2 .\n" + "}\n"
                + "WHERE {\n" + "  ?s ?p ?o .\n" + "  BIND(\n" + "    IF(\n"
                + "      STRSTARTS(STR(?s), \"http://oops.linkeddata.es/data/\"),\n"
                + "      IRI(CONCAT(\"urn:skolem:\", ENCODE_FOR_URI(STR(?s)))),\n" + "      ?s\n" + "    ) AS ?s2\n"
                + "  )\n" + "  BIND(\n" + "    IF(\n"
                + "      isIRI(?o) && STRSTARTS(STR(?o), \"http://oops.linkeddata.es/data/\"),\n"
                + "      IRI(CONCAT(\"urn:skolem:\", ENCODE_FOR_URI(STR(?o)))),\n" + "      ?o\n" + "    ) AS ?o2\n"
                + "  )\n" + "}";
        return applyConstructQuery(model, query);
    }

    private void printModelDifferences(Model expected, Model actual) {
        try (OutputStream expOut = new FileOutputStream("expected.ttl");
                OutputStream actOut = new FileOutputStream("actual.ttl")) {
            RDFDataMgr.write(expOut, expected, Lang.TURTLE);
            RDFDataMgr.write(actOut, actual, Lang.TURTLE);
        } catch (IOException e) {
            System.err.println("Error writing model differences: " + e.getMessage());
        }
    }

    public static void debugModelDiffIgnoringSubject(Model m1, Model m2) {
        Set<String> poSet1 = extractPredicateObjectPairs(m1);
        Set<String> poSet2 = extractPredicateObjectPairs(m2);

        Set<String> onlyInM1 = new HashSet<>(poSet1);
        onlyInM1.removeAll(poSet2);

        Set<String> onlyInM2 = new HashSet<>(poSet2);
        onlyInM2.removeAll(poSet1);

        System.out.println("Predicate-Object pairs only in first model:");
        onlyInM1.forEach(pair -> System.out.println("  " + pair));

        System.out.println("Predicate-Object pairs only in second model:");
        onlyInM2.forEach(pair -> System.out.println("  " + pair));
    }

    private static Set<String> extractPredicateObjectPairs(Model model) {
        Set<String> poPairs = new HashSet<>();
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            poPairs.add(stmt.getPredicate().toString() + " " + stmt.getObject().toString());
        }
        return poPairs;
    }

    public static void debugModelDiff(Model m1, Model m2) {
        Model onlyInM1 = ModelFactory.createDefaultModel().add(m1).remove(m2);
        Model onlyInM2 = ModelFactory.createDefaultModel().add(m2).remove(m1);

        System.out.println("Triples only in first model:");
        for (StmtIterator it = onlyInM1.listStatements(); it.hasNext();) {
            Statement stmt = it.next();
            System.out.println("  " + stmt);
        }

        System.out.println("Triples only in second model:");
        for (StmtIterator it = onlyInM2.listStatements(); it.hasNext();) {
            Statement stmt = it.next();
            System.out.println("  " + stmt);
        }
    }

    private void runTest(String nameTest) {
        try {
            final Path ontologyPath = Path.of(INPUT_DIR + nameTest + ".owl");
            final String ontologyContent = Files.readString(ontologyPath, StandardCharsets.UTF_8);
            final SrcType srcType = SrcType.RDF_CODE;
            final SrcSpec srcSpec = new SrcSpec(srcType, null, ontologyContent, null);

            final List<Checker> checkers = CheckersCatalogue.getAllCheckers();
            final SrcModel srcModel = ModelLoader.load(srcSpec);
            final Linter executor = new Linter();
            final Report report = executor.partialExecution(srcModel, Collections.emptyList(), checkers);
            final Model expectedBlank = RDFDataMgr.loadModel(OUTPUT_DIR + nameTest + ".ttl");
            final Model actualBlank = report.getOutputModel();
            final Model expected = normalize(expectedBlank);
            final Model actual = normalize(actualBlank);

            boolean isIsomorphic = expected.isIsomorphicWith(actual);

            if (!isIsomorphic) {
                printModelDifferences(expected, actual);
                System.out.println("Expected #triples: " + expected.size());
                System.out.println("Actual #triples: " + actual.size());
                debugModelDiffIgnoringSubject(expected, actual);
            }

            Assertions.assertTrue(isIsomorphic, "The generated output differs from the expected.");
        } catch (IOException | OWLOntologyCreationException e) {
            Assertions.fail("An exception occurred with test " + nameTest + ": " + e.getMessage(), e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "P02", "P03", "P04", "P05", "P06", "P07", "P08", "P10", "P10-A", "P10-B", "P10-C", "P11",
            "P12", "P13", "P19", "P20", "P21", "P22M1", "P22M2", "P22M3", "P22M4", "P24", "P25", "P26", "P27", "P28",
            "P29", "P30", "P31", "P32", "P33", /*"P34",*/ "P35", "P36", "P38", "P39", "P40", "P41" })
    public void testWithParameters(String nameTest) {
        String selectedTest = System.getProperty("testParameter");

        // Skip test if it doesn't match the selected parameter
        if (selectedTest != null && !selectedTest.isEmpty() && !selectedTest.trim().equals(nameTest)) {
            System.out.println("Skipping test with parameter: " + nameTest);
            return;
        }

        System.out.println("Running test with parameter: " + nameTest);
        runTest(nameTest);
    }
}
