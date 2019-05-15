1. Make sure you have installed Java 8 and it is on the path

    `java -version`

2. Download IoTCS client libraries from 

    [Oracle IoT Cloud Service Client Software Libraries](https://www.oracle.com/technetwork/indexes/downloads/iot-client-libraries-2705514.html#javase)

3. Unzip the binaries to some folder `<iotcs-client-binaries-folder>` (e.g.: _c:/IoTCS/client_)

4. Install the device library into a local maven repository

     _Linux_:
     ```bash
     mvn install:install-file -Dfile=<iotcs-client-binaries-folder>/javase/lib/device-library.jar -DgroupId=com.oracle.iot.client -DartifactId=device-library -Dversion=19.1.5 -Dpackaging=jar
     ```
    
    
     _Windows_:
     ```bash
     mvn install:install-file -Dfile=<iotcs-client-binaries-folder>\javase\lib\device-library.jar -DgroupId=com.oracle.iot.client -DartifactId=device-library -Dversion=19.1.5 -Dpackaging=jar
     ```
5. Install the IoTCS server certificate (e.g exported from the browser as base64 encoded CER) into a cacerts of your Java installation

     _Linux_:
     ```bash
     $JAVA_HOME/bin/keytool -import -file "iotserver.cer" -keystore "$JAVA_HOME/jre/lib/security/cacerts" -alias "iotserver"
     ```


     _Windows_:
     ```bash
     %JAVA_HOME%\bin\keytool -import -file "iotserver.cer" -keystore "%JAVA_HOME%\jre\lib\security\cacerts" -alias "iotserver"
     ```
5. Clone the github repository

    `git clone https://github.com/klrservices/GoTempReader.git`

6. Install the application

    _Linux_:
    ```
    cd GoTempReader
    ./gradlew installDist
    ```

    _Windows_:
    ```
    cd GoTempReader
    gradlew.bat installDist
    ```

7. Plug the Vernier's GoTemp temperature probe to any available USB port

8. Run the IoTCS client

    _Linux_:
    ```
    cd GoTempIoTCSClient/build/install/GoTempIoTCSClient
    bin/GoTempIoTCSClient <provisioning-file> <password>
    ```
    
    _Windows_:
    ```
    cd GoTempIoTCSClient\build\install\GoTempIoTCSClient
    bin\GoTempIoTCSClient.bat <provisioning-file> <password>
    ```
    
9. To stop the application press enter
