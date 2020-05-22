# Maven TouchCORE

## About

This repository hosts a script for a (mostly) automated conversion of the core / touchram repositories into a single, maven organized git respoitory.  

## Philosophy

If the preliminaries are covered, running the ```convert``` script crafts a new maven organized repository template out of the legacy TouchCORE repos.

### Preliminaries

To use this script, you need a system that:

 * Has the core sources in ```~/Code/core```
 * Has the touchram sources in ```~/Code/touchram```
 * Has the dynamic EMF dependencies present in ```/Applications/Eclipse Modeling.app/Contents/Eclipse/plugins/```

Additionally, it is strongly recommended, to **once** update the transitive EMF dependency list of TouchCORE:

 * Open Eclipse
 * Rightclick on ```gui``` project  
 => Properties  => Run / Debug  => TouchCORE  => Edit  => JRE => Show command line
 * Copy the list into a file ```deps```, then run:  
```cat deps | tr ':' '\n' | grep eclipse | grep plugins | sed 's/\ //g' > emfdeps```
 * Make sure emfdeps file is located **right besides this README.md file**.

### Changelog

This is a textual description of the steps performed by the ```convert``` script.

 * [Source relocation]()
 * [Maven ```pom``` file generation and placement]()
 * [Generation of custom maven artifacts]()

*Note: Co developpers still have to unzip the generated ```m2.zip``` in their homedirectories, to build with maven*

## Upshots

Using the new repository:

 * It is no longer necessary to checkout two repositories.
 * The project can be built from command line and with any IDE that supports maven.
 * Custom releases of TouchCORE are supported.
 * Server side build verifications and acceptance tests are feasible.

## Important to know

The maven built hard wires dependencies to specific eclipse plugin versions into the build. Developpers must absolutely use the identical versions on their side. Otherwise commits are very likely rejected by the server.  
Upgrading to new EMF versions is still possible, but all deverloppers must use the same plugin versions. Ideally all developpers use the auto installer, to ensure a sane setup.
