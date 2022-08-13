# for-developers-gcp-cloud-api-gateway

What we want to build is
![diagram](diagram-simple.drawio.png?raw=true "Title")

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

Alternatively, you can send requests to this function using curl from another terminal window: (commands are escaped for windows) - change the host to http://localhost:8080
example is for the Cloud hosts
```
curl -i "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user"
curl -i -X POST -H "Content-Type: application/json" "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?key=" -d "{\"username\": \"ted\",\"password\": \"secret\",\"firstname\": \"Ted\",\"lastname\": \"Baker\",\"email\": \"tedbaker@example.com\"}"
curl -i -X PUT -H "Content-Type: application/json" "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?username=ted&key=" -d "{\"firstname\": \"Teddy\",\"lastname\": \"Norman\",\"email\": \"tnorman@example.com\"}"
curl -i -X DELETE "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?username=bob&key="

```

## Deploy cloud function
To deploy the function with an HTTP trigger, run the following command in the helloworld-gradle directory:
```
gcloud auth login
gcloud config set project PROJECT_ID
gcloud functions deploy user-function-manual --region europe-west1 --entry-point functions.UserFunction --runtime java17 --trigger-http --memory 512MB --timeout 90 --max-instances 1 --service-account user-function@api-gateway-359117.iam.gserviceaccount.com
gcloud functions add-iam-policy-binding user-function-manual --region=europe-west1 --member="serviceAccount:user-function@api-gateway-359117.iam.gserviceaccount.com" --role="roles/cloudfunctions.invoker"
```
where user-function-manual is the registered name by which your function will be identified in the console, and --entry-point specifies your function's fully qualified class name (FQN).

## View Cloud Function logs
To view logs for your function with the gcloud CLI, use the logs read command, followed by the name of the function:
```
gcloud functions logs read user-function-manual --region europe-west3 
```
