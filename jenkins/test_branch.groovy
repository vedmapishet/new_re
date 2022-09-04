task_branch = "${TEST_BRANCH_NAME}"
def branch = task_branch.contains("origin") ? task_branch.split('/')[1] : task_branch.trim()
currentBuild.displayName = "$branch"
git_base_url = "git@gitlab.com:epickonfetka/cicd-threadqa.git"


withEnv([ "branch=${branch}"]) {
    stage("Checkout on Branch And Merge Master") {
        if (!"$branch".contains("master")) {
            try {
                sh "git clone git@gitlab.com:epickonfetka/cicd-threadqa.git"
                sh "git checkout $branch"
                sh "git merge master"
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
        sh "./gradlew clean testme"
    } catch (err){
        echo "some test are failed"
        throw("${err}")
    } finally {
        sh "./gradlew allureReport"
        sh "zip -r report.zip build/reports/allure-report/allureReport/*"
        echo "Stage was finished"
    }
}


