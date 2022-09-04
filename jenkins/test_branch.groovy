
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
                cleanWs()
                downloadProject("git@gitlab.com:epickonfetka/cicd-threadqa.git", "$branch")
                labelledShell(label: 'Merge Master', script: '''
                git checkout $branch
                git merge master
              ''')
            } catch (err) {
                labelledShell(label: 'Error Merge', script: '''
                echo "Failed to merge master to branch $branch"
              ''')
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
        labelledShell(label: 'Run tests', script: '''
        ./gradlew clean testme
      ''')
    } catch (err){
        labelledShell(label: 'Job Failed', script: '''
        echo "some test are failed"
      ''')
        throw("${err}")
    } finally {
        labelledShell(label: 'Zip test report', script: '''
                ./gradlew allureReport
                zip -r report.zip build/reports/allure-report/allureReport/*)
                ''')
        echo "Stage was finished"
    }
}


