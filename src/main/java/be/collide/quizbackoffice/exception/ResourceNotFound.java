package be.collide.quizbackoffice.exception;

import software.amazon.awssdk.http.HttpStatusCode;

public class ResourceNotFound extends CustomException {
    public ResourceNotFound(String message) {
        super(HttpStatusCode.NOT_FOUND, message);
    }
}
