package com.reactioncommerce.exception;

import com.reactioncommerce.tags.exception.DuplicateSlugException;
import com.reactioncommerce.tags.exception.TagNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {
    
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof TagNotFoundException) {
            return GraphqlErrorBuilder.newError()
                .errorType(graphql.ErrorType.DataFetchingException)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
        } else if (ex instanceof DuplicateSlugException) {
            return GraphqlErrorBuilder.newError()
                .errorType(graphql.ErrorType.ValidationError)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
        }
        return null;
    }
}
