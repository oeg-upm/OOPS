/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Linter {

    // private static String ERROR_MSG = ErrorOuts.getErrorMessageWrongExec();

    public static final String NS_OOPS_DEF = "http://oops.linkeddata.es/def#";
    public static final String NS_OOPS_DATA = "http://oops.linkeddata.es/data/";

    private final Logger logger = LoggerFactory.getLogger(Linter.class);

    // private final OntModel model;
    // private final OWLOntology modelOWL;
    // private final SrcType srcType;
    // private final String text;

    // private final List<Throwable> loadingExceptions;

    // public ModelOWLExt(final String text, final SrcType srcType) throws IOException, OWLOntologyCreationException {

    // OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

    // // create the model with OWL api
    // final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    // OWLOntology modelOWL = manager.createOntology();
    // this.loadingExceptions = new ArrayList<>();

    // this.srcType = srcType;
    // this.text = text;
    // try {
    // switch (srcType) {
    // case RDF_CODE:
    // InputStream is = new ByteArrayInputStream(text.getBytes());
    // model.read(is, null);

    // // cargar model en owl api desde codigo
    // InputStream is2 = new ByteArrayInputStream(text.getBytes());
    // modelOWL = manager.loadOntologyFromOntologyDocument(is2);
    // break;
    // case URI:
    // model.read(text, null);

    // // cargar model en owl api desde uri
    // // Let's load an ontology from the web
    // IRI iri = IRI.create(text);
    // modelOWL = manager.loadOntologyFromOntologyDocument(iri);
    // break;
    // default:
    // throw new UnsupportedOperationException("Not yet implemented");
    // }
    // } catch (org.apache.jena.shared.JenaException exc) {
    // if (exc.getMessage().contains("UTF-8 sequence")) {
    // // model = ModelFactory.createOntologyModel(spec, null);
    // model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    // try {
    // final byte[] bytes = text.getBytes("UTF-8");
    // final InputStream bytesStream = new ByteArrayInputStream(bytes);
    // model.read(bytesStream, null);
    // } catch (Exception exc2) {
    // loadingExceptions.add(exc);
    // loadingExceptions.add(exc2);
    // }
    // } else if ((exc instanceof org.apache.jena.shared.DoesNotExistException)
    // || (exc.getMessage().contains("Connection timed out: connect"))) {
    // // this.timeOut = true;
    // loadingExceptions.add(exc);
    // } else {
    // loadingExceptions.add(exc);
    // // System.out.println(a.getMessage());
    // exc.printStackTrace();
    // }
    // }
    // // Deprecates deprecates = new Deprecates(model);

    // this.model = model;
    // this.modelOWL = modelOWL;
    // }

    public static void main(final String[] args) {
        new Linter().run();
    }

    public void run() {
        final OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        final OntModel outputModel = ModelFactory.createOntologyModel(spec);

        outputModel.setNsPrefix("oops", NS_OOPS_DEF);
        final OntClass voafVocab = outputModel.createClass(NS_OOPS_DEF + "pitfall");

        final Resource res = outputModel.createResource(NS_OOPS_DATA + "1234567890");
        final Resource responseType = outputModel.createResource(NS_OOPS_DEF + "response");
        outputModel.add(res, RDF.type, responseType);
        final Resource pitfallType = outputModel.createResource(NS_OOPS_DEF + "pitfall");
        final Property pitfallProp = outputModel.createProperty(NS_OOPS_DEF + "hasPitfall");
        final Property titleProp = outputModel.createProperty(NS_OOPS_DEF + "hasCode");

        logger.debug("The input RDF model:");
        final OutputStream obj2 = new ByteArrayOutputStream();
        outputModel.write(obj2, "RDF/XML");
        logger.debug("------------------------------------------------------------------------------");
        logger.debug(obj2.toString());
        logger.debug("------------------------------------------------------------------------------");
    }

    public Report partialExecution(final SrcModel srcModel, final List<Integer> checkersToExecute,
            final List<Checker> checkers) {

        final Map<PitfallId, List<Pitfall>> pitfalls = new HashMap<>();
        final Map<PitfallId, Integer> numCases = new HashMap<>();

        final Map<WarningType, List<Warning>> warnings = new HashMap<>();
        for (final WarningType wType : WarningType.values()) {
            warnings.put(wType, new ArrayList<>());
        }
        final List<Throwable> exceptions = new ArrayList<>();

        final int numClasses = srcModel.getModel().listClasses().toList().size();
        final int numProperties = srcModel.getModel().listAllOntProperties().toList().size();

        final Instant timeStart = Instant.now();

        Model outputModel = createModel();
        outputModel.getNsPrefixMap().putAll(srcModel.getModel().getNsPrefixMap());
        UUID uuid = UUID.randomUUID();
        final Resource response = outputModel.createResource(NS_OOPS_DATA + uuid.toString());
        final Resource responseType = outputModel.createResource(NS_OOPS_DEF + "response");
        outputModel.add(response, RDF.type, responseType);
        final Resource pitfallTypeType = outputModel.createResource(NS_OOPS_DEF + "pitfallType");
        final Property pitfallTypeProp = outputModel.createProperty(NS_OOPS_DEF + "hasPitfallType");
        final Resource pitfallType = outputModel.createResource(NS_OOPS_DEF + "pitfall");
        final Property pitfallProp = outputModel.createProperty(NS_OOPS_DEF + "hasPitfall");
        // Property titleProp = outputModel.createProperty(NS_OOPS_DEF + "hasTitle");
        final Property titleProp = outputModel.createProperty(NS_OOPS_DEF + "hasCode");
        final Property nameProp = outputModel.createProperty(NS_OOPS_DEF + "hasName");
        final Property descriptionProp = outputModel.createProperty(NS_OOPS_DEF + "hasDescription");
        // Property valueProp = outputModel.createProperty(NS_OOPS_DEF + "hasValue");
        final Property valueProp = outputModel.createProperty(NS_OOPS_DEF + "hasAffectedElement");
        final Resource suggestType = outputModel.createResource(NS_OOPS_DEF + "suggestion");
        final Property suggestProp = outputModel.createProperty(NS_OOPS_DEF + "hasSuggestion");
        final Resource warningType = outputModel.createResource(NS_OOPS_DEF + "warning");
        final Property warningProp = outputModel.createProperty(NS_OOPS_DEF + "hasWarning");
        final Property importanceProp = outputModel.createProperty(NS_OOPS_DEF + "hasImportanceLevel");
        final Property casesProp = outputModel.createProperty(NS_OOPS_DEF + "hasNumberAffectedElements");
        // Property equivalentProp = outputModel.createProperty(NS_OOPS_DEF + "hasEquivalentProperty");
        final Property equivalentClass = outputModel.createProperty(NS_OOPS_DEF + "hasEquivalentClass");
        final Resource equivalentClassType = outputModel.createResource(NS_OOPS_DEF + "equivalentClass");
        final Property wrongEquivalentClass = outputModel.createProperty(NS_OOPS_DEF + "hasWrongEquivalentClass");
        final Resource wrongEquivalentClassType = outputModel.createResource(NS_OOPS_DEF + "wrongEquivalentClass");
        final Property sameLabel = outputModel.createProperty(NS_OOPS_DEF + "hasSameLabel");
        final Resource sameLabelType = outputModel.createResource(NS_OOPS_DEF + "sameLabel");
        final Property equivalentProp = outputModel.createProperty(NS_OOPS_DEF + "mightBeEquivalentProperty");
        final Resource equivalentPropType = outputModel.createResource(NS_OOPS_DEF + "equivalentProperty");
        final Property equivalentAttrProp = outputModel.createProperty(NS_OOPS_DEF + "hasEquivalentAttribute");
        final Resource equivalentAttrType = outputModel.createResource(NS_OOPS_DEF + "equivalentAttribute");
        final Property inverseProp = outputModel.createProperty(NS_OOPS_DEF + "mightBeInverseRelationship");
        final Resource inverseType = outputModel.createResource(NS_OOPS_DEF + "inverseProperty");
        // Property notInverse = outputModel.createProperty(NS_OOPS_DEF + "notInverseOf");
        final Property notInverse = outputModel.createProperty(NS_OOPS_DEF + "mightNotBeInverseOf");
        final Resource notInverseType = outputModel.createResource(NS_OOPS_DEF + "notInverseRelationship");
        final Property noSuggestionProp = outputModel.createProperty(NS_OOPS_DEF + "noSuggestion");
        final Resource noSuggestionType = outputModel.createResource(NS_OOPS_DEF + "noSuggestionProperty");

        {
            for (final Checker checker : checkers) {
                final CheckingContext context;
                try {
                    if (!shouldExecute(checker, checkersToExecute)) {
                        continue;
                    }
                    context = new CheckingContext(srcModel, outputModel);
                    logger.info("Checking {} ...", checker.getInfo().getId());
                    checker.check(context);
                    logger.info("Checking {} done.", checker.getInfo().getId());
                } catch (final Exception exc) {
                    logger.error("General/unknown error occurred during checking", exc);
                    exceptions.add(exc);
                    continue;
                }
                final Map<PitfallId, List<Pitfall>> results = context.getResults();
                logger.info("#Pitfall Ids triggered: {}", results.keySet().size());
                for (final Map.Entry<PitfallId, List<Pitfall>> pfEntry : results.entrySet()) {
                    final PitfallId pId = pfEntry.getKey();
                    final List<Pitfall> pfs = pfEntry.getValue();
                    final PitfallInfo pInfo = pfs.iterator().next().getInfo();
                    pitfalls.put(pId, pfs);
                    numCases.put(pId, pfs.size());

                    // escribir en el modelo
                    final Resource pitfallSubj = outputModel
                            .createResource(NS_OOPS_DATA + UUID.randomUUID().toString());
                    outputModel.add(pitfallSubj, RDF.type, pitfallType);
                    response.addProperty(pitfallProp, pitfallSubj);
                    final Literal title = outputModel.createTypedLiteral(pId.toString() + " - " + pInfo.getTitle(),
                            XSDDatatype.XSDstring);
                    pitfallSubj.addProperty(titleProp, title);
                    final Literal name = outputModel.createTypedLiteral(pInfo.getTitle(), XSDDatatype.XSDstring);
                    pitfallSubj.addProperty(nameProp, name);
                    final Literal description = outputModel.createTypedLiteral(pInfo.getExplanation(),
                            XSDDatatype.XSDstring);
                    pitfallSubj.addProperty(descriptionProp, description);
                    final Literal cases = outputModel.createTypedLiteral(pfs.size(), XSDDatatype.XSDinteger);
                    pitfallSubj.addProperty(casesProp, cases);
                    final Literal importance = outputModel.createTypedLiteral(pInfo.getImportance(),
                            XSDDatatype.XSDstring);
                    pitfallSubj.addProperty(importanceProp, importance);

                    for (final Pitfall result : pfs) {
                        for (final Resource ontRes : result.getResources()) {
                            final String resUri = ontRes.getURI();
                            final Literal value = outputModel.createTypedLiteral(resUri, XSDDatatype.XSDanyURI);
                            pitfallSubj.addProperty(valueProp, value);
                        }
                    }
                }
                for (final Warning warning : context.getWarnings()) {
                    warnings.get(warning.getType()).add(warning);
                }
            }

            // csvWriter.write(String.valueOf(numPitfalls) + ";\n");

            // if (symmetricOrTransitiveSuggestion.size() > 0) {
            // final Resource suggestionSubj = outputModel.createResource(NS_OOPS_DATA + UUID.randomUUID().toString());
            // outputModel.add(suggestionSubj, RDF.type, suggestType);
            // response.addProperty(suggestProp, suggestionSubj);
            // final Literal title = outputModel.createTypedLiteral(
            // "SUGGESTION: symmetric or transitive object properties.", XSDDatatype.XSDstring);
            // suggestionSubj.addProperty(titleProp, title);
            // final Literal description = outputModel.createTypedLiteral(
            // "The domain and range axioms are equal for each of the following object properties. Could they be
            // symmetric or transitive?",
            // XSDDatatype.XSDstring);
            // suggestionSubj.addProperty(descriptionProp, description);
            // final Literal cases = outputModel.createTypedLiteral(symmetricOrTransitiveSuggestion.size(),
            // XSDDatatype.XSDinteger);
            // suggestionSubj.addProperty(casesProp, cases);
            // // xmlOutput.append("<oops:Suggestion>\n");
            // // xmlOutput.append("<oops:Name>SUGGESTION: symmetric or transitive object properties.</oops:Name>\n");
            // // xmlOutput.append(
            // // "<oops:Description>The domain and range axioms are equal for each of the following object properties.
            // // Could they be symmetric or transitive?</oops:Description>\n");
            // // xmlOutput.append("<oops:NumberAffectedElements>").append(symmetricOrTransitiveSuggestion.size())
            // // .append("</oops:NumberAffectedElements>\n");
            // // xmlOutput.append("<oops:Affects>\n");

            // for (final ObjectProperty result : symmetricOrTransitiveSuggestion) {
            // Literal value = outputModel.createTypedLiteral(result.getURI(), XSDDatatype.XSDanyURI);
            // suggestionSubj.addProperty(valueProp, value);
            // // xmlOutput.append("<oops:AffectedElement>").append(result.getURI())
            // // .append("</oops:AffectedElement>\n");
            // }
            // // xmlOutput.append("</oops:Affects>\n");
            // // xmlOutput.append("</oops:Suggestion>\n");
            // }

            // for (final Map.Entry<WarningType, List<Warning>> stWarningsEntry : warnings.entrySet()) {
            //     final WarningType wType = stWarningsEntry.getKey();
            //     final List<Warning> stWarnings = stWarningsEntry.getValue();
            //     logger.debug("  warningtype: {}", wType);
            //     logger.debug("  stWarnings.size(): {}", stWarnings.size());
            //     if (stWarnings.size() > 0) {
            //         // xmlOutput.append("<oops:Warning>\n");
            //         switch (wType) {
            //             case CLASS :
            //                 // xmlOutput.append(
            //                 // "<oops:Name>WARNING: the following classes do not have rdf:type owl:Class or
            //                 // equivalent.</oops:Name>\n");
            //                 break;
            //             case PROPERTY :
            //                 // xmlOutput.append(
            //                 // "<oops:Name>WARNING: the following properties do not have rdf:type Property.</oops:Name>\n");
            //                 break;
            //             case ONTOLOGY :
            //                 // xmlOutput.append("<oops:Name>WARNING: There is an issue with the ontology</oops:Name>\n");
            //                 break;
            //             default :
            //                 throw new UnsupportedOperationException("Not Implemented");
            //         }
            //         // xmlOutput.append("<oops:NumberAffectedElements>").append(stWarnings.size())
            //         // .append("</oops:NumberAffectedElements>\n");
            //         // xmlOutput.append("<oops:Affects>\n");
            //         final Resource warningSubj = outputModel
            //                 .createResource(NS_OOPS_DATA + UUID.randomUUID().toString());
            //         outputModel.add(warningSubj, RDF.type, warningType);
            //         response.addProperty(warningProp, warningSubj);
            //         final Literal title = outputModel.createTypedLiteral(
            //                 "WARNING: symmetric or transitive object properties.", XSDDatatype.XSDstring);
            //         warningSubj.addProperty(titleProp, title);
            //         final Literal description = outputModel.createTypedLiteral(
            //                 "The domain and range axioms are equal for each of the following object properties. Could they be symmetric or transitive?",
            //                 XSDDatatype.XSDstring);
            //         warningSubj.addProperty(descriptionProp, description);
            //         final Literal cases = outputModel.createTypedLiteral(stWarnings.size(), XSDDatatype.XSDinteger);
            //         warningSubj.addProperty(casesProp, cases);

            //         for (final Warning warning : stWarnings) {
            //             for (final OntResource scopeItem : warning.getScope()) {
            //                 // xmlOutput.append("<oops:AffectedElement>").append(scopeItem.getURI())
            //                 // .append("</oops:AffectedElement>\n");
            //                 final Literal value = outputModel.createTypedLiteral(scopeItem.getURI(),
            //                         XSDDatatype.XSDanyURI);
            //                 warningSubj.addProperty(valueProp, value);
            //             }
            //         }
            //         // xmlOutput.append("</oops:Affects>\n");
            //         // xmlOutput.append("</oops:Warning>\n");
            //     }
            // }

            OutputStream obj = new ByteArrayOutputStream();
            outputModel.write(obj, "RDF/XML");
            logger.debug("The output model (in RDF/XML:");
            logger.debug("------------------------------------------------------------------------------");
            logger.debug(obj.toString());
            logger.debug("------------------------------------------------------------------------------");

            obj = new ByteArrayOutputStream();
            outputModel.write(obj, "Turtle");
            logger.debug("The output model (in Turtle):");
            logger.debug("------------------------------------------------------------------------------");
            logger.debug(obj.toString());
            logger.debug("------------------------------------------------------------------------------");

            // output = obj.toString();
            // xmlOutput.append("</oops:OOPSResponse>\n");
            // } catch (final Exception exc) {
            // // logger.error("ha habido una excepci�n durante la ejecución de las pitfalls");
            // exc.printStackTrace();
            // exceptions.add(exc);
            //// xmlOutput.setLength(0);
            //// xmlOutput.append(ERROR_MSG);
        }

        final Instant timeEnd = Instant.now();
        final Duration executionTime = Duration.between(timeStart, timeEnd);
        // final String xmlOutputStr = xmlOutput.toString();
        final String xmlOutputStr = null;
        return new Report(numClasses, numProperties, pitfalls, exceptions, executionTime, warnings,
                /* symmetricOrTransitiveSuggestion, importsFailing, */ xmlOutputStr, outputModel);
    }

    private boolean shouldExecute(Checker checker, List<Integer> checkersToExecute) {
        // an empty or nonexistent checkersToExecute list implies that all checkers are executed
        return checkersToExecute == null || checkersToExecute.isEmpty()
                || checkersToExecute.contains(checker.getInfo().getId().getNumeral());
    }

    private Model createModel() {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("oops", NS_OOPS_DEF);

        return model;
    }

    private OntModel createOntModel() {
        OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
        OntModel model = ModelFactory.createOntologyModel(s);

        model.setNsPrefix("oops", NS_OOPS_DEF);

        return model;
    }
}
