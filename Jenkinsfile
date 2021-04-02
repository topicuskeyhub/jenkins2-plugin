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
			withCredentials([usernamePassword(credentialsId: '2c438487-0a77-49ad-9d14-82093d1a9c7f', usernameVariable: 'CLIENT_ID', passwordVariable: 'CLIENT_SECRET')]) {
	            maven {
    	        }
			}
        }
    }

    reportIssues()
}

