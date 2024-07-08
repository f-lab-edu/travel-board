package com.user.utils.response;

import com.user.utils.error.ErrorMessage;
import com.user.utils.error.ErrorType;
import lombok.Getter;

import static com.user.utils.response.ResultType.ERROR;
import static com.user.utils.response.ResultType.SUCCESS;

@Getter
public class ApiResponse<T> {

    private final ResultType result;
    private final T data;
    private final ErrorMessage error;

    private ApiResponse(ResultType result, T data, ErrorMessage error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(SUCCESS, null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS, data, null);
    }

    public static ApiResponse<Void> error(ErrorType error) {
        return new ApiResponse<>(ERROR, null, new ErrorMessage(error));
    }

    public static ApiResponse<Void> error(ErrorType error, Object errorData) {
        return new ApiResponse<>(ERROR, null, new ErrorMessage(error, errorData));
    }

}
