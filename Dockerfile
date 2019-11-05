FROM openjdk:12
COPY . /nr-three-word-sequences
WORKDIR /nr-three-word-sequences
RUN ./gradlew build
ENTRYPOINT ["./gradlew", "run"]
CMD ["--args='src/test/resources/pg_moby_dick.txt'"]