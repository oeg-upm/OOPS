/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.checkers;

import es.upm.fi.oeg.oops.Arity;
import es.upm.fi.oeg.oops.CamelCase;
import es.upm.fi.oeg.oops.Checker;
import es.upm.fi.oeg.oops.CheckerInfo;
import es.upm.fi.oeg.oops.CheckingContext;
import es.upm.fi.oeg.oops.ConstrainsClasses;
import es.upm.fi.oeg.oops.ExtIterIterable;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SynsetHelp;
import es.upm.fi.oeg.oops.Tokenizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.extjwnl.JWNLException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P31 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(31, null),
            Set.of(new PitfallCategoryId('N', 2)), Importance.CRITICAL, "Defining wrong equivalent classes",
            "Two classes are defined as equivalent, " + "using owl:equivalentClass, "
                    + "when they are not necessarily equivalent.",
            RuleScope.CLASS, Arity.TWO_PLUS, "These classes might not be equivalent", AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws JWNLException {

        final OntModel model = context.getModel();
        final SynsetHelp dictionary = context.getDictionary();

        int recursive_level = 3;

        final HashMap<String, Set<OntClass>> preResults = new HashMap<>();
        for (final OntClass cls : new ExtIterIterable<>(model.listClasses())) {
            String classTag;
            String classTagT;
            try {
                classTag = cls.getLabel("en");
                if (classTag == null) {
                    classTag = cls.getLocalName();
                    // System.out.println("1 tag with lang:" + class_.getLocalName() + " -- " + new
                    // Tokenizar(class_tag).getTokensString());
                } else {
                    classTag = CamelCase.toCamelCase(classTag);
                }
                // System.out.println("1 tag with lang:" + class_tag);
            } catch (final Exception exc) {
                classTag = cls.getLocalName();
                // System.out.println("1 tag No lang:" + class_tag);
            }

            for (final OntClass clsFace : new ExtIterIterable<>(model.listClasses())) {
                String clsFaceTag = null;
                String clsFaceTagT = null;
                try {
                    clsFaceTag = clsFace.getLabel("en");
                    if (clsFaceTag == null) {
                        clsFaceTag = clsFace.getLocalName();
                        // System.out.println("2 tag with lang:" + class_face.getLabel("en") + " -- " +
                        // class_face.getLocalName());
                    } else {
                        clsFaceTag = CamelCase.toCamelCase(clsFaceTag);
                        // System.out.println("2 else tag with lang:" + class_face.getLabel("en") + " -- camel case" +
                        // class_face_tag);

                    }
                } catch (final Exception exc) {
                    clsFaceTag = clsFace.getLocalName();
                    // System.out.println("2 tag NO lang:" + class_face_tag);
                }

                if (classTag != null && clsFaceTag != null) {
                    classTagT = Tokenizer.tokenizedString(classTag);
                    clsFaceTagT = Tokenizer.tokenizedString(clsFaceTag);

                    final String clsUri = cls.getURI();
                    final String clsFaceUri = clsFace.getURI();

                    // If both classes have uri
                    // If both classes are not the same, it means has not the same uri
                    if (clsUri != null && clsFaceUri != null && !clsUri.equals(clsFaceUri)) {
                        final boolean equivalentOneSide = ConstrainsClasses.areEquivalentClasses(cls, clsFace);
                        final boolean equivalentOtherSide = ConstrainsClasses.areEquivalentClasses(clsFace, cls);

                        if (equivalentOneSide || equivalentOtherSide) {
                            // aqui incluir si tienen mismo id no presentar.

                            String localName1 = classTag;
                            localName1 = localName1.replaceAll("\\W", "");
                            localName1 = localName1.replaceAll("-", "");
                            localName1 = localName1.replaceAll("_", "");
                            localName1 = localName1.toLowerCase();

                            String localName2 = clsFaceTag;
                            localName2 = localName2.replaceAll("\\W", "");
                            localName2 = localName2.replaceAll("-", "");
                            localName2 = localName2.replaceAll("_", "");
                            localName2 = localName2.toLowerCase();

                            if (localName1.equalsIgnoreCase(localName2)) {
                                // no hay pitfall
                            }
                            // si son sin�nimos no hay pitfall
                            else if (dictionary.areSynonymousNouns(localName1, localName2)) {
                                // no hay pitfall
                            } else if (dictionary.areSynonymousNouns(classTag, clsFaceTag)) {
                                // no hay pitfall
                            } else if (dictionary.areSynonymousNouns(classTagT, clsFaceTagT)) {
                                // no hay pitfall
                            } else if (dictionary.containSynonymsForAllNoStopWords(classTag, clsFaceTag)
                                    && dictionary.containSynonymsForAllNoStopWords(clsFaceTag, classTag)) {
                                // no hay pitfall
                            } else {
                                Map<String, String> meronyms_part = dictionary.containHypernymWord(classTag, clsFaceTag,
                                        recursive_level);

                                // System.out.println("Size mapa:" + meronyms_part.size() + " Clase:"+class_tag+"
                                // Clase contra:"+class_face_tag);
                                boolean doBuildListResults = false;
                                if (meronyms_part.size() != 0) {
                                    doBuildListResults = true;
                                } else {
                                    meronyms_part = dictionary.containHypernymWord(clsFaceTag, classTag,
                                            recursive_level);
                                    if (meronyms_part.size() != 0) {
                                        doBuildListResults = true;
                                    } else {
                                        meronyms_part = dictionary.containMeronymWord(classTag, clsFaceTag, "PART",
                                                recursive_level);
                                        if (meronyms_part.size() != 0) {
                                            doBuildListResults = true;
                                        } else {
                                            meronyms_part = dictionary.containMeronymWord(classTag, clsFaceTag,
                                                    "SUBSTANCE", recursive_level);
                                            if (meronyms_part.size() != 0) {
                                                doBuildListResults = true;
                                            } else {
                                                meronyms_part = dictionary.containMeronymWord(classTag, clsFaceTag,
                                                        "MEMBER", recursive_level);
                                                if (meronyms_part.size() != 0) {
                                                    doBuildListResults = true;
                                                } else {
                                                    meronyms_part = dictionary.containMeronymWord(clsFaceTag, classTag,
                                                            "PART", recursive_level);
                                                    if (meronyms_part.size() != 0) {
                                                        doBuildListResults = true;
                                                    } else {
                                                        meronyms_part = dictionary.containMeronymWord(clsFaceTag,
                                                                classTag, "SUBSTANCE", recursive_level);
                                                        if (meronyms_part.size() != 0) {
                                                            doBuildListResults = true;
                                                        } else {
                                                            meronyms_part = dictionary.containMeronymWord(clsFaceTag,
                                                                    classTag, "MEMBER", recursive_level);
                                                            if (meronyms_part.size() != 0) {
                                                                doBuildListResults = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (doBuildListResults) {
                                    buildListResults(preResults, classTag, clsFaceTag, cls, clsFace);
                                }
                            }
                        }
                    }
                }
            }
        }

        context.addResultsIndividualSets(PITFALL_INFO, preResults.values());
    }

    /**
     * Puts an entry into the listResults.
     *
     * @param class_tag
     *     the name of the first ontology class.
     * @param class_face_tag
     *     the name of the second ontology class.
     * @param uric
     *     the first ontology class' uri.
     * @param uricf
     *     the second ontology class' uri.
     */
    public static void buildListResults(final HashMap<String, Set<OntClass>> preListResults, String class_tag,
            String class_face_tag, OntClass class_, OntClass class_face) {

        // If the tag class has not been considered yet,
        // because the class_face_tag was seen before
        if (!preListResults.containsKey(class_face_tag)) {
            Set<OntClass> pair_uris;
            if (!preListResults.containsKey(class_tag)) {
                pair_uris = new HashSet<>();
            } else {
                pair_uris = preListResults.get(class_tag);
            }
            pair_uris.add(class_);
            pair_uris.add(class_face);
            preListResults.put(class_tag, pair_uris);
        }
    }
}
