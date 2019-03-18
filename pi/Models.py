class Result:
    '''
    Analyse result data model
    '''

    def __init__(self):
        self.id = None  # id in database
        self.time = None
        self.values = None
        self.imgSrc = None

    def print(self):
        pass

    @staticmethod
    def fromTupple(data):
        '''
        @staticmethod
        convert tupple to Result
        '''

        if not data:
            return None

        result = Result()
        result.id = data[0]
        result.time = data[1]
        result.imgSrc = data[2]
        result.values = []
        for i in range(3, 11):
            result.values.append(float(data[i]))

        return result