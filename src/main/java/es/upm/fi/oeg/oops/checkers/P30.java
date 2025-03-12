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
import java.util.HashMap;
import java.util.Set;
import net.sf.extjwnl.JWNLException;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P30 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(30, null),
            Set.of(new PitfallCategoryId('N', 3)), Importance.IMPORTANT, "Equivalent classes not explicitly declared",
            "This pitfall consists in missing the definition of equivalent classes "
                    + "(owl:equivalentClass) in case of duplicated concepts. "
                    + "When an ontology reuses terms from other ontologies, "
                    + "classes that have the same meaning should be defined as equivalent "
                    + "in order to benefit the interoperability between both ontologies.",
            RuleScope.CLASS, Arity.TWO_PLUS, "These classes might be equivalent", AccompPer.TYPE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) throws JWNLException {

        final OntModel model = context.getModel();
        final SynsetHelp dictionary = context.getDictionary();

        final HashMap<String, Set<OntClass>> preResults = new HashMap<>();
        for (final OntClass cls : new ExtIterIterable<>(model.listNamedClasses())) {
            String classTag;
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
            // if (classTag != null){
            // class_tag_T = new Tokenizar(class_tag).getTokensString();
            // }

            for (final OntClass classFace : new ExtIterIterable<>(model.listNamedClasses())) {
                String classFaceTag;
                try {
                    classFaceTag = classFace.getLabel("en");
                    if (classFaceTag == null) {
                        classFaceTag = classFace.getLocalName();
                        // System.out.println("2 tag with lang:" + class_face.getLabel("en") + " -- " +
                        // class_face.getLocalName());
                    } else {
                        classFaceTag = CamelCase.toCamelCase(classFaceTag);
                    }
                } catch (final Exception exc) {
                    classFaceTag = classFace.getLocalName();
                    // System.out.println("2 tag NO lang:" + class_face_tag);
                }
                // if (class_face_tag != null){
                // class_face_tag_T = new Tokenizar(class_face_tag).getTokensString();
                // }

                final String clsUri = cls.getURI();
                final String clsFaceUri = classFace.getURI();

                // If both classes have uri
                // If both classes are not the same, it means has not the same uri
                if (clsUri != null && clsFaceUri != null && !clsUri.equals(clsFaceUri)) {
                    // boolean equivalent = ConstrainsClasses.Are_Equivalent_Classes(class_, class_face);

                    // esto lo he cambiado yo hasta que se mejor la busqueda. Tambien cambiado el if
                    // Map<String,String> synonymous_part = dictionary.containSynonymWord(class_tag,
                    // class_face_tag);

                    final boolean synonymous_part = dictionary.areSynonymousNouns(classTag, classFaceTag);

                    // System.out.println("Size mapa:" + synonymous_part.size() + " Clase:"+class_tag+" Clase
                    // contra:"+class_face_tag);
                    // if(synonymous_part.size()!=0){
                    if (synonymous_part) {
                        final boolean equivalentOneSide = ConstrainsClasses.areEquivalentClasses(cls, classFace);
                        final boolean equivalentOtherSide = ConstrainsClasses.areEquivalentClasses(classFace, cls);

                        // System.out.println("\tEquivalent:"+equivalent_oneside+" "+equivalent_otherside);
                        // if considered subclass of
                        // boolean subclass_oneside = ConstrainsClasses.isSubclassOf(class_, class_face);
                        // boolean subclass_otherside = ConstrainsClasses.isSubclassOf(class_face, class_);
                        // if(!equivalent_oneside && !equivalent_otherside && !subclass_oneside &&
                        // !subclass_otherside){
                        if (!equivalentOneSide && !equivalentOtherSide) {
                            // replace this by the upper line if
                            // exception for subclassOF
                            // If the tag class has not considered yet because the class_face_tag was seen before
                            P31.buildListResults(preResults, classTag, classFaceTag, cls, classFace);
                        }
                    }
                }
            }
        }

        context.addResultsIndividualSets(PITFALL_INFO, preResults.values());
    }

    /**
     * Returns the P30 pitfalls' number of the ontology model (passed as an argument in the construct).
     *
     * @return the P30 pitfalls' number of the ontology model.
     */
    // public int getNumberWithPitfall() {
    // return numberWithPitfall;
    // }

    /**
     * Returns a hashmap with the results after finding out the P30's pitfalls of the ontology model.
     *
     * @return a hashmap whose
     * <li>
     * <ol>
     * first component is the class' name of one of the classes that have to be equivalent but they are not.
     * </ol>
     * <li>
     * <ol>
     * second component is a set compound by the uri class' name mentioned before and the other uris which have
     * to be equivalent but they are not.
     * </ol>
     */
    // public HashMap<String, Set<String>> getResultsWithPitfall30() {
    // return preListResults;
    // }

    /**
     * Print the results after finding out the P30's pitfalls of the ontology model.
     */
    // public void printResultsWithPitfall30() {
    // Set<String> keys = preListResults.keySet();
    // Iterator<String> itk = keys.iterator();
    // while (itk.hasNext()) {
    // String k = itk.next();
    // System.out.println("Label:" + k);
    // Set<String> values = preListResults.get(k);
    // Iterator<String> itv = values.iterator();
    // while (itv.hasNext()) {
    // String value = (String) itv.next();
    // System.out.println("\tURI:" + value);
    // }
    // }
    // }
}
