from botoapi.BotoApiWrapper import BotoApiWrapper
from utilities.LoggerWrapper import LoggerWrapper


__author__ = "Paul Arenas"
__email__ = "paul.arenas@globant.com"

botoapi = BotoApiWrapper()
logger = LoggerWrapper()


def get_stacks(filter_stack=None):
    stacks_result = []
    try:
        stacks, token = botoapi.describe_stacks()
        while token:
            logger.debug("Fetched %d stacks. Getting more...", len(stacks))
            more_stacks, token = botoapi.describe_stacks(token)
            stacks.extend(more_stacks)
        if filter_stack:
            for stack in stacks:
                if filter_stack in stack['StackName'] and stack['StackStatus'] in ['CREATE_COMPLETE', 'UPDATE_COMPLETE']:
                    stacks_result.append(stack)
        else:
            for stack in stacks:
                if stack['StackStatus'] in ['CREATE_COMPLETE', 'UPDATE_COMPLETE']:
                    stacks_result.append(stack)
    except Exception as e:
        logger.error("Get Stacks failed! Error: {} Type: {}".format(e.__str__(), type(e).__name__))
    finally:
        return stacks_result


def get_stack_resources(name):
    try:
        resources, token = botoapi.list_stack_resources(name)
        while token:
            logger.debug("Fetched %d resources. Getting more...", len(resources))
            more_resources, token = botoapi.list_stack_resources(name, token)
            resources.extend(more_resources)
    except Exception as e:

        logger.error("Get Stack resources failed! Error: {} Type: {}".format(e.__str__(), type(e).__name__))
    finally:
        return resources
