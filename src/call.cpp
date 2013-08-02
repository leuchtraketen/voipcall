#include <windows.h>
#include <iostream>
#include <cstdlib>
using namespace std;
int main()
{
	WinExec("java -cp bin;. call.TestGui", SW_HIDE);
}
