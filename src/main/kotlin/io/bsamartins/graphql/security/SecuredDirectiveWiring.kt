package io.bsamartins.graphql.security

import com.netflix.graphql.dgs.DgsDirective
import com.netflix.graphql.dgs.context.DgsContext
import graphql.execution.DataFetcherResult
import graphql.language.StringValue
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@DgsDirective
class SecuredDirectiveWiring(private val directiveEvaluator: SecuredDirectiveEvaluator) : SchemaDirectiveWiring {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onObject(environment: SchemaDirectiveWiringEnvironment<GraphQLObjectType>): GraphQLObjectType {
        val field = environment.element
        val parentType = environment.fieldsContainer
        log.info("onObject parent={}, field={}", parentType.name, field.name)

        if (field.getDirective(SECURED_DIRECTIVE) == null) {
            return field
        }
        val expressionValue = field.getDirective(SECURED_DIRECTIVE)
            .getArgument(REQUIRES_ATTR).argumentValue.value as StringValue
        for (fieldDefinition in field.fieldDefinitions) {
            val originalDataFetcher = environment.codeRegistry.getDataFetcher(parentType, fieldDefinition)
            val authDataFetcher = DataFetcher { dataFetchingEnvironment ->
                val path = dataFetchingEnvironment.executionStepInfo.path
                log.info("onObject path={}", path)
                val requestData = DgsContext.getRequestData(dataFetchingEnvironment)
                val roles = requestData!!.headers!!["X-USER-ROLES"]?.toSet().orEmpty()
                val resultBuilder = DataFetcherResult.newResult<Any>()
                val result =
                    directiveEvaluator.evaluateObject(fieldDefinition.name, path.toString(), expressionValue.value, roles)
                if (result) {
                    return@DataFetcher originalDataFetcher[dataFetchingEnvironment]
                } else {
//                    val locations = listOf(field.definition.sourceLocation)
//                    val resultPath = dataFetchingEnvironment.executionStepInfo.path
                    return@DataFetcher resultBuilder.data(null).build()
//                    return@DataFetcher resultBuilder.error(AccessDeniedError(locations, resultPath)).build()
                }
            }
            // now change the field definition to have the new authorising data fetcher
            environment.codeRegistry.dataFetcher(parentType, fieldDefinition, authDataFetcher)
        }
        return field
    }

    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = environment.element
        val parentType = environment.fieldsContainer
        log.info("onField parent={}, field={}", parentType.name, field.name)

        // build a data fetcher that first checks authorisation roles before then calling the original data fetcher
        val originalDataFetcher = environment.codeRegistry.getDataFetcher(parentType, field)
        if (field.getDirective(SECURED_DIRECTIVE) == null) {
            return field
        }
        val authDataFetcher = DataFetcher { dataFetchingEnvironment ->
            val path = dataFetchingEnvironment.executionStepInfo.path
            log.info("onField path={}", path)
            val requestData = DgsContext.getRequestData(dataFetchingEnvironment)
            val roles = requestData!!.headers!!["X-USER-ROLES"]?.toSet().orEmpty()
            val expressionValue = dataFetchingEnvironment.fieldDefinition
                .getDirective(SECURED_DIRECTIVE)
                .getArgument(REQUIRES_ATTR).argumentValue.value as StringValue
            val resultBuilder = DataFetcherResult.newResult<Any>()
            val result = directiveEvaluator.evaluateField(field.name, path.toString(), expressionValue.value, roles)
            if (result) {
                return@DataFetcher originalDataFetcher[dataFetchingEnvironment]
            } else {
                return@DataFetcher resultBuilder.data(null).build()
            }
        }

        // now change the field definition to have the new authorising data fetcher
        environment.codeRegistry.dataFetcher(parentType, field, authDataFetcher)
        return field
    }

    companion object {
        const val SECURED_DIRECTIVE = "secured"
        const val REQUIRES_ATTR = "requires"
    }
}
