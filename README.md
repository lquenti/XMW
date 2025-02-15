# XMW - XML but only Ws for us

An example XML project comprised of multiple tomcat based microservices.

Uses BaseX as a database layer, and XML for all interop.

XML technologies used:
- SAX
- StAX
- DOM
- XML Streaming
- XSLT
- XQuery (and thus XPath)
- JAXB

## Architecture
- `User` is the IDM from which all services fetch user data
- `EXA` is the courses/exams/lectures/... database
- `StudIP` is the platform to interact with lectures/exams... Fetches stuff from both `Exa` and `User`
- `Logger` is a server that gets an XML stream (via HTTP + heartbeat XML events for keepalive) from all services using SAX
- `LoggerClient` is a library to send events to the logger server

## Screenshots

![sc01](./screenshots/01.png)
![sc02](./screenshots/02.png)
![sc03](./screenshots/03.png)
![sc04](./screenshots/04.png)
![sc05](./screenshots/05.png)
![sc06](./screenshots/06.png)
![sc07](./screenshots/07.png)
![sc08](./screenshots/08.png)
![sc09](./screenshots/09.png)
![sc10](./screenshots/10.png)

## Conventions

- Every service `SERVICE` writes its data in `~/xmw_data/<SERVICE>`
  - No service shall assume that the folder (or its parent) already exists

- ALl on the same tomcat, with the following application context names:
  - `User -> /user`
  - `EXA -> /exa`
  - `StudIP -> /studip`
  - `Logger -> /logger`
