FROM mongo:latest

# Generate keyfile
RUN mkdir -p /data/key \
  && openssl rand -base64 756 > /data/key/replica-set.key

# Modify keyfile permissions
RUN chown mongodb:mongodb /data/key/replica-set.key \
  && chmod 400 /data/key/replica-set.key
  

# Create app directory
WORKDIR /usr/src/configs

# Install app dependencies
COPY replicaSet.js .
COPY setup-mongo.sh .

RUN ["chmod", "+x", "./setup-mongo.sh"]
