SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell (find src -name \*.png ; find src -name \*.dtd) | sed s/^src\\///)
PACKAGES := $(shell find src/com/kreative/paint -type d \! -path \*/.\* | sed s/^src\\/// | tr / .)

all: clean bin doc materials.pmz decorate osxclean PowerPaint.jar PowerPaint.app PowerPaint.exe PowerPaint-src.tgz

eclipseall: eclipseclean materials.pmz decorate osxclean PowerPaint.jar PowerPaint.app PowerPaint.exe PowerPaint-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf materials.pmz
	rm -rf src/com/kreative/paint/res/materials.pmz
	rm -rf bin/com/kreative/paint/res/materials.pmz
	rm -rf PowerPaint*.jar
	rm -rf PowerPaint*.app
	rm -rf PowerPaint*.exe
	rm -rf PowerPaint*.tgz

eclipseclean:
	rm -rf materials.pmz
	rm -rf src/com/kreative/paint/res/materials.pmz
	rm -rf bin/com/kreative/paint/res/materials.pmz
	rm -rf PowerPaint*.jar
	rm -rf PowerPaint*.app
	rm -rf PowerPaint*.exe
	rm -rf PowerPaint*.tgz

bin:
	mkdir -p bin
	javac -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc:
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	find bin -name .DS_Store -delete

decorate:
	java -cp .:./bin com.kreative.paint.res.DecoratePMZ -V "Paint Materials"

materials.pmz:
	java -cp .:./bin com.kreative.paint.res.PackPMZ -V materials.pmz
	cp -f materials.pmz src/com/kreative/paint/res/materials.pmz
	cp -f materials.pmz bin/com/kreative/paint/res/materials.pmz

PowerPaint.jar: osxclean
	jar cmf dep/MANIFEST.MF PowerPaint.jar -C bin com/kreative/paint

PowerPaint.app: PowerPaint.jar
	mkdir PowerPaint.app
	mkdir PowerPaint.app/Contents
	mkdir PowerPaint.app/Contents/MacOS
	mkdir PowerPaint.app/Contents/Resources
	mkdir PowerPaint.app/Contents/Resources/Java
	cp dep/Info.plist PowerPaint.app/Contents
	cp dep/JavaApplicationStub PowerPaint.app/Contents/MacOS/PowerPaint
	cp dep/PkgInfo PowerPaint.app/Contents
	cp dep/*.icns PowerPaint.app/Contents/Resources
	cp PowerPaint.jar PowerPaint.app/Contents/Resources/Java

PowerPaint.exe: PowerPaint.jar
ifeq ($(LAUNCH4J_HOME),)
	echo 'Please set $$LAUNCH4J_HOME environment variable to compile exe.'
else
	cd "$(LAUNCH4J_HOME)" ; java -Dlaunch4j.tmpdir=/tmp/ -jar "$(LAUNCH4J_HOME)/launch4j.jar" "$(PWD)/dep/PowerPaint.xml"
endif

PowerPaint-src.tgz: osxclean
	tar -czf PowerPaint-src.tgz src/com/kreative/paint LICENSE

run:
	java -Xmx1024M -cp ./bin com.kreative.paint.ui.Main

.PHONY: all eclipseall clean eclipseclean osxclean decorate run
