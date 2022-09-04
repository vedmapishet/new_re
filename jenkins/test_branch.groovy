task_branch = "${TEST_BRANCH_NAME}"
def branch = task_branch.contains("origin") ? task_branch.split('/')[1].toString() : task_branch.trim().toString()
currentBuild.displayName = "$branch"

def execSh(String script){
    sh(returnStdout: true, script: script as GString)
}

withEnv([ "branch=${branch}"]) {
    stage("Checkout Branch") {
        if (!"$branch".contains("master")) {
            try {
                sh """
                git clone git@gitlab.com:epickonfetka/cicd-threadqa.git
                git checkout $branch
                git merge master
                """
            } catch (err) {
                echo "Failed to merge master to branch $branch"
                throw("${err}")
            }
        } else {
            echo "Current branch is master"
        }
    }

    stage("Run tests") {
        testPart()
    }
}

def testPart(){
    try {
        execSh( "./gradlew clean testme")
    } catch (err){
        echo "some test are failed"
        throw("${err}")
    } finally {
        sh "./gradlew allureReport"
        sh "zip -r report.zip build/reports/allure-report/allureReport/*"
        echo "Stage was finished"
    }
}


