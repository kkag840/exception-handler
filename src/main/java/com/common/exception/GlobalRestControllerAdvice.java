package com.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalRestControllerAdvice {

	private final MessageSource messageSource;

	@Autowired
	public GlobalRestControllerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private Locale getLocaleFromRequest(WebRequest request) {
		String acceptLanguageHeader = ((ServletWebRequest) request).getRequest().getHeader("Accept-Language");
		return acceptLanguageHeader != null ? Locale.forLanguageTag(acceptLanguageHeader) : Locale.ENGLISH;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final WebRequest requset) {
		log.info("**** GlobalRestControllerAdvice ::: handleMethodArgumentTypeMismatchException :: Error Message : {} ****", ex.getMessage());
		Locale currentLocale = getLocaleFromRequest(requset);
		LocaleContextHolder.setLocale(currentLocale);
		ExceptionResponse exceptionMessageObj = new ExceptionResponse();
		exceptionMessageObj.setStatus(HttpStatus.BAD_REQUEST.value());
		exceptionMessageObj.setCode(DefaultErrorCodes.NOT_NULL_VALIDATION.getValue());
		exceptionMessageObj.setMessage(ex.getMessage());
		exceptionMessageObj.setError(ex.getClass().getCanonicalName());
		exceptionMessageObj.setPath(((ServletWebRequest) requset).getRequest().getServletPath());
		return new ResponseEntity<>(exceptionMessageObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, final WebRequest request) {
		log.info("**** GlobalRestControllerAdvice ::: handleMethodArgumentNotValidException :: Error Message : {} ****", ex.getMessage());

		Locale currentLocale = getLocaleFromRequest(request);
		LocaleContextHolder.setLocale(currentLocale);

		List<String> validationErrors = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());


		ExceptionResponse exceptionMessageObj = new ExceptionResponse();
		exceptionMessageObj.setStatus(HttpStatus.BAD_REQUEST.value());
		exceptionMessageObj.setCode(DefaultErrorCodes.VALIDATION_FAILED.getValue());
		exceptionMessageObj.setMessage(DefaultErrorCodes.VALIDATION_FAILED.name());
		exceptionMessageObj.setError(ex.getClass().getCanonicalName());
		exceptionMessageObj.setErrors(validationErrors);
		exceptionMessageObj.setPath(((ServletWebRequest) request).getRequest().getServletPath());

		return new ResponseEntity<>(exceptionMessageObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(final BusinessException exc,final WebRequest requset) {
		log.info("**** GlobalRestControllerAdvice ::: handleBusinessException :: Error Message : {} ****", exc.getMessage());
		Locale currentLocale = getLocaleFromRequest(requset);
		LocaleContextHolder.setLocale(currentLocale);
		String defatulErrorMessage = exc.getErrorCode().getDefaultMessage();
		try {
			defatulErrorMessage = messageSource.getMessage(exc.getMessage(), exc.getArgs(), currentLocale);
		}catch (Exception e) {
			log.info("Rest Controller Advice Exception {}",defatulErrorMessage);
		}
		return ResponseEntity.status(Objects.nonNull(exc.getStatus()) ? exc.getStatus() : HttpStatus.BAD_REQUEST)
				.body(new ExceptionResponse(exc.getStatus().value(), exc.getStatus().getReasonPhrase(), defatulErrorMessage,
						((ServletWebRequest) requset).getRequest().getServletPath(), exc.getCode(), exc.getMessage()));
	}
	@ExceptionHandler({
			SQLException.class,
	})
	public ResponseEntity<ExceptionResponse> handleSQLException(SQLException ex) {
		log.info("**** GlobalRestControllerAdvice ::: handleSQLException :: Error Message : {} ****", ex.getMessage());

		ExceptionResponse exceptionMessageObj = new ExceptionResponse();
		exceptionMessageObj.setStatus(HttpStatus.BAD_REQUEST.value());
		exceptionMessageObj.setCode(DefaultErrorCodes.DATABASE_EXCEPTION.getValue());
		exceptionMessageObj.setMessage(ex.getMessage());
		exceptionMessageObj.setError(ex.getClass().getCanonicalName());
		return new ResponseEntity<>(exceptionMessageObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ExceptionResponse> handleMethodValidationException(final HandlerMethodValidationException ex, final WebRequest request) {
		log.info("**** GlobalRestControllerAdvice ::: handleMethodValidationException :: Error Message : {} ****", ex.getMessage());

		Locale currentLocale = getLocaleFromRequest(request);
		LocaleContextHolder.setLocale(currentLocale);

        List<String> validationErrors = ex.getAllValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> {
                            String paramName = result.getMethodParameter()
                                    .getParameterName();
                            return paramName + ": " + error.getDefaultMessage();
                        })
                )
                .toList();

        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCode(DefaultErrorCodes.VALIDATION_FAILED.getValue());
        response.setMessage("Validation failed");
        response.setError(ex.getClass().getCanonicalName());
        response.setErrors(validationErrors);
        response.setPath(((ServletWebRequest) request).getRequest().getServletPath());

        return ResponseEntity.badRequest().body(response);
    }

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exc, final WebRequest request) {
		log.info("**** GlobalRestControllerAdvice ::: handleHttpMessageNotReadableException :: Error Message : {} ****", exc.getMessage());
		LocaleContextHolder.setLocale(getLocaleFromRequest(request));
		String message = exc.getMostSpecificCause().getMessage().replaceAll(".*?from String \"(.*?)\": (.*?)(\\n|$).*", "Invalid value '$1'. $2")
				.replaceAll(".*Unexpected character '(.*?)'.*line: (\\d+), column: (\\d+).*", "Malformed JSON: Unexpected character '$1' at line $2, column $3.")
				.replaceAll(".*Missing required creator property '(.*?)'.*", "Missing required field: '$1'.")
				.replaceAll(".*Unrecognized field \"(.*?)\".*", "Unknown field '$1' in request.")
				.replaceAll(".*Null value for property '(.*?)'.*", "Field '$1' cannot be null.")
				.replaceAll(".*Cannot deserialize value of type `(.*?)` from String \"(.*?)\".*", "Invalid value '$2'. Expected a valid $1.");

		ExceptionResponse exceptionMessageObj = new ExceptionResponse();
		exceptionMessageObj.setStatus(HttpStatus.BAD_REQUEST.value());
		exceptionMessageObj.setCode(DefaultErrorCodes.VALIDATION_FAILED.getValue());
		exceptionMessageObj.setMessage(message);
		exceptionMessageObj.setError(exc.getClass().getCanonicalName());
		exceptionMessageObj.setPath(((ServletWebRequest) request).getRequest().getServletPath());
		return new ResponseEntity<>(exceptionMessageObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

}
