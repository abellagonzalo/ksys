package abellagonzalo.commands

import picocli.CommandLine.Command

// TODO - Add support for * wildcard
// TODO - Add support for regex
@Command(name = "list")
class ListCommand : BaseCommand() {
    override fun call(): Int {
        return 0
    }
}
