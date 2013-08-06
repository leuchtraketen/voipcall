rm -f call.exe
i586-mingw32msvc-c++ -mwindows -o call.exe src/call.cpp -Wl,--subsystem,windows
rm -f compiled.zip ; zip -r compiled.zip bin/ call.exe call.sh src/
rm -f dependencies.zip ; zip -r dependencies.zip img/ lib/

#find src/ -exec git add '{}' \;
git commit -a
git push origin master
