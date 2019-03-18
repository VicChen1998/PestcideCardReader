import sys
import sqlite3

from Models import Result


class Database:
    def __init__(self):
        # create sqlite connection and cursor
        self.connect = sqlite3.connect(sys.path[0] + '/records.db')
        self.cursor = self.connect.cursor()

        # detect if table is exist
        self.cursor.execute(
            "SELECT COUNT(*) FROM sqlite_master where type='table' and name='record'")

        # if not exist. create table.
        if list(self.cursor)[0][0] != 1:
            sql = 'CREATE TABLE record (            \
                    id INTEGER PRIMARY KEY AUTOINCREMENT,    \
                    time          TEXT NOT NULL,    \
                    imgSrc        TEXT NOT NULL,    \
                    reading1   CHAR(4) NOT NULL,    \
                    reading2   CHAR(4) NOT NULL,    \
                    reading3   CHAR(4) NOT NULL,    \
                    reading4   CHAR(4) NOT NULL,    \
                    reading5   CHAR(4) NOT NULL,    \
                    reading6   CHAR(4) NOT NULL,    \
                    reading7   CHAR(4) NOT NULL,    \
                    reading8   CHAR(4) NOT NULL);'

            self.cursor.execute(sql)
            self.connect.commit()

    def __del__(self):
        self.connect.close()

    def save(self, result):
        sql = 'INSERT INTO record VALUES (NULL,"' + \
            result.time + '","' + result.imgSrc + '"'
        for i in range(8):
            sql += ',' + str(round(result.values[i], 2))
        sql += ')'

        self.cursor.execute(sql)
        self.connect.commit()

    def count(self):
        sql = 'SELECT COUNT(*) FROM record;'
        self.cursor.execute(sql)
        self.connect.commit()
        return list(self.cursor)[0][0]

    def read(self, pageIndex, pageSize=10):
        sql = 'SELECT * FROM record ORDER BY id DESC LIMIT ' + \
            str(pageSize) + ' OFFSET ' + str(pageIndex * pageSize) + ';'
        self.cursor.execute(sql)

        return list(self.cursor)

    def delete(self, _id):
        sql = 'DELETE FROM record WHERE id=' + str(_id)
        self.cursor.execute(sql)
        self.connect.commit()

database = Database()
