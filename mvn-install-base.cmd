cd todo-parent
mvn -e clean install
cd ..
cd todo-liberty-server
mvn -e clean install
cd ..
cd todo-service-api
mvn -e clean install
cd ..
cd todo-service-inmemory-impl
mvn -e clean install
cd ..
cd todo-service-jpa-impl
mvn -e clean install
cd ..
cd todo-service-mongodb-impl
mvn -e clean install
cd ..
