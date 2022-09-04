branch = "${TEST_BRANCH_NAME}"
currentBuild.displayName = "$branch"

def downloadProject(repo, branch) {
    cleanWs()
    checkout scm: [
            $class: 'GitSCM', branches: [[name: branch]],
            userRemoteConfigs: [[
                                        url: repo
                                ]]
    ]
}


withEnv([ "branch=${branch}"]) {
    stage("Merge Master") {
        if (!"$branch".contains("master")) {
            try {
                downloadProject("git@gitlab.com:epickonfetka/cicd-threadqa.git", "$branch")
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
    downloadProject("git@gitlab.com:epickonfetka/cicd-threadqa.git", "$branch")
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


