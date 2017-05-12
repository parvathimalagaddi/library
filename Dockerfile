FROM maven

COPY ./LibraryApp .



RUN mvn clean package 

CMD ["java","-jar","target/library-app-fat.jar"]

