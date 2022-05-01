package io.bsamartins.graphql.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SecuredDirectiveEvaluator(private val beanFactory: BeanFactory, @Value("\${io.bsamartins.roles:}") private val roles: Set<String>) {
    private val logger = LoggerFactory.getLogger(this::class.java)
//    fun evaluateExpression(expressionValue: String, userUuid: String?, fieldName: String): Boolean {
//        val context = StandardEvaluationContext()
//        context.setBeanResolver(BeanFactoryResolver(beanFactory))
//        context.setVariable("userUuid", userUuid)
//        val expressionParser: ExpressionParser = SpelExpressionParser()
//        val expression = expressionParser.parseExpression(expressionValue)
//        return expression.getValue(context, Boolean::class.java)!!
//    }
    fun evaluateExpression(role: String, userUuid: String?, fieldName: String, path: String?): Boolean {
        logger.info("Evaluating path [{}] requiring role [{}]", path, role)
        return roles.contains(role)
    }
}
