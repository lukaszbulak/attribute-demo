package pl.square.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import mu.KotlinLogging
import org.springframework.stereotype.Component
import pl.square.data.DataHolder
import pl.square.model.Attribute
import pl.square.model.AttributeValue
import pl.square.model.Lang
import pl.square.model.LocalizedString
import java.lang.IllegalStateException


enum class AttributesMutationStatus {
    SUCCESS, ERROR
}

@Component
class AttributesMutation(private val store: DataHolder): Mutation {
    
    companion object {
        val logger = KotlinLogging.logger {  }
    }
    
    @GraphQLDescription("Add language")
    fun addLanguage(code: String): AttributesMutationStatus = try {
        store.languages.add(Lang(code))
        AttributesMutationStatus.SUCCESS
    } catch (e: Exception) {
        logger.error { e }
        AttributesMutationStatus.ERROR
    }

    @GraphQLDescription("Add attribute")
    fun addAttribute(attr: Attribute): AttributesMutationStatus = try {
        store.attributes.add(attr)
        AttributesMutationStatus.SUCCESS
    } catch (e: Exception) {
        logger.error { e }
        AttributesMutationStatus.ERROR
    }

    @GraphQLDescription("Add attribute value")
    fun addAttributeValue(attributeCode: String, value: String, localizedValues: List<LocalizedString>, sortOrder: Int): AttributesMutationStatus = try {
        val attr = store.attributes.find { attribute -> attribute.name == attributeCode }
            ?: throw IllegalStateException("attribute with name $attributeCode not found")
        val attrVal = AttributeValue(attr, value, localizedValues, sortOrder)
        store.attributeValues.add(attrVal)
        AttributesMutationStatus.SUCCESS
    } catch (e: Exception) {
        logger.error { e }
        AttributesMutationStatus.ERROR
    }

}
