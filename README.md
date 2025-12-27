# Exception Handler

A Spring Boot based exception handling library that provides centralized exception handling and localized error messages for microservices.

## Features

- **Global Exception Handler** - Centralized exception handling using Spring's `@RestControllerAdvice`
- **Custom Business Exceptions** - Type-safe exception handling with custom error codes
- **Localization Support** - Multi-language error messages (English, Hindi)
- **Structured Error Responses** - Consistent error response format across all endpoints
- **Comprehensive Logging** - Detailed logging for all exception handlers

## Supported Exceptions

- `MethodArgumentTypeMismatchException` - Handles type mismatch in request parameters
- `MethodArgumentNotValidException` - Handles validation errors in request body
- `HandlerMethodValidationException` - Handles method parameter validation errors
- `HttpMessageNotReadableException` - Handles malformed JSON requests
- `SQLException` - Handles database errors
- `BusinessException` - Custom business logic exceptions

## Configuration

The application uses the following configuration files:
- `application.properties` - Main application configuration
- `messages.properties` - Default English error messages
- `messages_en_IN.properties` - English (India) messages
- `messages_hi_IN.properties` - Hindi (India) messages

## Error Codes

| Code | Message |
|------|---------|
| NOT_NULL_VALIDATION | Field validation failed |
| DATABASE_EXCEPTION | Database error occurred |
| VALIDATION_FAILED | Validation errors occurred |

## Project Structure

```
src/
├── main/
│   ├── java/com/common/exception/
│   │   ├── BusinessException.java
│   │   ├── DefaultErrorCodes.java
│   │   ├── ErrorCodes.java
│   │   ├── ExceptionHandlerApplication.java
│   │   ├── ExceptionResponse.java
│   │   └── GlobalRestControllerAdvice.java
│   └── resources/
│       ├── application.properties
│       ├── messages.properties
│       ├── messages_en_IN.properties
│       └── messages_hi_IN.properties
└── test/
    └── java/com/common/exception/
        └── ExceptionHandlerApplicationTests.java
```

## Building

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## License

MIT License

