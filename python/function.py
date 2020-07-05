import argparse
import csv
import os

from os import path

from aws.cloudformation import *
from utilities.LoggerWrapper import LoggerWrapper

logger = LoggerWrapper()

__author__ = "Paul Arenas"
__email__ = "paul.arenas@globant.com"


def fetch_args():
    """
       Is an arguments parser which showcases all possible arguments this python function takes in.
    """
    parser = argparse.ArgumentParser(
        description='Parameter to run the script')
    parser.add_argument('-fs', '--filter_stack', metavar='filter-stack',
                        help='''Provide the environment''', default='dev')
    return parser


def write_report(report):
    try:
        report_sorted = sorted(report, key=lambda resource: (resource['Type'], resource['Resource']))
        fieldnames = ['Type', 'Resource', 'Tags', 'MissingTags', 'Created']
        name_csv = "report.csv"
        if path.exists(name_csv):
            os.remove(name_csv)
        with open(name_csv, mode='a', newline='') as csv_file:
            writer = csv.DictWriter(csv_file, fieldnames=fieldnames, delimiter='\t', quotechar='"')
            writer.writeheader()
            for r in report_sorted:
                writer.writerow(r)
    except Exception as e:
        logger.error("Write report failed! Error: {} Type: {}".format(e.__str__(), type(e).__name__))


def main():
    try:
        report = []
        parser = fetch_args()
        args = parser.parse_args()
        stacks = get_stacks(args.filter_stack)
        if len(stacks) > 0:
            logger.info("Stacks: %d",  len(stacks))
            for stack in stacks:
                name = stack['StackName']
                logger.info("Stack Name: %s", name)
                resources = get_stack_resources(name)
                for resource in resources:
                    resource = {
                        "Type": resource['ResourceType'],
                        "Resource": resource['PhysicalResourceId'],
                        "Tags": "3,4",
                        "MissingTags": "1,2",
                        "Created":"pipeline tool"
                    }
                    report.append(resource)
        if len(report) > 0:
            write_report(report)
    except Exception as e:
        logger.error("Something failed! Error: {} Type: {}".format(e.__str__(), type(e).__name__))


if __name__ == "__main__":
    main()
