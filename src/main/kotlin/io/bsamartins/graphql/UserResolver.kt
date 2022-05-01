package io.bsamartins.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
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
                name = "Tom Cruise",
                email = "bla@hotmail.com"
            )
        )
    }

    @DgsData(parentType = "UserModel", field = "employment")
    fun getEmployment(dfe: DgsDataFetchingEnvironment): EmploymentModel {
        val user = dfe.getSource<UserModel>()
        val email = user.name!!.lowercase().replace(" ", ".") + "@acme.com"
        return EmploymentModel(
            salary = 100_000.0,
            email = email,
            company = "ACME"
        )
    }
}
