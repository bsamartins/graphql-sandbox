package io.bsamartins.graphql.security

import org.springframework.beans.factory.BeanFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Component
class SecuredDirectiveEvaluator(private val beanFactory: BeanFactory) {
    // "admin"
    val roles: Set<String> = setOf()
//    fun evaluateExpression(expressionValue: String, userUuid: String?, fieldName: String): Boolean {
//        val context = StandardEvaluationContext()
//        context.setBeanResolver(BeanFactoryResolver(beanFactory))
//        context.setVariable("userUuid", userUuid)
//        val expressionParser: ExpressionParser = SpelExpressionParser()
//        val expression = expressionParser.parseExpression(expressionValue)
//        return expression.getValue(context, Boolean::class.java)!!
//    }
    fun evaluateExpression(role: String, userUuid: String?, fieldName: String): Boolean {
        return roles.contains(role)
    }
}
