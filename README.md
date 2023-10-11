Full doc coming soon...



quick local test :
- disable firewall
- java 17 jdk needed
- mvn or mvn wrapper (.\mvnw) validate : This adds the Spread and Alcatraz jars to the local maven repository.
- mvn or mvn wrapper (.\mvnw) clean install : builds the project.
- Configure IDE run configuration. I recommend IntelliJ. Use the quarkus template. Server uses -Dquarkus.package.main-class=server in Configuraiton Arguments. Client uses -Dquarkus.package.main-class=client.
- check "Allow multiple instances" for each
- Run spread -n localhost in assets/spread-cli/.
- Run Quarkus Server Instance with Server run configuration
- Run 2 Client instances

note: cli input (System.in) bugs with IntelliJ Debug Mode. I recommend you just run normally