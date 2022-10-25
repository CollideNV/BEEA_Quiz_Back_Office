package be.collide.quizbackoffice.resource.error;


public record ErrorMessage(int httpStatus, String message) {
}