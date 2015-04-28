package services

class GitService {

    static boolean isEverythingToCommit() {
        def executionStatus = ("git status").execute()

        executionStatus.waitFor()

        String status = "${executionStatus.getInputStream()}"

        !status.contains('nothing to commit') ||
                status.contains('ahead') ||
                status.contains('behind')
    }

    static String getCurrentBranch() {
        def executionStatus = ('git rev-parse --abbrev-ref HEAD').execute()

        executionStatus.waitFor()

        executionStatus.getText()
    }

    static void push(String tagName) {
        ("git push origin ${tagName}").execute().waitFor()
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
            existsTag = true
        }

        existsTag
    }
}