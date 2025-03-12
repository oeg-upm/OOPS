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
import es.upm.fi.oeg.oops.ExtIterIterable;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.RuleScope;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Checker.class)
public class P34 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(34, null),
            Set.of(new PitfallCategoryId('N', 41)), Importance.MINOR, "Untyped class",
            "An ontology element is used as a class " + "without having been explicitly declared as such "
                    + "using the primitives owl:Class or rdfs:Class. "
                    + "This pitfall is related with the common problems listed in [8].",
            RuleScope.CLASS, Arity.ONE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final OntModel model = context.getModel();

        final Set<OntResource> preResults = new HashSet<>();
        for (final Statement sta : new ExtIterIterable<>(model.listStatements())) {
            final Resource subject = sta.getSubject();
            final Property predicate = sta.getPredicate();
            final String predicateUri = predicate.getURI();
            final RDFNode object = sta.getObject();

            // Resource resource = sta.getSubject();

            // String predicate_name = predicate.getLocalName();
            // String object_name = getName(objectUri);

            // System.out.println();
            // System.out.println("Sujeto: "+subjectUri);
            // System.out.println("Predicado: "+predicateUri);
            // System.out.println("Objeto: "+objectUri);

            // If the predicate is rdf:type or rdfs:domain or rdfs:range
            // if(predicateUri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
            //
            // if (object.isURIResource()) {
            //
            // OntClass isclass = model.getOntClass(objectUri);
            // if (isclass == null &&
            // !fromModels(model.getOntResource(objectUri)) &&
            // !listResults.contains(objectUri)){
            // this.listResults.add(objectUri);
            //
            // }
            // else {
            //// System.out.println("La clase es null: " + k);
            // }
            // }
            //
            // }
            // else
            if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#domain")
                    || predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#range")
                    || predicateUri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                analyze(model, preResults, object);
            } else if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")
                    || predicateUri.equals("http://www.w3.org/2002/07/owl#disjointWith")
                    || predicateUri.equals("http://www.w3.org/2002/07/owl#equivalentClass")) {
                analyze(model, preResults, object);
                analyze(model, preResults, subject);
            }
        }

        context.addResultsIndividual(PITFALL_INFO, preResults);
    }

    private void analyze(final OntModel model, final Set<OntResource> preResults, final RDFNode node) {
        if (node.isURIResource()) {
            final String nodeUri = node.toString();
            final OntResource res = model.getOntResource(nodeUri);
            final OntClass ontClass = model.getOntClass(nodeUri);
            final boolean isClass = ontClass != null;
            if (!isClass && !Checker.fromModels(res)) {
                preResults.add(res);
            } else {
                // System.out.println("The class is null: " + k);
            }
        }
    }

    // /**
    // * Runs from the command line eyeball with java and the RDF file previous passed to constructor.
    // *
    // * @throws IOException
    // * brought about the script file's reading.
    // */
    // public void executeCommand() throws IOException {
    // /*
    // * To locate librarys which only will be use occasionally don't modify the classpath variable at ambient level
    // * Instead we are going to use the -cp or -classpath option at time to execute java If we are at UNIX Operating
    // * System. And example can be : $ java -classpath
    // * "$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*" -check name_file.rdf $ java -cp
    // * "$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*" -check name_file.rdf If we are at Windows
    // * Operating System. And example can be : C:> java -classpath "%CLASSPATH%;C:\osmosislatina.jar;.; -check
    // * name_file.rdf C:> java -cp "%CLASSPATH%;C:\osmosislatina.jar;.; -check name_file.rdf
    // */

    // // Build the script's content
    // String script_content = "#!/bin/sh\n";
    // // The next line have to be modify depends on where eyeball libs are
    // script_content = script_content.concat(
    // "java -cp \"$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*\" jena.eyeball -assume owl -check
    // ");
    // script_content = script_content.concat(rdf_path);

    // // The next line have to be modify depends on where the java project is
    // File file = new File("/Users/anamaria/Documents/workspace/SSII_Pitfalls/script");
    // FileWriter file_writer = new FileWriter(file, false);
    // BufferedWriter buffer_writer = new BufferedWriter(file_writer);
    // PrintWriter print_writer = new PrintWriter(buffer_writer);
    // print_writer.write(script_content);
    // print_writer.close();
    // buffer_writer.close();

    // String line = null, class_not_declared = null, class_name = null, id_name = null;
    // // Execute the command
    // Process p = Runtime.getRuntime().exec(command);
    // BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    // BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    // // Read the output's command
    // while ((line = stdInput.readLine()) != null) {
    // // System.out.println(line);
    // if (line.contains("On statement")) {
    // if ((class_not_declared = stdInput.readLine()) != null) {
    // // System.out.println(class_not_declared);
    // if (class_not_declared.contains("class not declared in any schema")) {
    // String[] split_2dot = line.split(":", -1);
    // if (split_2dot[3].contains("domain") || split_2dot[3].contains("range")
    // || split_2dot[3].contains("type") || split_2dot[3].contains("subClassOf")) {
    // String[] last_split = split_2dot[4].split("[/|#]", 0);
    // class_name = last_split[last_split.length - 1];
    // } else {
    // class_name = split_2dot[4];
    // }
    // String[] split_hashkey = split_2dot[2].split("#");
    // // If we have information about the class' ID name, store it
    // if (split_hashkey.length != 1) {
    // id_name = null;
    // id_name = split_hashkey[1].replace(" rdf", "");
    // }
    // preListResults.put(class_name, id_name);
    // }
    // }
    // }

    // }
    // numberWithPitfall = preListResults.size();
    // }

    // private String getName(String uri) {
    // String name = null;
    // String[] parts = uri.split("[\\/#]", 0);
    // return name = parts[parts.length - 1];
    // }

    // private boolean isIndividualDefinition(OntModel model, String subjectUri) {
    // boolean isindividual = false;
    // StmtIterator it = model.listStatements();

    // while (it.hasNext() && !isindividual) {
    // Statement sta = it.nextStatement();
    // Resource resource = sta.getSubject();
    // if (resource.toString().equals(subjectUri)) {
    // RDFNode object = sta.getObject();
    // String objectUri = object.toString();
    // if (objectUri.contains("owl#Thing")) {
    // isindividual = true;
    // }
    // }
    // }
    // return isindividual;
    // }

    // private String objectNotDefinedValueConstraints(OntModel model, String objectblank_uri) {
    // String object_values_from = null;
    // StmtIterator it = model.listStatements();
    // boolean valuesfrom_statement = false;
    // while (it.hasNext() && !valuesfrom_statement) {
    // Statement sta = it.nextStatement();
    // Resource resource = sta.getSubject();
    // String subjectUri = resource.toString();
    // if (subjectUri.equals(objectblank_uri)) {
    // Property predicate = sta.getPredicate();
    // String predicate_name = predicate.getLocalName();
    // if (predicate_name.equals("allValuesFrom") || predicate_name.equals("someValuesFrom")) {
    // valuesfrom_statement = true;
    // RDFNode object = sta.getObject();
    // String objectUri = object.toString();
    // OntClass object_class = model.getOntClass(objectUri);
    // if (object_class == null)
    // object_values_from = objectUri;
    // }
    // }

    // }
    // return object_values_from;
    // }

    // /*
    // * private void buildListResults(String uri_class, String uri_resource){ Set<String> pair_uris; //If the tag class
    // * has not considered yet because the class_face_tag was seen before if(!listResults.containsKey(uri_resource) ){
    // * pair_uris = new HashSet<String>(); pair_uris.add(uri_class); listResults.put(uri_resource, pair_uris); }else{
    // * pair_uris = listResults.get(uri_resource); pair_uris.add(uric); pair_uris.add(uricf);
    // listResults.put(class_tag,
    // * pair_uris); } }
    // */
}
