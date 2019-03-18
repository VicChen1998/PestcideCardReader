import os
import time

from PyQt5.QtCore import Qt, QSize
from PyQt5.QtGui import QPixmap, QImage, QColor
from PyQt5.QtWidgets import QFrame, QLabel, QPushButton
from PyQt5.QtWidgets import QGridLayout, QVBoxLayout

from Fonts import FONT_LARGE, FONT_MID
from Widgets import *
from Models import Result
from Database import database
from RecordPage import recordPage


class AnalysePage(Page):
    '''
    @Singleton

    Contents:
        · 样本图像 sample image
        · 分析结果表格 result table
        · 按钮区域 buttons pane

    Analyse Process：
        · 拍摄样板照片
        · 按锚点裁剪出每个样本
        · 计算样本区域内蓝色通道平均值
        · 计算出样本抑制率
        · 结构化存储结果
        · 显示样板图像/每个样本图像/抑制率读数结果
        · [optional] 打印结果

    Others:
        · navigate to Record Page
        · navigate back to Launchpad
    '''

    def __init__(self):
        '''
        init UI use grid layout
            · left  : board image
            · mid   : result table
            · right : buttons pane

        init current analyse result as None
        '''
        super().__init__()

        self.grid = QGridLayout()
        self.setLayout(self.grid)

        self.initBoardImage()
        self.initResultTable()
        self.initButtonPane()

        # init current analyse result as None
        self.result = None

    def addPaneTitle(self, title, row, col):
        ''' add a pane title with consistent style at self.grid(row,col) '''
        label = QLabel()
        label.setText(title)
        label.setFont(FONT_MID)
        label.setAlignment(Qt.AlignCenter)
        self.grid.addWidget(label, row, col)

    ### Board Image Pane ###
    def initBoardImage(self):
        ''' init board image view with default sketch image '''
        self.addPaneTitle('检测图像', 0, 0)
        self.boardImageView = BoardImageView()
        self.grid.addWidget(self.boardImageView, 1, 0)

    def setBoardImage(self, image):
        self.boardImageView.setImage(image)

    ### Result Table Pane ###
    def initResultTable(self):
        ''' init result table view and table items '''
        self.addPaneTitle('检测结果', 0, 1)
        # init table
        self.resultTable = Table(8, 3, QSize(419, 420))
        # init col title and set col width
        self.resultTable.setColTitle(0, '图像')
        self.resultTable.setColTitle(1, '抑制率')
        self.resultTable.setColTitle(2, '备注')
        self.resultTable.setColumnWidth(0, 50)
        self.resultTable.setColumnWidth(1, 120)
        self.resultTable.setColumnWidth(2, 200)
        # init row title and height
        for i in range(8):
            self.resultTable.setRowTitle(i, '  ' + str(i+1) + '  ')
            self.resultTable.setRowHeight(i, 46)
        # init sample image view of each row
        for i in range(8):
            label = QLabel()
            label.setScaledContents(True)
            self.resultTable.setCellWidget(i, 0, label)
        # init reading view of each row
        for i in range(8):
            label = QLabel()
            label.setAlignment(Qt.AlignCenter)
            label.setFont(FONT_MID)
            self.resultTable.setCellWidget(i, 1, label)
        # add table to grid
        self.grid.addWidget(self.resultTable, 1, 1)

    def setResult(self, result):
        ''' 
        set result to each pane after analyse:
            · receive a structured analyse result
            · set it as self current result
            · set board image
            · set each sample's image and reading to result table
        '''
        self.result = result
        self.setBoardImage(QPixmap(result.imgSrc))
        for i in range(8):
            self.setSampleImage(i, result.images[i])
            self.setSampleReading(i, result.values[i])

    def setSampleImage(self, index, image):
        ''' set sample image at result table row(index)'''
        label = self.resultTable.cellWidget(index, 0)
        label.setPixmap(image)

    def setSampleReading(self, index, reading):
        ''' set sample reading at result table row(index) '''
        reading = 100.0 if reading > 100 else round(reading, 1)
        label = self.resultTable.cellWidget(index, 1)
        label.setText('%6.2f %%' % reading)


    ### Button Pane ###
    def initButtonPane(self):
        ''' init buttons pane view and add buttons to it '''
        self.buttonsVbox = QVBoxLayout()
        buttonsPane = QFrame()
        buttonsPane.setLayout(self.buttonsVbox)
        self.grid.addWidget(buttonsPane, 0, 2, 3, 1, Qt.AlignRight)
        self.addButton('检测样品', self.analyse)
        self.addButton('打印结果', self.print)
        self.addButton('检测记录', self.toRecordPage)
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

    ### Functions ###
    def analyse(self):
        ''' 
        main function of Analyse Page
            · take photo of board
            · cut out each sample sub image
            · count average blue channel value
            · count suppression rate
            · save as structured Result class
            · call setResult() to show
        '''
        # init a empty Result object
        result = Result()
        # record current time
        result.time = time.strftime(
            '%Y.%m.%d %H:%M:%S', time.localtime(time.time()))
        # take a photo of board and get photo path
        result.imgSrc = self.takePhoto()
        # read the photo
        image = QImage(result.imgSrc)
        # resize to height = 480
        image = image.scaledToHeight(480)

        # set anchors
        anchor_x = [0.17, 0.17, 0.17, 0.17, 0.74, 0.74, 0.74, 0.74]
        anchor_y = [0.125, 0.265, 0.405, 0.555, 0.125, 0.265, 0.405, 0.555]

        result.values = []
        result.images = []
        for i in range(8):
            # cut each sample from board photo
            sample = image.copy(anchor_x[i] * image.width(),
                                anchor_y[i] * image.height(),
                                image.width() * 0.10,
                                image.height() * 0.045)
            result.images.append(QPixmap.fromImage(sample))
            # collect blue channel of each pixel
            blue_sum = 0
            for w in range(sample.width()):
                for h in range(sample.height()):
                    blue_sum += QColor(sample.pixel(w, h)).blue()
            # count average blue channel
            blue_avg = blue_sum / (sample.width() * sample.height())
            # count suppression rate
            # less blue -> more yellow -> less suppression
            percent = blue_avg * (100 / 180)
            # store suppression percentage
            result.values.append(percent)

        self.setResult(result)
        database.save(result)

    def takePhoto(self):
        ''' take a photo of board '''
        # TODO: implement
        import random
        rand_index = random.randint(1, 18)
        return sys.path[0] + '/record/' + str(rand_index) + '.jpg'

    def print(self):
        ''' 
        print analyse result
            · call compiled binary C program
            · sent serialized result data
            · connect usb printer and print
        '''

        # check if have current result
        if not self.result:
            return

        # cat print command
        cmd = 'sudo ' + sys.path[0] + '/print ' + self.result.time

        for i in range(8):
            percent = self.result.values[i]
            percent = 100.0 if percent > 100 else round(percent, 1)
            cmd += ' ' + str(percent)

        # run print command
        os.system(cmd)

    def toRecordPage(self):
        recordPage.showFullScreen()


# singleton pattern
analysePage = AnalysePage()
