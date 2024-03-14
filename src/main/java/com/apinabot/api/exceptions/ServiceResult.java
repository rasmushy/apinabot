package com.apinabot.api.exceptions;

import lombok.Getter;
/***
 * Results from API calls. The class is used to return the result of an API call.
 * The class can contain either the data or an exception
 *
 * @param <T> the type of the data
 * @author rasmushy
 */
@Getter
public class ServiceResult<T> {
    private T data;
    private Exception error;

    public ServiceResult(T data) {
        this.data = data;
        this.error = null;
    }

    public ServiceResult(Exception exception) {
        this.error = exception;
        this.data = null;
    }

    public boolean isSuccess() {
        return error == null;
    }

}
