#!/usr/bin/env bash

hostname='localhost'
url=$1

#Wait for application to start
sleep 5

#ping app url
status_code=$(curl -o /dev/null -s -w "%{http_code}" ${url})

echo ${status_code}

if [ ${status_code} == 200 ];
then
  echo "PASS: ${hostname}:${port} is reachable"
else
  echo "FAIL: ${hostname}:${port} is unreachable"
  exit 1
fi