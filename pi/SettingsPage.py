import sys

from PyQt5.QtWidgets import QVBoxLayout, QPushButton

from Widgets import Page
from Fonts import FONT_LARGE

from WifiPage import WifiListPage


class SettingsPage(Page):
    def __init__(self):
        super().__init__()

        self.vbox = QVBoxLayout()
        self.setLayout(self.vbox)
        self.addButton('连接wifi以启用局域网共享', self.wifi)
        self.addButton('返回', self.back)
        self.addButton('[Dev Mode - Quit]', self.quit)
        self.vbox.addStretch()

    def addButton(self, title, event):
        button = QPushButton()
        button.setText(title)
        button.setFont(FONT_LARGE)
        button.setMinimumHeight(75)
        button.clicked.connect(event)
        self.vbox.addWidget(button)

    def quit(self):
        sys.exit()

    def wifi(self):
        self.wifiListPage = WifiListPage()
        self.wifiListPage.showFullScreen()

settingsPage = SettingsPage()
