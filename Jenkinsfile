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
			withCredentials([usernamePassword(credentialsId: '2c2c8566-5ee8-4b0e-84af-74c588766f75', usernameVariable: 'CLIENT_ID', passwordVariable: 'CLIENT_SECRET')]) {
	            maven {
    	        }
			}
        }
    }

    reportIssues()
}

