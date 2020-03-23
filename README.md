Rotate a matrix by 90 degree in clockwise
===============

This is the Spring Boot command line application which tries to read 2D array data from input file with *.in extension. If input file content and format is valid, 2D array is generated and rotated. Rotated matrix content is appended to input file. Input file extension is changed to *.out.

## How to build

This is the Maven project. Maven is used as build automation tool. Apache Maven 3.x and minimum JDK8 should be available on the host. 

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home
export PATH="${JAVA_HOME}/bin:$PATH"
export MAVEN_HOME=/Users/kemalatik/etc/apache-maven-3.5.0/
export PATH="${MAVEN_HOME}/bin:$PATH"

$ cd etc/rotate-matrix
$ mvn compile
$ mvn package
```

## Run using Maven

```bash
mvn springboot:run
```
## Run using java -jar

```bash
java -jar -Dspring.profiles.active=local -Xms512m -Xmx512m rotate-matrix-1.0.0.jar
```

## application.yml file

Maven build will generate fat jar which also includes application.yml. 

```yaml
spring:
  main:
    web-application-type: none
  application:
    name: rotate-matrix

config:
  inputDirectory: ./tmp/
  delayInMilis : 1000

logging:
  pattern:
    console: "%d %-5level [%thread] %logger{0} : %msg%n"
    file: "%d %-5level [%thread] %logger : %msg%n"
  level:
    org.springframework: INFO
    task: INFO
  file:
    name: logs/dev_app.log
```

* `inputDirectory` is the directory path for the input files. App looks this directory to read input files. 
* `delayInMilis` is the configurable delay between each directory read attempt.   

## How to use

Reference directory structure

```bash
$ ls
rotate-matrix-1.0.0.jar		tmp
```
### input file(e.g. array.in) 

```text
1,3,4,10
2,6,7,11
5,8,9,12
13,14,15,16
 ```

### output file(e.g. array.out) 

```text
1,3,4,10
2,6,7,11
5,8,9,12
13,14,15,16
-----
13,5,2,1
14,8,6,3
15,9,7,4
16,12,11,10
 ```

## Create Docker image

Docker desktop must be running on the host to run application in the docker container.

```bash
$ docker-compose build
Building rotate-matrix
Step 1/9 : FROM openjdk:8-jre
 ---> 11c7adda2eb7
Step 2/9 : MAINTAINER Kemal Atik <k.atik@oriontec.de>
 ---> Using cache
 ---> 8ca683c78134
Step 3/9 : ADD target/classes/application.yml /app/application.yml
 ---> Using cache
 ---> 00f8e82cf35e
Step 4/9 : ADD target/rotate-matrix-1.0.0.jar /app/rotate-matrix-1.0.0.jar
 ---> 605f4aafa9dc
Step 5/9 : ADD start.sh /app/start.sh
 ---> b71539cd5bba
Step 6/9 : WORKDIR /app
 ---> Running in 72e2eb895ea2
Removing intermediate container 72e2eb895ea2
 ---> 8f4c550d75f9
Step 7/9 : RUN mkdir -p /app/tmp
 ---> Running in 193f41ac2b01
Removing intermediate container 193f41ac2b01
 ---> b7ebcee41b73
Step 8/9 : EXPOSE 2000
 ---> Running in 06b6029f200d
Removing intermediate container 06b6029f200d
 ---> 78ad54ecc08b
Step 9/9 : ENTRYPOINT ["./start.sh"]
 ---> Running in eda37b6b597c
Removing intermediate container eda37b6b597c
 ---> bfa04cbe5b26
Successfully built bfa04cbe5b26
Successfully tagged rotate-matrix:v1
```

Docker image created
```
$ docker image ls
REPOSITORY                                          TAG                 IMAGE ID            CREATED             SIZE
rotate-matrix                                       v1                  bfa04cbe5b26        5 seconds ago       277MB
```

## Run Docker image in the new container

```
$ docker run  -d -v /Users/kemalatik/etc/github/rotate-matrix/tmp:/app/tmp rotate-matrix:v1 
fd2a9f3f16ed45b005e129c15c82af90667c96a8e3b4906be778411e2f610175
```

**Notes**

/Users/kemalatik/etc/github/rotate-matrix/tmp on the host is mounted to /app/tmp in the container.
Docker volume is used to share files between a host system and the Docker container. 
If file is uploaded to mounted directory on the host, Docker container will access it.

Container is running

```
$ docker container ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
fd2a9f3f16ed        rotate-matrix:v1    "./start.sh"        6 minutes ago       Up 6 minutes        2000/tcp            keen_wiles

## See container log files

```
## See log files

```
$ docker logs -f $(docker ps -q)
LOGBACK: No context given for c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy@901506536

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.1.RELEASE)

2020-03-22 19:57:19,678 INFO  [main] Main : Starting Main v1.0.0 on fd2a9f3f16ed with PID 6 (/app/rotate-matrix-1.0.0.jar started by root in /app)
2020-03-22 19:57:19,685 INFO  [main] Main : The following profiles are active: local
2020-03-22 19:57:20,744 INFO  [main] ThreadPoolTaskExecutor : Initializing ExecutorService
2020-03-22 19:57:20,752 INFO  [main] ThreadPoolTaskExecutor : Initializing ExecutorService 'threadPoolTaskExecutor'
2020-03-22 19:57:20,882 INFO  [main] Main : Started Main in 2.339 seconds (JVM running for 3.283)
2020-03-22 19:57:20,885 INFO  [main] Main : Application Started
2020-03-22 19:57:20,904 INFO  [executor-1] AsynchronousService : Async Task started
2020-03-22 19:57:43,120 INFO  [executor-1] AsynchronousService : Input file path : /app/./tmp/file.in
2020-03-22 19:57:43,155 INFO  [executor-1] AsynchronousService : File content is valid
2020-03-22 19:57:43,166 INFO  [executor-1] AsynchronousService : Array to be rotated [[1, 3, 4, 10], [2, 6, 7, 11], [5, 8, 9, 12], [13, 14, 15, 16]]
2020-03-22 19:57:43,168 INFO  [executor-1] AsynchronousService : Rotated array [[13, 5, 2, 1], [14, 8, 6, 3], [15, 9, 7, 4], [16, 12, 11, 10]]
2020-03-22 19:57:43,178 INFO  [executor-1] AsynchronousService : File updated.

```
