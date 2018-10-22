#biopax-validator-issue10

Embedding BioPAX Validator in another project and enabling Aspectj LTW can be problematic
(requires a special maven assembly configuration). See BioPAX/validator#10 (issue).

This is a quick-fix, follow-up, example "project" to help a user who was trying 
to run the BioPAX Validator 4.0.0 from own java code
and validate all the BioPAX (.owl) files in a data directory.

I just refactored the original pom.xml and Biopax.java source files to make it work,
(removed path/name specific source code and unused parameters, imports, dependencies) 
but did not try to polish the app's code and design)


Build and try it (requires java >= 8): 

```
mvn clean package
cd target
unzip example-distr.zip
cd example
java -Xmx8g -javaagent:lib/spring-instrument-4.2.4.RELEASE.jar -Dpaxtools.CollectionProvider=org.biopax.paxtools.trove.TProvider -jar example.jar testdata
```

For some reason (perhaps a configuration bug) the app does not work if a simple configuration fo the maven-assembly-plugin (jar-with-dependencies) or maven-shade-plugin are used instead of using advanced assembly (see src/main/resources/assembly.xml) and manifest.

