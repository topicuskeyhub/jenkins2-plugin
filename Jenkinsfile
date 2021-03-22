config {
    daysToKeep = -1
    numberToKeep = 30
    artifactDaysToKeep = -1
    artifactNumberToKeep = 30
    concurrentBuilds = false
}

node {
    git.checkout { }

    catchError {
        stage("Build") {
            maven {
            }
        }
    }

    reportIssues()
}

