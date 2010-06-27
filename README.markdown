jasmine-maven-plugin
====================
**A Maven Plugin for processing JavaScript sources, specs, and executing Jasmine**

Put this in your POM
--------------------

<project>
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <groupId>searls</groupId>
        <artifactId>jasmine-maven-plugin</artifactId>
        <version>0.11.1-SNAPSHOT</version>
        <executions>
          <execution>
            <goals>
              <goal>resources</goal>
              <goal>testResources</goal>
              <goal>test</goal>
              <goal>preparePackage</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>
  </build>
  ...
  <repositories>
    <repository>
      <id>searls-maven-thirdparty</id>
      <url>http://searls-maven-repository.googlecode.com/svn/trunk/thirdparty</url>
    </repository>
  </repositories>
  <pluginRepositories>
    <pluginRepository>
      <id>searls-maven-releases</id>
      <url>http://searls-maven-repository.googlecode.com/svn/trunk/releases</url>
    </pluginRepository>
    <pluginRepository>
      <id>searls-maven-snapshots</id>
      <url>http://searls-maven-repository.googlecode.com/svn/trunk/snapshots</url>
    </pluginRepository>
  </pluginRepositories>
  ...
</project>
And Smoke It
------------

    mvn package
    

Usage Notes
-----------

### Project layout
The jasmine-maven-plugin sports a default project directory layout that should be convenient for most green field projects. The included example project (which is in [src/test/resources/jasmine-webapp-example](http://github.com/searls/jasmine-maven-plugin/tree/master/src/test/resources/jasmine-webapp-example/) demonstrates this convention and looks something like this:

    |-- pom.xml
    |-- src
    |   |-- main
    |   |   |-- javascript
    |   |   |   |-- HelloWorld.js
    |   |   |   `-- vendor
    |   |   |       `-- jquery-1.4.2.min.js
    |   |   |-- resources
    |   |   `-- webapp
    |   |       |-- META-INF
    |   |       |   `-- MANIFEST.MF
    |   |       |-- WEB-INF
    |   |       |   |-- lib
    |   |       |   `-- web.xml
    |   |       `-- index.jsp
    |   `-- test
    |       `-- javascript
    |           |-- FailSpec.js
    |           `-- HelloWorldSpec.js
    `-- target
        |-- classes
        |-- jasmine
        |   |-- SpecRunner.html
        |   |-- spec
        |   |   |-- FailSpec.js
        |   |   `-- HelloWorldSpec.js
        |   `-- src
        |       |-- HelloWorld.js
        |       `-- vendor
        |           `-- jquery-1.4.2.min.js
        |-- jasmine-webapp-example
        |   |-- META-INF
        |   |   `-- MANIFEST.MF
        |   |-- WEB-INF
        |   |   |-- classes
        |   |   `-- web.xml
        |   |-- index.jsp
        |   `-- js
        |       |-- HelloWorld.js
        |       `-- vendor
        |           `-- jquery-1.4.2.min.js
        `-- jasmine-webapp-example.war

As seen above, production JavaScript is placed in `src/main/javascript`, while test specs are each in `src/test/javascript`. The plugin does support nested directories and will maintain your directory structure as it processes the source directories.

### Goals
At the moment, the plugin is only tested to work if all of its goals are configured to be executed.

* processResources     - This goal binds to the process-resources phase and copies the `src/main/javascript` directory into `target/jasmine/src`. It can be changed by configuring a parameter named `srcDir` in the plugin execution section of the POM.
* processTestResources - This goal binds to the process-test-resources phase and copies the `src/test/javascript` directory into `target/jasmine/spec`. It can be changed by configuring a parameter named `testSrcDir` in the plugin execution section of the POM.
* test                 - This goal binds to the test phase and generates a Jasmine runner file in `target/jasmine/SpecRunner.html` based on the sources processed by the previous two goals and Jasmine's own dependencies. It will respect the `skipTests` property, and will not halt processing if `haltOnFailure` is set to false.
* preparePackage       - This goal binds to the prepare-package phase and copies the production JavaScript sources from `target/jasmine/src` to `js` within the package directory (e.g. `target/your-webapp/js`). The sub-path can be cleared or overridden by setting the `packageJavaScriptPath` property

## Maintainers
* [Justin Searls](mailto:searls@gmail.com), Pillar Technology

## Contributions
Pull requests are, of course, welcome! A few todos as of 6/27, if anyone is interested in tackling them:
* Parse & format ignored tests (currently only passing & failing tests are paresed)
* A report moo that generates JUnit-style XML, so results can easily be rolled up by CI servers.
* A facility that automatically executes the other goals if only `test` or `preparePackage` is run.

## Acknowledgments
* Thanks to Pivotal Labs for authoring and publishing [Jasmine](http://github.com/pivotal/jasmine)
* Thanks to christian.nelson and sivoh1, owners of the [javascript-test-maven-plugin](http://code.google.com/p/javascript-test-maven-plugin/), which provided a similar implementation from which to glean several valuable lessons.