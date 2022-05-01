package io.bsamartins.graphql.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SecuredDirectiveEvaluator() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun evaluateField(path: String, requiredFieldRole: String?, requiredTypeRole: String?, roles: Set<String> = emptySet()): Boolean {
        logger.info("Evaluating field [{}] typeRole[{}], fieldRole[{}]", path, requiredTypeRole, requiredFieldRole)
        if (requiredTypeRole != null && requiredTypeRole !in roles) {
            return false
        }
        if (requiredFieldRole != null && requiredFieldRole !in roles) {
            return false
        }
        return true
    }
}
