pipeline {
    agent any

    tools {
        // Jenkins > Manage Jenkins > Tools kısmında Maven'a verdiğin isim (M3)
        maven 'M3' 
    }

    triggers {
        // GitHub'ı dakikada bir kontrol eder
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kodu Çek') {
            steps {
                // Kendi repo bilgilerini buraya yaz
                git branch: 'main', url: 'https://github.com/canberkduran/SpringTodoTest.git'
            }
        }

        stage('2. Erişim ve Birim Testleri') {
            steps {
                echo 'Sadece TodoServiceTest (example.com kontrolü dahil) çalıştırılıyor...'
                
                // -Dtest=TodoServiceTest komutu sadece senin yazdığın testi çalıştırır.
                // Diğer veritabanı hatası veren genel testleri (DemoApplicationTests) atlar.
                sh 'mvn clean test -Dtest=TodoServiceTest'
            }
        }
    }

    post {
        always {
            // Test sonuçlarını Jenkins arayüzünde "Test Result" olarak görmek için
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'TEBRİKLER: Test Başarılı, example.com 200 döndü! ✅'
        }
        failure {
            echo 'HATA: Test Başarısız, siteye erişilemedi veya kod hatası var! ❌'
        }
    }
}
