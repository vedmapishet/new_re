task_branch = "${TEST_BRANCH_NAME}"
def branch_cutted = task_branch.contains("origin") ? task_branch.split('/')[1] : task_branch.trim()
currentBuild.displayName = "$branch_cutted"


node {
    withEnv(["branch=${branch_cutted}"]) {
        stage("Checkout Branch") {
            if (!"$branch_cutted".contains("master")) {
                try {
                    getProject("git@gitlab.com:epickonfetka/cicd-threadqa.git", "$branch_cutted")

                    labelledShell(label: 'Merge Master to Branch', script: '''
                  echo "Working with $branch"
                  git clone git@gitlab.com:epickonfetka/cicd-threadqa.git
                  git checkout $branch
                  git merge master
                   ''')
                } catch (err) {
                    echo "Failed to merge master to branch $branch_cutted"
                    throw ("${err}")
                }
            } else {
                echo "Current branch is master"
            }
        }

        stage("Run tests") {
            getProject("git@gitlab.com:epickonfetka/cicd-threadqa.git", "$branch_cutted")
            testPart()
        }
    }
}

def getProject(String repo, String branch) {
    cleanWs()
    checkout scm: [
            $class           : 'GitSCM', branches: [[name: branch]],
            userRemoteConfigs: [[
                                        url: repo
                                ]]
    ]
}

def testPart() {
    try {
        labelledShell(label: 'Run API tests', script: '''
            chmod +x gradlew
            ./gradlew -x test apiTests
        ''')
        labelledShell(label: 'Run UI tests', script: '''
            ./gradlew -x test uiTests
        ''')
    } catch (err) {
        echo "some test are failed"
        throw ("${err}")
    } finally {
        labelledShell(label: 'Generate Allure Report', script: '''
            ./gradlew allureReport
            zip -r report.zip build/reports/allure-report/allureReport/*
            ''')
    }
}


