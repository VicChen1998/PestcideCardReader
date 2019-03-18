from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QHBoxLayout, QLabel

from Widgets import Page
from Fonts import FONT_MAX

class AboutPage(Page):
    def __init__(self):
        super().__init__()
        self.hbox = QHBoxLayout()
        self.setLayout(self.hbox)

        self.label = QLabel()
        self.label.setText('农药残留检测仪')
        self.label.setFont(FONT_MAX)
        self.label.setAlignment(Qt.AlignCenter)
        self.hbox.addWidget(self.label)

    def mouseReleaseEvent(self, event):
        self.hide()



aboutPage = AboutPage()