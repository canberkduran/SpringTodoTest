pipeline {
    agent any

    tools {
        maven 'M3' 
    }

    triggers {
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kaynak Kodu Çek') {
            steps {
                git branch: 'main', url: 'https://github.com/canberkduran/SpringTodoTest.git'
            }
        }

        stage('2. Test ve Paketleme') {
            steps {
                echo 'JUnit testleri çalıştırılıyor ve JAR paketi oluşturuluyor...'
                // Hem example.com testini yapar hem de uygulamayı paketler
                // -DskipTests demediğimiz için testler başarısız olursa paketleme yapılmaz
                sh 'mvn clean package'
            }
        }

        stage('3. Docker ile Deploy') {
            steps {
                echo 'Docker konteynerleri güncelleniyor...'
                // Veritabanı ve Uygulamanın olduğu docker-compose dosyasını tetikler
                // --build: Kod değişikliği varsa imajı yeniden oluşturur
                // -d: Arka planda çalıştırır
                sh 'docker-compose up -d --build'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Uygulama ve Veritabanı başarıyla ayağa kalktı! 🚀'
        }
    }
}
