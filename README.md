# for-developers-gcp-cloud-api-gateway

What we want to build is
![diagram](diagram-simple.drawio.png?raw=true "Title")

project api-gateway-359117 is the example project used in the tutorial, you should use your own project id.

# Cloud Function step by step

## Documentation
- For all the details see https://cloud.google.com/functions/docs/running/function-frameworks
- The Rest api is inspired by https://help.sonatype.com/iqserver/automating/rest-apis/user-rest-api---v2

## Installation
Install Google Cloud SDK as in guide at https://cloud.google.com/sdk/docs/install 

## Build Cloud Function locally 
Run the following command to confirm that your function builds:
```
gradlew build
```
## Test Cloud Function locally
To test the function, run the following command:
```
gradlew runFunction -Prun.functionTarget=functions.UserFunction
```

Alternatively, you can send requests to this function using curl from another terminal window: (commands are escaped for windows) 
```
curl -i "http://localhost:8080"
curl -i -X POST -H "Content-Type: application/json" "http://localhost:8080" -d "{\"username\": \"ted\",\"password\": \"secret\",\"firstname\": \"Ted\",\"lastname\": \"Baker\",\"email\": \"tedbaker@example.com\"}"
curl -i -X PUT -H "Content-Type: application/json" "http://localhost:8080?username=ted" -d "{\"firstname\": \"Teddy\",\"lastname\": \"Norman\",\"email\": \"tnorman@example.com\"}"
curl -i -X DELETE "http://localhost:8080?username=bob"
curl -i -X DELETE "http://localhost:8080?username=ted"
```

## Deploy cloud function
To deploy the function with an HTTP trigger, run the following command in the helloworld-gradle directory:
```
gcloud auth login
gcloud config set project PROJECT_ID
gcloud functions deploy user-function-manual --region europe-west1 --entry-point functions.UserFunction --runtime java17 --trigger-http --memory 512MB --timeout 90 --max-instances 1 --service-account user-function@api-gateway-360218.iam.gserviceaccount.com
gcloud functions add-iam-policy-binding user-function-manual --region=europe-west1 --member="serviceAccount:user-function@api-gateway-360218.iam.gserviceaccount.com" --role="roles/cloudfunctions.invoker"
```
where user-function-manual is the registered name by which your function will be identified in the console, and --entry-point specifies your function's fully qualified class name (FQN).

## View Cloud Function logs
To view logs for your function with the gcloud CLI, use the logs read command, followed by the name of the function:
```
gcloud functions logs read user-function-manual --region europe-west1 
```

# Google Api Gateway
## Enable APIs
API Gateway requires that you enable the following Google services:
API Gateway API
Service Management API
Service Control API
and this can be done by using following commands:
```
gcloud services enable apigateway.googleapis.com
gcloud services enable servicemanagement.googleapis.com
gcloud services enable servicecontrol.googleapis.com
```
## Create API
Enter the following command, where:
API_ID specifies the name of your API. See API ID requirements for API naming guidelines.
PROJECT_ID specifies the name of your Google Cloud project.
```
gcloud api-gateway apis create user-api --project=api-gateway-360218
```
## Create API Config
Before API Gateway can be used to manage traffic to your deployed API backend, it needs an API config.
```
gcloud api-gateway api-configs create user-api-config --api=user-api --openapi-spec=user-api.yaml --project=api-gateway-360218 --backend-auth-service-account=user-function@api-gateway-359117.iam.gserviceaccount.com
```
## Create API Gateway
Now deploy the API config on a gateway. Deploying an API config on a gateway defines an external URL that API clients can use to access your API.
```
gcloud api-gateway gateways create apigateway-eu --api=user-api --api-config=user-api-config --location=europe-west1 --project=api-gateway-360218
```
## Enable your API
```
gcloud services enable user-api-1yw8dyurd1ka5.apigateway.api-gateway-360218.cloud.goog
```
## Create API keys
https://cloud.google.com/docs/authentication/api-keys#creating_an_api_key
## Test you API
```
curl -i "https://apigateway-eu-3t8e894t.ew.gateway.dev/v1/user"
curl -i -X POST -H "Content-Type: application/json" "https://apigateway-eu-3t8e894t.ew.gateway.dev/v1/user?key=" -d "{\"username\": \"ted\",\"password\": \"secret\",\"firstname\": \"Ted\",\"lastname\": \"Baker\",\"email\": \"tedbaker@example.com\"}"
curl -i -X PUT -H "Content-Type: application/json" "https://apigateway-eu-3t8e894t.ew.gateway.dev/v1/user?username=ted&key=" -d "{\"firstname\": \"Teddy\",\"lastname\": \"Norman\",\"email\": \"tnorman@example.com\"}"
curl -i -X DELETE "https://apigateway-eu-3t8e894t.ew.gateway.dev/v1/user?username=bob&key="
```
## Cleanup
```
gcloud api-gateway gateways delete apigateway-eu --location=europe-west1 --project=api-gateway-360218
gcloud api-gateway api-configs delete user-api-config --api=user-api --project=api-gateway-360218
gcloud api-gateway apis delete user-api --project=api-gateway-360218
gcloud functions delete user-function-manual --region=europe-west1
```