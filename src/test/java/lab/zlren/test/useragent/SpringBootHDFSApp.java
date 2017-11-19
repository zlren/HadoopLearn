package lab.zlren.test.useragent;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.hadoop.fs.FsShell;

import java.util.Collection;

/**
 * @author zlren
 * @date 2017-11-19
 */
@Slf4j
@SpringBootApplication
public class SpringBootHDFSApp implements CommandLineRunner {

    @Autowired
    FsShell fsShell;


    @Override
    public void run(String... strings) throws Exception {
        Collection<FileStatus> lsr = fsShell.lsr("/springhdfs");
        for (FileStatus fileStatus : lsr) {
            log.info("{}", fileStatus.getPath());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHDFSApp.class);
    }
}
