/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops.checkers;

import es.upm.fi.oeg.oops.Checker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class CopyOfP34 {

    private String command;
    // This is the rdf/owl file's path
    private String rdf_path;
    // static private HashMap<String,Set<String>> listResults = new HashMap<String,Set<String>>();
    HashMap<String, String> preListResults = new HashMap<String, String>();
    List<String> listResults = new ArrayList<String>();
    int numberWithPitfall = 0;

    /**
     * Constructor. Initializes a newly created <tt>P34</tt> object.
     *
     * @param model
     *     Ontlogy model where search for.
     */
    public CopyOfP34(OntModel model) {
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement sta = it.nextStatement();
            Property predicate = sta.getPredicate();
            Resource resource = sta.getSubject();
            String subject_uri = resource.toString();
            RDFNode rdfnode = sta.getObject();
            String object_uri = rdfnode.toString();
            String predicate_name = predicate.getLocalName();
            String object_name = getName(object_uri);
            String predicate_uri = predicate.getURI();
            /*
             * System.out.println("Sujeto:"+subject_uri); System.out.println("Predicado:"+predicate_uri);
             * System.out.println("Objeto:"+object_uri); System.out.println();
             */

            // If the predicate is rdf:type or rdfs:domain or rdfs:range
            if (predicate_name.equals("type") || predicate_name.equals("domain") || predicate_name.equals("range")
                    || predicate_name.equals("equivalentClass")) {
                boolean isclass = object_name.equals("Class");
                boolean ispro = object_name.equals("Property");
                boolean isobpro = object_name.equals("ObjectProperty");
                boolean object_isblanknode = object_uri.contains("/");
                boolean subject_isblanknode = subject_uri.contains("/");
                boolean object_isontologytype = object_name.equals("Ontology");
                boolean isthing = object_uri.contains("owl#Thing");

                // If the object is not owlClass and rdf_syntax_nsProperty and owlThing and rdf_schemaSubPropertyOf
                // and if the object is not a literal and is not empty node and is not "Ontology" (due to owl:Ontology
                // tag)
                // and the subject is not an empty node
                if (!isclass && !ispro && !isobpro && !rdfnode.isLiteral() && object_isblanknode && subject_isblanknode
                        && !object_isontologytype && !isthing) {
                    OntProperty ontpro = model.getOntProperty(subject_uri);
                    OntClass object = model.getOntClass(object_uri);
                    // If the subject is not an ontology property
                    if (ontpro == null && object == null) {
                        if (!this.isIndividualDefinition(model, subject_uri)) {
                            preListResults.put(object_uri, subject_uri);
                        }
                    }
                }
            } else {
                if (predicate_name.equals("subClassOf") || predicate_name.equals("disjointWith")) {
                    OntClass object_class = model.getOntClass(object_uri);

                    if (object_class == null) {
                        boolean object_isblanknode = object_uri.contains("/");
                        // If the statement is the value constrains axiom (with subClassOf).
                        if (predicate_name.equals("subClassOf") && !object_isblanknode) {
                            String class_not_defined = this.objectNotDefinedValueConstraints(model, object_uri);
                            // If the resource used such as class and is not defined like a class.
                            if (class_not_defined != null) {
                                // If the statement's subject is not a blank node then store it
                                if (subject_uri.contains("/")) {
                                    preListResults.put(class_not_defined, subject_uri);
                                } else {
                                    preListResults.put(class_not_defined, null);
                                }
                            }
                        } else {
                            // If the statement is a normal subClassOf or disjointWith.
                            boolean isemptynode = subject_uri.contains("/");
                            // When the uri is "", we do not consider this statement.It happens because the rdf:resource
                            // or rdf:ID is not defined.
                            if (!object_uri.equals("")) {
                                if (isemptynode)
                                    preListResults.put(object_uri, subject_uri);
                                else
                                    preListResults.put(object_uri, null);
                            }
                        }
                    }
                }
            }
        }
        // numberWithPitfall = preListResults.size();

        Set<String> keys = preListResults.keySet();
        Iterator<String> itk = keys.iterator();
        while (itk.hasNext()) {
            String k = itk.next();
            OntClass isclass = model.getOntClass(k);
            if (isclass != null && !Checker.fromModels(model.getOntResource(k))) { // added Maria
                this.listResults.add(k);
            } else {
                // System.out.println("La clase es null: " + k);
            }

        }
        numberWithPitfall = listResults.size();
    }

    /**
     * Constructor. Initializes a newly created <tt>P34</tt> object.
     *
     * @param path_file
     *     the RDF/OWL file's path.
     */
    public CopyOfP34(String path_file) {
        // Fix the rdf/owl file's path
        rdf_path = path_file;
        // Fix the script's path which contain a java command
        command = "/Users/anamaria/Documents/workspace/SSII_Pitfalls/script";
    }

    /**
     * Runs from the command line eyeball with java and the RDF file previous passed to constructor.
     *
     * @throws IOException
     *     brought about the script file's reading.
     */
    public void executeCommand() throws IOException {
        /*
         * To locate librarys which only will be use occasionally don't modify the classpath variable at ambient level
         * Instead we are going to use the -cp or -classpath option at time to execute java If we are at UNIX Operating
         * System. And example can be : $ java -classpath
         * "$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*" -check name_file.rdf $ java -cp
         * "$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*" -check name_file.rdf If we are at Windows
         * Operating System. And example can be : C:> java -classpath "%CLASSPATH%;C:\osmosislatina.jar;.; -check
         * name_file.rdf C:> java -cp "%CLASSPATH%;C:\osmosislatina.jar;.; -check name_file.rdf
         */

        // Build the script's content
        String script_content = "#!/bin/sh\n";
        // The next line have to be modify depends on where eyeball libs are
        script_content = script_content.concat(
                "java -cp \"$CLASSPATH:.:/Users/anamaria/Documents/EyeBall/eyeball-2.3/lib/*\" jena.eyeball -assume owl -check ");
        script_content = script_content.concat(rdf_path);

        // The next line have to be modify depends on where the java project is
        File file = new File("/Users/anamaria/Documents/workspace/SSII_Pitfalls/script");
        FileWriter file_writer = new FileWriter(file, false);
        BufferedWriter buffer_writer = new BufferedWriter(file_writer);
        PrintWriter print_writer = new PrintWriter(buffer_writer);
        print_writer.write(script_content);
        print_writer.close();
        buffer_writer.close();

        String line = null, class_not_declared = null, class_name = null, id_name = null;
        // Execute the command
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        // Read the output's command
        while ((line = stdInput.readLine()) != null) {
            // System.out.println(line);
            if (line.contains("On statement")) {
                if ((class_not_declared = stdInput.readLine()) != null) {
                    // System.out.println(class_not_declared);
                    if (class_not_declared.contains("class not declared in any schema")) {
                        String[] split_2dot = line.split(":", -1);
                        if (split_2dot[3].contains("domain") || split_2dot[3].contains("range")
                                || split_2dot[3].contains("type") || split_2dot[3].contains("subClassOf")) {
                            String[] last_split = split_2dot[4].split("[/|#]", 0);
                            class_name = last_split[last_split.length - 1];
                        } else {
                            class_name = split_2dot[4];
                        }
                        String[] split_hashkey = split_2dot[2].split("#");
                        // If we have information about the class' ID name, store it
                        if (split_hashkey.length != 1) {
                            id_name = null;
                            id_name = split_hashkey[1].replace(" rdf", "");
                        }
                        preListResults.put(class_name, id_name);
                    }
                }
            }

        }
        numberWithPitfall = preListResults.size();
    }

    /**
     * Returns a hashmap with the results after finding out the P34's pitfalls of the ontology model.
     *
     * @return a hashmap whose
     * <li>
     * <ol>
     * first component is the class' name. That have to be the type class that exits but it is not.
     * </ol>
     * <li>
     * <ol>
     * second component is the class's id if the class has.
     * </ol>
     */
    public HashMap<String, String> getPreListResults() {
        return preListResults;
    }

    /**
     * Returns the P34 pitfalls' number of the ontology model (passed as an argument in the construct).
     *
     * @return the P34 pitfalls' number of the ontology model.
     */
    public int getNumberWithPitfall() {
        return numberWithPitfall;
    }

    /**
     * Print the results after finding out the P34's pitfalls of the ontology model.
     */
    public void printResultsWithPitfall34() {
        Set<String> keys = preListResults.keySet();
        Iterator<String> itk = keys.iterator();
        while (itk.hasNext()) {
            String k = itk.next();
            String value = preListResults.get(k);
            // If we have information about the class' ID name
            if (value != null)
                System.out.println("\tClass:" + k + "\tID:" + value);
            else
                System.out.println("\tClass:" + k);
        }
    }

    private String getName(String uri) {
        String name = null;
        String[] parts = uri.split("[\\/]", 0);
        parts = parts[parts.length - 1].split("#");
        return name = parts[parts.length - 1];
    }

    private boolean isIndividualDefinition(OntModel model, String subject_uri) {
        boolean isindividual = false;
        StmtIterator it = model.listStatements();

        while (it.hasNext() && !isindividual) {
            Statement sta = it.nextStatement();
            Resource resource = sta.getSubject();
            if (resource.toString().equals(subject_uri)) {
                RDFNode rdfnode = sta.getObject();
                String object_uri = rdfnode.toString();
                if (object_uri.contains("owl#Thing")) {
                    isindividual = true;
                }
            }
        }
        return isindividual;
    }

    private String objectNotDefinedValueConstraints(OntModel model, String objectblank_uri) {
        String object_values_from = null;
        StmtIterator it = model.listStatements();
        boolean valuesfrom_statement = false;
        while (it.hasNext() && !valuesfrom_statement) {
            Statement sta = it.nextStatement();
            Resource resource = sta.getSubject();
            String subject_uri = resource.toString();
            if (subject_uri.equals(objectblank_uri)) {
                Property predicate = sta.getPredicate();
                String predicate_name = predicate.getLocalName();
                if (predicate_name.equals("allValuesFrom") || predicate_name.equals("someValuesFrom")) {
                    valuesfrom_statement = true;
                    RDFNode rdfnode = sta.getObject();
                    String object_uri = rdfnode.toString();
                    OntClass object_class = model.getOntClass(object_uri);
                    if (object_class == null)
                        object_values_from = object_uri;
                }
            }

        }
        return object_values_from;
    }

    /*
     * private void buildListResults(String uri_class, String uri_resource){ Set<String> pair_uris; //If the tag class
     * has not considered yet because the class_face_tag was seen before if(!listResults.containsKey(uri_resource) ){
     * pair_uris = new HashSet<String>(); pair_uris.add(uri_class); listResults.put(uri_resource, pair_uris); }else{
     * pair_uris = listResults.get(uri_resource); pair_uris.add(uric); pair_uris.add(uricf); listResults.put(class_tag,
     * pair_uris); } }
     */
}
