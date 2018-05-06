# Project Milestone #4 - Special Milestone

Repository containing work done for Milestone #4 for CSC519-DevOps course

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
    - Instance type : t2.micro
    - Security groups for opening port 8080 (for jetty), port 22 (for ssh) and port 3002 (for checkbox.io)

The script is run from an ec2 instance. The pem file that is given in the provisioning script is downloaded on to the ansible control server and saved as ec2.key in repo path - inside `~/DevOps_Project`.

### Steps for execution
 
```bash
cd ~
git clone https://github.ncsu.edu/ajagana/DevOps_Project.git
cd DevOps_Project
git checkout m4
ansible-playbook -i hosts main.yml --vault-password-file vault_pass.txt
```
Make sure to update the ansible vault password in the vault_pass.txt file as well as to have the ec2.key file before running the playbook.

### Special Component 
- For the special component, we introduced a Monitoring/Restart Monkey in our pipeline. 
- The Monkey will monitor all the application servers for CPU utilization percentage. It uses Redis in-memory data store to store the values.
- The Monkey is configured to run in a separate proxy server and it periodically updates the memory store with the server's utilization values. It also restarts any server that has high utilization rate over a period of time.
- Whenever there is a request from a user, the monitor monkey will route the request to the server that has high CPU availability at that instant of time.
- If it continuously sees a high CPU utilization for a server, it would remove that server from the data store(so that no request are routed to it). After removing, it would gracefully restart the instance in order to kill any background process that has been consuming high CPU. Once the instance is restarted, the monkey brings up the application and other required processes in the instance and adds it back to the memory store.
- The objective of the monkey is to provide high responsiveness and availability to the end user.

### System Diagram

![untitled diagram 1 2](https://media.github.ncsu.edu/user/5810/files/57f52096-4fa4-11e8-884b-87ae7d4e8be7)

### Code explanation
- The monkey.py script runs on the production servers and constantly measures the cpu utilisation. This measured value of cpu utilisation is pushed to a redis hash. When the cpu utilisation goes above a defined threshold for three cycles, the server reboots and it is ensured that the checkbox server.js and monkey.py are executed on reboot.
- The proxy server has redis installed on it and the production servers update the `production` hash on this server. The rebooting of the production server results in the removal of the corresponding production server from the `production` hash.
- The proxy.js script routes the requests by obtaining all entries from the `production` hash and chooses the instance with lowest cpu utilisation.

### Screen cast

Here is a [link](https://www.youtube.com/watch?v=eKs5PW8gMPg) to the screencast demonstrating the entire pipeline, along with the special component.
