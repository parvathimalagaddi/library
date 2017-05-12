FROM maven

COPY ./LibraryApp /



RUN mvn clean package -f /LibraryApp/pom.xml

CMD ["java","-jar","/LibraryApp/target/library-app-fat.jar"]

