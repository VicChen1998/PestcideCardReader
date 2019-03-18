.SUFFIXES : .x .o .c .s

TARGET 	= main
SRCS 	= main.c
LIBS	= -lPrinterLibs -L./libs/raspberrypi/arm-rpi-4.9.3-linux-gnueabihf -static

all: 
	$(CXX) $(SRCS) $(LIBS) -o $(TARGET)

clean:
	rm -f *.o 
	rm -f *.x 
	rm -f *.flat
	rm -f *.map
	rm -f temp
	rm -f *.img
	rm -f $(TARGET)	
	rm -f *.gdb
	rm -f *.bak
