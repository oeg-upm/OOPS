/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "OOPS!", mixinStandardHelpOptions = true, description = "Lints RDF/OWL ontologies")
public class Cli implements Callable<Integer> {

    private final Logger logger = LoggerFactory.getLogger(Cli.class);

    // @Parameters(index = "0", description = "ontology file")
    // private File ontology;

    // @Parameters(index = "0", description = "ontology file")
    // private URL namespace;

    @Option(names = { "--input-file",
            "--ontology-file" }, description = "Path to RDF/OWL ontology file on the local file-system")
    private Path ontology;

    @Option(names = { "--iri", "--ontology-url" }, description = "IRI/URL to RDF/OWL ontology (HTTP)")
    private URL namespace;

    // @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    // private String algorithm = "SHA-256";

    public Logger getLogger() {
        return logger;
    }

    @Override
    public Integer call() throws Exception {

        final SrcSpec srcSpec;
        final SrcType srcType;
        if (ontology != null) {
            srcType = SrcType.RDF_CODE;
            final String ontologyContent = Files.readString(ontology, StandardCharsets.UTF_8);
            srcSpec = new SrcSpec(srcType, null, ontologyContent, null);
        } else if (namespace != null) {
            srcType = SrcType.URI;
            srcSpec = new SrcSpec(srcType, namespace.toExternalForm(), null, null);
        } else {
            throw new IllegalStateException();
        }

        final List<Checker> checkers = CheckersCatalogue.getAllCheckers();
        final SrcModel srcModel = ModelLoader.load(srcSpec);
        final Linter executor = new Linter();
        logger.info("Checkers supplied: " + checkers.size());
        final Report report = executor.partialExecution(srcModel, Collections.emptyList(), checkers);
        // Files.write(Path.of("tstOut.owl.xml"), report.getXmlOutput().getBytes());
        report.getOutputModel().write(Files.newOutputStream(Path.of("tstOut.model.ttl")), "Turtle");

        return 0;
    }

    public static void main(final String... args) {
        final Cli cli = new Cli();
        final CommandLine cliWrapper = new CommandLine(cli);
        int exitCode;
        try {
            exitCode = cliWrapper.execute(args);
        } catch (final Throwable exc) {
            cli.getLogger().error("Generic issue detected", exc);
            exitCode = 1;
        }
        System.exit(exitCode);
    }
}
