package abellagonzalo

import abellagonzalo.scenarios.Tag
import abellagonzalo.services.FilterScenariosService
import abellagonzalo.services.Identifiable
import org.junit.jupiter.api.Test

class FilterScenariosServiceTests {

    private class TestIdentifiable(override val id: String, vararg tags: String) :
        Identifiable {
        override val tags: List<Tag> = tags.map { Tag(it) }
    }

    private val scenarios = listOf(
        TestIdentifiable("id1", "group1"),
        TestIdentifiable("id2", "group2"),
        TestIdentifiable("scenario1", "group1"),
        TestIdentifiable("scenario2", "group2")
    )

    @Test
    fun `Filter scenarios by id`() {
        val scenariosToRun = FilterScenariosService().filter("id1", scenarios)
        scenariosToRun.assertIdsAre("id1")
    }

    @Test
    fun `Filter scenarios by ends-with`() {
        val scenariosToRun = FilterScenariosService().filter("*2", scenarios)
        scenariosToRun.assertIdsAre("id2", "scenario2")
    }

    @Test
    fun `Filter scenarios by starts-with`() {
        val scenariosToRun = FilterScenariosService().filter("id*", scenarios)
        scenariosToRun.assertIdsAre("id1", "id2")
    }

    @Test
    fun `Filter scenario by contains`() {
        val scenariosToRun = FilterScenariosService()
            .filter("*cena*", scenarios)
        scenariosToRun.assertIdsAre("scenario1", "scenario2")
    }

    @Test
    fun `Filter by tag`() {
        val scenariosToRun = FilterScenariosService()
            .filter(Tag("group1"), scenarios)
        scenariosToRun.assertIdsAre("id1", "scenario1")
    }

    private fun List<Identifiable>.assertIdsAre(vararg ids: String) {
        assertEquals(this.size, ids.size)
        ids.zip(this).forEach { assertEquals(it.first, it.second.id) }
    }
}
