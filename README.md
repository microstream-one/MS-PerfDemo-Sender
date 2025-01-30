# MS-PerfDemo-Request-Sender-WebApplication

<img width="1309" alt="PerfDemoArchitecture" src="https://github.com/user-attachments/assets/40a4d1d5-ccc5-47b8-a867-6de278f89bf7" />

This application configures the data to be tested and also send the requests in a multithreaded for getting the different metrics and measurements.

## Links to other part projects:
1. REST Proxy EclipseStore (Blue on the left): https://github.com/microstream-one/MS-PerfDemo-REST-ES/tree/master
2. REST Proxy PostgreSQL (yellow on the right): https://github.com/microstream-one/MS-PerfDemo-REST-PostgreSQL

## Setup

1. Build an image by using the following command:
   ```clean package install "production"```
3. Copy that image to your docker environment
4. Use the docker compose file from sender project here to deploy whole infrastructure:
   https://github.com/microstream-one/MS-PerfDemo-Sender/tree/master/Microstream-Config/performance-demo
5. Use following command to start containers: ```docker-compose up -d```

## Usage of the Frontend

1. Login with Username / Password --> 123/123 as default
2. Main screen should look like following:

<img width="1196" alt="PerformanceTest_doc" src="https://github.com/user-attachments/assets/e3d205e2-54f0-461b-a587-bd4ad30c3ece" />
