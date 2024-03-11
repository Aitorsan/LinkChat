@echo off
cd /d bin
for /F "delims=" %%i in ('dir /b') do (rmdir "%%i" /s/q || del "%%i" /s/q)
cd ../
del linkchat.jar
javac -cp src src/LinkApp.java -d ./bin
jar -cvfm linkchat.jar resources/manifest.txt -C bin . lib
md install\resources
copy linkchat.jar install
copy resources install\resources
echo creating installer ...
jpackage -t "msi" --win-dir-chooser --win-menu --win-per-user-install --win-shortcut --icon ./resources/messages-icon.ico --name linkChat --input install --main-jar linkchat.jar --main-class LinkApp
rmdir /s /q install