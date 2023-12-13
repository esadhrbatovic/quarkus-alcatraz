This project was developed using the quarkus framework, for faster developer experience: 
It provides hot code reload functionality and dependency injection using Jakarta EE.
We use maven for dependency management and packaging.
Client and server are both in this one codebase, however using multiple entry points via 
run arguments in quarkus, you can run this application as either a client or a server.
- Client entry is at at.ac.fhcampuswien.alcatraz.client.ClientApplication
- Server entry is at at.ac.fhcampuswien.alcatraz.server.ServerApplication

quick local test :
- disable firewall
- java 17 jdk needed
- mvn or mvn wrapper (.\mvnw) validate : This adds the Spread and Alcatraz jars to the local maven repository.
- mvn or mvn wrapper (.\mvnw) clean install : builds the project.
- Configure IDE run configuration. IntelliJ is recommended. Use the quarkus template. Server uses -Dquarkus.package.main-class=server in Configuraiton Arguments. Client uses -Dquarkus.package.main-class=client.
- make sure serverIps: 127.0.0.1 and spread-server: 127.0.0.1 is configured in application.properties
- check "Allow multiple instances" for each
- Run spread -n localhost in assets/spread-cli/.
- Run Quarkus Server Instance with Server run configuration
- Run some Client instances with Client run configuraion

- optional way to run after mvn validate and mvn clean install - in root of project: mvn quarkus:dev -Dquarkus.package.main-class=server OR mvn quarkus:dev -Dquarkus.package.main-class=client 

Testing in LAN - same steps as local test with following adjustments:
- spread.conf should be the same on all nodes that have a running spread daemon
- our example: Spread_Segment  192.168.92.255:4803 {
    anna  192.168.92.113
    julia  192.168.92.106
    esad 192.168.92.174
}
- make sure the application properties are set like the following: 
- spread-server=your ip or the ip of a machine where a spread daemon is running, if you are anna and have a spread daemon running : spread-server=192.168.92.113
- serverIps=list of known servers, in our case it might be serverIps: 192.168.178.31,192.168.178.36
- instead of running spread -n localhost simply run spread
