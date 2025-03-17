/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.apache.jena.atlas.lib.StrUtils;

public class FollowRedirectRDF {

    private static final String ACCEPT_HEADER_VALUE = StrUtils.strjoin(List.of("application/rdf+xml",
            "application/turtle;q=0.9", "application/x-turtle;q=0.9", "text/n3;q=0.8", "text/turtle;q=0.8",
            "text/rdf+n3;q=0.7", "application/xml;q=0.5", "text/xml;q=0.5", "text/plain;q=0.4", // N-triples
            "*/*;q=0.2"), ",");

    public static class FollowRes {

        private final String newURL;
        private final String contentType;

        public FollowRes(final String newURL, final String contentType) {
            this.newURL = newURL;
            this.contentType = contentType;
        }

        public String getNewURL() {
            return newURL;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static FollowRes follow(final String url) {

        String newURL = url;
        String contentType = null;

        try {
            final URL obj = URI.create(url).toURL();
            final HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setRequestProperty("Accept", ACCEPT_HEADER_VALUE);
            conn.setInstanceFollowRedirects(true);

            // conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            // conn.addRequestProperty("User-Agent", "Mozilla");
            // conn.addRequestProperty("Referer", "google.com");

            // System.out.println("Request URL ... " + url);

            // normally, 3xx is redirect
            final int status = conn.getResponseCode();
            contentType = conn.getContentType();
            final boolean redirect = status != HttpURLConnection.HTTP_OK && (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER);

            // System.out.println("Response Code ... " + status);

            if (redirect) {
                // get redirect url from "location" header field
                newURL = conn.getHeaderField("Location");

                // // get the cookie if need, for login
                // String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connection again
                // conn = (HttpURLConnection) new URL(newUrl).openConnection();
                // conn.setRequestProperty("Cookie", cookies);
                // conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                // conn.addRequestProperty("User-Agent", "Mozilla");
                // conn.addRequestProperty("Referer", "google.com");

                // System.out.println("Redirect to URL : " + newUrl);

                // contentType = conn.getContentType();
            }

            // BufferedReader in = new BufferedReader(
            // new InputStreamReader(conn.getInputStream()));
            // String inputLine;
            // StringBuffer html = new StringBuffer();

            // while ((inputLine = in.readLine()) != null) {
            // html.append(inputLine);
            // }
            // in.close();

            // System.out.println("URL Content... \n" + html.toString());
            // System.out.println("Done follows redirect and there was no redirect");
        } catch (final Exception exc) {
            exc.printStackTrace();
        }

        return new FollowRes(newURL, contentType);
    }
}
