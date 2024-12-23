/*
 * Copyright 2022 Andreas Schipplock
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
package de.schipplock.settings;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class JsonSettings implements Settings {

    private static final String ALLOWED_URI_SCHEME = "file";

    private URI uri;

    private final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting(true).withNullValues(true);

    private final Jsonb jsonb = JsonbBuilder.create(jsonbConfig);

    private final Data data = new Data();

    private JsonSettings() {
    }

    private JsonSettings(URI uri) {
        this.uri = uri;
        checkUri();
        createRequiredDirectories();
        readExistingSettings();
    }

    private JsonSettings(String uri) {
        try {
            this.uri = URI.create(uri);
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new InvalidUriException(format("invalid uri scheme: %s", uri), ex);
        }
        checkUri();
        createRequiredDirectories();
        readExistingSettings();
    }

    private void checkUri() {
        if (uri == null) {
            throw new InvalidUriException("uri is null");
        }
        if (!ALLOWED_URI_SCHEME.equals(uri.getScheme())) {
            throw new InvalidUriException(format("invalid uri scheme: %s", uri.getScheme()));
        }
    }

    private void createRequiredDirectories() {
        if (Files.exists(Path.of(uri).getParent())) {
            return;
        }
        try {
            Files.createDirectories(Path.of(uri).getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void readExistingSettings() {
        if (!Files.exists(Path.of(uri))) {
            return;
        }
        try {
            if (Files.size(Path.of(uri)) == 0) {
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Map<String, List<String>> items = jsonb.fromJson(Files.readString(Path.of(uri)), Map.class);
            data.setItems(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void persist() {
        try {
            Files.writeString(Path.of(uri), jsonb.toJson(data.getItems()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Settings forUri(URI uri) {
        return new JsonSettings(uri);
    }

    public static Settings forUri(String uri) {
        return new JsonSettings(uri);
    }

    @Override
    public void setValue(String key, String value) {
        data.add(key, List.of(value));
    }

    @Override
    public void setValues(String key, List<String> values) {
        data.add(key, values);
    }

    @Override
    public String getValue(String key) {
        return data.get(key).stream().findFirst().get();
    }

    @Override
    public List<String> getValues(String key) {
        return data.get(key);
    }

    @Override
    public void reload() {
        readExistingSettings();
    }
}
