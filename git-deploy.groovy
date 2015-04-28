#!/usr/bin/env groovy

import services.DeployService
import services.GitService
import services.SwiftService

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

DeployService.doDeploy(e, opt.t, opt.r as Boolean)

return