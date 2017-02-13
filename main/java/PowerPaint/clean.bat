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
