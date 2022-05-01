package io.bsamartins.graphql.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SecuredDirectiveEvaluator() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun evaluateField(fieldName: String, path: String, requiredFieldRole: String?, requiredTypeRole: String?, roles: Set<String> = emptySet()): Boolean {
        logger.info("Evaluating field [{}] typeRole[{}], fieldRole[{}]", path, requiredFieldRole, requiredTypeRole)
        logger.info("User roles: {}", roles)
        if (requiredTypeRole != null && requiredFieldRole !in roles) {
            return false
        }
        return requiredFieldRole in roles
    }

    fun evaluateObject(fieldName: String, path: String, role: String, roles: Set<String> = emptySet()): Boolean {
        logger.info("Evaluating object path [{}] requiring role [{}]", path, role)
        logger.info("User roles: {}", roles)
        return role in roles
    }
}
