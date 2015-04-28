package services

import services.GitService
import services.SwiftService

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

                if (GitService.isEverythingToCommit()) {
                    throw new RuntimeException('you should commit and push you branch')
                }

                GitService.createTag(tagName)

                GitService.push(tagName)
            }

            GitService.checkout(tagName)
        }

        SwiftService.uploadFile(env)

        GitService.checkout(GitService.getCurrentBranch())
    }

}