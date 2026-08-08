# Plexus Resources

[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-resources.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.plexus/plexus-resources)
[![GitHub CI](https://github.com/codehaus-plexus/plexus-resources/actions/workflows/maven.yml/badge.svg)](https://github.com/codehaus-plexus/plexus-resources/actions/workflows/maven.yml)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/org/codehaus/plexus/plexus-resources/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/org/codehaus/plexus/plexus-resources/README.md)
[![License](https://img.shields.io/github/license/codehaus-plexus/plexus-resources.svg?label=License)](https://www.apache.org/licenses/LICENSE-2.0)

Retrieves a resource by name without the caller needing to know where it lives. `ResourceManager` tries a
set of loaders in turn — the filesystem, the thread context classpath, a jar, a URL — and hands back a
stream or a `File`.

Useful when a plugin accepts a configuration file that a user might supply as a path, as something on the
classpath, or as a URL, and you would rather not write that lookup three times.

## Status

Maintained, quietly. The API is small and settled; expect dependency updates rather than new features.

## Using it

```xml
<dependency>
  <groupId>org.codehaus.plexus</groupId>
  <artifactId>plexus-resources</artifactId>
  <version>1.3.1</version>
</dependency>
```

Check the badge above for the current version.

```java
@Inject
ResourceManager resourceManager;

resourceManager.addSearchPath( FileResourceLoader.ID, project.getFile().getParentFile().getAbsolutePath() );

InputStream in = resourceManager.getResourceAsInputStream( "checkstyle.xml" );
File file = resourceManager.getResourceAsFile( "checkstyle.xml" );
```

`getResourceAsFile` will materialise a temporary file when the resource is not already one, so it works for
classpath and URL resources too.

## Requirements

Java 8 or later.

## Documentation

- [Project site](https://codehaus-plexus.github.io/plexus-resources/)
- [Javadoc](https://javadoc.io/doc/org.codehaus.plexus/plexus-resources)
- [Release notes](https://github.com/codehaus-plexus/plexus-resources/releases)

## Contributing

See [CONTRIBUTING.md](https://github.com/codehaus-plexus/.github/blob/master/CONTRIBUTING.md). In short:
`mvn verify` builds, and run `mvn spotless:apply` before pushing or CI will fail on formatting.

Please report security vulnerabilities privately — see
[SECURITY.md](https://github.com/codehaus-plexus/.github/blob/master/SECURITY.md), not a public issue.
