"""
    Enum with different aws services that the lambda used
"""
from enum import Enum


class AwsService(Enum):
    ATHENA = 'athena'
    CFN = 'cloudformation'
    CLOUD_WATCH = 'cloudwatch'
    DMS = "dms"
    GLUE = "glue"
    S3 = 's3'
    SSM = 'ssm'
    STS = 'sts'
    SNS = 'sns'


