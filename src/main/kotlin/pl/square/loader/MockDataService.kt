package pl.square.loader

import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.square.data.DataHolder
import pl.square.model.Attribute
import pl.square.model.AttributeValue
import pl.square.model.Lang
import pl.square.model.LocalizedString


@Service
@Profile("mock")
class MockDataService(val store: DataHolder) {

    private val logger = KotlinLogging.logger {}

    init {
        loadAttributes()
        loadValues()
        logger.info("Mock data loaded")
    }

    private fun loadAttributes() {
        val languagesList = listOf(Lang("pl_PL"), Lang("it_IT"), Lang("nl_NL"))

        val attributes = listOf(
            Attribute(
                "helmetsize",
                listOf(
                    LocalizedString(Lang("it_IT"), "Tagliecasco")
                )
            ),
            Attribute(
                "sizeman",
                listOf(
                    LocalizedString(Lang("nl_NL"), "aat Heren")
                )
            ),
            Attribute(
                "color",
                listOf(
                    LocalizedString(Lang("pl_PL"), "niebieski")
                )
            )
        )

        store.languages.addAll(languagesList)
        store.attributes.addAll(attributes)

    }


    private fun loadValues() {

        val values = listOf(
            AttributeValue(
                Attribute(
                    "helmetsize",
                    listOf(
                        LocalizedString(Lang("it_IT"), "Tagliecasco")
                    )
                ),
                "35mm",
                listOf(
                    LocalizedString(Lang("it_IT"), "35 mm")
                ),
                1
            ),

            AttributeValue(
                Attribute(
                    "sizeman",
                    listOf(
                        LocalizedString(Lang("nl_NL"), "Maat Heren")
                    )
                ),
                "medium_long_sleeved",
                listOf(
                    LocalizedString(Lang("nl_NL"), "Medium - Lange mouwen"),
                    LocalizedString(Lang("sv_SE"), "Medium-Lång Ärm")
                ),
                2
            ),

            AttributeValue(
                Attribute(
                    "color",
                    listOf(
                        LocalizedString(Lang("fr_FR"), "Kleur"),
                        LocalizedString(Lang("cs_CZ"), "Barva")
                    )
                ),
                "stainless_steel",
                listOf(
                    LocalizedString(Lang("fr_FR"), "Acier Inoxydable"),
                    LocalizedString(Lang("cs_CZ"), "Nerezová Ocel")
                ),
                2
            )
        )

        store.attributeValues.addAll(values)

        //

        // find languages in attributeValues
        val valuesFlat = store.attributeValues.flatMap { av -> av.localizedValues }
        store.languages.addAll(valuesFlat.map { v -> v.language })
    }

}
