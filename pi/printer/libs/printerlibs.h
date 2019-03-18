#ifndef PRINTERLIBS_LINUX_H
#define PRINTERLIBS_LINUX_H

#ifdef __cplusplus
extern "C" {
#endif

#define HORIZONTALALIGNMENT_LEFT		-1
#define HORIZONTALALIGNMENT_CENTER		-2
#define HORIZONTALALIGNMENT_RIGHT		-3
#define ALIGN_HLEFT						HORIZONTALALIGNMENT_LEFT
#define ALIGN_HCENTER					HORIZONTALALIGNMENT_CENTER
#define ALIGN_HRIGHT					HORIZONTALALIGNMENT_RIGHT

#define POS_BARCODE_TYPE_UPCA		0x41
#define POS_BARCODE_TYPE_UPCE		0x42
#define POS_BARCODE_TYPE_EAN13		0x43
#define POS_BARCODE_TYPE_EAN8		0x44
#define POS_BARCODE_TYPE_CODE39		0x45
#define POS_BARCODE_TYPE_ITF		0x46
#define POS_BARCODE_TYPE_CODABAR	0x47
#define POS_BARCODE_TYPE_CODE93		0x48
#define POS_BARCODE_TYPE_CODE128	0x49

#define ENCODING_GBK		0
#define ENCODING_UTF8		1
#define ENCODING_BIG5		3
#define ENCODING_SHIFTJIS	4
#define ENCODING_EUCKR		5

#define BINARYALGORITHM_DITHER		0
#define BINARYALGORITHM_THRESHOLD	1

#define COMPRESSMETHOD_NONE			0
#define COMPRESSMETHOD_COMPRESS		1

/**端口函数****************************************************************************/
// 返回端口句柄。非零表示打开成功，零表示打开失败。
void *Port_OpenCom(const char * pName, int dwBaudrate);
void *Port_OpenTcp(const char * szIp, unsigned short nPort);
void *Port_OpenUsb(const char * pName);
void *Port_OpenLpt(const char * pName);
// 返回写入的字节数，返回-1表示写入失败
int Port_Write(void *handle, const void *buffer, unsigned int count, unsigned int timeout);
// 返回读取的字节数，返回-1表示读取失败
int Port_Read(void *handle, void *buffer, unsigned int count, unsigned int timeout);
void Port_Close(void *handle);

/**页模式打印函数****************************************************************************/
bool PAGE_PageEnter(void *handle);
bool PAGE_PagePrint(void *handle);
bool PAGE_PageExit(void *handle);
bool PAGE_SetPrintArea(void *handle, int left, int top, int right, int bottom, int direction);
bool PAGE_DrawString(void *handle, const char *pszString, int x, int y, int nWidthScale, int nHeightScale, int nFontType, int nFontStyle);
bool PAGE_DrawUTF8String(void *handle, const char *pszString, int x, int y, int nWidthScale, int nHeightScale, int nFontType, int nFontStyle);
bool PAGE_DrawRect(void *handle, int x, int y, int nWidth, int nHeight, int nColor);
bool PAGE_DrawBarcode(void *handle, const char *pszBarcodeContent, int x, int y, int nBarcodeUnitWidth, int nBarcodeHeight, int nHriFontType, int nHriFontPosition, int nBarcodeType);
bool PAGE_DrawQRCode(void *handle, const char *pszContent, int x, int y, int nQRCodeUnitWidth, int nVersion, int nEcLevel);
bool PAGE_DrawImage(void *handle, bool *img, int x, int y, int imgw, int imgh);
bool PAGE_DrawBitmap(void *handle, const char *szFileName, int x, int y, int dstWidth, int dstHeight, int nBinaryAlgorithm);


/**热敏打印函数****************************************************************************/
// 打印
bool POS_PrintString(void *handle, const char * pszString, int x, int nWidthScale, int nHeightScale, int nFontType, int nFontStyle); // 打印文本
bool POS_PrintUTF8String(void *handle, const char * pszString, int x, int nWidthScale, int nHeightScale, int nFontType, int nFontStyle); // 打印文本
bool POS_SetBarcode(void *handle, const char * pszBarcodeContent, int x, int nBarcodeUnitWidth, int nBarcodeHeight, int nHriFontType, int nHriFontPosition, int nBarcodeType); // 打印条码
bool POS_SetQRCode(void *handle, const char * pszContent, int x, int nQRCodeUnitWidth, int nVersion, int nEcLevel);	//打印QR码
bool POS_PrintImage(void *handle, bool * img, int x, int imgw, int imgh, int nCompressMethod);	// 打印图片
bool POS_PrintPicture(void *handle, const char * szFileName, int x, int dstw, int dsth, int nBinaryAlgorithm, int nCompressMethod); // 打印bmp图片
bool POS_SelfTest(void *handle); // 打印自测页

// 进纸
bool POS_FeedLine(void *handle);
bool POS_FeedNLine(void *handle, int nLine);
bool POS_FeedNDot(void *handle, int nDot);

// 查询
bool POS_QueryStatus(void *handle, int type, char * status, unsigned int timeout); // 查询打印机状态
bool POS_RTQueryStatus(void *handle, int type, char * status, unsigned int timeout);	// 实时查询打印机状态
bool POS_TicketSucceed(void *handle, int dwSendIndex, unsigned int timeout); // 单据打印结果查询

// 设置
bool POS_SetMotionUnit(void *handle, int nHorizontal, int nVertical);
bool POS_SetLineHeight(void *handle, int nDistance);	// 设置行高
bool POS_SetRightSpacing(void *handle, int nDistance); // 设置字符右间距

                                               // 其他
bool POS_Reset(void *handle);	// 软件复位
bool POS_KickOutDrawer(void *handle, int nID, int nHighLevelTime, int nLowLevelTime); // 开钱箱
bool POS_FeedAndCutPaper(void *handle); // 进纸到切刀位置并切纸
bool POS_FullCutPaper(void *handle); // 直接全切
bool POS_HalfCutPaper(void *handle); // 直接半切
bool POS_Beep(void *handle, int nBeepCount, int nBeepMillis); // 蜂鸣器鸣叫 nBeepCount 鸣叫次数 nBeepMillis 每次鸣叫的时间 = 100 * nBeemMillis ms


/**标签打印函数****************************************************************************/
bool LABEL_PageBegin(void *handle, int startx, int starty, int width, int height, int rotate);
bool LABEL_PageEnd(void *handle);
bool LABEL_PagePrint(void *handle, int num);

bool LABEL_PageFeed(void *handle);
bool LABEL_PageCalibrate(void *handle);

bool LABEL_DrawLine(void *handle, int startx, int starty, int endx, int endy, int width, int color);
bool LABEL_DrawBox(void *handle, int left, int top, int right, int bottom, int borderwidth, int bordercolor);
bool LABEL_DrawPlainText(void *handle, int startx, int starty, int font, int style, const char *str);
bool LABEL_DrawRectangel(void *handle, int left, int top, int right, int bottom, int color);
bool LABEL_DrawBarcode(void *handle, int startx, int starty, int type, int height, int unitwidth, int rotate, const char *str);
bool LABEL_DrawQRCode(void *handle, int startx, int starty, int version, int ecc, int unitwidth, int rotate, const char *str);
bool LABEL_DrawPDF417(void *handle, int startx, int starty, int colnum, int lwratio, int ecc, int unitwidth, int rotate, const char *str);
bool LABEL_DrawImage(void *handle, int startx, int starty, int width, int height, int style, const bool *img);
bool LABEL_DrawBitmap(void *handle, int startx, int starty, int dstw, int dsth, int style, const char *pszFile, int nBinaryAlgorithm);

#ifdef __cplusplus
}
#endif

#endif // PRINTERLIBS_H
