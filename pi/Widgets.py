import sys
from PyQt5.QtCore import Qt, QSize
from PyQt5.QtGui import QPixmap
from PyQt5.QtWidgets import QWidget, QLabel, QTableWidget, QTableWidgetItem

from Fonts import FONT_MID


class Page(QWidget):
    '''
    Base page template
    with a default back function
    '''

    def __init__(self):
        super().__init__()
        self.resize(800, 480)
        self.setCursor(Qt.BlankCursor)

    def back(self):
        self.hide()


class BoardImageView(QLabel):
    '''
    Board Image Widget
    '''

    def __init__(self):
        super().__init__()

        self.setAlignment(Qt.AlignCenter)
        self.setImage(QPixmap(sys.path[0] + '/image/sketch.jpg'))

    def setImage(self, image: QPixmap):
        ''' resize image to fixed size and show '''
        image = image.scaledToHeight(420)
        self.setPixmap(image)
        # call repaint() to force refresh interface
        self.repaint()


class Table(QTableWidget):
    def __init__(self, row: int = 0, col: int = 0, size: QSize = None):
        super().__init__()
        self.setRowCount(row)
        self.setColumnCount(col)
        if size:
            self.setFixedSize(size)

    def setRowTitle(self, index: int, title: str, size: QSize = None):
        ''' set row title of result table '''
        titleItem = QTableWidgetItem(title)
        titleItem.setFont(FONT_MID)
        if size:
            titleItem.setSizeHint(size)
        self.setVerticalHeaderItem(index, titleItem)

    def setColTitle(self, index: int, title: str, size: QSize = None):
        ''' set col title of result table '''
        titleItem = QTableWidgetItem(title)
        titleItem.setFont(FONT_MID)
        titleItem.setSizeHint(QSize(0, 50))
        if size:
            titleItem.setSizeHint(size)
        self.setHorizontalHeaderItem(index, titleItem)
