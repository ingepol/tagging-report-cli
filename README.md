# Get and Validate tagging in the aws Resources
Find and validate which tags have aws resources and which are missing

Run the script 

`$ python function.py`

And you can add flag if you want filter the stacks 

`$ python function.py -fs stg-ecp-ingestion-cli`

Before run the scipt open a session with aws-okta or aws cli config

When script ran, the result will be a csv file, separeated by tab, with 5 columns sorted by type and resource:

Type: AWS type resource 
Resource: Name resource	in aws
Tags: Tags that resource has.	
MissingTags:	
Created: Created by cloud formation (could be by pipeline), custom lambdas or service catalog template


