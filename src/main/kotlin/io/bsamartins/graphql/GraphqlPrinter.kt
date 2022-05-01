package io.bsamartins.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class GraphqlPrinter(private val schema: GraphQLSchema) {

    @PostConstruct
    fun onInitialize() {
//        schema.directives.forEach {
//            println(it)
//        }
        schema.allTypesAsList.filterIsInstance<GraphQLObjectType>()
            .forEach {
                println("\t" + it.name)
                println("\t\tDirectives: ")
                if (it.directives.isEmpty()) {
                    println("\t\t\t<none>")
                } else {
                    it.directives.forEach { directive -> println("\t\t\t$directive") }
                }
            }
    }
}
