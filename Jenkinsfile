pipeline {
    agent any
    
    tools {
        // Jenkins Tools kısmında verdiğin Maven ismini buraya yaz (M3 demiştik)
        maven 'M3' 
    }

    triggers {
        // Her dakika kontrol eder
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kodu Çek') {
            steps {
                checkout scm
            }
        }

        stage('2. JUnit Testlerini Çalıştır') {
            steps {
                // Java koduna eklediğin example.com testi burada koşacak
                sh 'mvn test -Dtest=TodoServiceTest'
            }
        }

        stage('3. Deploy') {
            steps {
                echo "Erişim testi başarılı, deploy adımı..."
            }
        }
    }
}
