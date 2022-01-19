package pl.square.loader

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import pl.square.data.DataHolder
import pl.square.model.Attribute
import pl.square.model.AttributeValue
import pl.square.model.Lang
import pl.square.model.LocalizedString
import java.io.InputStreamReader
import kotlin.streams.toList


@Service
@Profile("!mock")
class CsvLoaderService(val store: DataHolder) {

    private val logger = KotlinLogging.logger {}
    private val CSV_FORMAT = CSVFormat.DEFAULT.builder().setDelimiter(';').build()

    init {
        try {
            loadAttributes()
            loadValues()
        } catch (e: Exception) {
            logger.error ("Error loading CSV data")
        }
    }

    private fun loadAttributes() {
        val stream = javaClass.classLoader.getResourceAsStream("attributes.csv")
            ?: throw IllegalStateException("cannot find resource: attributes.csv")
        val reader = InputStreamReader(stream, "UTF-8")
        val parser = CSVParser(reader, CSV_FORMAT)
        val iter = parser.iterator()

        // header is first row
        // code;label-it_IT;label-en_GB;label-en_IE;label-pl_PL;label-nl_NL;label-nl_BE;label-nb_NO;label-fi_FI;label-en_US;label-es_ES;label-de_DE;label-de_CH;label-de_AT;label-da_DK;label-fr_FR;label-sv_SE;label-cs_CZ
        val header = iter.next()
        // local language list for relative label language parsing
        val languagesList = parseLanguages(header, 0)

        val attributes = ArrayList<Attribute>()
        while (iter.hasNext()) {
            val row = iter.next()
            // supplierpacksize;"Numero in confezioni dal fornitore";"Number in Package from Supplier";"Number in Package from Supplier";"Numer w Opakowaniu od Dostawcy";"Aantal in het pakket van de leverancier";"Aantal in het pakket van de leverancier";"Antall i pakke fra leverandør";"Toimittajan tuotenumero";;"Número en el paquete del distribuidor";"Anzahl im Paket vom Lieferanten";"Anzahl im Paket vom Lieferanten";"Anzahl im Paket vom Lieferanten";"antal i pakken fra leverandören";"Quantité dans le carton du fournisseur";"Antal i paket från leverantör";"Počet V Balení Od Dodavatele"
            val localizedLabels = ArrayList<LocalizedString>()
            for (k in 1 until row.size()) {
                localizedLabels.add(LocalizedString(languagesList[k - 1], row.get(k)))
            }
            val code = row.get(0)
            val attr = Attribute(code, localizedLabels)
            attributes.add(attr)
        }

        logger.info("attributes done")
        store.languages.addAll(languagesList)
        store.attributes.addAll(attributes)
    }

    private fun loadValues() {
        val stream = javaClass.classLoader.getResourceAsStream("options.csv")
            ?: throw IllegalStateException("cannot find resource: options.csv")
        val reader = InputStreamReader(stream, "UTF-8")
        val parser = CSVParser(reader, CSV_FORMAT)
        val iter = parser.iterator()

        // header is first row. parse appropriate languages here
        // code;label-es_ES;label-nl_NL;label-nl_BE;label-nb_NO;label-it_IT;label-fr_FR;label-fi_FI;label-en_US;label-sv_SE;label-en_IE;label-en_GB;label-de_DE;label-de_CH;label-de_AT;label-da_DK;label-cs_CZ;label-pl_PL;attribute;sort_order
        val header = iter.next()

        // each file can have own language list
        val languagesList = parseLanguages(header, 2)
        // extend with missing languages
        store.languages.addAll(languagesList)

        // remaining rows are values
        // 35mm;"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";"35 mm";helmetsize;0
        val attributeValues = ArrayList<AttributeValue>()
        while (iter.hasNext()) {
            val row = iter.next()
            val value = row[0]
            val code = row[row.size() - 2]
            val attribute = store.attributes.find { attribute -> attribute.name == code }
            if (attribute == null) {
                logger.warn ("attribute $code not found")
                continue
            }
            val sortOrder = row[row.size() - 1]
            val localizedValues = ArrayList<LocalizedString>()
            for (k in 1 until row.size() - 2) {
                val langCode = languagesList[k - 1].code
                // get object from global list
                val lang = store.languages.find { lang -> lang.code == langCode }
                if (lang == null) {
                    logger.warn ("language $langCode not found")
                    continue
                }
                localizedValues.add(LocalizedString(lang, row.get(k)))
            }
            val av = AttributeValue(attribute, value, localizedValues, sortOrder.toInt())
            attributeValues.add(av)
        }
        store.attributeValues.addAll(attributeValues)
    }


    /**
     * lastOffset: for options skip last 2 fields in row.
     *             for attributes read up to last cell
     *
     *    Later save data in `store.languages`
     *    As this is a HashSet, in case of doubled language between files, only one will be stored
     */
    private fun parseLanguages(header: CSVRecord, lastOffset: Int): List<Lang> {
        return header.stream().limit((header.size() - lastOffset).toLong()).skip(1)
            .map { l -> Lang(l.substring("label-".length)) }.toList()
    }


}
