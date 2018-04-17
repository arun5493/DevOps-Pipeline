# Project Milestone #3 - Deployment Milestone

Repository containing work done for Milestone #3 for CSC519-DevOps course

### Submitted by
Name  | Unity ID
------------- | -------------
Arun Jaganathan | ajagana
Balasubramanian Ramesh Babu | bramesh
Krithika Sekhar | ksekhar
Vivek Mani | vmani2


### Environment

All instances are provisioned as AWS EC2 instances.

    - AMI : ami-965e6bf3
    - Instance type : t2.micro(expect jenkins server which is of type xlarge)
    - Security groups for opening port 8080 (for jetty), port 22 (for ssh), MySQL and Mongo dbs port,monitoring dashboard port and port 3002 (for checkbox.io)

The script is run from an ec2 instance. The pem file that is given in the provisioning script is downloaded on to the ansible control server and saved as ec2.key in repo path - inside `~/DevOps_Project`.

### Steps for execution
 
#### For executing Deployment, Canary Release and Rolling Update

```bash
cd ~
git clone https://github.ncsu.edu/ajagana/DevOps_Project.git
cd DevOps_Project
git checkout m3
ansible-playbook -i hosts main.yml --vault-password-file vault_pass.txt
```

#### For executing Infrastructure Upgrade

```bash
cd ~
git clone https://github.ncsu.edu/ajagana/DevOps_Project.git
cd DevOps_Project
git checkout m3
cd ..
mv DevOps_Project/k8-clusterConfig k8-clusterConfig
cd k8-clusterConfig
ansible-playbook main.yml --vault-password-file vault_pass.txt
```

Make sure to update the ansible vault password in the vault_pass.txt file as well as to have the ec2.key file before running the playbook.

### Report

#### Deployment - Balasubramanian Ramesh Babu & Vivek Mani
- Deployment code from milestone 1 was used as starting point. All applications are remotely deployed on to AWS EC2 instances.
- A single instance for both MongoDB and MySQL were maintained. Both the databases run in the Jenkins instance(to reduce the number of instances) and their bind address was changed inorder to receive connections from other IPs. An instance with higher processing power was given for Jenkins to handle the load. All the application servers connect to the databases running in this instance.
- Webhooks for both checkbox.io and itrust2 are created dynamically through ansible. This makes sure that when ever there is change pushed to production, a Jenkins build is triggered.
- Role for creating new instances checks the hosts file and if hosts are already present in the file, no new instances are created, remaining roles run on the existing instances 

#### Canary Release - Balasubramanian Ramesh Babu 
- This required setting up a separate server for a canary version of the source code. To do this, a forked repo of the checkbox.io source code was used and minor changes were made to distinguish the canary update.
- A single mongodb instance was required. This was achieved by installing mongodb on the Jenkins server. This also required updating the bind ip in the mongodb.conf file to 0.0.0.0
- A proxy server was initiated and js code was written to establish a proxy mechanism. Every 3rd request is routed to the canary server. In the event of a failure of the canary server, all requests are routed to the primary checkbox server.
- Git hooks were configured to ensure a push to the respective branch triggers the corresponding build. A push to the master branch triggers a build for the checkbox job; a push to the canary branch triggers a build for the canary job.


#### Rolling Update - Vivek Mani
- To achieve the rolling update strategy `serial` option is used in the deploy itrust role. The value is set to 1, which makes sure that the deployment takes place one server at a time.
- Code from the monitoring workshop was extended to create a simple monitor dashboard for the five itrust2 instances. Using sockets it updates automatically. This process runs in the Jenkins server under port 3001.

##### Dashboard snapshot sample
![image](https://media.github.ncsu.edu/user/5810/files/f91d049e-4107-11e8-9265-aa7a2031591c)


#### Infrastructure Upgrade - Arun Jaganathan & Krithika Sekhar
##### Kubernetes  
- This required creating a playbook to install Kubernetes and its dependencies on the Master and the nodes that were spawned.
- A single instance of mongodb was installed on the master node and the container was configured to have the MONGO_IP to be the public ip of the master node.
- A Kubernetes cluster was initialised with a master and 3 nodes.
- A checkbox.io container was created using a Dockerfile and the corresponding container was built, pushed to the docker hub and used by Kubernetes to spawn a service, with a replication factor of 3 (uses all available nodes).
- The service type was configured to be NodePort, in order to forward port 80. The environment variables are also set when running the service using encrypted Ansible variables.
- The dynamically assigned NodePort is printed and used to access checkbox.io.
- Failure of a node does not disrupt the service, as the other nodes are used to keep the replication active.

##### Redis feature flagserver  
- Created a Redis feature flagserver by modifying the checkbox.io code. This is done by using a flag variable, 'createFlag', which can be set to true or false to turn on or off the features of creating a survey or data study.  
- Wrote Ansible playbooks to install Redis-server on Master and Slave instances and also to ensure mirroring can be achieved by setting protected mode to 'no'  

### Screen cast

Here is a [link](https://youtu.be/jDjjkOPpITw) to the screencast demonstrating the playbook execution. Deployment and Rolling update part.

Here is a [link](https://drive.google.com/file/d/14pBo8gQSNCFyoJ9uGTljv4qL4QQ1X7IN/view?usp=sharing) to the screencast explaining the code for canary release along with a demo.

Here is a [link](https://youtu.be/s5EeWNARSYQ) to the screencast explaining the code and demo for infrastructure upgrade using kubernetes cluster creation and Redis server.
