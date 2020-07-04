"""Provides a wrapper for boto3 APIs."""

from enums.AwsService import AwsService
from utilities.LoggerWrapper import LoggerWrapper


import boto3
import os


__author__ = "Paul Arenas"
__email__ = "paul.arenas@globant.com"


class BotoApiWrapper(object):
    """
        Init sesion boto
        Documentation Boto3 API
            https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/index.html
    """
    def __init__(self, access_key=None, secret_access=None, session_token=None):
        self.logger = LoggerWrapper()
        self.region = os.environ['AWS_REGION']

        if access_key is None:
            self.session = boto3.session.Session()
        else:
            self.session = boto3.Session(
                aws_access_key_id=access_key,
                aws_secret_access_key=secret_access,
                aws_session_token=session_token,
            )

    def __get_client(self, service):
        return self.session.client(service)

    def __get_resource(self, service):
        return self.session.resource(service)

    def describe_stacks(self, token=None):
        try:
            client = self.__get_client(AwsService.CFN.value)
            if token:
                response = client.describe_stacks(NextToken=token)
            else:
                response = client.describe_stacks()

            token = response['NextToken'] if 'NextToken' in response else None
            stacks = response['Stacks']
        except Exception as ex:
            self.logger.error("Describe stacks exception: {}, type: {}".format(ex.__str__(), type(ex).__name__))
            raise
        return stacks, token

    def list_stack_resources(self, name, token=None):
        try:
            client = self.__get_client(AwsService.CFN.value)
            if token:
                response = client.list_stack_resources(StackName=name, NextToken=token)
            else:
                response = client.list_stack_resources(StackName=name)
            token = response['NextToken'] if 'NextToken' in response else None
            resources = response['StackResourceSummaries']
        except Exception as ex:
            self.logger.error("List stack resources exception: {}, type: {}".format(ex.__str__(), type(ex).__name__))
            raise
        return resources, token
