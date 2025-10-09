# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```





## Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3r5d5LsKMBihKboynKZbvEqIW8mFNl2TFW6YCqwYagAkmgVAmkg65BTA0AwOpOzbt5Nn+sy0yXtASAAF4oBwUYxnGhSgZhyCpjA6YAIwETmqh5vMixFiW9TigVyDroeHDmEgJqqA29GmF5grDvy9SHnIKDPvEGKHde22LpUy4BmuAaXq+zrVbpVn+i54oZKoAGYHp8I1WBhGGeNxljBRVH1pCVzdUmlSiTheFKURANkUDl4g8hYONgxnjeH4-heCg6AxHEiS4-jLm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDMDiHoNpgpffUnNIZ9T06R2tkuvZQlk+eyNc2gnn3Vtt47UYKDcMel7S-BstzqFDrhfUGQzBANA3S+W5nQKkOi3Z737lV7Z8wJkunm9H1fSB1T6WMI1jXsLmMuuZMNhDya9dhMC4fhoze-yvuLP7rWlaeMC2yOABmvYIJYa1NpjzH+Ci67+Ng4oavxaIwAA4kqGgUz91OVwzzP2EqHMy4LEOPbC-oC9z7upeLyA5NXOaa5R2v2z5yXKzUSBp5Yh1jyjOvT+dQr1FFT63doso1rLSULpbA9djAs1FaYWXqjAeVn+uvddQrVtU5K9V6o1LVtdGKCxgpPNW9D-UnBDWjj7fME1izQHqD4V+8R36tRzhjTaU9D7+TAIGBAI81AYhXofe8EUKDcHAJWBAVclQegth7Ts9Ry45AADyYJ5OUSeH5hbULRJg1QrsECAWFpQmoLwxgtxzAsBoLgxGdHBp7b6UMw5gBhlHcYQi1AiLES4CR6NGJYwCBwAA7G4JwKAnAxAjMEOAXEABs8AJyGEwTAIosjKZSOpq0DozdW4wJRlmJRAA5JUkiJIi0doopUvijJoX7r6e6u1rGYKXrLNYPilTy07IrXW9JT6UDngvDW98cH7jwRvcUW8zbyF3jASAgsKFPyiafQqGNL65XynU407duZIPri-CicDP4dV-iHGRJQ5GAOAdmWOYCxiTUgQqGB3SEEbUflSJW6S9roliQw7QeSwoXQio+UhcxyFLKPpEqhMAlE5WkMwruX56hwBiUqLhPDu7SNFl7M50gFiqP8YmUOgz5EETeR8sRdFc5MWxpYVWDlNgEyQAkMA4K+wQChQAKQgOKPZhh-DJFAGqexgzHEBOccyGSPQlFty1khLM2AEDAHBVAOAEAHJQASUqc5XyMIsKeQIqlNLKD0sZXsAA6iwHKjNViCJZe82olknn4rFifAAVqitAsT77MrmOctYIAGXQGSXK1Jq9RwZKgFkxeuSD75P1pFIppsrw7zihU7mVTj71FvhfVUjTb4tPJW0x+HS6pdONR-dq39Op-x+X1Qaw0xkA0maWaBAbmrwOBYghZMhDmoNiW8zZettmFIlOs0pcU3nmpSscuV9QC3ADddldFMA0AQGYGnXwTZ2llv9AAIRDG5NQYBg0-3jP0+Asi0ywxAdGvYsaGQVnrY25tuxk3zJScgvyCpsBaFWUqAVup4hNGpbSjEby1hBU1dqqA2a16XWZGu-a6KZQoi1QCEtetnUpx8HbVtDxOUwBReKTBDyhYyuqQSmAozczjJ-WgROSjX37gzn2bObLnkAMjlGsDAMINQYrKnfkcGs5zLztjLwNLoWwqI-KRAwZYDAGwFSwgeQCh2Lrm20sNM6YMyZr0Ywf8rlwnqPEJ6FG8DYIA1+WVdkQDcDwDyPQBhhNIMWWkw1EnKPoOE1U3NKs1aGBNCQt4qg1VqDWA6tAtpDl8Jtm+kclzP3XKHZR-9-cOmgdGuM6T+hDDKbwEmsNAy+oobHWhvYbmDAwE84XGD6dM4IY0UAA
