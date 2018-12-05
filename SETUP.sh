cd mason
mvn package
mvn install
cd ..
ln -sf $(pwd)/mason/target/mason*.jar $(pwd)/contrib/geomason/local-repo/mason.jar
cd contrib/geomason
./SETUP.sh
mvn package
mvn install
cd ..
