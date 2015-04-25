#!/usr/bin/env groovy

if (this.args?.size() != 2) {
    throw new RuntimeException('Faltan parametros ejemplo "uploadFile [prod|test] [tagName]"')
}

String env = this.args[0]

if(!(env in ['test', 'prod'])) {
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