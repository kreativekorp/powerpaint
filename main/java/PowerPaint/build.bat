rem make clean

rmdir /S /Q bin
rmdir /S /Q doc
rmdir /S /Q "Paint Materials"
rmdir /S /Q PowerPaint.app
del /F /Q materials.pmz
del /F /Q src\com\kreative\paint\ui\materials.pmz
del /F /Q bin\com\kreative\paint\ui\materials.pmz
del /F /Q PowerPaint*.jar
del /F /Q PowerPaint*.exe
del /F /Q PowerPaint*.tgz

rem make bin

mkdir bin
dir /S /B src\*.java | find /V "\ui\mac\" > sources.txt
javac -sourcepath src @sources.txt -d bin
del /F /Q sources.txt
xcopy /S src\*.png bin\*.png
xcopy /S src\*.dtd bin\*.dtd

rem make materials.pmz

mkdir "Paint Materials"
mkdir "Paint Materials\Alphabets"
mkdir "Paint Materials\Brushes"
mkdir "Paint Materials\Color Palettes"
mkdir "Paint Materials\Dither Algorithms"
mkdir "Paint Materials\Font Collections"
mkdir "Paint Materials\Frames"
mkdir "Paint Materials\Gradients"
mkdir "Paint Materials\Lines"
mkdir "Paint Materials\Patterns"
mkdir "Paint Materials\Rubber Stamps"
mkdir "Paint Materials\Shapes"
mkdir "Paint Materials\Textures"
xcopy /S src-materials\alpx\*.* "Paint Materials\Alphabets\"
xcopy /S src-materials\ditx\*.* "Paint Materials\Dither Algorithms\"
xcopy /S src-materials\frnx\*.* "Paint Materials\Frames\"
xcopy /S src-materials\grdx\*.* "Paint Materials\Gradients\"
xcopy /S src-materials\lnsx\*.* "Paint Materials\Lines\"
xcopy /S src-materials\patx\*.* "Paint Materials\Patterns\"
xcopy /S src-materials\rcpx\*.* "Paint Materials\Color Palettes\"
xcopy /S src-materials\rfpx\*.* "Paint Materials\Font Collections\"
xcopy /S src-materials\shpx\*.* "Paint Materials\Shapes\"
xcopy /S src-materials\txrx\*.* "Paint Materials\Textures\"
mkdir "Paint Materials\Brushes\00Brushes.spnd"
mkdir "Paint Materials\Brushes\01Calligraphy.spnd"
mkdir "Paint Materials\Brushes\02Charcoal.spnd"
mkdir "Paint Materials\Brushes\10Sprinkles.spnd"
mkdir "Paint Materials\Brushes\11ExtraSprinkles.spnd"
mkdir "Paint Materials\Rubber Stamps\20EmojiOne.spnd"
xcopy /S src-materials\spnx\00Brushes.spnd\*.* "Paint Materials\Brushes\00Brushes.spnd\"
xcopy /S src-materials\spnx\01Calligraphy.spnd\*.* "Paint Materials\Brushes\01Calligraphy.spnd\"
xcopy /S src-materials\spnx\02Charcoal.spnd\*.* "Paint Materials\Brushes\02Charcoal.spnd\"
xcopy /S src-materials\spnx\10Sprinkles.spnd\*.* "Paint Materials\Brushes\10Sprinkles.spnd\"
xcopy /S src-materials\spnx\11ExtraSprinkles.spnd\*.* "Paint Materials\Brushes\11ExtraSprinkles.spnd\"
xcopy /S src-materials\spnx\20EmojiOne.spnd\*.* "Paint Materials\Rubber Stamps\20EmojiOne.spnd\"
del /F /S /Q "Paint Materials\*.dtd"
java -cp bin com.kreative.paint.material.MaterialPacker pack materials.pmz "Paint Materials"\*
xcopy materials.pmz src\com\kreative\paint\ui\
xcopy materials.pmz bin\com\kreative\paint\ui\

rem make PowerPaint.jar

jar cmf dep\MANIFEST.MF PowerPaint.jar -C bin com\kreative\paint
