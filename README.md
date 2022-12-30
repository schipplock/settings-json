# Settings JSON

`settings-json` ist eine Java Bibliothek, die Einstellungen in einer `JSON`-Datei
speichern und auch wieder auslesen kann. Diese Bibliothek wurde mit Java 17 getestet.

## Installation

**Das muss in die pom.xml:**

```xml
<dependency>
    <groupId>de.schipplock.settings</groupId>
    <artifactId>settings-json</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Wie verwende ich diese Bibliothek?

**Es wird folgender Import benötigt:**

```java
import de.schipplock.settings.JsonSettings;
```

**Einstellungen speichern:**

```java
var settings = JsonSettings.forUri(Path.of(System.getProperty("user.home"), "settings4223.json").toUri());
settings.setValue("language", "de");
settings.setValue("theme", "Monokai Pro");
settings.persist();
```

In `%USERPROFILE%\settings4223.json` (Windows) oder `$HOME/settings4223.json` (Linux) wurden die Einstellungen
gespeichert.

**Einstellungen lesen:**

```java
var settings = JsonSettings.forUri(Path.of(System.getProperty("user.home"), "settings4223.json").toUri());
settings.getValue("language");
settings.getValue("theme");
```

## License
[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)