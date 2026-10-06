package com.shengyi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.code = 20000;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.code = 20000;
        result.message = "success";
        return result;
    }

    public static <T> Result<T> error(int code, String message){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
