package lab.zlren.test.useragent;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author zlren
 * @date 2017-11-18
 */
public class SpringHadoopHDFSApp {

    private ApplicationContext context;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        context = new ClassPathXmlApplicationContext("beans.xml");
        fileSystem = (FileSystem) context.getBean("fileSystem");
    }

    @After
    public void tearDown() throws IOException {
        context = null;
        fileSystem.close();
    }

    /**
     * 测试创建hdfs文件夹
     *
     * @throws IOException
     */
    @Test
    public void testMkdir() throws IOException {
        fileSystem.mkdirs(new Path("/springhdfs"));
    }

    @Test
    public void testText() throws IOException {
        FSDataInputStream in = fileSystem.open(new Path("/springhdfs/partitionerfile"));
        IOUtils.copyBytes(in, System.out, 1024);
        in.close();
    }
}
