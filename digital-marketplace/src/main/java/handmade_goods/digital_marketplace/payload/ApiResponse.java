package handmade_goods.digital_marketplace.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import handmade_goods.digital_marketplace.model.review.Review;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private T data;
    private String message;
    private String timestamp;

    public ApiResponse() {
        this.timestamp = Instant.now().toString();
    }

    public ApiResponse(String status, T data, String message) {
        this();
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, message);
    }

    public static ApiResponse<String> success(String message) {
        return new ApiResponse<>("success", null, message);
    }

    public static ApiResponse<String> error(String message) {
        return new ApiResponse<>("error", null, message);
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}