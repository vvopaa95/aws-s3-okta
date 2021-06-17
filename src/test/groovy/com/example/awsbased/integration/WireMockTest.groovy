package com.example.awsbased.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import org.springframework.util.SocketUtils
import spock.lang.Specification

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

class WireMockTest extends Specification {
    static def HTTP_PORT_PROPERTY = "wiremock.server.port"
    static def HTTPS_PORT_PROPERTY = "wiremock.https.port"
    static def PORT_MIN_NUMBER = 10000
    static def PORT_MAX_NUMBER = 12500

    static {
        setAvailablePortToProperty(HTTP_PORT_PROPERTY)
        setAvailablePortToProperty(HTTPS_PORT_PROPERTY)
        disableSslVerification()
    }

    static WireMockServer wireMockServer() {
        WireMockServer server = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .extensions(new ResponseTemplateTransformer.Builder()
                        .global(false)
                        .permittedSystemKeys("wiremock.*")
                        .build())
                .httpsPort(System.getProperty(HTTPS_PORT_PROPERTY) as int)
                .port(System.getProperty(HTTP_PORT_PROPERTY) as int))
        server.start()
        server
    }

    static void disableSslVerification() {
        def sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            X509Certificate[] getAcceptedIssuers() {
                println("getAcceptedIssuers")
                return null
            }

            void checkClientTrusted(X509Certificate[] certs, String authType) {
                println("checkClientTrusted")
            }

            void checkServerTrusted(X509Certificate[] certs, String authType) {
                println("checkServerTrusted")
            }
        }}, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true)
    }

    static void setAvailablePortToProperty(String key) {
        System.setProperty(key, SocketUtils.findAvailableTcpPort(PORT_MIN_NUMBER, PORT_MAX_NUMBER) as String)
    }
}
