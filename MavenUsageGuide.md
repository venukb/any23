## How to use Any23 with Maven 2+ ##

To use **Any23** with Maven you must first declare the following repository in your project POM

```
<repositories>
    <!-- Specific repository for Any23 dependencies without a dedicated repository. -->
    <repository>
        <id>any23-repository-external</id>
        <url>http://any23.googlecode.com/svn/repo-ext</url>
    </repository>
    <!-- The Any23 modules repository. -->
    <repository>
        <id>any23-repository</id>
        <url>http://any23.googlecode.com/svn/repo</url>
    </repository>
</repositories>
```

Then you can declare the following dependencies:

The library Core:

```
<dependency>
    <groupId>org.deri.any23</groupId>
    <artifactId>any23-core</artifactId>
    <version>0.6.1</version>
</dependency>
```

Optionally the REST Service:

```
<dependency>
    <groupId>org.deri.any23</groupId>
    <artifactId>any23-service</artifactId>
    <version>0.6.1</version>
</dependency>
```

Optionally one or more [plugin dependencies](http://any23.googlecode.com/svn/trunk/plugins).