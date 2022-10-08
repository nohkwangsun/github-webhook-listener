#!/bin/bash

docker pull ghcr.io/nohkwangsun/bookstore:main
docker rm -f bookstore
docker run -d -p10000:10000 --name bookstore -v database:/bookstore/database --pull never ghcr.io/nohkwangsun/bookstore:main
