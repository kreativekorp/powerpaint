SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell (find src -name \*.png ; find src -name \*.dtd ; find src -name \*.icc) | sed s/^src\\///)
PACKAGES := $(shell find src/com/kreative/paint -type d \! -path \*/.\* | sed s/^src\\/// | tr / .)

all: clean bin doc materials.pmz PowerPaint.jar PowerPaint.app PowerPaint.exe PowerPaint-src.tgz

eclipseall: eclipseclean materials.pmz PowerPaint.jar PowerPaint.app PowerPaint.exe PowerPaint-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf "Paint Materials"
	rm -rf materials.pmz
	rm -rf src/com/kreative/paint/ui/materials.pmz
	rm -rf bin/com/kreative/paint/ui/materials.pmz
	rm -rf PowerPaint*.jar
	rm -rf PowerPaint*.app
	rm -rf PowerPaint*.exe
	rm -rf PowerPaint*.tgz

eclipseclean:
	rm -rf "Paint Materials"
	rm -rf materials.pmz
	rm -rf src/com/kreative/paint/ui/materials.pmz
	rm -rf bin/com/kreative/paint/ui/materials.pmz
	rm -rf PowerPaint*.jar
	rm -rf PowerPaint*.app
	rm -rf PowerPaint*.exe
	rm -rf PowerPaint*.tgz

bin: src
	mkdir -p bin
	javac -classpath dep/orange-extensions.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc -classpath dep/orange-extensions.jar -sourcepath src $(PACKAGES) -d doc

materials.pmz: src-materials bin
	mkdir -p "Paint Materials/Alphabets"
	mkdir -p "Paint Materials/Brushes"
	mkdir -p "Paint Materials/Color Palettes"
	mkdir -p "Paint Materials/Color Profiles"
	mkdir -p "Paint Materials/Dither Algorithms"
	mkdir -p "Paint Materials/Font Collections"
	mkdir -p "Paint Materials/Frames"
	mkdir -p "Paint Materials/Gradients"
	mkdir -p "Paint Materials/Lines"
	mkdir -p "Paint Materials/Patterns"
	mkdir -p "Paint Materials/Rubber Stamps"
	mkdir -p "Paint Materials/Shapes"
	mkdir -p "Paint Materials/Textures"
	cp -Rf src-materials/alpx/*.alpx "Paint Materials/Alphabets"
	cp -Rf src-materials/ditx/*.ditx "Paint Materials/Dither Algorithms"
	cp -Rf src-materials/frnx/*.frnd "Paint Materials/Frames"
	cp -Rf src-materials/grdx/*.grdx "Paint Materials/Gradients"
	cp -Rf src-materials/lnsx/*.lnsx "Paint Materials/Lines"
	cp -Rf src-materials/patx/*.patx "Paint Materials/Patterns"
	cp -Rf src-materials/rcpx/*.rcpx "Paint Materials/Color Palettes"
	cp -Rf src-materials/rfpx/*.rfpx "Paint Materials/Font Collections"
	cp -Rf src-materials/shpx/*.shpx "Paint Materials/Shapes"
	cp -Rf src-materials/spnx/0*.spnd "Paint Materials/Brushes"
	cp -Rf src-materials/spnx/1*.spnd "Paint Materials/Brushes"
	cp -Rf src-materials/spnx/2*.spnd "Paint Materials/Rubber Stamps"
	cp -Rf src-materials/txrx/*.txrd "Paint Materials/Textures"
	cp -f src/com/kreative/paint/material/colorpalette/ISOcoated_v2_300_eci.icc "Paint Materials/Color Profiles/CMYK ISO Coated v2 300%.icc"
	java -cp ./bin com.kreative.paint.material.MaterialPacker pack materials.pmz "Paint Materials"/*
	cp -f materials.pmz src/com/kreative/paint/ui/materials.pmz
	cp -f materials.pmz bin/com/kreative/paint/ui/materials.pmz

PowerPaint.jar: bin materials.pmz
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF PowerPaint.jar -C bin com/kreative/paint

PowerPaint.app: dep PowerPaint.jar
	mkdir -p PowerPaint.app/Contents/MacOS
	mkdir -p PowerPaint.app/Contents/Resources/Java
	cp -f dep/PkgInfo PowerPaint.app/Contents
	cp -f dep/Info.plist PowerPaint.app/Contents
	cp -f dep/universalJavaApplicationStub PowerPaint.app/Contents/MacOS/PowerPaint
	cp -f dep/*.icns PowerPaint.app/Contents/Resources
	cp -f PowerPaint.jar PowerPaint.app/Contents/Resources/Java

PowerPaint.exe: dep PowerPaint.jar
ifeq ($(LAUNCH4J_HOME),)
	echo 'Please set $$LAUNCH4J_HOME environment variable to compile exe.'
else
	cd "$(LAUNCH4J_HOME)" ; java -Dlaunch4j.tmpdir=/tmp/ -jar "$(LAUNCH4J_HOME)/launch4j.jar" "$(PWD)/dep/PowerPaint.xml"
endif

PowerPaint-src.tgz: src src-materials
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	find src-materials -name .DS_Store -delete
	tar -czf PowerPaint-src.tgz src/com/kreative/paint src-materials LICENSE

run: bin materials.pmz
	java -Xmx1024M -cp ./bin com.kreative.paint.ui.Main

install: PowerPaint.jar
	sudo mkdir -p /usr/local/lib/
	sudo cp -f PowerPaint.jar /usr/local/lib/powerpaint.jar
	sudo chmod -f 644 /usr/local/lib/powerpaint.jar
	sudo mkdir -p /usr/local/bin/
	sudo cp -f dep/powerpaint /usr/local/bin/powerpaint
	sudo chmod -f 755 /usr/local/bin/powerpaint
	sudo mkdir -p /usr/share/icons/hicolor/128x128/apps/
	sudo cp -f dep/powerpaint.png /usr/share/icons/hicolor/128x128/apps/powerpaint.png
	sudo chmod -f 644 /usr/share/icons/hicolor/128x128/apps/powerpaint.png
	sudo mkdir -p /usr/share/applications/
	sudo cp -f dep/powerpaint.desktop /usr/share/applications/powerpaint.desktop
	sudo chmod -f 644 /usr/share/applications/powerpaint.desktop

.PHONY: all eclipseall clean eclipseclean run install
