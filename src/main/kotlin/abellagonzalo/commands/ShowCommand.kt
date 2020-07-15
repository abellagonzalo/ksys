package abellagonzalo.commands

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

// TODO - Add support for * wildcard
// TODO - Add support for regex
@Command(name = "show")
class ShowCommand : BaseCommand() {

    @Parameters(index = "0")
    var filter: String = ""

    override fun call(): Int {
        return 0
    }

}