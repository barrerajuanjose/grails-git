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

String e = opt.e

if (!(e in ['test', 'prod'])) {
    throw new RuntimeException('env should be "prod" or "test"')
}

GitService.status()

//DeployService.doDeploy(e, opt.t, opt.r as Boolean)

class DeployService {

    static void doDeploy(String env, String tagName, boolean rollback) {
        if (env == 'prod') {
            if (!tagName) {
                throw new RuntimeException('tag name should not be empty')
            }

            boolean existsTag = GitService.existsTag(tagName)

            if (rollback) {
                if (!existsTag) {
                    throw new RuntimeException('tag should exist for rollback')
                }
            } else {
                if (existsTag) {
                    throw new RuntimeException('tag was exist, do you want rollback?')
                }

                GitService.createTag(tagName)
            }

            GitService.checkout(tagName)
        }

        SwiftService.executeDeploy(env)
    }

}

class GitService {

    static void status() {
        def executionStatus = ("git status").execute()

        executionStatus.waitFor()

        String status = "${executionStatus.getInputStream()}"

        println status
    }

    static void createTag(String tagName) {
        ("git tag ${tagName}").execute().waitFor()
    }

    static void checkout(String tagName) {
        ("git checkout ${tagName}").execute().waitFor()
    }

    static boolean existsTag(String tagName) {
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
}

class SwiftService {
    static boolean executeDeploy(String env) {
        println '############'
        println "DEPLOY ${env}"
        println '############'
    }
}