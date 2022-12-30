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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

class SettingsTests {

    private static File tmpFile;

    private static Settings settings;

    @BeforeAll
    static void setup() {
        try {
            tmpFile = File.createTempFile("jsonsettings", ".json");
            settings = JsonSettings.forUri(tmpFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNullUri() {
        Exception exception = assertThrows(InvalidUriException.class, () -> {
            JsonSettings.forUri((URI) null);
        });
        assertEquals("uri is null", exception.getMessage());
    }

    @Test
    void testInvalidUriScheme() {
        Exception exception = assertThrows(InvalidUriException.class, () -> {
            JsonSettings.forUri("https://foo.bar");
        });
        assertEquals("invalid uri scheme: https", exception.getMessage());
    }

    @Test
    void testValidUriScheme() {
        Settings settings = JsonSettings.forUri(tmpFile.toURI());
        assertInstanceOf(JsonSettings.class, settings);
    }

    @Test
    void testSingleValue() {
        settings.setValue("foo", "bar");
        assertEquals("bar", settings.getValue("foo"));
    }

    @Test
    void testMultipleValues() {
        settings.setValues("themes", List.of("theme1", "theme2"));
        assertEquals(2, settings.getValues("themes").size());
    }

    @Test
    void testReload() {
        settings.setValues("themes", List.of("theme1", "theme2"));
        settings.reload();
        assertEquals(2, settings.getValues("themes").size());
    }
}
