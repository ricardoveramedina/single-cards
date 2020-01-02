run:
	java -jar build/libs/singlecards-0.0.1-SNAPSHOT.jar $(arg0) $(arg1) $(arg2) $(arg3)

build: clean
	./gradlew build

clean:
	./gradlew clean