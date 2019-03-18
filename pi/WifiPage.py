import time
import sched
import threading
import pywifi

from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QVBoxLayout, QPushButton, QLabel


from Widgets import Page
from Fonts import FONT_LARGE

from Keyboard import keyboard


class WifiListPage(Page):
    def __init__(self):
        super().__init__()

        self.wifi = pywifi.PyWiFi()
        self.iface = self.wifi.interfaces()[1]

        self.vbox = QVBoxLayout()
        self.vbox.setAlignment(Qt.AlignCenter)
        self.setLayout(self.vbox)

        self.label = QLabel('正在搜索wifi...')
        self.label.setAlignment(Qt.AlignCenter)
        self.label.setFont(FONT_LARGE)
        self.vbox.addWidget(self.label)

        self.vbox.addStretch()
        self.backBt = QPushButton('返回')
        self.backBt.setFont(FONT_LARGE)
        self.backBt.setFixedHeight(50)
        self.backBt.clicked.connect(self.back)
        self.vbox.addWidget(self.backBt)

        self.scan()

    def scan(self):
        self.iface.scan()

        time.sleep(0.3)
        self.label.hide()

        count = 0
        for wifi in self.iface.scan_results():
            self.addWifiItem(wifi)

            count += 1
            if(count >= 8):
                return

    def addWifiItem(self, wifi):
        wifiItem = WifiItem(self, wifi)
        self.vbox.insertWidget(0, wifiItem)

    def setHint(self, text):
        self.label.setText(text)
        self.label.show()



class WifiItem(QPushButton):
    def __init__(self, root, wifi):
        super().__init__()
        self.root = root
        self.wifi = wifi
        self.setText('  ' + wifi.ssid + '  ')
        self.setFont(FONT_LARGE)
        self.setFixedHeight(45)

        self.clicked.connect(self.enterPasswd)

    def enterPasswd(self):
        keyboard.popup(finishEvent=self.connect)

    def connect(self, passwd: str = None):
        self.root.setHint('连接中...')
        self.root.repaint()
        self.root.iface.disconnect()

        profile = pywifi.Profile()
        profile.ssid = self.wifi.ssid
        profile.auth = pywifi.const.AUTH_ALG_OPEN
        profile.akm.append(pywifi.const.AKM_TYPE_WPA2PSK)
        profile.cipher = pywifi.const.CIPHER_TYPE_CCMP
        profile.key = passwd

        self.root.iface.remove_all_network_profiles()
        tmp_profile = self.root.iface.add_network_profile(profile)
        self.root.iface.connect(tmp_profile)

        time.sleep(3)

        if self.root.iface.status() == pywifi.const.IFACE_CONNECTED:
            self.root.setHint('已连接上')
        else:
            self.root.setHint('连接失败')
            self.root.iface.disconnect()
