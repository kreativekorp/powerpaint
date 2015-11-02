SRCFILES = src/test/*.java src/com/kreative/*/*.java src/com/kreative/*/*/*.java src/com/kreative/*/*/*/*.java
PACKAGES = com.kreative.paint com.kreative.paint.datatransfer com.kreative.paint.draw com.kreative.paint.filter com.kreative.paint.form com.kreative.paint.form.ui com.kreative.paint.format com.kreative.paint.geom com.kreative.paint.gradient com.kreative.paint.io com.kreative.paint.messages com.kreative.paint.palette com.kreative.paint.pattern com.kreative.paint.pict com.kreative.paint.powerbrush com.kreative.paint.rcp com.kreative.paint.res com.kreative.paint.rfp com.kreative.paint.stroke com.kreative.paint.swing com.kreative.paint.tool com.kreative.paint.tool.ui com.kreative.paint.ui com.kreative.paint.ui.about com.kreative.paint.ui.dialog com.kreative.paint.ui.mac com.kreative.paint.ui.menu com.kreative.paint.ui.progress com.kreative.paint.undo com.kreative.paint.util

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
	cp src/com/kreative/paint/alphabet/*.dtd bin/com/kreative/paint/alphabet/
	cp src/com/kreative/paint/dither/*.dtd bin/com/kreative/paint/dither/
	cp src/com/kreative/paint/frame/*.dtd bin/com/kreative/paint/frame/
	cp src/com/kreative/paint/gradient/*.dtd bin/com/kreative/paint/gradient/
	cp src/com/kreative/paint/palette/*.png bin/com/kreative/paint/palette/
	cp src/com/kreative/paint/pattern/*.dtd bin/com/kreative/paint/pattern/
	cp src/com/kreative/paint/powershape/*.dtd bin/com/kreative/paint/powershape/
	cp src/com/kreative/paint/rcp/*.dtd bin/com/kreative/paint/rcp/
	cp src/com/kreative/paint/rcp/*.png bin/com/kreative/paint/rcp/
	cp src/com/kreative/paint/rfp/*.dtd bin/com/kreative/paint/rfp/
	cp src/com/kreative/paint/stroke/*.dtd bin/com/kreative/paint/stroke/
	cp src/com/kreative/paint/tool/*.png bin/com/kreative/paint/tool/
	cp src/com/kreative/paint/ui/about/*.png bin/com/kreative/paint/ui/about/
	cp src/com/kreative/paint/ui/progress/*.png bin/com/kreative/paint/ui/progress/

doc:
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	rm -f src/.DS_Store
	rm -f src/*/.DS_Store
	rm -f src/*/*/.DS_Store
	rm -f src/*/*/*/.DS_Store
	rm -f src/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/.DS_Store
	rm -f bin/*/.DS_Store
	rm -f bin/*/*/.DS_Store
	rm -f bin/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/*/.DS_Store

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

.PHONY: all eclipseall clean eclipseclean osxclean decorate
