/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// package es.upm.fi.oeg.oops;

// import java.io.BufferedOutputStream;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.InputStream;
// import java.io.PrintStream;
// import java.util.Date;
// import org.apache.jena.ontology.OntDocumentManager;
// import org.apache.jena.ontology.OntModel;
// import org.apache.jena.ontology.OntModelSpec;
// import org.apache.jena.ontology.ProfileRegistry;
// import org.apache.jena.rdf.model.Model;
// import org.apache.jena.rdf.model.ModelFactory;

// public class LoadJenaModel {

// String exceptionInLoadJenaModel = null;
// Boolean exception = false;

// public LoadJenaModel(OntModel modelJENA, String content) {

// try {
// modelJENA.read(content, null);
// // System.out.println(modelJENA.listStatements().toList().size());
// } catch (java.lang.Exception a) {
// // System.err.println(a.getMessage());
// this.exceptionInLoadJenaModel = "exception when loading RDF";
// // this.exception = true;

// try {
// // Crear el directorio si no existe
// File dir1 = new File(".");
// String path = dir1.getCanonicalPath();
// new File(path + "/Ontologies").mkdirs();
// String date = new Date().toString() + ";";
// String plainDate = date.replace(" ", "").replace(";", "").replace(":", "");

// // crear fichero para ese endpoint.
// String fileName = path + "/Ontologies/Temp" + plainDate + ".ttl";

// PrintStream fileToWrite = new PrintStream(
// new BufferedOutputStream(new FileOutputStream(new File(fileName), true)), true);
// System.out.println("fichero creado 1: " + fileName);

// fileToWrite.print(content);
// fileToWrite.close();

// modelJENA.read(new FileInputStream(fileName), null, "TURTLE");
// // System.out.println(modelJENA.listStatements().toList().size());
// File file = new File(fileName);
// file.delete();
// } catch (java.lang.Exception b) {
// // System.err.println(b.getMessage());
// this.exceptionInLoadJenaModel = "exception when loading TTL";
// // this.exception = true;

// try {
// modelJENA.read(content, null, "N3");
// } catch (java.lang.Exception c) {
// // System.err.println(c.getMessage());
// this.exceptionInLoadJenaModel = "exception when loading N3";
// this.exception = true;
// }
// }
// }
// }

// public String getExceptionDetail() {
// return this.exceptionInLoadJenaModel;
// }

// public Boolean getException() {
// return this.exception;
// }
// }
