package pl.square.data

import org.springframework.stereotype.Service
import pl.square.model.Attribute
import pl.square.model.AttributeValue
import pl.square.model.Lang

@Service
class DataHolder {

    val languages  = HashSet<Lang>()
    val attributes = ArrayList<Attribute>()
    val attributeValues = ArrayList<AttributeValue>()

}