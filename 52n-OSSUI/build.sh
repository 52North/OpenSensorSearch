sudo rm /var/lib/tomcat6/webapps/OSSUI.war
sudo rm -r /var/lib/tomcat6/webapps/OSSUI
mvn install
sudo mv ./target/OSSUI.war /var/lib/tomcat6/webapps
