/*
 * Copyright 2019 the Joy Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leadpony.joy.internal;

import static org.leadpony.joy.internal.Requirements.requireNonNull;

import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonGenerator;

/**
 * @author leadpony
 */
class JsonWriterImpl implements JsonWriter {

    private final JsonGenerator generator;

    private boolean alreadyWritten;
    private boolean alreadyClosed;

    JsonWriterImpl(JsonGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void writeArray(JsonArray array) {
        requireNonNull(array, "array");
        checkState();

        alreadyWritten = true;

        generator.writeStartArray();
        for (JsonValue item : array) {
            generator.write(item);
        }
        generator.writeEnd();
        generator.flush();
    }

    @Override
    public void writeObject(JsonObject object) {
        requireNonNull(object, "object");
        checkState();

        alreadyWritten = true;

        generator.writeStartObject();
        for (Map.Entry<String, JsonValue> property : object.entrySet()) {
            generator.writeKey(property.getKey());
            generator.write(property.getValue());
        }
        generator.writeEnd();
        generator.flush();
    }

    @Override
    public void write(JsonStructure value) {
        requireNonNull(value, "value");
        if (value.getValueType() == ValueType.ARRAY) {
            writeArray((JsonArray) value);
        } else {
            writeObject((JsonObject) value);
        }
    }

    @Override
    public void write(JsonValue value) {
        requireNonNull(value, "value");
        checkState();
        alreadyWritten = true;
        generator.write(value);
        generator.flush();
    }

    @Override
    public void close() {
        if (alreadyClosed) {
            return;
        }
        alreadyClosed = true;
        generator.close();
    }

    private void checkState() {
        if (alreadyClosed) {
            throw new IllegalStateException(Message.WRITER_ALREADY_CLOSED.toString());
        }
        if (alreadyWritten) {
            throw new IllegalStateException(Message.WRITER_ALREADY_WRITTEN.toString());
        }
    }
}
