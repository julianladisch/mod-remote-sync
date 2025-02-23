package mod_remote_sync

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.DefaultGrailsPluginManager
import groovy.transform.CompileStatic

import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory

import io.micronaut.context.annotation.Bean
import io.undertow.Undertow.Builder
import org.springframework.scheduling.annotation.EnableScheduling


class Application extends GrailsAutoConfiguration {
  static void main(String[] args) {
    // Ensure we have force UTC to be the local application TZ.
    final TimeZone utcTz = TimeZone.getTimeZone("UTC")
    if (TimeZone.default != utcTz) {
      TimeZone.default = utcTz
    }

    // This should be last...
    GrailsApp.run(Application, args)

  }

  @Bean
  UndertowServletWebServerFactory embeddedServletContainerFactory(){
    UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory()
    factory.builderCustomizers << new UndertowBuilderCustomizer() {

      @Override
      public void customize(Builder builder) {

        // Min of 2, Max of 4 I/O threads
        builder.ioThreads = Math.min(Math.max(Runtime.getRuntime().availableProcessors(), 2), 4)

        final int heap_coef = (Runtime.getRuntime().maxMemory() / 1024 / 1024)/256
        int workers_per_io = 8
        if (heap_coef <= 2) {
          workers_per_io = 6
        } else if (heap_coef <= 1) {
          workers_per_io = 4
        }

        // 8 Workers per I/O thread
        builder.workerThreads = builder.workerThreads = builder.ioThreads * workers_per_io

        // Enable HTTP2
        builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)

        println("Runtime memory reported ${Runtime.getRuntime().maxMemory() / 1024 / 1024} mb");
        println("Runtime CPUs reported ${Runtime.getRuntime().availableProcessors()}");
        println("Allocated ${builder.ioThreads} IO Threads");
        println("Allocated ${builder.workerThreads} worker threads");
        println("Allocated ${builder.bufferSize} bytes of ${builder.directBuffers ? 'direct' : 'indirect'} buffer space per thread");
      }
    }
  }

}
