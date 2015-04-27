#!/usr/bin/env groovy

def cli = new CliBuilder(
        usage: 'git-deploy --env environment [--tag tagName, --rollback true]\n',
        header: 'Available options (use -h for help):\n'
)

cli.with {
    h(longOpt: 'help', 'Usage information', required: false)
    e(longOpt: 'env', 'Environment [test|prod]', required: true, args: 1)
    t(longOpt: 'tag', 'Tag name for prod environment', required: false, args: 1)
    r(longOpt: 'rollback', 'Rollback tagName', required: false, args: 1)
}

def opt = cli.parse(args)

if (!opt) return

if (opt.h) {
    cli.usage()
    return
}

String env = opt.e

if (!(env in ['test', 'prod'])) {
    throw new RuntimeException('env should be "prod" or "test"')
}

if (env == 'prod')  {
    String tagName = opt.t
    Boolean rollback = opt.r as Boolean

    if(!tagName) {
        throw new RuntimeException('tag name should not be empty')
    }

    boolean existsTag = existsTag(tagName)

    if(rollback) {
        if (!existsTag) {
            throw new RuntimeException('tag should exist for rollback')
        }
    } else {
        if (existsTag) {
            throw new RuntimeException('tag was exist, do you want rollback?')
        }

        createTag(tagName)
    }

    checkout(tagName)
}

executeDeploy(env)

void createTag(String tagName) {
    def executionCreateTag = ("git tag ${tagName}").execute()

    executionCreateTag.waitFor()
}

void checkout(String tagName) {
    def executionCreateTag = ("git checkout ${tagName}").execute()

    executionCreateTag.waitFor()
}

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

boolean executeDeploy(String env) {
    println '############'
    println "DEPLOY ${env}"
    println '############'
}