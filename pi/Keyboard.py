from PyQt5.QtCore import Qt, QMargins, QSize
from PyQt5.QtWidgets import QFrame, QHBoxLayout, QVBoxLayout, QPushButton, QLineEdit


from Widgets import Page
from Fonts import FONT_LARGE, FONT_LARGE_ITALIC


class Keyboard(Page):
    def __init__(self):
        super().__init__()

        self.target = None

        self.vbox = QVBoxLayout()
        self.setLayout(self.vbox)

        self.inputRow = InputRow()
        self.vbox.addWidget(self.inputRow)

        self.hbox0 = KeyboardRow(self)
        self.hbox1 = KeyboardRow(self)
        self.hbox2 = KeyboardRow(self)
        self.hbox3 = KeyboardRow(self)
        self.hbox4 = KeyboardRow(self)
        self.vbox.addWidget(self.hbox0)
        self.vbox.addWidget(self.hbox1)
        self.vbox.addWidget(self.hbox2)
        self.vbox.addWidget(self.hbox3)
        self.vbox.addWidget(self.hbox4)

        for key, shift in zip('1234567890-=', '!@#$%^&*()_+'):
            self.hbox0.addKey(key, shift)

        for key, shift in zip('qwertyuiop[]', 'QWERTYUIOP{}'):
            self.hbox1.addKey(key, shift)

        for key, shift in zip("asdfghjkl;'", 'ASDFGHJKL:"'):
            self.hbox2.addKey(key, shift)

        self.shift = False
        self.initShiftBt()

        for key, shift in zip('zxcvbnm,./', 'ZXCVBNM<>?'):
            self.hbox3.addKey(key, shift)

        self.hbox4.hbox.addSpacing(155)
        self.initSpaceBt()
        self.initDelete()
        self.initOkBt()
        self.initBackBt()

    def popup(self, target=None, finishEvent=None):
        if target:
            self.target = target
            self.inputRow.setText(target.text())
        if finishEvent:
            self.finishEvent = finishEvent

        self.showFullScreen()

    def initShiftBt(self):
        self.shiftBt = QPushButton()
        self.shiftBt.setText('Shift')
        self.shiftBt.setFont(FONT_LARGE)
        self.shiftBt.setFixedSize(QSize(75, 55))
        self.shiftBt.clicked.connect(self.shiftPress)
        self.hbox3.addWidget(self.shiftBt)

    def shiftPress(self):
        if self.shift:
            self.shift = False
            self.shiftBt.setFlat(False)
            self.shiftBt.setFont(FONT_LARGE)
            self.hbox0.unshift()
            self.hbox1.unshift()
            self.hbox2.unshift()
            self.hbox3.unshift()
        else:
            self.shift = True
            self.shiftBt.setFlat(True)
            self.shiftBt.setFont(FONT_LARGE_ITALIC)
            self.hbox0.shift()
            self.hbox1.shift()
            self.hbox2.shift()
            self.hbox3.shift()

    def initOkBt(self):
        self.okBt = QPushButton()
        self.okBt.setText('确认')
        self.okBt.setFont(FONT_LARGE)
        self.okBt.setFixedSize(QSize(100, 55))
        self.okBt.clicked.connect(self.okPress)
        self.hbox4.addWidget(self.okBt)

    def okPress(self):
        self.hide()
        text = self.inputRow.text()
        self.inputRow.setText('')

        if self.target:
            self.target.setText(text)
            self.target = None
        if self.finishEvent:
            self.finishEvent(text)
            self.finishEvent = None


    def initBackBt(self):
        self.backBt = QPushButton()
        self.backBt.setText('返回')
        self.backBt.setFont(FONT_LARGE)
        self.backBt.setFixedSize(QSize(100, 55))
        self.backBt.clicked.connect(self.back)
        self.hbox4.addWidget(self.backBt)

    def initSpaceBt(self):
        self.spaceBt = QPushButton()
        self.spaceBt.setText('Space')
        self.spaceBt.setFont(FONT_LARGE)
        self.spaceBt.setFixedSize(QSize(300, 55))
        self.spaceBt.clicked.connect(self.spacePress)
        self.hbox4.addWidget(self.spaceBt)

    def spacePress(self):
        text = self.inputRow.text()
        self.inputRow.setText(text + ' ')

    def initDelete(self):
        self.deleteBt = QPushButton()
        self.deleteBt.setText('Del')
        self.deleteBt.setFont(FONT_LARGE)
        self.deleteBt.setFixedSize(QSize(100, 55))
        self.deleteBt.clicked.connect(self.deletePress)
        self.hbox4.addWidget(self.deleteBt)

    def deletePress(self):
        text = self.inputRow.text()
        self.inputRow.setText(text[:-1])



class InputRow(QLineEdit):
    def __init__(self):
        super().__init__()
        self.setFixedHeight(70)
        self.setFont(FONT_LARGE)
        self.setStyleSheet('padding: 0 15px')
        self.setCursor(Qt.BlankCursor)


class KeyboardRow(QFrame):
    def __init__(self, root):
        super().__init__()
        self.root = root
        self.hbox = QHBoxLayout()
        self.hbox.setContentsMargins(QMargins(0, 0, 0, 0))
        self.hbox.setContentsMargins(QMargins(0, 0, 0, 0))
        self.setLayout(self.hbox)

        self.buttons = []

    def addKey(self, key, shift, width=55):
        button = Key(self.root, key, shift, width)
        self.hbox.addWidget(button)
        self.buttons.append(button)

    def addWidget(self, widget):
        self.hbox.addWidget(widget)

    def shift(self):
        for bt in self.buttons:
            bt.setText(bt.shift)

    def unshift(self):
        for bt in self.buttons:
            bt.setText(bt.key)


class Key(QPushButton):
    def __init__(self, root, key, shift, width):
        super().__init__()
        self.root = root
        self.key = key
        self.shift = shift
        self.setText(key)
        self.setFont(FONT_LARGE)
        self.setFixedSize(QSize(width, 55))

    def mouseReleaseEvent(self, event):
        super().mouseReleaseEvent(event)
        text = self.root.inputRow.text()
        self.root.inputRow.setText(text + self.text())


keyboard = Keyboard()
