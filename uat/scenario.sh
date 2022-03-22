#!/usr/bin/env bash

hostname='localhost'
port=$1

#Wait for application to start
sleep 5

#ping app url
status_code=$(curl -s -o /dev/null -w "%{http_code}" "localhost:${port}/")

if [ ${status_code} == 200 ]
then
  echo "PASS: ${hostname}:${port} is reachable"
else
  echo "FAIL: ${hostname}:${port} is unreachable"
  exit 1
fi