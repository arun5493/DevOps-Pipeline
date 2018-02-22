#### In master-machine run the following command to install ansible first

```
sudo apt-get update
sudo apt-get -y install git make vim python-dev python-pip libffi-dev libssl-dev libxml2-dev libxslt1-dev libjpeg8-dev zlib1g-dev
pip install ansible
```



#### Create a variable that stores the destination of git clone

#### Install Java

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

##### Update Environment Variable:

```
sudo nano /etc/environment
JAVA_HOME="/usr/lib/jvm/java-8-oracle"
source /etc/environment
echo $JAVA_HOME
```
#### Install Maven
```
sudo apt-get install maven

```

#### Install MYSQL
```
sudo apt-get install mysql-server
```

### Install Tomcat 9 - Try without Tomcat9 once

###### Create a folder in your ubuntu and do the following:
```
wget http://www-us.apache.org/dist/tomcat/tomcat-9/v9.0.4/bin/apache-tomcat-9.0.4.tar.gz
tar xzf apache-tomcat-9.0.4.tar.gz
mv apache-tomcat-9.0.2 apache-tomcat9
cd apache-tomcat9/bin/
./startup.sh
```

The last command will start the server in port 8080 by default


### Copy the property template files.

```
src/main/java/db.properties.template copied to src/main/java/db.properties
src/main/resources/hibernate.properties.template copied to src/main/resources/hibernate.properties
src/main/java/email.properties.template copied to src/main/java/email.properties
```

### Copy the sample database
```
mvn process-test-classes
```

### Start the iTrust application
```
mvn jetty:run
```

The application will start up in port 8080 in the following address:

```
http://localhost:8080/iTrust2
```
