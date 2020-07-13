# Get and Validate tagging in the aws Resources
Find and validate which tags have aws resources and which are missing

## Requeriments
- Java 1.8
  - [Download](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
- Gradle > 6.5.x
  - [Download](https://gradle.org/next-steps/?version=6.5.1&format=bin)
  - [Installation Guide](https://gradle.org/install/) 
- Docker
  - [Get docker](https://docs.docker.com/get-docker/)
- Docker Compose
  - [Get docker compose](https://docs.docker.com/compose/install/)


## Code Quality with SonarQube
You can configure SonarQube to analyze the code quality, follow the steps below: 
1. Run
   - Run sonar server with docker, you can find docker-compose.yml file in develop-env folder
   - Run `$ docker-compose up -d` command.
2. Create and get token
   - Open browser and go through http://localhost:9000/.
   - Login using `admin/admin`.
   - Go through [My Account/Security](http://localhost:9000/account/security/) page.
   - Then generate a token, using your preferred name. Make sure you copy it now, you won't be able to see it again!
3. Open file gradle/gradle.properties file and replace your token in systemProp.sonar.login property

# Compile the CLI

Run the clean and jar gradle task

# Running

Run cli **run.sh**
`java -jar build/libs/aws_tagging-1.0.jar -t Stack -s ecp-ingestion-validation`

# Report taggin

After CLI ran, the result will be a csv file, separeated by tab, with 7 columns sorted by type and resource

## Columns
1. Type: AWS type resource   
2. Resource: Name resource in aws
3  Tags: Tags that resource has.	
4. MissingTags: Tags that resource don't have and they should have. 	
5. Created:
   - Created by cloud formation (could be provisioined by pipeline, CI/CD)
   - Custom resources (Created them lambdas or service catalog product template)
6. Classic Coverage	
7. Modern Coverage

# AWS Types supported

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

# It's comming
- More types that support tags
- Flexible Classics tags with a json file, that's mean you can use your custom tags
- Flexible Moderns tags with json file, that's mean you can use your custom tags

