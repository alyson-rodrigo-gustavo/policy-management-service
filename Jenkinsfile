pipeline {
    // Pode ser adaptado para rodar dentro de um container Docker/Kubernetes específico
    agent any

    // Definição das ferramentas que devem estar previamente configuradas no Jenkins global
    tools {
        maven 'Maven 3' // Nome configurado no Global Tool Configuration do Jenkins
        jdk 'JDK 21'    // Nome configurado no Global Tool Configuration do Jenkins
    }

    environment {
        // Variáveis de ambiente e IDs das credenciais cadastradas no Jenkins
        DOCKER_IMAGE = 'alysongustavo/policy-management-service'
        IMAGE_TAG = "v1.0.${env.BUILD_NUMBER}"

        // AWS EKS Configs
        AWS_REGION = 'us-east-1'
        EKS_CLUSTER_NAME = 'policy-management-cluster'

        // Credenciais (Os IDs devem bater com os cadastrados no Jenkins Credentials)
        DOCKER_CREDS = credentials('docker-hub-credentials-id')
        AWS_CREDS = credentials('aws-credentials-id')
    }

    stages {
        stage('1. Build (Skip Tests)') {
            steps {
                echo 'Fazendo o build dos fontes e gerando o .jar sem executar testes...'
                // Usa o package para garantir que o empacotamento do Spring Boot funciona
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('2. SonarQube Analysis') {
            steps {
                echo 'Executando a análise estática de código no SonarQube...'
                // Requer o plugin do SonarQube configurado no Jenkins
                withSonarQubeEnv('SonarQube-Server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('3. Unit & Integration Tests') {
            steps {
                echo 'Executando testes unitários e de integração...'
                // Usa o 'verify' para garantir que os testes do plugin Failsafe (seus arquivos *IT.java) também rodem
                sh 'mvn verify'
            }
            post {
                always {
                    // Salva os relatórios do JUnit e Jacoco no Jenkins para visualização
                    junit 'target/surefire-reports/*.xml, target/failsafe-reports/*.xml'
                }
            }
        }

        stage('4. Build & Push Docker Image') {
            steps {
                echo "Construindo a imagem Docker ${DOCKER_IMAGE}:${IMAGE_TAG}..."
                sh "docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest ."

                echo 'Realizando login e enviando para o Docker Hub...'
                sh "echo \$DOCKER_CREDS_PSW | docker login -u \$DOCKER_CREDS_USR --password-stdin"

                sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
            post {
                always {
                    // Limpeza local para não lotar o disco do servidor Jenkins
                    sh "docker logout"
                    sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} || true"
                    sh "docker rmi ${DOCKER_IMAGE}:latest || true"
                }
            }
        }

        stage('5. Deploy to AWS EKS') {
            steps {
                script {
                    // Define qual overlay usar baseado na branch ou parâmetro do Job
                    def environment = "dev"

                    sh "aws eks update-kubeconfig --region us-east-1 --name cluster-name"

                    // O comando mágico do Kustomize
                    sh "kubectl apply -k k8s/overlays/${environment}"
                }
            }
        }
    }

    post {
        success {
            echo "🚀 Pipeline finalizado com sucesso! Deploy da versão ${IMAGE_TAG} realizado no EKS."
        }
        failure {
            echo "❌ Pipeline falhou. Verifique os logs."
        }
    }
}