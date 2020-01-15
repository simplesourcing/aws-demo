# Simplesourcing aws-demo
Demo application using Simplesourcing deployed on AWS using Elasticseaarch, Fargate and MSK.

This demo app shows a basic account application with the below commands:
* CreateAccount
* Deposit
* Withdraw

Simplesourcing and Kafka are used for a distributed [log](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying). [Elasticsearch](https://www.elastic.co/) is used as the read-store for this demo application.


## Project prerequisites
* JDK 11
* Gradle

If you are a [nix](https://nixos.org/nix/) user you can use the provided [shell.nix](shell.nix) file with `nix-shell`.

## Building
To build the application:

```
gradle bootJar
```

To package the application as a Docker container:

```
gradle createDockerfile
```

Followed by:
```
docker build -f build/docker/Dockerfile -t simplesource-demo .
```

## Running locally
Use the provided [docker-compose.yml](docker-compose.yml) file to start Elasticsearch and Kafka locally.

Modify your hosts file adding the below:

```
127.0.0.1 broker kafka elasticsearch
```

Start docker-compose:

```
docker-compose up
```

Create the Elasticsearch indexes:
```
curl -X PUT "localhost:9200/simplesourcedemo_account_transaction?pretty" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "account": { "type":  "keyword" },
      "ammount": { "type":  "double" }
    }
  }
}
'

curl -X PUT "localhost:9200/simplesourcedemo_account_summary?pretty" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "accountName": { "type":  "keyword" },
      "balance": { "type":  "double" }
    }
  }
}
'
```

Export the below environment variables:

```
export SERVER_PORT=8083
export KAFKA_BOOTSTRAP_SERVERS=kafka:9092
export ELASTICSEARCH_HOST=elasticsearch
export ELASTICSEARCH_PORT=9200
```

Start the app:
```
gradle run
```

Open a web browser and go to [http://localhost:8083](http://localhost:8083).

# Detailed AWS deployment instructions

## Fargate app deployment

### Set up the ECS cluster for Fargate
* Go to the ECS Dashboard
* Click “Clusters” on the left hand side
* Click “Create Cluster”
* Select “Networking Only” Powered by Fargate
* Click “Next step on the bottom left”“
* Cluster Name” enter simplesourcing
* Click “Create on the bottom left”
### Set up the Simplesourcing task definition
* Go to the ECS dashboard
* Click “Task Definitions” on the left
* Click the blue “Create new Task Definition” button.
* Select “FARGATE” for launch type.
* Enter “Simplesourcing” for “Task Definition Name”
* Task memory 4gb
* Task vcpu: 2
* Click “Add container”
* Container name: simplesourcingImage: 121125835837.dkr.ecr.ap-southeast-2.amazonaws.com/simplesouring-demo (note taken from ecr screen)
* Port mappings: 8083
*Environment variables:
```
KAFKA_BOOTSTRAP_SERVERS = b-3.simplesourcing.tjoxvf.c2.kafka.ap-southeast-2.amazonaws.com:9092,b-1.simplesourcing.tjoxvf.c2.kafka.ap-southeast-2.amazonaws.com:9092,b-2.simplesourcing.tjoxvf.c2.kafka.ap-southeast-2.amazonaws.com:9092(Note: get this from the MSK “view client information” link on the cluster details page)

ELASTICSEARCH_URL = https://vpc-simplesourcing-oguo4ewt7wawgarqbi3numhuo4.ap-southeast-2.es.amazonaws.com(Note: this is the VPC endpoint link on the Elasticsearch cluster overview page)
```

* Go to the bottom, click next (not 100% sure the button name, as forgot to note it before clicking)
* Go to the bottom, click create
* Go to the Clusters overview page
* Select simplesourcing
* Click the Tasks tab
* Click “Run new Task”
* Launch type: “Fargate”
* Cluster VPC: select the vpc we created
* Subnets, select the public subnet
* Click edit for security group.
* Delete the existing http rule, create a new one
* Custom TCP, port 8083, source anywhere.
* Auto-assign public IP: enabled
* Click “Run task” on the bottom.
* Click the task, note the public IP. Open a browser and goto the url at port 8083, I.e http://13.236.137.102:808
