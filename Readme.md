## Spring Boot File Upload / Download Rest API Example

**Tutorial**: [Uploading an Downloading files with Spring Boot](https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/)

## Steps to Setup

**1. Clone the repository** 

```bash
git clone https://github.com/gkalidindi/spring-boot-file-upload-download-rest-api-example.git
```

**2. Specify the file uploads directory**

Open `src/main/resources/application.properties` file and change the property `file.upload-dir` to the path where you want the uploaded files to be stored.

```
file.upload-dir=/Users/gkalidindi/Documents/Docmation_Git_Projects/DigitalRiver/spring-boot-file-upload-download-rest-api-example/uploadedFiles
```

**2. Run the app using maven**

```bash
cd spring-boot-file-upload-download-rest-api-example
mvn spring-boot:run
```

That's it! The application can be accessed at `http://localhost:8080`.

You may also package the application in the form of a jar and then run the jar file like so -

```bash
mvn clean package
java -jar target/file-demo-0.0.1-SNAPSHOT.jar
```

Follow steps from below link to Deploy this app on Heroku
```bash
https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku
https://devcenter.heroku.com/articles/deploying-java-applications-with-the-heroku-maven-plugin
```

You can log into the Heroku app through terminal
```bash
heroku login
heroku run bash -a APPNAME
$ cd app

APPNAME is the name of your Heroku application And in the folder app are your files.

When you finish your commands and want to return to your terminal you can write

$ exit
```