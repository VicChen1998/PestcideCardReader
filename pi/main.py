import sys
from PyQt5.QtWidgets import QApplication, QWidget


if __name__ == '__main__':
    app = QApplication(sys.argv)

    from Launchpad import Launchpad
    launchpad = Launchpad()
    launchpad.showFullScreen()

    sys.exit(app.exec_())



# fswencam -r 1920x1080 --rotate 90 --no-banner test.jpg