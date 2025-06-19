/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.checkers;

import es.upm.fi.oeg.oops.Arity;
import es.upm.fi.oeg.oops.Checker;
import es.upm.fi.oeg.oops.CheckerInfo;
import es.upm.fi.oeg.oops.CheckingContext;
import es.upm.fi.oeg.oops.ConstrainsClasses;
import es.upm.fi.oeg.oops.ExtIterIterable;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.Linter;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SynsetHelp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P32 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(32, null),
            Set.of(new PitfallCategoryId('N', 7)), Importance.MINOR, "Several classes with the same label",
            "Two or more classes have the same content for natural language annotations for naming, "
                    + "for example the rdfs:label annotation. "
                    + "This pitfall involves lack of accuracy when defining terms.",
            RuleScope.CLASS, Arity.TWO_PLUS,
            "These classes contain the same label. "
                    + "Maybe they should be replaced by one class with several labels, "
                    + "or they might be equivalent classes, "
                    + "and thus should be marked as such with `owl:equivalentClass`.",
            AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    private static final List<String> LANGS = List.of("EN", "ES", "IT", "DE", "FR", "PT");

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Map<String, Set<OntClass>> preResults = new HashMap<>();
        for (final OntClass class2 : new ExtIterIterable<>(model.listClasses())) {
            for (final OntClass classFace2 : new ExtIterIterable<>(model.listClasses())) {
                final String clsUri = class2.getURI();
                final String clsFaceUri = classFace2.getURI();

                // If both classes have URI
                // If both classes are not the same, it means has not the same URI
                if (clsUri != null && clsFaceUri != null && !clsUri.equals(clsFaceUri)) {
                    final String className = class2.getLocalName(), classFaceName = classFace2.getLocalName();
                    if (this.classesWithSameLabelLanguages(context.getDictionary(), class2, classFace2)) {
                        final boolean equivalentOneSide = ConstrainsClasses.areEquivalentClasses(class2, classFace2);
                        final boolean equivalentOtherSide = ConstrainsClasses.areEquivalentClasses(classFace2, class2);
                        if (!equivalentOneSide && !equivalentOtherSide) {
                            // If the label class has not considered yet,
                            // because the label_class_face was seen before
                            P31.buildListResults(preResults, className, classFaceName, class2, classFace2);
                        }
                    }
                }
            }
        }

        addToOutput(context, preResults);
    }

    private void addToOutput(CheckingContext context, Map<String, Set<OntClass>> preResults) {
        Model outputModel = context.getOutputModel();
        final Resource sameLabelType = outputModel.createResource(Linter.NS_OOPS_DEF + "sameLabel");

        P31.addToOutput(PITFALL_INFO, context, sameLabelType, preResults);
    }

    /**
     * Returns whether two ontology classes have the same content in the rdf:Label annotation or not whit the specific
     * language. The same content means if the all compound words of the rdf:Label are equals ignoring upper and lower
     * case and other signs.
     *
     * @param cls
     *     ontology class where search for.
     * @param clsFace
     *     ontology class where search.
     * @param lang
     *     the language which we want to know whether the content in the rdf:Label annotation are the same.
     *
     * @return <tt>true</tt> if and only if these classes have the same content in the rdf:Label annotation whit the
     * specific language, <tt>false</tt> in other fact.
     */
    private boolean classesWithSameLabel(final SynsetHelp dictionary, final OntClass cls, final OntClass clsFace,
            final String lang) {
        boolean sameLabel = false;
        for (final RDFNode nodeClass : new ExtIterIterable<>(cls.listLabels(lang))) {
            for (final RDFNode nodeClassFace : new ExtIterIterable<>(clsFace.listLabels(lang))) {
                final String labelClass = nodeClass.toString();
                final String labelClassFace = nodeClassFace.toString();
                if (labelClass != null && labelClassFace != null
                        && dictionary.areEqualsWords(labelClass, labelClassFace)) {
                    sameLabel = true;
                }
            }
        }
        return sameLabel;
    }

    /**
     * Returns whether two ontology classes have the same content in the rdf:Label annotation or not. The same content
     * means if the all compound words of the rdf:Label are equals ignoring upper and lower case and other signs. Looks
     * for in English, Spanish, Italian, German, French and Portuguese.
     *
     * @param cls
     *     ontology class where search for.
     * @param clsFace
     *     ontology class where search.
     *
     * @return <tt>true</tt> if and only if these classes have the same content in the rdf:Label annotation,
     * <tt>false</tt> in other fact.
     */
    private boolean classesWithSameLabelLanguages(final SynsetHelp dictionary, final OntClass cls,
            final OntClass clsFace) {

        boolean sameLabel = false;
        for (final String lang : LANGS) {
            sameLabel = classesWithSameLabel(dictionary, cls, clsFace, lang);
            if (sameLabel) {
                break;
            }
        }
        return sameLabel;
    }
}
