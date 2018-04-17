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
TODO
```

Make sure to update the ansible vault password in the vault_pass.txt file as well as to have the ec2.key file before running the playbook.

### Report

#### Deployment - Balasubramanian Ramesh Babu & Vivek Mani
- Deployment code from milestone 1 was used as starting point. All applications are remotely deployed on to AWS EC2 instances.
- A single instance for both MongoDB and MySQL were maintained. Both the databases run in the Jenkins instance(to reduce the number of instances) and their bind address was changed inorder to receive connections from other IPs. An instance with higher processing power was given for Jenkins to handle the load. All the application servers connect to the databases running in this instance.
- Webhooks for both checkbox.io and itrust2 are created dynamically through ansible. This makes sure that when ever there is change pushed to production, a Jenkins build is triggered.
- Role for creating new instances checks the hosts file and if hosts are already present in the file, no new instances are created, remaining roles run on the existing instances 

#### Canary Release - Balasubramanian Ramesh Babu 

TODO

#### Rolling Update - Vivek Mani
- To achieve the rolling update strategy `serial` option is used in the deploy itrust role. The value is set to 1, which makes sure that the deployment takes place one server at a time.
- Code from the monitoring workshop was extended to create a simple monitor dashboard for the five itrust2 instances. Using sockets it updates automatically. This process runs in the Jenkins server under port 3001.

##### Dashboard snapshot sample
![image](https://media.github.ncsu.edu/user/5810/files/f91d049e-4107-11e8-9265-aa7a2031591c)


#### Infrastructure Upgrade - Arun Jaganathan & Krithika Sekhar
##### Kubernetes  
TODO  

##### Redis feature flagserver  
- Created a Redis feature flagserver by modifying the checkbox.io code. This is done by using a flag variable, 'createFlag', which can be set to true or false to turn on or off the features of creating a survey or data study.  
- Wrote Ansible playbooks to install Redis-server on Master and Slave instances and also to ensure mirroring can be achieved by setting protected mode to 'no'  

### Screen cast

Here is a [link](https://youtu.be/jDjjkOPpITw) to the screencast demonstrating the playbook execution. Deployment and Rolling update part.

Here is a [link]() to the screencast explaining the code for canary release along with a demo.

Here is a [link]() to the screencast explaining the code and demo for infrastructure upgrade using kubernetes and Redis server.
