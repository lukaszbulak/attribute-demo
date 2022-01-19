package pl.square.loader;

import org.junit.jupiter.api.Test;
import pl.square.data.DataHolder;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvLoaderServiceTest {

    @Test
    public void parseCsvFiles() {

        // given
        var data = new DataHolder();

        // when
        // parsing occurs in constructor. fixed data in csv files in resources
        new CsvLoaderService(data);

        // then
        // assert sizes
        assertEquals(17, data.getLanguages().size());
        assertEquals(18, data.getAttributes().size());
        assertEquals(1266, data.getAttributeValues().size());
        // assert random values
        assertThat(data.getLanguages(), hasItems(
                        hasProperty("code", is("pl_PL")),
                        hasProperty("code", is("da_DK"))
                )
        );
        assertThat(data.getAttributes(), hasItem(
                hasProperty("labels", hasItem(hasProperty("label", is("Youth Sizes cl"))))
                )
        );
        assertThat(data.getAttributeValues(), hasItem(
                allOf (hasProperty("value", is("small_30mm")),
                        hasProperty("attribute", hasProperty("name", is("helmetsize")))
                )));
    }

}
