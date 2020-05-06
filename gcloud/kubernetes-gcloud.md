gcloud auth login
gcloud config set project jobrunr-tutorial-salary-slip
gcloud config set compute/zone europe-west1

gcloud services enable container.googleapis.com sqladmin.googleapis.com

gcloud container clusters create salary-slip-service --num-nodes=2
gcloud container clusters list
gcloud container clusters describe salary-slip-service
gcloud sql instances create "salary-slip-service-db" --database-version POSTGRES_9_6 --region "europe-west1" --tier db-f1-micro --storage-type HDD
gcloud sql instances list
gcloud sql databases create "salary-slip-database" --instance="salary-slip-service-db"
gcloud sql users set-password postgres --instance "salary-slip-service-db" --password "salary-slip-database-passw0rd"
gcloud iam service-accounts create cloudsql-proxy --display-name cloudsql-proxy
gcloud iam service-accounts list --filter=displayName:cloudsql-proxy --format='value(email)'
SA_EMAIL=$(gcloud iam service-accounts list \
    --filter=displayName:cloudsql-proxy \
    --format='value(email)')
gcloud projects add-iam-policy-binding jobrunr-tutorial-salary-slip \
    --role roles/cloudsql.client \
    --member serviceAccount:$SA_EMAIL
    
kubectl create secret generic cloudsql-db-credentials \
    --from-literal username=salary-slip-user \
    --from-literal password=salary-slip-database-passw0rd
    
    
# Cleanup
gcloud projects remove-iam-policy-binding jobrunr-tutorial-salary-slip \
    --role roles/cloudsql.client \
    --member serviceAccount:$SA_EMAIL    
gcloud iam service-accounts delete $SA_EMAIL
gcloud sql instance delete "salary-slip-service-db"
gcloud container clusters delete salary-slip-service



# With terraform
## build docker image
jibDockerbuild
gcloud auth configure-docker
docker push gcr.io/jobrunr-tutorial-salary-slip/jobrunr-example-paycheck:1.0



gcloud beta billing accounts list

export TF_VAR_billing_account=00FF6E-E8038B-BEFD5F
export TF_ADMIN=jobrunr-admin-tutorial

gcloud config set project jobrunr-tutorial-salary-slip
gcloud beta billing projects link jobrunr-tutorial-salary-slip --billing-account ${TF_VAR_billing_account}
gcloud iam service-accounts create terraform --display-name "Terraform admin account"
gcloud iam service-accounts list
gcloud iam service-accounts keys create ~/.config/gcloud/jobrunr-tutorial-salary-slip-terraform-admin.json --iam-account=terraform@jobrunr-tutorial-salary-slip.iam.gserviceaccount.com
gcloud projects add-iam-policy-binding jobrunr-tutorial-salary-slip --member='serviceAccount:terraform@jobrunr-tutorial-salary-slip.iam.gserviceaccount.com' --role='roles/editor'
gcloud projects add-iam-policy-binding jobrunr-tutorial-salary-slip --member='serviceAccount:terraform@jobrunr-tutorial-salary-slip.iam.gserviceaccount.com' --role='roles/resourcemanager.projectIamAdmin'
export TF_CREDS=~/.config/gcloud/jobrunr-tutorial-salary-slip-terraform-admin.json
export GOOGLE_APPLICATION_CREDENTIALS=${TF_CREDS}

gcloud services enable sqladmin.googleapis.com && gcloud services enable container.googleapis.com

gcloud container clusters get-credentials jobrunr-tutorial-salary-slip --region europe-west1


## TODO:
[x] add support for cloudsql?
[x] test locally using cloudsql
- build to docker image
- test in terraform kubernetes