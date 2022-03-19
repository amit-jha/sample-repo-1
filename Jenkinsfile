#! /usr/bin/groovy

pipeline {
    agent any
    options{
        disableConcurrentBuilds()
    }
    stages{
        stage("Test"){
            steps{
                echo "Execute Unit Test"
                unitTest()
            }
        }
        stage("Build Application"){
            steps{
                echo "Building Docker image"
                buildImage()
            }
        }

    }

    def unitTest(){
        dir('SampleSearchApi'){
            sh 'mvn clean test'
        }
        dir('SampleSearchApi'){
//             sh 'docker build . -t shanu040/search-api:${BUILD_VERSION}'
                def appImg = docker.build("shanu040/search-api:${BUILD_VERSION}")
        }
    }

}