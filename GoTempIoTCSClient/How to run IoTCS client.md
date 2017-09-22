1. Make sure you have installed Java 8 and it is on the path

    `java -version`

2. Download IoTCS client libraries from 

    [Oracle IoT Cloud Service Client Software Libraries](http://www.oracle.com/technetwork/indexes/downloads/iot-client-libraries-2705514.html#javase)

3. Unzip the binaries to some folder <iotcs-client> (e.g.: _c:/IoTCS/client/17.3.5.0.0-11_)

4. Clone the github repository

    `git clone https://github.com/klrservices/GoTempReader.git`

5. Install the application

    _Linux_:
    ```
    cd GoTempReader
    ./gradlew -PiotcsClientDir=<iotcs-client> installDist
    ```

    _Windows_:
    ```
    cd GoTempReader
    gradlew.bat -PiotcsClientDir=<iotcs-client> installDist
    ```

6. Plug the Vernier's GoTemp temperature probe to any available USB port

7. Run the IoTCS client

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
    
8. To stop the application press enter