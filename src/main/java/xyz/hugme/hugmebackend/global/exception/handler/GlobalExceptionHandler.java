package xyz.hugme.hugmebackend.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.hugme.hugmebackend.global.exception.BusinessException;
import xyz.hugme.hugmebackend.global.exception.dto.ErrorResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        printLog(e, request);

        StringBuilder sb = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append("\n");
        }
        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * @ModelAttribute 으로 binding error 발생시 BindException 발생한다.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException e, HttpServletRequest request) {
        printLog(e, request);

        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append("\n");
        }

        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, sb.toString());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e, HttpServletRequest request){
        printLog(e, request);
        return createErrorResponse(e.getStatusCode(), e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e, HttpServletRequest request){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("예외처리 범위 외의 오류 발생. " + httpStatus.toString()); // enum.name() enum.toString() 차이 기억하자
        printLog(e, request);
        e.printStackTrace();
        return createErrorResponse(httpStatus.value(), httpStatus, e.getMessage());
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponse(int statusCode, HttpStatus httpStatus, String errorMessage) {
        ErrorResponseDto errDto = new ErrorResponseDto(statusCode, httpStatus, errorMessage);
        return ResponseEntity.status(httpStatus).body(errDto);
    }

    private void printLog(Exception e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName() + ": 발생, 에러 메시지: "+ e.getMessage() + ",요청 METHOD " + request.getMethod() + ", 요청 url + " + request.getRequestURI());
    }


}

















