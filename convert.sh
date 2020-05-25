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
## If MAKE_EMF_ARTS is set, this script will also populate the local .m2/repository/p2/osgi/bundle directory with custom EMF artifacts, that are not provided in the official maven repository
#MAKE_EMF_ARTS=true
## Variablkes for input locations
TARGET=~/Desktop/touchcore
CORESOURCE=~/Code/core
RAMSOURCE=~/Code/touchram

# Make sure we start with a clean target folder.
if [ -d $TARGET ]; then
	rm -rf $TARGET
	echo " * Target folder not clean, erasing."
fi
echo " * Target folder created"
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

echo " * Copied standalone projects"

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

echo " * FUSABLE PROJECTS NOT YET FUSED."

# This function iterates over the file "emfdeps" and for each line, outputs a corresponding maven dependency declration. If additionally the variable "MAKE_EMF_ARTS" is set, it will also directly craft a maven-artifact from the specified jar and store it in your local "~/.m2/repository/" folder.
# NOTE: YOU HAVE TO PROVIDE "emfdeps", before calling this scrip. See README.md
function mavenizeEmf {

   echo -n " * Creating custom EMF maven references."

   if [ ! -f emfdeps ]; then
	echo "ERROR: emfdeps not found. The README explains how to create it."
	exit -1
   fi

   if [ -f PARENTDEPS.txt ]; then
	rm PARENTDEPS.txt
   fi

   ## The lines in "emfdeps" list the EMF jars. The path contains a whitepsace, so we have to set the fiels seperator to "only newlines"
   IFS=$'\n'       # make newlines the only separator

   ## Now build a dependency block statement for every jar specified in t "emfdeps"
   for i in $(cat emfdeps); do
	# wrap up the jar as a custom maven artifact
	echo "<dependency>" >> PARENTDEPS.txt
GROUPID="p2.osgi.bundle"
	echo "  <groupId>$GROUPID</groupId>" >> PARENTDEPS.txt
	echo -n "  <artifactId>" >> PARENTDEPS.txt 
ARTIFACT=$(echo -n "$i" | cut -f7 -d '/' | cut -f1 -d '_')
	echo -n $ARTIFACT >> PARENTDEPS.txt 
	echo "</artifactId>" >> PARENTDEPS.txt 
	echo -n "  <version>"  >> PARENTDEPS.txt 
VERSION=$(echo -n "$i" | cut -f7 -d '/' | cut -f2 -d '_' | sed 's/\.jar//')
	echo -n $VERSION >> PARENTDEPS.txt 
	echo "</version>" >> PARENTDEPS.txt 
	echo "</dependency>" >> PARENTDEPS.txt
	echo "" >> PARENTDEPS.txt

	## Optionally, if MAKE_EMF_ARTS is set, also populate local .m2 driectory.
	if [ ! -z $MAKE_EMF_ARTS ]; then
		mvn install:install-file -Dfile=$i -DgroupId=$GROUPID -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."
	fi
   done
   echo ""
   echo " * Created dynamic EMF dependency pom entries: PARENTDEPS.txt"
}

# merges the generated maven dependency block into the parent pom template
function createParentPom() {
	## print template until flag
	sed '/DEPEN/q' parent-pom.xml | grep -v DEPENDENCIES_FLAG > pom.xml
	## insert generated dependency block
	cat PARENTDEPS.txt >> pom.xml
	## print template after flag
	sed -n '/DEPEN/,$p' parent-pom.xml | grep -v DEPENDENCIES_FLAG >> pom.xml

	## move the generated pom to the generated repository root.
	mv pom.xml $TARGET

	echo "Created parent root with full EMF dependency list at $TARGET/pom.xml"
}

# copies prepared custom poms for the individual projects into the repository, to refer to them as maven modules
function createModulePoms() {
	
  ## currently only commons supported
  cp pom-commons.xml $TARGET/ca.mcgill.sel.commons/pom.xml
}


## The actual fusion routine starts here:
copyAndMergeSources
mavenizeEmf
createParentPom
createModulePoms



