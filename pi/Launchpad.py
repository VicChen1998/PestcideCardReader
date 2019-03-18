import sys

from PyQt5.QtWidgets import QWidget, QFrame, QLabel
from PyQt5.QtWidgets import QGridLayout, QVBoxLayout
from PyQt5.QtGui import QPixmap
from PyQt5.QtCore import Qt

from Fonts import FONT_LARGE, FONT_MID
from AnalysePage import analysePage
from RecordPage import recordPage
from SettingsPage import settingsPage
from AboutPage import aboutPage


class Launchpad(QWidget):
    def __init__(self):
        super().__init__()

        grid = QGridLayout()
        grid.setSpacing(0)

        cwd = sys.path[0]

        grid.addWidget(LaunchButton(
            '开始检测', cwd + '/image/start.png', analysePage), 0, 0)
        grid.addWidget(LaunchButton(
            '检测记录', cwd + '/image/record.png', recordPage), 0, 1)
        grid.addWidget(LaunchButton(
            '系统设置', cwd + '/image/settings.png', settingsPage), 0, 2)
        grid.addWidget(LaunchButton('关于', cwd + '/image/about.png', aboutPage), 1, 0)

        self.setLayout(grid)
        self.setCursor(Qt.BlankCursor)


class LaunchButton(QFrame):
    def __init__(self, title, img_src, targetPage):
        super().__init__()

        self.targetPage = targetPage

        vbox = QVBoxLayout()
        vbox.setAlignment(Qt.AlignCenter)

        icon = QPixmap(img_src)
        icon_lb = QLabel()
        icon_lb.setPixmap(icon)
        vbox.addWidget(icon_lb)

        title_lb = QLabel()
        title_lb.setText(title)
        title_lb.setAlignment(Qt.AlignCenter)
        title_lb.setFont(FONT_LARGE)
        vbox.addWidget(title_lb)

        self.setLayout(vbox)

    def mouseReleaseEvent(self, e):
        if e.button() == Qt.LeftButton:
            self.targetPage.showFullScreen()
            # self.targetPage.show()
