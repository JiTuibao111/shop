package com.example.wikipro.common;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Result<T>{


    private Integer code;

    private String message;

    private T data;

    public Result(ResultStatus resultStatus, T data){
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }


    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result success(T data){

        return  new Result<>(ResultStatus.SUCCESS,data);
    }


    public static <T> Result<T> success(ResultStatus resultStatus,T data){
        return  new Result<T>(resultStatus,data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultStatus.SUCCESS.getCode(), message, data);
    }
    public static <T> Result<T> success(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> failure(){
        return  new Result<T>(ResultStatus.SERVER_ERROR,null);
    }


    public static <T> Result<T> failure(T data){
        return  new Result<T>(ResultStatus.SERVER_ERROR,data);
    }

     /* 返回失败，带有消息
     * @param message
     * @return
     */
    public static Result<?> failure(String message) {
        return new Result<>(ResultStatus.SERVER_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> failure(String message, T data) {
        return new Result<>(ResultStatus.SERVER_ERROR.getCode(), message, data);
    }


    public static <T> Result<T> failure(ResultStatus resultStatus) {
        return new Result<>(resultStatus, null);
    }

    public static Result<?> failure(Integer code, String message) {
        return new Result<>(code, message, null);
    }


    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }


    public static  <T> Result<T> failure(ResultStatus resultStatus,T data){
        return  new Result<>(resultStatus,data);
    }


    public boolean isSuccess() {
        return code == ResultStatus.SUCCESS.getCode();
    }




}
