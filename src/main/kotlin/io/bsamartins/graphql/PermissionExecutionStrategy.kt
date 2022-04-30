package io.bsamartins.graphql

import graphql.ExecutionResult
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStrategyParameters
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
@Qualifier("query")
class PermissionExecutionStrategy : AsyncExecutionStrategy() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): CompletableFuture<ExecutionResult> {
//        logger.info("parameters={}", parameters)
//        logger.info("field={}, fields={}", parameters.field, parameters.fields.keys)
        return super.execute(executionContext, parameters)
    }
}
