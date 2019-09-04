#!/usr/bin/env bash

#setup
gcloud config set project [PROJECT_ID]
gcloud config set compute/zone us-central1-b

APP=cryptocurrency-loader
PROJECT_ID="$(gcloud config get-value project -q)"

docker build -t gcr.io/${PROJECT_ID}/${APP}:v1
gcloud auth configure-docker
docker push gcr.io/${PROJECT_ID}/${APP}:v1

#docker run --rm -p 8080:8080 gcr.io/${PROJECT_ID}/hello-app:v1 # local run

gcloud container clusters create my-cluster --num-nodes=2
#gcloud compute instances list
#gcloud container clusters get-credentials hello-cluster

kubectl run ${APP} --image=gcr.io/${PROJECT_ID}/${APP}:v1 --port 8080
kubectl expose deployment ${APP} --type=LoadBalancer --port 80 --target-port 8080
#kubectl get pods

#https://cloud.google.com/kubernetes-engine/docs/tutorials/hello-app

#cleanup
#kubectl delete service hello-web
#gcloud container clusters delete hello-cluster