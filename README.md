# Get and Validate tagging in the aws Resources
Find and validate which tags have aws resources and which are missing

Make 
`$ make all`

Run cli **run.sh**
`./run.sh Main 'type=Stack filter=stg-ecp-ingestion-cli'`

Make and run
`$ make all && ./run.sh Main 'type=Stack filter=stg-ecp-ingestion-cli'`

When the type is Stack, the filter parameter should be sending.

Before run the scipt open a session with aws-okta or aws cli config

When script ran, the result will be a csv file, separeated by tab, with 5 columns sorted by type and resource:

Type: AWS type resource 
Resource: Name resource	in aws
Tags: Tags that resource has.	
MissingTags: Tags that resource don't have and they should have. 	
Created: Created by cloud formation (could be by pipeline), custom lambdas or service catalog template
Classic Coverage	
Modern Coverage

### Type supported

| Type              | Desctiption                                                   |
| :----             |:-----------                                                   | 
| EventSubscription | DMS subscription                                              |    
| SubnetGroup       | DMS Subnet groups                                             |
| Task              | DMS Replication tasks                                         |
| Endpoint          | DMS Source and target endpoints                               |
| Instance          | DMS Replication instances                                     |
| Function          | Lambda function                                               |
| Parameter         | Parameter storage                                             |
| Portfolio         | Service catalog portfolios                                    |
| Product           | Service catalog provisioned products                          |
| Role              | IAM Role                                                      |
| Rule              | Cloudwatch rule events                                        |
| Stack             | Cloudformation stack, get resources by stacks with a filter   |
| S3                | Bucket s3                                                     |
| Topic             | SNS topics                                                    |


