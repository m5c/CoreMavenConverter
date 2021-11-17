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
#MAKE_EMF_ARTS=false
## Variablkes for input locations
TARGET=~/Desktop/touchcore
CORESOURCE=~/Code/core
RAMSOURCE=~/Code/touchram

## parse options, see: https://unix.stackexchange.com/a/20977
while getopts "ab:" opt; do
    case $opt in
    a) aflag=true ; MAKE_EMF_ARTS=true;
    esac
done

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
	"ca.mcgill.sel.core.controller"
	"ca.mcgill.sel.commons")

	## Declare array with RAM projects names of things we want to preserve:
	PreserveRamProjects=(
	"ca.mcgill.sel.ram"
	"ca.mcgill.sel.ram.edit"
	"ca.mcgill.sel.classdiagram"
	"ca.mcgill.sel.classdiagram.edit"
	"ca.mcgill.sel.restif"
	"ca.mcgill.sel.restif.edit"
	"ca.mcgill.sel.restif.controller"
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
}

function fuseFusableProjects()
{
	## Array of projects to be fused into a single controller project.
	
	### everything that becomes the future MAIN controller (other langs may provide extra controllers, e.g. the RIF language)
	## Module dependencies are listed behind.
	ControllerCoreProjects=(
#	"ca.mcgill.sel.core.controller" 		# => core, commons
	"ca.mcgill.sel.core.evaluator" 			# => core, commons, familiar bridge (JAR)
	"ca.mcgill.sel.core.language"   		# => commons, [weaver]
	"ca.mcgill.sel.core.weaver"			# => commons, core(util)
	"ca.mcgill.sel.perspective")			# => core, [corelangauge], cdm, [cdm-controller] 
	ControllerRamProjects=(
	"ca.mcgill.sel.classdiagram.controller"		# => classdiagram(impl) / (util), [corecontroller], commons, corectl/util
	"ca.mcgill.sel.ram.classloader"			# => asm (JAR)
	"ca.mcgill.sel.ram.controller"			# => commons, [corectl], core(util), [coreweaver], ram(impl), ram(util)
	"ca.mcgill.sel.ram.generator"			# => [corelang], core(util), ram(util)
	"ca.mcgill.sel.ram.validator"			# => commons, core(util), [ramctl], ram(impl)
	"ca.mcgill.sel.ram.weaver")			# => commons, core(util), [coreweaver], ram(util)

	## Create a new project / maven module with all the sources of controller logic
	mkdir -p $TARGET/ca.mcgill.sel.touchcore.controller/src
	## Iterate over corresponding projects of both source directories and sources into the target controller project
	for i in ${ControllerCoreProjects[@]}; do
	{
		cp -npr $CORESOURCE/$i/src $TARGET/ca.mcgill.sel.touchcore.controller
	}
	done
	for i in ${ControllerRamProjects[@]}; do
	{
		cp -npr $RAMSOURCE/$i/src $TARGET/ca.mcgill.sel.touchcore.controller
	}
	done

	### Gui that becomes the new view package
	## Create a new project / maven module with all the sources of controller logic
	mkdir -p $TARGET/ca.mcgill.sel.touchcore.view/src	
	cp -npr $RAMSOURCE/ca.mcgill.sel.ram.gui/src $TARGET/ca.mcgill.sel.touchcore.view/
}

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

   ## The lines in "emfdeps" list the EMF jars. The path contains a whitepsace, so we have to set the field seperator to "only newlines"
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
		mvn install:install-file -Dfile=$i -DgroupId=$GROUPID -DartifactId=$ARTIFACT -Dversion=$VERSION -Dpackaging=jar -DcreateChecksum=true >> $TARGET/conversion-log.txt
		echo "------------------------" >> $TARGET/conversion-log.txt
		echo -n "."
	fi
   done
   echo ""
   echo " * Created dynamic EMF dependency pom entries: PARENTDEPS.txt"
}

# merges the generated maven dependency block into the parent pom template
function createParentPom() {
	## print template until flag
	sed '/DEPEN/q' poms/pom-parent.xml | grep -v DEPENDENCIES_FLAG > pom.xml
	## insert generated dependency block
	cat PARENTDEPS.txt >> pom.xml
	## print template after flag
	sed -n '/DEPEN/,$p' poms/pom-parent.xml | grep -v DEPENDENCIES_FLAG >> pom.xml

	## move the generated pom to the generated repository root.
	mv pom.xml $TARGET

	echo " * Full EMF dependency list fused into parent template, stored at $TARGET/pom.xml"
}

function wrapCustomNonEmfArtifacts() {
   
	## If MAKE_EMF_ARTS is set, populate local .m2 directory with custom jars from friendly academic institutions
	if [ ! -z $MAKE_EMF_ARTS ]; then
	echo -n " * Creating custom non-EMF maven references."
		
		# familiarbridge
		mvn install:install-file -Dfile=staticjars/familiar-bridge-0.0.1.jar -DgroupId=friend.of.mcgillsel -DartifactId=familiarbridge -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."
		
		# asm
		mvn install:install-file -Dfile=staticjars/asm-5.0.3.jar -DgroupId=friend.of.mcgillsel -DartifactId=asm -Dversion=5.0.3-SNAPSHOT -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."

		# mt4j
		mvn install:install-file -Dfile=staticjars/mt4j.jar -DgroupId=friend.of.mcgillsel -DartifactId=mt4j -Dversion=mspatch-1.0 -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."

		# antlr
		mvn install:install-file -Dfile=staticjars/antlr-generator-3.2.0-patch.jar -DgroupId=friend.of.mcgillsel -DartifactId=antlr -Dversion=3.2.0-SNAPSHOT -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."
		
		# abegotree
		mvn install:install-file -Dfile=staticjars/org.abego.treelayout.core.jar -DgroupId=friend.of.mcgillsel -DartifactId=abegotree -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -DcreateChecksum=true &> /dev/null
		echo -n "."

		echo ""
	fi
}

# copies prepared custom poms for the individual projects into the repository, to refer to them as maven modules
function createModulePoms() {

  # Inject poms for all copied projects	
  cp poms/pom-classdiagram.xml $TARGET/ca.mcgill.sel.classdiagram/pom.xml
  cp poms/pom-classdiagramedit.xml $TARGET/ca.mcgill.sel.classdiagram.edit/pom.xml
  cp poms/pom-commons.xml $TARGET/ca.mcgill.sel.commons/pom.xml
  cp poms/pom-core.xml $TARGET/ca.mcgill.sel.core/pom.xml
  cp poms/pom-coreedit.xml $TARGET/ca.mcgill.sel.core.edit/pom.xml
  cp poms/pom-corecontroller.xml $TARGET/ca.mcgill.sel.core.controller/pom.xml
  cp poms/pom-ram.xml $TARGET/ca.mcgill.sel.ram/pom.xml
  cp poms/pom-ramedit.xml $TARGET/ca.mcgill.sel.ram.edit/pom.xml
  cp poms/pom-ramexpressions.xml $TARGET/ca.mcgill.sel.ram.expressions/pom.xml
  cp poms/pom-ramexpressionside.xml $TARGET/ca.mcgill.sel.ram.expressions.ide/pom.xml
  cp poms/pom-ramexpressionstests.xml $TARGET/ca.mcgill.sel.ram.expressions.tests/pom.xml
  cp poms/pom-ramexpressionsui.xml $TARGET/ca.mcgill.sel.ram.expressions.ui/pom.xml
  cp poms/pom-restif.xml $TARGET/ca.mcgill.sel.restif/pom.xml
  cp poms/pom-restifedit.xml $TARGET/ca.mcgill.sel.restif.edit/pom.xml
  cp poms/pom-restifcontroller.xml $TARGET/ca.mcgill.sel.restif.controller/pom.xml
  cp poms/pom-view.xml $TARGET/ca.mcgill.sel.touchcore.view/pom.xml
 

  # Inject poms for the two resulting merged projects (controller)
  cp poms/pom-touchcorecontroller.xml $TARGET/ca.mcgill.sel.touchcore.controller/pom.xml

  echo " * Module poms initialized."
}

# copy the libfolder with os specific opengl drivers into the view project, so maven can place it in the target folder (has to reside right next to the jar-with-dependencies.)
function copyRuntimeLibs()
{
  cp -r lib $TARGET/ca.mcgill.sel.touchcore.view
}

# Patching navigation par sources, for generic SDK compatibility
function patchSources() {
  echo " * WARNING: Stub patch applied on NavigationBar.java"
  cp patches/NavigationBar.java $TARGET/ca.mcgill.sel.touchcore.view/src/ca/mcgill/sel/ram/ui/components/navigationbar/
}

copyHelperScripts() {
  cp helperscripts/* $TARGET
}

## The actual fusion routine starts here:
copyAndMergeSources
fuseFusableProjects
mavenizeEmf
wrapCustomNonEmfArtifacts
createParentPom
createModulePoms
copyRuntimeLibs
patchSources
copyHelperScripts


