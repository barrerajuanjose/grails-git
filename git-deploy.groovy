#!/usr/bin/env groovy


def cli = new CliBuilder(
        usage: 'deploy --dept department --app app [--serv serv, --scope scope] -v version\n',
        header: 'Available options (use -h for help):\n',
        footer: '\nby @bhaslop.\n'
)

cli.with {
    h(longOpt: 'help', 'Usage information', required: false)
    d(longOpt: 'dept', 'Department', required: true, args: 1)
    a(longOpt: 'app', 'Application', required: true, args: 1)
    s(longOpt: 'serv', 'Service', required: true, args: 1)
    c(longOpt: 'scope', 'Scope', required: false, args: 1)
    v(longOpt: 'version', 'Build version', required: true, args: 1)
}

def opt = cli.parse(args)

if( !opt ) return

if( opt.h ) {
    cli.usage()
    return
}


def query = [
        dept: opt.d,
        app: opt.a,
        serv: opt.s
]

if (this.args?.size() != 2) {
    throw new RuntimeException('Faltan parametros ejemplo "uploadFile [prod|test|rollback] [tagName]"')
}

String env = this.args[0]

if(!(env in ['test', 'prod', 'rollback'])) {
    throw new RuntimeException('env debe ser "prod" o "test"')
}

String tagName = this.args[1]

if(existsTag(tagName)) {
    return 'NO SE CREO LA VERSION'
}

println 'legos'

boolean existsTag(String tagName) {
    boolean existsTag = false
    def executionExistsTagName = ("git show ${tagName}").execute()

    executionExistsTagName.waitFor()

    String existsTagName = "${executionExistsTagName.getInputStream()}"

    if (existsTagName != '') {
        println "Ya existe un tag con el nombre ${tagName}"
        println existsTagName

        existsTag = true
    }

    existsTag
}