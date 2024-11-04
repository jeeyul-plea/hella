package kr.plea.hella.global.exception;

import lombok.Getter;

@Getter
public class RootException extends RuntimeException {

    private final ExceptionCode code;

    public RootException(ExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
