package abellagonzalo.events

data class ThreadCreatedEvent(
    val threadGroupId: String,
    val sharedSetupOrScenarioId: String
)