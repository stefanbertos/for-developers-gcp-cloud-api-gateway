package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class UserFunction implements HttpFunction {
    static final Map<String, UserVO> users = new HashMap<>();

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        BufferedWriter writer = response.getWriter();

        // Default values avoid null issues (with switch/case) and exceptions from get() (optionals)
        String contentType = request.getContentType().orElse("");
        System.out.println("Content type is " + contentType);
        Gson gson = new Gson();

        switch (request.getMethod()) {
            case "GET":
                //Get all users
                writer.write(gson.toJson(users));
                response.setStatusCode(HttpURLConnection.HTTP_OK);
                break;
            case "POST":
                //Create user
                if (!"application/json".equals(contentType)) {
                    writer.write("Invalid or missing Content-Type header");
                    response.setStatusCode(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
                    break;
                }
                UserVO newUser = gson.fromJson(request.getReader(), UserVO.class);
                users.put(newUser.getUsername(), newUser);
                response.setStatusCode(HttpURLConnection.HTTP_CREATED);
                break;
            case "PUT":
                //Update user
                if (!"application/json".equals(contentType)) {
                    writer.write("Invalid or missing Content-Type header");
                    response.setStatusCode(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
                    break;
                }
                String userToUpdate = request.getPath().replace("/","");
                if (users.containsKey(userToUpdate)) {
                    UserVO updatedUser = gson.fromJson(request.getReader(), UserVO.class);
                    updatedUser.setUsername(userToUpdate);
                    users.put(userToUpdate, updatedUser);
                    writer.write(gson.toJson(updatedUser));
                    response.setStatusCode(HttpURLConnection.HTTP_OK);
                } else {
                    writer.write("Unable to update non-existend user");
                    response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
                }
                break;
            case "DELETE":
                String userToRemove = request.getPath().replace("/","");
                if (users.containsKey(userToRemove)) {
                    users.remove(userToRemove);
                    writer.write("User deleted");
                    response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
                } else {
                    writer.write("Unable to delete non-existend user");
                    response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
                }
                break;
            default:
                response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
                writer.write("Http method not supported");
                break;
        }
    }
}