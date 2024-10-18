package com.example.demo.exception;

public class ProductDeletionException extends RuntimeException {
    
    // 提供一个带有自定义消息的构造函数
    public ProductDeletionException(String message) {
        super(message); // 调用父类 RuntimeException 的构造函数，将消息传递给它
    }

    // 你可以根据需要添加更多构造函数，例如带有异常原因 (cause)
    public ProductDeletionException(String message, Throwable cause) {
        super(message, cause); // 调用父类的构造函数，传递消息和原因
    }
}

