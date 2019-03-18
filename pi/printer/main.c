#include "libs/printerlibs.h"
#include <stdio.h>

int main(int argc, char *argv[])
{

	void *h = Port_OpenUsb("/dev/usb/lp0");
	if (h)
	{
		{
			char line[48] = "";
			sprintf(line, "检测时间: %s %s\r\n", argv[1], argv[2]);
			POS_PrintUTF8String(h, line, 0, 0, 0, 0, 0);
		}
		POS_PrintUTF8String(h, "序号··抑制率········备注········\r\n", 0, 0, 0, 0, 0);
		for (int i = 1; i <= 8; i++)
		{
			char line[32] = "";
			sprintf(line, "· %i   %s %%\r\n", i, argv[i + 2]);
			POS_PrintUTF8String(h, line, 0, 0, 0, 0, 0);
		}
		POS_PrintUTF8String(h, "\r\n\r\n\r\n", 0, 0, 0, 0, 0);

		Port_Close(h);
	}
	else
	{
		printf("not connect.\n");
	}
	return 0;
}
