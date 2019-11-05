FROM openjdk:12
COPY . /three-words-app
WORKDIR /three-words-app
RUN ./gradlew build
ENTRYPOINT ["./gradlew", "run"]
CMD ["--args='src/test/resources/pg_moby_dick.txt'"]