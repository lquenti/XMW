#!/bin/bash

# auth
curl -X POST -d "username=hbrosen&password=hunter2" http://localhost:8080/User_war_exploded/auth

# create

# No PW
curl -X POST \
  http://localhost:8080/User_war_exploded/create \
  -H 'Content-Type: application/xml' \
  -d '<User username="hbrosen2">
       <name>Brosenne</name>
       <firstname>Hendrik</firstname>
       <faculty>Computer Science</faculty>
       <group id="g_lecturer">Lecturer</group>
       <group id="g_employee">Employee</group>
     </User>'

# PW
curl -X POST \
  http://localhost:8080/User_war_exploded/create \
  -H 'Content-Type: application/xml' \
  -d '<User username="hbrosen2">
       <name>Brosenne</name>
       <firstname>Hendrik</firstname>
       <password>hunter2</password>
       <faculty>Computer Science</faculty>
       <group id="g_lecturer">Lecturer</group>
       <group id="g_employee">Employee</group>
     </User>'

# invlaid xml
curl -X POST \
  http://localhost:8080/User_war_exploded/create \
  -H 'Content-Type: application/xml' \
  -d '<User username="hbrosen2">
       <name>Brosenne</name>
       <firstname>Hendrik</firstname>
       <password>hunter2</password>
       <faculty>Computer Science</faculty>
       <group id="g_lecturer">Lecturer</group>
       <group id="g_employee">Employee</group>
     </Use>'

