package abellagonzalo.teardown

interface CleanerManager {
    fun createNew(): ScenarioCleaner
}