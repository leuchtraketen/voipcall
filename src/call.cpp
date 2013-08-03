#include <windows.h>
#include <iostream>
#include <cstdlib>
using namespace std;
int main()
{
	system("java -Xmx64m -Xms64m -cp bin;. update.Main");
	WinExec("java -cp bin;. call.gui.Main", SW_HIDE);
}
