/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.apache.jena.ontology.OntResource;

public class OntResourceSerializer extends StdSerializer<OntResource> {

    public OntResourceSerializer() {
        this(null);
    }

    public OntResourceSerializer(final Class<OntResource> type) {
        super(type);
    }

    @Override
    public void serialize(final OntResource value, final JsonGenerator jGen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jGen.writeStartObject();
        jGen.writeStringField("uri", value.getURI());
        jGen.writeEndObject();
    }
}
