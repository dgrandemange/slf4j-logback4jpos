slf4j-logback4jpos
==================
A lf4j-logback appender implementation for jPos.

This implementation work is inspired by Victor Salaman's initial work on <a href="https://github.com/jpos/jPOS-EE">jPOS-EE</a> core module, which is the reference implementation. 

This variation deals with jPos native Loggeable objects, in a way so that their formatting by logback (actually a simple call to toString()) is cancelled, and delegated to the underlying jPos logger.
Support for logback Encoder is also added.


Adding slf4j-logback4jpos to your mavenized jPos project :
---------------------------------------------------------
Simply add the following snippet to your pom.xml :

    <repositories>
    
      <!-- ... your specific repositories if there are any ... -->
    
      <repository>
    		<id>dgrandemange-mvn-repo-releases</id>
    		<name>dgrandemange GitHub Maven Repository releases</name>
    		<url>https://github.com/dgrandemange/dgrandemange-mvn-repo/raw/master/releases/</url>
    	</repository>
    
    </repositories>

    <dependencies>
    
        <!-- ... your project dependencies -->
    
        <dependency>
		<groupId>org.jpos.jposext</groupId>
		<artifactId>slf4j-logback4jpos</artifactId>
		<version>1.0.1</version>        
        </dependency>        
    </dependencies>

Running the project demo :
--------------------------
First :
> mvn -Pdemo install

Then, under runtime directory :
> java -jar q2.jar

See class '/src/demo/java/org/jpos/jposext/logging/slf4j/logback/transaction/DemoParticipant.java' and logback configuration file '/src/demo/resources/cfg/logback.xml' for usage.
