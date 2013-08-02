rm -f call.exe
i586-mingw32msvc-c++ -mwindows -o call.exe src/call.cpp -Wl,--subsystem,windows
rm -f compiled.zip ; zip -r compiled.zip bin/ call.exe src/ img/ lib/

