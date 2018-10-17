# Ontology Repair
 Implementation of Contraction, Revision and SRW pseudo-contraction operations.
 ### Compiling
 Requires Java 8 and Maven 3.
 `./install.sh` <- Generates a standalone plugin.
 In any case, a JAR file will be generated in the *target* subdirectory.
 ### Running
 `java -jar target/ontologyrepair-1.0.0-SNAPSHOT.jar <operation> <minimality-postulate> -i <ontology-file-name> -o <output-file-name> -f <formula-to-be-contracted>`, where:
 
 `<operation>`: `-c` for Contraction, `-r` for Revision and `-srw` for SRW pseudo-contraction.
 
 `<minimality-postulate>`: Required for the Contraction Operation:
 -  `--core-retainment`, for the Kernel Contraction;
 -  `--relevance`, for the Partial Meet Contraction.
 
 Example:
`java -jar target/ontologyrepair-1.0.0-SNAPSHOT.jar -srw -i examples/cisne.owl -o output.owl -f "cisne_negro Type: AnimalBranco`