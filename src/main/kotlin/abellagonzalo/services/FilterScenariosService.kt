package abellagonzalo.services

import abellagonzalo.scenarios.Tag

class FilterScenariosService {
    fun filter(pattern: String, scenarios: List<Identifiable>): List<Identifiable> {
        val patternTrimmed = pattern.trim('*')
        val startsWithStar = pattern.startsWith('*')
        val endsWithStar = pattern.endsWith('*')

        if (startsWithStar && endsWithStar)
            return scenarios.filter { it.id.contains(patternTrimmed) }

        if (startsWithStar)
            return scenarios.filter { it.id.endsWith(patternTrimmed) }

        if (endsWithStar)
            return scenarios.filter { it.id.startsWith(patternTrimmed) }

        return scenarios.filter { it.id == pattern }
    }

    fun filter(tag: Tag, scenarios: List<Identifiable>): List<Identifiable> {
        return scenarios.filter { it.tags.contains(tag) }
    }
}
