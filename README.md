# Maven TouchCORE

## About

This is test repo to demonstrate the feasibility of maintining the current two bitbucket git repositories [touchram] and [core] as a single fused maven repository.

## Philosophy

## Changelog

### Internal project layout

 * The contents of the previous repositories core and touchram have been fused in a new repository ```mavcore```. This repository.
 * Preliminary cleaning:
   * All non project folders on top-level have been removed
   * All ```editors```have been removed
 * All *not-mandatorily-individual* eclipse projects have been fused into a new top level project: ```ca.mcgill.sel.touchcore```:  
Fused src and test folders of the following:  
```for i in ca*; do cp -vpnr $i/src ca.mcgill.sel.touchcore/; done``` (stash everything excluded first.)
   * ```ca.mcgill.sel.commons```
   * ```ca.mcgill.sel.core.controller```
   * ```ca.mcgill.sel.core.evaluator```
   * ```ca.mcgill.sel.core.gui```
   * ```ca.mcgill.sel.core.language```
   * ```ca.mcgill.sel.core.weaver```
   * ```ca.mcgill.sel.perspective```
   * ```ca.mcgill.sel.classdiagram.controller```
   * ```ca.mcgill.sel.ram.classloader```  
Note: Additional JARs contained and required.
     * ```asm-5.0.3.jar```
     * ```commons-lang3-3.2.1.jar```
   * ```ca.mcgill.sel.ram.controller```
   * ```ca.mcgill.sel.ram.generator```
   * ```ca.mcgill.sel.ram.gui```
   * ```ca.mcgill.sel.ram.validator```
   * ```ca.mcgill.sel.ram.weaver```
 * All *mandatorily individual* eclipse projects remain untouched:
   * ```ca.mcgill.sel.core``` and the corresponding:
     * ```ca.mcgill.sel.core.edit```
   * ```ca.mcgill.sel.ram``` and the corresponding:
     * ```ca.mcgill.sel.ram.edit```
   * ```ca.mcgill.sel.classdiagram``` and the corresponding:
     * ```ca.mcgilll.sel.classdiagram.edit
   * ```ca.mcgill.sel.restif``` and the corresponding:
     * ```ca.mcgilll.sel.restif.edit```
   * ```ca.mcgill.sel.ram.expressions``` and the corresponding:
     * ```ca.mcgill.sel.ram.expressions.ide```
     * ```ca.mcgill.sel.ram.expressions.tests```
     * ```ca.mcgill.sel.ram.expressions.ui```
 



### Maven configuration files

 * Above all projects, on top level is a parent pom.xml
   * Specifies recurring dependences, to slim down the extending project-level pom.xmls.
   * Specifies build configurations for different releases, e.g. with / without websocket API included.
 * Build requires custom artifacts
   * Everything EMF realted. See [EMF maven extraction mandatory build preparations](emf-extraction.md)

## Building
