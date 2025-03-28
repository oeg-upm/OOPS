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
import es.upm.fi.oeg.oops.Constants;
import es.upm.fi.oeg.oops.Importance;
import es.upm.fi.oeg.oops.PitfallCategoryId;
import es.upm.fi.oeg.oops.PitfallId;
import es.upm.fi.oeg.oops.PitfallInfo;
import es.upm.fi.oeg.oops.PitfallInfo.AccompPer;
import es.upm.fi.oeg.oops.RuleScope;
import es.upm.fi.oeg.oops.SrcSpec;
import es.upm.fi.oeg.oops.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import org.kohsuke.MetaInfServices;

//import org.omg.CORBA.portable.OutputStream;

@MetaInfServices(Checker.class)
public class P41 implements Checker {

    private static final PitfallInfo PITFALL_INFO = new PitfallInfo(new PitfallId(41, null),
            Set.of(new PitfallCategoryId('N', 8)), Importance.IMPORTANT, "No license declared",
            "The ontology metadata omits information about the license that applies to the ontology.",
            RuleScope.ONTOLOGY, Arity.ZERO, "The ontology is missing a license statement", AccompPer.INSTANCE);

    public static final CheckerInfo INFO = new CheckerInfo(PITFALL_INFO);

    public P41() {
    }

    @Override
    public CheckerInfo getInfo() {
        return INFO;
    }

    @Override
    public void check(final CheckingContext context) {

        final SrcSpec srcSpec = context.getSrcModel().getSrcSpec();

        boolean byPredicate = false;
        boolean byText = false;

        StringBuffer output = new StringBuffer();
        switch (srcSpec.getSrcType()) {
            case RDF_CODE :
                try {
                    final String uri = Constants.licensiusServiceLicenseRaw;
                    final URL url = URI.create(uri).toURL();
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                    conn.setRequestMethod("POST");

                    /*
                     * java.io.OutputStream out = conn.getOutputStream(); Writer writer = new OutputStreamWriter(out,
                     * "UTF-8"); writer.write(onto); writer.close(); out.close();
                     */

                    try (final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                        wr.write(srcSpec.getContent());
                    }

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
                    }
                    final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        output.append(linea);
                    }
                    br.close();

                    // System.out.println("license mode 1: " + output);

                    if (output.toString().toLowerCase().startsWith("unknown")) {
                        context.addResult(PITFALL_INFO, Collections.emptySet());
                    } else {
                        byPredicate = true;
                    }

                    conn.disconnect();
                } catch (final Exception exc) {
                    exc.printStackTrace();
                    // System.out.println("P41 exception ");
                }
                break;
            case URI :
                try {
                    final String encodedData = Utils.urlEncode(srcSpec.getIri());
                    final String uri = Constants.licensiusURIServiceLicense + encodedData;
                    final URL url = URI.create(uri).toURL();

                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
                    }
                    try (final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            output.append(line);
                        }
                    }

                    System.out.println("license mode 2: " + output);

                    if (output.toString().toLowerCase().startsWith("[]")) {
                        context.addResult(PITFALL_INFO, Collections.emptySet());
                    } else {
                        byPredicate = true;
                    }

                    // System.out.println("Get license: " + output);
                    conn.disconnect();
                } catch (final MalformedURLException exc) {
                    // exc.printStackTrace();
                } catch (final IOException exc) {
                    // exc.printStackTrace();
                }
                break;
            default :
                System.out.println("this should not be possible. P41");
        }

        // if (!byPredicate){
        // // try to guess
        // String output = "";
        // try {
        // String encodedData = URLEncoder.encode(onto);
        // String uri= this.licensiusServiceGuess;
        // uri+=encodedData;
        // URL url = new URL(uri);
        // HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // conn.setDoOutput(true);
        // conn.setRequestMethod("GET");
        // conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
        // if (conn.getResponseCode() != 200) {
        // throw new RuntimeException("HTTP error code : "+ conn.getResponseCode());
        // }
        // BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        // String linea;
        // while ((linea = br.readLine()) != null) {
        // output +=linea;
        // }
        //
        //// System.out.println("license: " + output);
        //
        // if (output.toLowerCase().startsWith("unknown")){
        //// numberWithPitfall = 1;
        //// listResults.add("It has no licence defined");
        // }
        // else{
        // byPredicate = true;
        // }
        //
        //// System.out.println("Get license: " + output);
        // conn.disconnect();
        // } catch (MalformedURLException e) {
        //// e.printStackTrace();
        // } catch (IOException e) {
        //// e.printStackTrace();
        // }
        // }
    }

    // private static class MissingLicensePitfall implements Pitfall {
    //
    // // public MissingLicensePitfall() {
    // // }
    //
    // @Override
    // public PitfallInfo getInfo() {
    // return INFO;
    // }
    //
    // @Override
    // public List<OntResource> getResources() {
    // return Collections.emptyList();
    // }
    //
    // @Override
    // public String toString() {
    // return "The ontology is missing a license statement";
    // }
    // }
}
