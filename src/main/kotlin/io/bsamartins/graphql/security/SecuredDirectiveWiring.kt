package io.bsamartins.graphql.security

import com.netflix.graphql.dgs.DgsDirective
import com.netflix.graphql.dgs.context.DgsContext
import graphql.execution.DataFetcherResult
import graphql.language.StringValue
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
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

        if (!field.hasDirective(SECURED_DIRECTIVE)) {
            return field
        }
        val requires = field.requiredRole()!!
        for (fieldDefinition in field.fieldDefinitions) {
            val originalDataFetcher = environment.codeRegistry.getDataFetcher(parentType, fieldDefinition)
            val authDataFetcher = DataFetcher { dataFetchingEnvironment ->
                val path = dataFetchingEnvironment.executionStepInfo.path
                log.info("onObject path={}", path)
                val requestData = DgsContext.getRequestData(dataFetchingEnvironment)
                val roles = requestData!!.headers!!["X-USER-ROLES"]?.firstOrNull()?.split(",")?.toSet().orEmpty()
                val resultBuilder = DataFetcherResult.newResult<Any>()
                val result =
                    directiveEvaluator.evaluateObject(fieldDefinition.name, path.toString(), requires, roles)
                if (result) {
                    return@DataFetcher originalDataFetcher[dataFetchingEnvironment]
                } else {
                    log.info("Skipping field: {}", path)
                    return@DataFetcher resultBuilder.data(null).build()
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
        if (!field.hasDirective(SECURED_DIRECTIVE) && !parentType.hasDirective(SECURED_DIRECTIVE)) {
            return field
        }
        val authDataFetcher = DataFetcher { dataFetchingEnvironment ->
            val path = dataFetchingEnvironment.executionStepInfo.path
            log.info("onField path={}", path)
            val requestData = DgsContext.getRequestData(dataFetchingEnvironment)
            val roles = requestData!!.headers!!["X-USER-ROLES"]?.firstOrNull()?.split(",")?.toSet().orEmpty()
            val typeRequires = dataFetchingEnvironment.fieldType.requiredRole()
            val fieldRequires = dataFetchingEnvironment.fieldDefinition.requiredRole()
            val resultBuilder = DataFetcherResult.newResult<Any>()
            val result = directiveEvaluator.evaluateField(
                fieldName = field.name,
                path = path.toString(),
                requiredFieldRole = fieldRequires,
                requiredTypeRole = typeRequires,
                roles = roles
            )
            if (result) {
                return@DataFetcher originalDataFetcher[dataFetchingEnvironment]
            } else {
                log.info("Skipping field: {}", path)
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

private fun GraphQLFieldDefinition.requiredRole(): String? {
    val value = this.getDirective(SecuredDirectiveWiring.SECURED_DIRECTIVE)
        .getArgument(SecuredDirectiveWiring.REQUIRES_ATTR).argumentValue.value as StringValue
    return value.value
}

private fun GraphQLOutputType.requiredRole(): String? {
    val directive = if (this is GraphQLObjectType) {
        this.getDirective(SecuredDirectiveWiring.SECURED_DIRECTIVE)
    } else null

    val value = directive?.getArgument(SecuredDirectiveWiring.REQUIRES_ATTR)?.argumentValue?.value as StringValue?
    return value?.value
}

private fun GraphQLFieldsContainer.hasDirective(directiveName: String): Boolean {
    if (this is GraphQLObjectType) {
        return this.hasDirective(directiveName)
    }
    return false
}
