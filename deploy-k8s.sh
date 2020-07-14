#!/usr/bin/env bash
LATEST_TAG=$(docker images robachmann/pod-lifecycle-notifier  --format "{{.Tag}}" | sort -r | head -n 1) && echo $LATEST_TAG
sed -i '' "s/pod-lifecycle-notifier:.*/pod-lifecycle-notifier:$LATEST_TAG/g" k8s/deployment.yml
CLUSTER_NAME=$(kubectl config current-context) && echo $CLUSTER_NAME
sed -i '' "s/clusterName:.*/clusterName: $CLUSTER_NAME/g" k8s/secret-callback.yml
kubectl apply -f k8s/secret-callback.yml
kubectl apply -f k8s/deployment.yml
