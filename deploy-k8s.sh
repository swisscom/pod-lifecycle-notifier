#!/usr/bin/env bash
CLUSTER_NAME=$(kubectl config current-context) && echo "CLUSTER_NAME=$CLUSTER_NAME"
sed -i '' "s/KUBERNETES_CLUSTER: .*/KUBERNETES_CLUSTER: \"$CLUSTER_NAME\"/g" k8s/notifier-config.yml
kubectl apply -f k8s/
kubectl rollout restart daemonset notifier-daemonset -n pod-lifecycle-notifier
