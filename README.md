# grpc-demo-java
gRPC demo on Java which includes

1. SSL/TLS based secure communication using HTTP/2
2. Unary call
3. Server streaming
4. Client streaming
5. Bi-directional streaming
6. Binary data communication
7. Blocking and Non-blocking clients

## Steps to run

1. Generate cert.pem and key.pem and place in src/main/resources in server and client
    `openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes` 
2. Run maven build `mvn clean install`
3. Run the server `java -jar grpc-server/target/grpc-server-1.0.0.jar`
4. Run the client with different options - `java -jar grpc-client/target/grpc-client-1.0.0.jar <blocking/nonblocking> <option>`,
    1 - sendMetadata
    2 - getUserByEmail
    3 - getAllUsers
    4 - saveUser
    5 - saveAllUsers
    6 - addPhoto
