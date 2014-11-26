#!/bin/bash
# use this script to package the WAR and start a test liberty test server running the WAR
mvn -e -DskipTests clean pre-integration-test
echo
echo You can now access your application at http://localhost:9080/