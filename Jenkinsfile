#! /usr/bin/groovy

pipeline {
    agent any
    options{
        disableConcurrentBuilds()
    }
    stages{
        stage("Unit Test"){
            steps{
                echo "Executing unit tests"
                runUnitTest()
            }
        }
        stage("Build Application"){
                    steps{
                        echo "Building application"
                        buildApp()
                    }
                }
        stage("Build Docker Image"){
            steps{
                echo "Building docker image"
                buildImage()
            }
        }
        stage("Deploy-Dev"){
            steps{
                echo "Deploying to dev"
                deploy('dev')
            }
        }
        stage("Run UAT-Dev"){
            steps{
                echo "Running UAT on dev"
                runUAT('http://192.20.0.3:9090')
            }
        }
        stage("Deploy-Prod"){
                    steps{
                        echo "Deploying to dev"
                        deploy('prod')
                    }
                }
        stage("Run UAT-Prod"){
            steps{
                echo "Running UAT on prod"
                runUAT('http://192.20.0.3:8080')
            }
        }
    }
}

def runUnitTest(){
    sh 'mvn clean test'
}

def buildApp(){
    sh 'mvn clean install -Dmaven.test.skip=true'
}

def buildImage(){
    def appImg = docker.build("shanu040/search-api:${BUILD_NUMBER}")
}

def deploy(environment) {

    	def containerName = ''
    	def port = ''
    	def network = 'search-api-network'
    	def config_server_url='http://192.20.0.2:4040'

    	if ("${environment}" == 'dev') {
    		containerName = "search-api-dev"
    		port = "9090"
    	}
    	else if ("${environment}" == 'prod') {
            		containerName = "search-api-prod"
            		port = "8080"
            	}
    	else {
    		println "Environment not valid"
    		System.exit(0)
    	}

    	sh "docker ps -f name=${containerName} -q | xargs -r docker stop"
    	sh "docker ps -a -f name=${containerName} -q | xargs -r docker rm"
    	sh "docker run --rm -d --name=${containerName} --network=${network} --ip=192.20.0.3 -e ARTICLE-FINDER.STORE-LOCATION=/tmp/store -e SPRING.CONFIG.IMPORT=optional:configserver:${config_server_url} -p ${port}:8080 shanu040/search-api:${BUILD_NUMBER}"
}

def runUAT(port){
        sh "chmod -R 777 uat/scenario.sh"
        sh "uat/scenario.sh ${port}"
}
