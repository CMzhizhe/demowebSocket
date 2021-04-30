package com.dhh.websocket.exceptionhandle;

public class ExceptionHandle {
    //unchecked异常会自动传递给 onError
    public static class ServerException extends RuntimeException {
        public int code;
        public String message;

        public ServerException(int code,String message){
            this.code = code;
            this.message = message;
        }
    }
}
