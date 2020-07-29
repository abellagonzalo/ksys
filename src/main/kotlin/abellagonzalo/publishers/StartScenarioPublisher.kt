package abellagonzalo.publishers

import abellagonzalo.publishers.EndScenarioPublisher

interface StartScenarioPublisher {
    fun publishStart(id: String): EndScenarioPublisher
}