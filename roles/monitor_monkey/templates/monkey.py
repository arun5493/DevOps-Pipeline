import psutil
import math
import time
import os
import subprocess
from Naked.toolshed.shell import execute_js
import datetime
threshold = 90
reboot_command = "sudo reboot now"
cpu_util_list = []
cpu_util_list.append(0)
cpu_util_list.append(0)
cpu_util_list.append(0)
file = open("/home/ubuntu/Monitor/log","a")
host_ip = subprocess.check_output("dig +short myip.opendns.com @resolver1.opendns.com", shell=True).rstrip('\n')
while True:
    cpu_usage = int(math.floor(psutil.cpu_percent(interval=1)))
    file.write(str(datetime.datetime.now()) + "\t" + "CPU Utilisation: " + str(cpu_usage) + "\n")
    parameters = host_ip + " " +str(cpu_usage)
    success = execute_js('/home/ubuntu/Monitor/addToRedis.js',parameters)
    cpu_overload = 0
    if cpu_usage >= threshold:
        cpu_overload = 1
    cpu_util_list.pop(0)
    cpu_util_list.append(cpu_overload)
    if cpu_util_list[0] + cpu_util_list[1] + cpu_util_list[2] == 3:
        file.write(str(datetime.datetime.now()) + "\t"+ "High CPU usage for " + host_ip + "\n" + "Scheduled system reboot \n")
        failure = execute_js('/home/ubuntu/Monitor/removeFromRedis.js',host_ip)
        file.close()
        os.system(reboot_command)
    time.sleep(5)

