package pl.square.graphql.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pl.square.data.DataHolder
import pl.square.model.Attribute
import pl.square.model.AttributeValue
import pl.square.model.Lang


@Component
class AttributesQuery (val store: DataHolder) : Query {

    @GraphQLDescription("get languages")
    fun languages(): List<Lang> {
        return store.languages.toList()
    }

    @GraphQLDescription("get attributes with descriptions")
    fun attributes(): List<Attribute> {
        return store.attributes
    }


    @GraphQLDescription("get attribute values")
    fun attributeValues(): List<AttributeValue> {
        return store.attributeValues
    }


}
