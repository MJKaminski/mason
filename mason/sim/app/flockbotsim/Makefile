### MASON Makefile
#### By Sean Luke

#### Relevant Stuff:
#### To see all your make options:  type   make help
#### To add flags (like -O) to javac:  change the FLAGS variable below

JAVAC = javac ${FLAGS}
JAVA = java ${CP} ${APP}

FLAGS = -d out -target 1.6 -source 1.6 -g -nowarn ${CP}
CP = -cp "../../../../:../../../../*:out"
DIRS = *.java \
	api/*.java \
	jbox2d/*.java

APP = sim.app.flockbotsim.MyFlockbotSimWithUI

# Make the main MASON code, not including 3D code
all: clean
	${JAVAC} ${DIRS}

# Make the main code
run: all
	${JAVA}

# Delete all jmf gunk, checkpoints, backup emacs gunk classfiles,
# documentation, and odd MacOS X poops
clean:
	find . -name "*.class" -exec rm -f {} \;
	find . -name "jmf.log" -exec rm -f {} \;
	find . -name ".DS_Store" -exec rm -f {} \;
	find . -name "*.checkpoint" -exec rm -f {} \;
	find . -name "*.java*~" -exec rm -f {} \;
	find . -name ".#*" -exec rm -rf {} \;
	rm -rf jar/*.jar docs/classdocs/resources docs/classdocs/ec docs/classdocs/sim docs/classdocs/*.html docs/classdocs/*.css docs/classdocs/package*
