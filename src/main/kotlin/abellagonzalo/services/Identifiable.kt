package abellagonzalo.services

import abellagonzalo.scenarios.Tag

interface Identifiable {
    val id: String
    val tags: List<Tag>
}
