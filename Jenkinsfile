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

            }
        }
    }
}


def unitTest(){

        sh 'mvn clean test'

}
/*
def buildImage(){
    dir('SampleSearchApi'){
            def appImg = docker.build("shanu040/search-api:${BUILD_VERSION}")
    }
}


def deploy(environment) {

    	def containerName = ''
    	def port = ''

    	if ("${environment}" == 'dev') {
    		containerName = "search-api-dev"
    		port = "8080"
    	}
    	else {
    		println "Environment not valid"
    		System.exit(0)
    	}

    	sh "docker ps -f name=${containerName} -q | xargs --no-run-if-empty docker stop"
    	sh "docker ps -a -f name=${containerName} -q | xargs -r docker rm"
    	sh "docker run --rm -d --name=${containerName} --network=search-api-network -e ARTICLE-FINDER.STORE-LOCATION=/tmp/store -e SPRING.CONFIG.IMPORT=optional:configserver:http://192.20.0.2:4040/ -p ${port}:8080 shanu040/search-api"
} */
