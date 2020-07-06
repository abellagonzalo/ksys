package hola

class ScenarioRunner {
    fun run(instances: List<ScenarioInstance>) {
        if (instances.isEmpty()) throw Exception("No scenarios to run.")

        val thg = ThreadGroup("ksys-thread-group-1")
        val th = object : Thread(thg, "ksys-thread-1") {

            override fun run() {
                instances.first().sharedSetup()
                instances.first().sharedValidate()

                instances.forEachIndexed { index, scenarioInstance ->

                    scenarioInstance.setup()
                    scenarioInstance.execute()
                    scenarioInstance.validate()
                    scenarioInstance.teardown()

                    println("Scenario ${scenarioInstance.id}: ${scenarioInstance.status}")
                    if (scenarioInstance.status == TestOutcome.FAILED)
                        scenarioInstance.exception!!.printStackTrace(System.out)
                }

                instances.last().sharedTeardown()
            }
        }

        th.start()
        th.join()
    }

    fun setupOnly(instances: List<ScenarioInstance>) {
        if (instances.size > 1) throw Exception("Only one test can run when using --setup-only")
        val scenarioInstance = instances.first()
        scenarioInstance.sharedSetup()
        scenarioInstance.sharedValidate()
        scenarioInstance.setup()
    }
}