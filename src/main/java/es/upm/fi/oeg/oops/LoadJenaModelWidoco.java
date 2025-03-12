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
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.Date;
// import org.apache.jena.ontology.OntDocumentManager;
// import org.apache.jena.ontology.OntModel;
// import org.apache.jena.ontology.OntModelSpec;
// import org.apache.jena.ontology.ProfileRegistry;
// import org.apache.jena.rdf.model.Model;
// import org.apache.jena.rdf.model.ModelFactory;
// import org.apache.jena.util.FileManager;

// public class LoadJenaModelWidoco {

// String exceptionInLoadJenaModel = null;
// Boolean exception = false;

// public LoadJenaModelWidoco(OntModel modelJENA, String content) {
// // this procedure is for loading from URI

// try {
// modelJENA.read(content, null);
// System.out.println("load without CN");
// }

// catch (java.lang.Exception a) {
// // System.err.println(a.getMessage());
// this.exceptionInLoadJenaModel = "exception when loading RDF";
// // this.exception = true;
// // if the vocabulary is from a URI, I download it locally. This is done
// // because Jena doesn't handle https very well.

// for (String serialization : Constants.vocabPossibleSerializations) {
// System.out.println("Attempting to download vocabulary in " + serialization);
// try {
// URL url = new URL(content);
// HttpURLConnection connection = (HttpURLConnection) url.openConnection();
// connection.setRequestMethod("GET");
// connection.setInstanceFollowRedirects(true);
// connection.setRequestProperty("Accept", serialization);
// int status = connection.getResponseCode();
// boolean redirect = false;
// if (status != HttpURLConnection.HTTP_OK) {
// if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
// || status == HttpURLConnection.HTTP_SEE_OTHER)
// redirect = true;
// }
// // there are some vocabularies with multiple redirections:
// // 301 -> 303 -> owl
// while (redirect) {
// String newUrl = connection.getHeaderField("Location");
// connection = (HttpURLConnection) new URL(newUrl).openConnection();
// connection.setRequestProperty("Accept", serialization);
// status = connection.getResponseCode();
// if (status != HttpURLConnection.HTTP_MOVED_TEMP && status != HttpURLConnection.HTTP_MOVED_PERM
// && status != HttpURLConnection.HTTP_SEE_OTHER)
// redirect = false;
// }
// InputStream in = (InputStream) connection.getInputStream();
// String newOntologyPath = new File("tmp" + new Date().getTime()).getAbsolutePath() + File.separator
// + "Ontology";
// Files.copy(in, Paths.get(newOntologyPath), StandardCopyOption.REPLACE_EXISTING);
// in.close();
// modelJENA.read(FileManager.get().open(newOntologyPath), null);
// break; // if the vocabulary is downloaded, then we don't download it for the other serializations
// } catch (Exception e) {
// System.err.println("Failed to download vocabulary in " + serialization);
// }
// }
// }
// }

// public LoadJenaModelWidoco(OntModel modelJENA, InputStream content) {
// // this procedure is for loading from code

// try {
// modelJENA.read(content, null);
// System.out.println("First attempt: " + modelJENA.listStatements().toList().size());

// }

// catch (java.lang.Exception a) {
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
// System.out.println("fichero creado 2: " + fileName);

// fileToWrite.print(content);
// System.out.println("load widoco he escrito en el fichero");

// fileToWrite.close();

// modelJENA.read(new FileInputStream(fileName), null, "TTL");
// System.out.println("Second attempt: " + modelJENA.listStatements().toList().size());
// File file = new File(fileName);
// file.delete();
// }

// catch (java.lang.Exception b) {
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
