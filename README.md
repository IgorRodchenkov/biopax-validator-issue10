# biopax-validator-issue10

This is an example fix to help a user who was trying 
to run the BioPAX Validator 4.0.0 from own java code
to validate all the BioPAX (.owl) files in a data directory.

Embedding BioPAX Validator in another project and enabling Aspectj LTW was problematic
(required a special maven assembly configuration). 
See [BioPAX/validator#10](https://github.com/BioPAX/validator/issues/10) (issue).
For some reason, this app (as well as the validator console app) did not work if a simple 
configuration for the maven-assembly-plugin (jar-with-dependencies) or maven-shade-plugin 
were used instead of advanced assembly (see src/main/resources/assembly.xml) and manifest.

I updated the biopax-validator and refactored user's source code to make things work.
This project uses biopax-validator:5.0.0-SNAPSHOT library and maven-shade-plugin
(it does not require special zip assembly configuration).
However, it does not work properly (skips syntax errors) if build with Spring framework version 5
(load time weaving does not work despite -javaagent option). 

Build and try it (requires java >= 8): 

```
mvn clean package
java -Xmx8g -javaagent:${settings.localRepository}/spring-instrument-${spring.version}.jar \
-Dpaxtools.CollectionProvider=org.biopax.paxtools.trove.TProvider -jar target/example.jar src/test/resources
```

if you're running Java 9 and above, also include the following command-line options before -jar:
```
--add-opens java.base/java.lang=ALL-UNNAMED
```
