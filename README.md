# for-developers-gcp-function-java

For all the details see https://cloud.google.com/functions/docs/running/function-frameworks

The Rest api is inspired by https://help.sonatype.com/iqserver/automating/rest-apis/user-rest-api---v2

Install Google Cloud SDK as in guide at https://cloud.google.com/sdk/docs/install 


![diagram](diagram.drawio.png?raw=true "Title")

Run the following command to confirm that your function builds:
```
gradlew build
```

To test the function, run the following command:
```
gradlew runFunction -Prun.functionTarget=functions.UserFunction
```

If testing completes successfully, it displays the URL you can visit in your web browser to see the function in action: http://localhost:8080/. You should see a Hello World! message.

Alternatively, you can send requests to this function using curl from another terminal window: (commands are escaped for windows)
```
curl -i "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user"
curl -i -X POST -H "Content-Type: application/json" "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?key=AIzaSyCIH_cobHdF74Efap0o7uQsbwotCce2ETg" -d "{\"username\": \"ted\",\"password\": \"secret\",\"firstname\": \"Ted\",\"lastname\": \"Baker\",\"email\": \"tedbaker@example.com\"}"
curl -i -X PUT -H "Content-Type: application/json" "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?username=ted&key=AIzaSyCIH_cobHdF74Efap0o7uQsbwotCce2ETg" -d "{\"firstname\": \"Teddy\",\"lastname\": \"Norman\",\"email\": \"tnorman@example.com\"}"
curl -i -X DELETE "https://apigateway-eu-dkgv397r.ew.gateway.dev/v1/user?username=bob&key=AIzaSyCIH_cobHdF74Efap0o7uQsbwotCce2ETg"

```

To deploy the function with an HTTP trigger, run the following command in the helloworld-gradle directory:
```
gcloud auth login
gcloud config set project PROJECT_ID
gcloud functions deploy user-function-manual --region europe-west1 --entry-point functions.UserFunction --runtime java17 --trigger-http --memory 512MB --timeout 90 --max-instances 1 --service-account user-function@api-gateway-359117.iam.gserviceaccount.com
gcloud functions add-iam-policy-binding user-function-manual --region=europe-west1 --member="serviceAccount:user-function@api-gateway-359117.iam.gserviceaccount.com" --role="roles/cloudfunctions.invoker"
```
where user-function-manual is the registered name by which your function will be identified in the console, and --entry-point specifies your function's fully qualified class name (FQN).

To view logs for your function with the gcloud CLI, use the logs read command, followed by the name of the function:
```
gcloud functions logs read user-function-manual --region europe-west3 
```
