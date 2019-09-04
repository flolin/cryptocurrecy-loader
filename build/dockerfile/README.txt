#docker build -t /vertx/crypto .
#docker run -t -i -p 8080:8080 /vertx/crypto
#docker images | awk '{print $3}' | grep -v #name# | xargs docker rmi -f