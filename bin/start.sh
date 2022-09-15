#!/bin/bash

git pull

nohup ./mvnw spring-boot:run &>> github-webhook-listener.out &
