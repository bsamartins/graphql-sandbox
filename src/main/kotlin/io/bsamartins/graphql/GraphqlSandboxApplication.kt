package io.bsamartins.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphqlSandboxApplication

fun main(args: Array<String>) {
    runApplication<GraphqlSandboxApplication>(*args)
}
