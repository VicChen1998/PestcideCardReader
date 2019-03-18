import os
import sys
import math

from PyQt5.QtWidgets import QGridLayout, QHBoxLayout, QVBoxLayout, QFrame, QPushButton
from PyQt5.QtCore import Qt, QSize

from Fonts import FONT_LARGE, FONT_MID
from Widgets import *
from Models import Result
from Database import database


class RecordPage(Page):
    def __init__(self):
        super().__init__()
        self.current_record = None

        self.grid = QGridLayout()

        self.initBoardImage()
        self.initResultTable()
        self.initRecordTable()
        self.initRecordTableHint()
        self.initButtonPane()

        self.readRecord(0)

        self.setLayout(self.grid)

    def showFullScreen(self):
        super().showFullScreen()
        self.readRecord(self.pageIndex)

    def addPaneTitle(self, title, row, col):
        ''' add a pane title with consistent style at self.grid(row,col) '''
        label = QLabel()
        label.setText(title)
        label.setFont(FONT_MID)
        label.setAlignment(Qt.AlignCenter)
        self.grid.addWidget(label, row, col)

    def initBoardImage(self):
        ''' init board image view with default sketch image '''
        self.addPaneTitle('检测图像', 0, 0)
        self.boardImageView = BoardImageView()
        self.grid.addWidget(self.boardImageView, 1, 0, 2, 1)

    def initResultTable(self):
        self.addPaneTitle('检测结果', 0, 1)
        self.resultTable = Table(8, 1, QSize(160, 362))
        self.resultTable.setColTitle(0, '抑制率', QSize(0, 40))
        self.resultTable.setColumnWidth(0, 140)

        for i in range(8):
            self.resultTable.setRowTitle(i, ' ' + str(i+1) + ' ')
            self.resultTable.setRowHeight(i, 40)

        for i in range(8):
            label = QLabel()
            label.setAlignment(Qt.AlignCenter)
            label.setFont(FONT_MID)
            self.resultTable.setCellWidget(i, 0, label)

        self.grid.addWidget(self.resultTable, 1, 1)

    def setCurrentRecord(self, record: Result):
        self.current_record = record
        if record:
            self.boardImageView.setImage(QPixmap(record.imgSrc))
            for i in range(8):
                label = self.resultTable.cellWidget(i, 0)
                label.setText('%6.2f %% ' % record.values[i])
        else:
            self.boardImageView.setImage(QPixmap(sys.path[0] + '/image/sketch.jpg'))
            for i in range(8):
                label = self.resultTable.cellWidget(i, 0)
                label.setText('')

    def initRecordTable(self):
        self.addPaneTitle('检测记录', 0, 2)
        self.recordTable = Table(10, 1, QSize(260, 362))

        self.recordTable.setColTitle(0, '时间')
        self.recordTable.setColumnWidth(0, 258)

        for i in range(10):
            self.recordTable.setRowTitle(i, '')
            self.recordTable.setRowHeight(i, 31)

        for i in range(10):
            label = RecordTableRow(self)
            label.setAlignment(Qt.AlignCenter)
            label.setFont(FONT_MID)
            self.recordTable.setCellWidget(i, 0, label)

        self.grid.addWidget(self.recordTable, 1, 2)

    def setRecordTableData(self, index, record):
        label = self.recordTable.cellWidget(index, 0)
        label.setRecord(Result.fromTupple(record))

    def initRecordTableHint(self):
        self.tableHint = QFrame()
        self.tableHint.hbox = QHBoxLayout()
        self.tableHint.setLayout(self.tableHint.hbox)
        self.grid.addWidget(self.tableHint, 2, 1, 1, 2)

        self.prePageBt = QPushButton()
        self.prePageBt.setFont(FONT_MID)
        self.prePageBt.setMinimumHeight(50)
        self.prePageBt.setText('<')
        self.prePageBt.clicked.connect(self.prePage)

        self.nextPageBt = QPushButton()
        self.nextPageBt.setFont(FONT_MID)
        self.nextPageBt.setMinimumHeight(50)
        self.nextPageBt.setText('>')
        self.nextPageBt.clicked.connect(self.nextPage)

        self.hintLabel = QLabel()
        self.hintLabel.setFont(FONT_MID)

        self.recordCount = 0
        self.pageCount = 0
        self.pageIndex = 0

        self.tableHint.hbox.addWidget(self.prePageBt)
        self.tableHint.hbox.addStretch()
        self.tableHint.hbox.addWidget(self.hintLabel)
        self.tableHint.hbox.addStretch()
        self.tableHint.hbox.addWidget(self.nextPageBt)

    def refreshRecordTableHint(self):
        hintText = str(self.recordCount) + '条记录' + '  '
        hintText += str(self.pageIndex + 1) + '/'
        hintText += str(self.pageCount) + '页'

        self.hintLabel.setText(hintText)

    def initButtonPane(self):
        ''' init buttons pane view and add buttons to it '''
        self.buttonsVbox = QVBoxLayout()
        buttonsPane = QFrame()
        buttonsPane.setLayout(self.buttonsVbox)
        self.grid.addWidget(buttonsPane, 0, 3, 3, 1, Qt.AlignRight)

        self.addButton('打印', self.print)
        self.addButton('删除', self.delete)
        self.buttonsVbox.addStretch()
        self.addButton('返回', self.back)

    def addButton(self, title, event):
        '''
        add a button to buttons pane.\n
        must bind to a callable event.
        '''
        button = QPushButton()
        button.setText(title)
        button.setFont(FONT_LARGE)
        button.setMinimumWidth(150)
        button.setMinimumHeight(70)
        button.clicked.connect(event)
        self.buttonsVbox.addWidget(button)

    def readRecord(self, pageIndex, pageSize=10):

        self.recordCount = database.count()
        self.pageIndex = pageIndex
        self.pageCount = math.ceil(self.recordCount / pageSize)
        self.refreshRecordTableHint()

        records = database.read(pageIndex, pageSize)

        for i in range(10):
            if i < len(records):
                self.setRecordTableData(i, records[i])
            else:
                self.setRecordTableData(i, None)

    def prePage(self):
        if(self.pageIndex > 0):
            self.pageIndex -= 1
            self.readRecord(self.pageIndex)

    def nextPage(self):
        if(self.pageIndex + 1 < self.pageCount):
            self.pageIndex += 1
            self.readRecord(self.pageIndex)

    def print(self):
        if not self.current_record:
            return

        # cat print command
        cmd = 'sudo ' + sys.path[0] + '/print ' + self.current_record.time

        for i in range(8):
            percent = self.current_record.values[i]
            percent = 100.0 if percent > 100 else round(percent, 1)
            cmd += ' ' + str(percent)

        # run print command
        os.system(cmd)

    def delete(self):
        if not self.current_record:
            return

        database.delete(self.current_record.id)
        self.current_record = None
        self.readRecord(self.pageIndex)

        selectedIndexs = self.recordTable.selectedIndexes()
        if len(selectedIndexs) > 0:
            row = selectedIndexs[0].row()
            self.setCurrentRecord(self.recordTable.cellWidget(row, 0).record)


class RecordTableRow(QLabel):
    def __init__(self, root: RecordPage, record=None):
        super().__init__()
        self.record = record
        self.root = root

    def setRecord(self, record):
        if record:
            self.record = record
            self.setText(record.time)
        else:
            self.record = None
            self.setText('')

    def mouseReleaseEvent(self, event):
        super().mouseReleaseEvent(event)
        if self.record:
            self.root.setCurrentRecord(self.record)


recordPage = RecordPage()
