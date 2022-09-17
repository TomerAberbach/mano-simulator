# Mano Simulator

> An assembler and hardware simulator for the Mano Basic Computer, a 16 bit computer.

This is a JavaFX application that compiles assembly code for and runs a simulation of Mano's Computer as detailed in:

Computer System Architecture, 3rd edition
by M. Morris Mano
Published by Prentice-Hall, c 1993
Chapter 5, pp 123-172.

## Usage

1. Download the latest jar from [releases](https://github.com/TomerAberbach/mano-simulator/releases)
2. Install a Java version compatible with the downloaded jar's Java version
3. For jars supporting Java versions lower than 11, install JavaFX
4. Assuming `mano-simulator.jar` is in the current working directory, run the following command:

   ```sh
   $ java -jar mano-simulator.jar
   ```

   If the `java` command is not found, then [ensure `java` is in your `PATH`](https://www.java.com/en/download/help/path.html).

## Development

1. Install [Git](https://github.com/git-guides/install-git)
2. Run the following command to clone the repository:

   ```sh
   $ git clone git@github.com:TomerAberbach/mano-simulator.git
   ```

3. Install [Maven](https://maven.apache.org)
4. Assuming the current working directory is the cloned repository, run the following command to package the application:

   ```sh
   $ mvn package
   ```

   An executable jar should appear at `target/mano-simulator-VERSION.jar`, where `VERSION` matches the version in `pom.xml`. Replace `VERSION` in and run the following command to run the application:

   ```sh
   $ java -jar target/mano-simulator-VERSION.jar
   ```

## Examples

See [example programs](examples.md).

## License

[MIT](https://github.com/TomerAberbach/mano-simulator/blob/main/license) © [Tomer Aberbach](https://github.com/TomerAberbach)
