echo -n $[$(cat .version||echo 0)+1] > .version
rm -f call.exe ; i586-mingw32msvc-c++ -mwindows -o call.exe src/call.cpp -Wl,--subsystem,windows ; i586-mingw32msvc-strip call.exe
rm -f compiled.zip ; zip -r compiled.zip bin/ src/ .version call.sh call.exe
rm -f dependencies.zip ; zip -r dependencies.zip img/ lib/

find src/ -exec git add '{}' \;
git commit -a
git push origin master
