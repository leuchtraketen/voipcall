#include <windows.h>
#include <iostream>
#include <cstdlib>
using namespace std;
int main()
{
	system("java -Xmx64m -Xms64m -cp bin;. update.Main");
	WinExec("java -cp bin;lib/commons-lang3-3.1.jar call.gui.Main", SW_HIDE);
}
