FROM maven

RUN echo ppp

RUN git clone https://github.com/parvathimalagaddi/library.git


RUN mvn clean package -f /library/LibraryApp/pom.xml

CMD ["java","-jar","/library/LibraryApp/target/library-app-fat.jar"]


