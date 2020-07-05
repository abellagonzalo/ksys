package hola

class ScenarioRunner {
    fun run(instances: List<ScenarioInstance>) {
        instances.forEachIndexed { index, scenarioInstance ->
            val thg = ThreadGroup("ksys-thread-group-1")
            val th = object : Thread(thg, "ksys-thread-1") {
                override fun run() {
                    // Register scenario in locator
                    RunningScenarioLocator.register(scenarioInstance)

                    if (index == 0) {
                        scenarioInstance.sharedSetup()
                        scenarioInstance.sharedValidate()
                    }

                    scenarioInstance.setup()
                    scenarioInstance.execute()
                    scenarioInstance.validate()
                    scenarioInstance.teardown()

                    if (index == instances.size - 1)
                        scenarioInstance.sharedTeardown()

                    println("Scenario ${scenarioInstance.id}: ${scenarioInstance.status}")
                    if (scenarioInstance.status == TestOutcome.FAILED)
                        scenarioInstance.exception!!.printStackTrace(System.out)

                    RunningScenarioLocator.removeScenario()
                }
            }
            th.start()
            th.join()
        }
    }

    fun setupOnly(instances: List<ScenarioInstance>) {
        if (instances.size > 1) throw Exception("Only one test can run when using --setup-only")
        val scenarioInstance = instances.first()
        scenarioInstance.sharedSetup()
        scenarioInstance.sharedValidate()
        scenarioInstance.setup()
    }
}