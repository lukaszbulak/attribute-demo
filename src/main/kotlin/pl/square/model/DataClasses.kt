package pl.square.model

import org.springframework.util.StringUtils
import java.security.InvalidParameterException
import java.util.*

data class Lang(val code: String) {
    init {
        // verify that provided code conforms to standard locale
        val locale = StringUtils.parseLocaleString(code) ?: throw InvalidParameterException("bad locale: $code")
        if (!Locale.getAvailableLocales().contains(locale)) {
            throw InvalidParameterException("locale not found: $code")
        }
    }
}

data class LocalizedString(val language: Lang, val label: String)
data class Attribute(val name: String, val labels: List<LocalizedString>)
data class AttributeValue(val attribute: Attribute, val value: String, val localizedValues: List<LocalizedString>, val sortOrder: Int)
