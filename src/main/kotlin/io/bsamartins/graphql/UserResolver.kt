package io.bsamartins.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import io.bsamartins.graphql.model.types.EmploymentModel
import io.bsamartins.graphql.model.types.UserModel

@DgsComponent
class UserResolver {

    @DgsQuery(field = "findUsers")
    fun findUsers(): List<UserModel> {
        return listOf(
            UserModel(
                id = 1,
                name = "bla",
                employment = EmploymentModel(
                    salary = 100_000.0,
                    company = "ACME"
                ),
                email = "bla@hotmail.com"
            )
        )
    }
}
