package io.bsamartins.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import io.bsamartins.graphql.model.types.DirectorModel
import io.bsamartins.graphql.model.types.MovieModel

@DgsComponent
class MovieResolver {

    @DgsQuery(field = "findMovies")
    fun findMovies(): List<MovieModel> {
        return listOf(
            MovieModel(
                id = 1,
                title = "bla",
                director = DirectorModel(
                    id = 1,
                    name = "Bla Bla",
                    salary = 100_000.0
                ),
            )
        )
    }
}
