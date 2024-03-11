@echo off
echo removing temp files ...
cd /d bin
for /F "delims=" %%i in ('dir /b') do (rmdir "%%i" /s/q || del "%%i" /s/q)
cd ../
del linkserver.jar
echo compiling...
javac -cp src src/Main.java -d ./bin
echo creating executable jar...
jar -cvfm linkserver.jar manifest.txt -C bin . lib
md install\resources
copy linkserver.jar install
copy resources .\install\resources
echo creating installer ....
jpackage -t "msi" --win-dir-chooser --win-menu --win-per-user-install --win-shortcut --icon resources/stack.ico --name linkServer --input install --main-jar linkserver.jar --main-class Main
rmdir /s /q install