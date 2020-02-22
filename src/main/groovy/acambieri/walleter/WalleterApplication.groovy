package acambieri.walleter

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@CompileStatic
class WalleterApplication {

    static void main(String[] args) {
        SpringApplication.run(WalleterApplication, args)
    }

}
