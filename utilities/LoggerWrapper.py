import os
import logging.config

__author__ = "Paul Arenas"
__email__ = "paul.arenas@globant.com"

class LoggerWrapper(object):

    def __init__(self):

        path_logging_ini = os.path.join(os.path.dirname(__file__), '../resources/logging.ini')

        logging.config.fileConfig(path_logging_ini)

        self.logger = logging.getLogger()

    def debug(self, message, *args):
        self.logger.debug(message, *args)

    def info(self, message, *args):
        self.logger.info(message, *args)

    def warning(self, message, *args):
        self.logger.warning(message, *args)

    def error(self, message, *args):
        self.logger.error(message, *args)

    def critical(self, message, *args):
        self.logger.critical(message, *args)
