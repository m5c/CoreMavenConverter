# Maven TouchCORE

## About

This repository hosts a script for a (mostly) automated conversion of the core / touchram repositories into a single, maven organized git respoitory.  

## Philosophy

If the preliminaries are covered, running the ```convert``` script crafts a new maven organized repository template out of the legacy TouchCORE repos. If the ```-a``` switch is provided, the script will use the [```emfdeps```](emfdeps) file to also convert eclipse plugins to local maven artifacts and create [maven artifacts of external jars](staticjars).

 > Note: Your original sources are not modified. All output is placed in ```~/Desktop/touchcore```

### Preliminaries

To use this script, you need a system that:

 * Has the core sources in ```~/Code/core```
 * Has the touchram sources in ```~/Code/touchram```
 * Has the webcore sources in ```~/Code/touchcore-web/webcore-server```
 * Has the dynamic EMF dependencies present in ```/Applications/Eclipse Modeling.app/Contents/Eclipse/plugins/```

 > Note: If your touchcore installation uses different pathes, you can modify the default locations (Search for ```*SOURCE``` variables in ```convert.sh```)

Additionally, it is strongly recommended, to **once** update the transitive EMF dependency list of TouchCORE:

 * Open Eclipse
 * Rightclick on ```gui``` project  
 => Properties  => Run / Debug  => TouchCORE  => Edit  => JRE => Show command line
 * Copy the list into a file ```deps```, then run:  
```cat deps | tr ':' '\n' | grep eclipse | grep plugins > emfdeps```
 * Make sure emfdeps file is located **right beside this README.md file**.

 > Note: emfdeps will contain a space character in every path but that is ok. The script knows how to handle that when iterating over the entries.


### Building

 * You can still build everything with eclipse, just like before. Eclipse is not bothered by the presence of some additional maven configuration files.
 * Additionally you can also use any IDE that supports maven, as long as you do not need Eclipse specific functionality, like metamodel editing or code generation.
 * This maven command will create a self contained jar: 
 ```mvn clean package``` (or for convenience, use the ```./make``` command)
 * Additionally the build process supports profiles. E.g. if you want a version that powers up the REST backend, compile it with:  
```mvn clean package -Prestbackend```

 * Speeded up building:
   * Use the ```-T X``` option, to compile on multiple cores parallel
   * Compile only what you need (e.g.: ```-pl ca.mcgill.sel.core```) and the dependencies (```-am```)  
  => 0.8 secs instead of 3 secs for ```core```  
  => 6.9 secs instead of 14 secs for ```touchcore```

## Upshots

Using the new repository:

 * It is no longer necessary to checkout two repositories.
 * The project can be built from command line and with any IDE that supports maven.
 * Custom profiled releases of TouchCORE are supported.
 * Server side build verifications and acceptance tests are feasible, e.g. for CI pipelines.

## Important to know

 * The maven built hard wires dependencies to specific eclipse plugin versions into the build. Developers must absolutely use the identical versions on their side. Otherwise commits are very likely rejected by the server.  
 * Upgrading to new EMF versions is still possible, but all developers must use the same plugin versions. Ideally all developers use the auto installer, to ensure a sane setup.
 * The structure of the preserved projects slightly deviates from the maven convention. This is required to not confuse eclipse, which is to **-bleep-** dumb to just detect a standard maven layout. This means the sources do not reside in ```src/main/java```, but simply in ```src```. To ensure their inclusion by maven, all preserved projects override the default source location, with a specific entry:  

```xml
<properties>
  <src.dir>src</src.dir>
</properties>
<build>
  <sourceDirectory>${src.dir}</sourceDirectory>
</build>
```

 * Similar to src dir override, some ```pom``` files override the resource location. This is required so files loaded at runtime are correclty referenced as resources. Namely:  
```
./ca.mcgill.sel.ram/bin/models/languages/RestTree.core
./ca.mcgill.sel.ram/resources/models/Association/Association.core
./ca.mcgill.sel.ram/resources/models/perspectives/RestInterfacePerspective.core
./ca.mcgill.sel.ram/resources/models/perspectives/DomainModelling.core
./ca.mcgill.sel.ram/resources/models/perspectives/DesignModellingWithRAM.core
./ca.mcgill.sel.ram/resources/models/perspectives/EnvironmentModelling.core
./ca.mcgill.sel.ram/resources/models/perspectives/RifRamPerspective.core
./ca.mcgill.sel.ram/resources/models/perspectives/DomainDesignUseCasePerspective.core
./ca.mcgill.sel.ram/resources/models/perspectives/DomainUseCasePerspective.core
./ca.mcgill.sel.ram/resources/models/perspectives/UseCasePerspective.core
./ca.mcgill.sel.ram/resources/models/perspectives/DesignModelling.core
./ca.mcgill.sel.ram/resources/models/languages/EnvironmentModelLanguage.core
./ca.mcgill.sel.ram/resources/models/languages/ClassDiagram.core
./ca.mcgill.sel.ram/resources/models/languages/UseCaseLanguage.core
./ca.mcgill.sel.ram/resources/models/languages/ClassDiagramLanguage.core
./ca.mcgill.sel.ram/resources/models/languages/ReusableAspectModels.core
./ca.mcgill.sel.ram/resources/models/languages/RestTree.core
```


## Possibly easier in future versions

Some Eclipse plug-ins seem to be available on official repos now. So the emfdeps list could possibly be shortened.

 * [Xtext](https://mvnrepository.com/artifact/org.eclipse.xtext/org.eclipse.xtext)
 * [EMF](https://mvnrepository.com/artifact/org.eclipse.emf/org.eclipse.emf.ecore)

## Known Issues

 * ~~```restbackend``` build profile does not compile. ```core.utils``` package has unsatisfied dependencies to ```emf.commons``` (although properly specified in ```pom.xml```).~~ [Fixed](https://stackoverflow.com/a/41448035)
 * ~~Build from IntelliJ is confused by multiple ```src``` entries in ```ram.expressions``` ```pom.xml```.~~ [Fixed!](https://stackoverflow.com/a/58694915)
 * ```restbackend``` compiles into wrong target name: ```WebCORE-REST-exec.jar```.
 * ```restbackend``` seems to have missing dependencies at runtime:  
```bash
Caused by: java.lang.NoClassDefFoundError: org/apache/log4j/Logger
	at org.eclipse.ocl.pivot.utilities.ToStringVisitor.<clinit>(ToStringVisitor.java:124)
```
   * Fixed with [additional inclusion A](https://mvnrepository.com/artifact/log4j/log4j/1.2.17), [additional inclusion B](https://mvnrepository.com/artifact/com.google.inject/guice/1.0).
