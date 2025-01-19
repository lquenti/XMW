# XMW

XML but only Ws for us

## Conventions

- Every service `SERVICE` writes its data in `~/xmw_data/<SERVICE>`
  - No service shall assume that the folder (or its parent) already exists

- ALl on the same tomcat, with the following application context names:
  - `User -> /user`
  - `EXA -> /exa`
  - `StudIP -> /studip`
  - `Logger -> /logger`