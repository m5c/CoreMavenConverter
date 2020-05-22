# Custom script to covert the current mavenless TouchCORE project layout into a single maven organized modular repository.
# Calling this script will asume the legacu repositories are located at:
# ~/Code/core
# ~/Code/touchram
# Additionally it asumes a correctly setup eclipse modeling version is installed at
# /Applications/Eclipse\ Modelling/
# The latter is important, because this script extracts EMF specific jars from the eclipse installation, to craft custom maven artifacts from it. Use the setup-skript to be on the safe side.
# The new repository will we created on your Desktop. Change the TARGET variable to override this.
#
#! /bin/bash
#
TARGET=~/Desktop/touchcore
CORESOURCE=~/Code/core
RAMSOURCE=~/Code/touchram

# Make sure we start with a clean target folder.
if [ -d $TARGET ]; then
	rm -rf $TARGET
fi
mkdir $TARGET

# Copy projects from ancient ram/core repositories that remain as they are, fuse all others into a new eclips project.
function copyAndMergeSources()
{
# Copy all projects that remain untouched "as they are" into the target folder:
## Declare array with CORE projects names of things we want to preserve:
PreserveCoreProjects=(
"ca.mcgill.sel.core"
"ca.mcgill.sel.core.edit"
"ca.mcgill.sel.commons")

## Declare array with RAM projects names of things we want to preserve:
PreserveRamProjects=(
"ca.mcgill.sel.ram"
"ca.mcgill.sel.ram.edit"
"ca.mcgill.sel.classdiagram"
"ca.mcgill.sel.classdiagram.edit"
"ca.mcgill.sel.restif"
"ca.mcgill.sel.restif.edit"
"ca.mcgill.sel.ram.expressions"
"ca.mcgill.sel.ram.expressions.ide"
"ca.mcgill.sel.ram.expressions.tests"
"ca.mcgill.sel.ram.expressions.ui")

## Actually copy those projects
for i in ${PreserveCoreProjects[@]}; do
  cp -r $CORESOURCE/$i $TARGET/
done
for i in ${PreserveRamProjects[@]}; do
  cp -r $RAMSOURCE/$i $TARGET/
done

## Fuse the sources and tests of the remaining projects
FuseCoreProjects=(
"ca.mcgill.sel.core.controller"
"ca.mcgill.sel.core.evaluator"
"ca.mcgill.sel.core.gui"
"ca.mcgill.sel.core.language"
"ca.mcgill.sel.core.weaver"
"ca.mcgill.sel.perspective"
"ca.mcgill.sel.classdiagram.controller")
FuseRamProjects=(
"ca.mcgill.sel.ram.classloader"
"ca.mcgill.sel.ram.controller"
"ca.mcgill.sel.ram.generator"
"ca.mcgill.sel.ram.gui"
"ca.mcgill.sel.ram.validator"
"ca.mcgill.sel.ram.weaver")


}

# Copy the template pom.xml into each of the copied projects. This pom referes to a poarent pom we will later on place on top of all these projects, to ensure they have all their dependencies satisfied. Only update the artifact name in the copied template.
function populatePoms() {

   for i in $(cat emfdeps); do
	# wrap up the jar as a custom maven artifact
	echo "<dependency>";
	echo "  <groupId>p2.osgi.bundle</groupId>";
	echo -n "  <artifactId>"
ARTIFACT=$(echo -n "$i" | cut -f7 -d '/' | cut -f1 -d '_')
	echo -n $ARTIFACT
	echo "</artifactId>";
	echo -n "  <version>";
VERSION=$(echo -n "$i" | cut -f7 -d '/' | cut -f2 -d '_')
	echo -n $VERSION;
	echo "</version>";
	echo "</dependency>";
	echo "";
   done
}

## The actual fusion routine starts here:
#copyAndMergeSources
populatePoms > PARENTDEPS.txt


