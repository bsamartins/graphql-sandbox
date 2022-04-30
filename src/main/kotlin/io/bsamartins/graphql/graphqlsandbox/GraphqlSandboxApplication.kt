package io.bsamartins.graphql.graphqlsandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphqlSandboxApplication

fun main(args: Array<String>) {
    runApplication<GraphqlSandboxApplication>(*args)
}
