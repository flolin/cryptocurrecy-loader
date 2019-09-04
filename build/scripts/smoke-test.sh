#!/usr/bin/env bash

NOW=$(date +"%H:%M:%S")
echo "$NOW Running smoke test..."

n=0

until [[ $n -ge 5 ]]
do
   curl -s "http://localhost:8080/ping" | grep 'OK' &> /dev/null

   if [[ $? == 0 ]]; then
      echo "Smoke test passed."
      exit 0
   fi

   NOW=$(date +"%H:%M:%S")
   echo "$NOW Retrying..."

   n=$[$n+1]
   sleep 5
done

echo "Smoke test failed."
exit 1